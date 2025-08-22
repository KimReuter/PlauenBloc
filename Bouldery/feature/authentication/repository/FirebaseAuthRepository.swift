//
//  FirebaseAuthRepository.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation
import FirebaseAuth
import FirebaseFirestore
import Combine

final class FirebaseAuthRepository: AuthRepository, ObservableObject {
    
    
    // MARK: - State (StateFlow-Ersatz)
    
    @Published private var isLoggedIn: Bool = false
    @Published private var isInitialized: Bool = false
    
    var isLoggedInPublisher: AnyPublisher<Bool, Never> { $isLoggedIn.eraseToAnyPublisher() }
    var isInitializedPublisher: AnyPublisher<Bool, Never> { $isInitialized.eraseToAnyPublisher() }
    
    var isLoggedInValue: Bool { isLoggedIn }
    var isInitializedValue: Bool { isInitialized }
    
    private let db = Firestore.firestore()
    private var authListenerHandle: AuthStateDidChangeListenerHandle?
    
    // MARK: - Init / Deinit
    
    init() {
        // initialer Zustand
        Task { [weak self] in
            guard let self = self else { return }
            self.isLoggedIn = (Auth.auth().currentUser != nil)
            self.isInitialized = true
        }
        // Live-Listener
        authListenerHandle = Auth.auth().addStateDidChangeListener { [weak self] _, user in
            self?.isLoggedIn = (user != nil)
        }
    }
    
    deinit {
        if let h = authListenerHandle {
            Auth.auth().removeStateDidChangeListener(h)
        }
    }
    
    // MARK: - API
    
    func signUp(userName: String, email: String, password: String) async -> AuthResult {
        do {
            let result = try await Auth.auth().createUser(withEmail: email, password: password)
            
            // DisplayName setzen (Parität zu Android updateProfile)
            let changeRequest = result.user.createProfileChangeRequest()
            changeRequest.displayName = userName
            try await changeRequest.commitChanges()
            
            // users/{uid} mit Default-Rolle sichern/anlegen
            await ensureUserDoc(uid: result.user.uid,
                                userName: result.user.displayName ?? userName,
                                email: result.user.email)
            
            self.isLoggedIn = true
            return .success
        } catch {
            return .error(mapSignupError(error))
        }
    }
    
    func signIn(email: String, password: String) async -> AuthResult {
        do {
            let result = try await Auth.auth().signIn(withEmail: email, password: password)
            
            // auch beim SignIn sicherstellen (Migration/Import-Fälle)
            await ensureUserDoc(uid: result.user.uid,
                                userName: result.user.displayName,
                                email: result.user.email)
            
            Task {
                do {
                    try await Firestore.firestore()
                        .collection("users")
                        .document(result.user.uid)
                        .setData(["debugPing": Date().timeIntervalSince1970], merge: true)
                    print("✅ direct test write ok")
                } catch {
                    print("❌ direct test write failed:", error)
                }
            }
            
            self.isLoggedIn = true
            return .success
        } catch {
            return .error(mapSigninError(error))
        }
    }
    
    func fetchUserRole(userId: String) async -> UserRole? {
        do {
            let snap = try await db.collection("users").document(userId).getDocument()
            guard let roleString = snap.get("role") as? String else { return .USER }
            return UserRole(rawValue: roleString) ?? .USER
        } catch {
            print("⚠️ Fehler beim Abrufen der Rolle: \(error.localizedDescription)")
            return nil
        }
    }
    
    func sendPasswort(email: String) async -> Result<Void, Error> {
        do {
            try await Auth.auth().sendPasswordReset(withEmail: email)
            return .success(())
        } catch {
            print("Fehler beim Passwort-Zurücksetzen: \(error.localizedDescription)")
            return .failure(error)
        }
    }
    
    func getCurrentUserId() -> String? {
        Auth.auth().currentUser?.uid
    }
    
    func signOut() {
        do {
            try Auth.auth().signOut()
            self.isLoggedIn = false
        } catch {
            print("⚠️ SignOut Fehler: \(error.localizedDescription)")
        }
    }
    
    // MARK: - Internals
    
    /// Legt users/{uid} an (falls fehlend) oder ergänzt fehlende Felder (z. B. role).
    private func ensureUserDoc(uid: String, userName: String?, email: String?) async {
        let ref = db.collection("users").document(uid)
        do {
            let snap = try await ref.getDocument()
            print("ensureUserDoc: exists=\(snap.exists) for uid=\(uid)")
            if snap.exists {
                if snap.get("role") == nil {
                    try await ref.setData(["role": UserRole.USER.rawValue], merge: true)
                    print("ensureUserDoc: role added USER")
                }
            } else {
                let display = (userName?.isEmpty == false)
                ? userName!
                : (email?.split(separator: "@").first.map(String.init) ?? "User")
                let userData: [String: Any] = [
                    "uid": uid,
                    "userName": display,
                    "role": UserRole.USER.rawValue,
                    "totalPoints": 0,
                    "email": email ?? ""
                ]
                try await ref.setData(userData, merge: true)
                print("ensureUserDoc: created users/\(uid)")
            }
        } catch {
            print("❌ ensureUserDoc error:", error)
        }
    }
    
    /// Falls du es extern brauchst: existiert, aber `ensureUserDoc` deckt es ab.
    func addUserToFirestore(userId: String, userName: String, role: UserRole) async {
        let userData: [String: Any] = [
            "uid": userId,
            "userName": userName,
            "role": role.rawValue,
            "totalPoints": 0
        ]
        do {
            try await db.collection("users").document(userId).setData(userData, merge: true)
        } catch {
            print("⚠️ Fehler beim User-Set: \(error.localizedDescription)")
        }
    }
    
    // MARK: - Error Mapping
    
    private func mapSignupError(_ error: Error) -> AuthError {
        let ns = error as NSError
        let code = AuthErrorCode(_bridgedNSError: ns)?.code
        switch code {
        case .emailAlreadyInUse?: return .emailAlreadyInUse
        case .weakPassword?:      return .weakPassword
        case .invalidEmail?:      return .invalidEmailFormat
        default:                  return .unknown(error.localizedDescription)
        }
    }
    
    private func mapSigninError(_ error: Error) -> AuthError {
        let ns = error as NSError
        let code = AuthErrorCode(_bridgedNSError: ns)?.code
        switch code {
        case .userNotFound?:     return .invalidEmail
        case .invalidEmail?:     return .invalidEmailFormat
        case .wrongPassword?:    return .wrongPassword
        case .networkError?:     return .networkError
        case .tooManyRequests?:  return .tooManyRequests
        default:
            print("🔥 Auth Fehler nicht gemappt: \(error.localizedDescription)")
            return .unknown(error.localizedDescription)
        }
    }
}
