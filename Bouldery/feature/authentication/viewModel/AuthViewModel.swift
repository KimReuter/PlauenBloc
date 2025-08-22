//
//  AuthViewModel.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation
import Combine
import FirebaseAuth

@MainActor
final class AuthViewModel: ObservableObject {

    // Repo (Interface via Protocol – wie in Kotlin)
    private let authRepository: AuthRepository
    private var bag = Set<AnyCancellable>()

    // === StateFlow-Parität ===
    // aus dem Repo „durchgereicht“
    @Published var isLoggedIn: Bool = false
    @Published var isInitialized: Bool = false

    // eigene States (wie _userRole, _userId in Kotlin)
    @Published var userRole: UserRole? = nil
    @Published var userId: String? = nil

    init(authRepository: AuthRepository = FirebaseAuthRepository()) {
        self.authRepository = authRepository

        // StateFlow -> Combine
        authRepository.isLoggedInPublisher
            .receive(on: RunLoop.main)
            .assign(to: &$isLoggedIn)

        authRepository.isInitializedPublisher
            .receive(on: RunLoop.main)
            .assign(to: &$isInitialized)

        // initiale UserId (wie im Kotlin-init)
        self.userId = authRepository.getCurrentUserId()
    }

    // signIn(email, password, onResult)
    func signIn(email: String,
                password: String,
                onResult: @escaping (AuthResult) -> Void) {
        Task {
            let result = await authRepository.signIn(email: email, password: password)
            if case .success = result {
                let uid = authRepository.getCurrentUserId()
                self.userId = uid
                if let uid {
                    let role = await authRepository.fetchUserRole(userId: uid)
                    self.userRole = role
                }
            }
            onResult(result)
        }
    }

    // loadUserRole()
    func loadUserRole() {
        guard let uid = authRepository.getCurrentUserId() else { return }
        Task {
            let role = await authRepository.fetchUserRole(userId: uid)
            self.userRole = role
        }
    }

    // sendPasswordReset(email, onResult)
    func sendPasswordReset(email: String,
                           onResult: @escaping (Result<Void, Error>) -> Void) {
        Task {
            let res = await authRepository.sendPasswort(email: email)
            onResult(res)
        }
    }

    // signUp(userName, email, password, onResult) inkl. Passwortvalidierung
    func signUp(userName: String,
                email: String,
                password: String,
                onResult: @escaping (AuthResult) -> Void) {
        guard isValidPassword(password) else {
            onResult(.error(.weakPassword))
            return
        }
        Task {
            let result = await authRepository.signUp(userName: userName, email: email, password: password)
            if case .success = result {
                // wie in Kotlin: Profilname + userId setzen
                if let user = Auth.auth().currentUser {
                    let change = user.createProfileChangeRequest()
                    change.displayName = userName
                    try? await change.commitChanges()
                    self.userId = user.uid
                } else {
                    self.userId = authRepository.getCurrentUserId()
                }
            }
            onResult(result)
        }
    }

    // isValidPassword(password)
    func isValidPassword(_ password: String) -> Bool {
        let minLength = 8
        let hasUppercase = password.contains { $0.isUppercase }
        let hasDigit     = password.contains { $0.isNumber }
        let hasSpecial   = password.contains { !$0.isLetter && !$0.isNumber }
        return password.count >= minLength && hasUppercase && hasDigit && hasSpecial
    }

    // signOut()
    func signOut() {
        authRepository.signOut()
        self.userId = nil
        self.userRole = nil
    }
}
