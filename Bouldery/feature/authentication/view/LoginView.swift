//
//  LoginView.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import SwiftUI

struct LoginView: View {
    @EnvironmentObject var vm: AuthViewModel
    
    @State private var isSignUp = false
    @State private var email = ""
    @State private var password = ""
    @State private var name = ""
    @State private var errorText: String?
    
    enum Field {
        case email, password, name
    }
    @FocusState private var focusedField: Field?
    
    
    var body: some View {
        ZStack {
            AppTheme.Palette.bg.ignoresSafeArea()
            
            VStack(spacing: 16) {
                Text(isSignUp ? "Account erstellen" : "Anmelden")
                    .font(.title.bold())
                
                VStack(spacing: 12) {
                    TextField("E‑Mail", text: $email)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled(true)
                        .textContentType(.emailAddress)
                        .keyboardType(.emailAddress)
                        .submitLabel(isSignUp ? .next : .go)
                        .padding(10)
                        .background(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(focusedField == .email ? AppTheme.Palette.green : Color.gray.opacity(0.4), lineWidth: 2)
                        )
                        .focused($focusedField, equals: .email)
                    
                    
                    VStack(alignment: .leading, spacing: 6) {
                        PasswordField(password: $password,
                                      isFocused: Binding(
                                        get: { focusedField == .password },
                                        set: { $0 ? (focusedField = .password) : (focusedField = nil) }
                                      )
                        )
                        .focused($focusedField, equals: .password)

                        if isSignUp && focusedField == .password {
                            PasswordRequirementsCard(password: password)
                                .padding(.top, 4)
                        }
                    }
                    .animation(.easeInOut(duration: 0.2), value: focusedField)
                    
                    if isSignUp {
                        TextField("Name", text: $name)
                            .textContentType(.name)
                            .submitLabel(.go)
                            .padding(10)
                            .background(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(focusedField == .name ? AppTheme.Palette.green : Color.gray.opacity(0.4), lineWidth: 2)
                            )
                            .focused($focusedField, equals: .name)
                    }
                }
                
                PrimaryButton(title: isSignUp ? "Registrieren" : "Login") {
                    if isSignUp {
                        vm.signUp(userName: name, email: email, password: password) { result in handle(result) }
                    } else {
                        vm.signIn(email: email, password: password) { result in handle(result) }
                    }
                }
                .disabled(!isFormValid)
                .opacity(isFormValid ? 1 : 0.6)
                
                // Unterstrichener Text-Button zum Umschalten
                Button {
                    withAnimation(.easeInOut(duration: 0.2)) {
                        isSignUp.toggle()
                        errorText = nil
                    }
                } label: {
                    Text(isSignUp ? "Schon ein Konto? Anmelden" : "Neu hier? Jetzt registrieren")
                        .underline()
                        .font(.footnote)
                }
                .buttonStyle(.plain)
                
                if !isSignUp {
                    Button {
                        if email.isEmpty {
                            errorText = "Bitte gib deine E-Mail ein, um das Passwort zurückzusetzen."
                        } else {
                            Task {
                                await vm.sendPasswordReset(email: email) { result in
                                    switch result {
                                    case .success:
                                        errorText = "Wenn ein Account mit dieser E-Mail existiert, haben wir dir eine Reset-Mail geschickt. Bitte prüfe auch deinen Spam-Ordner."
                                    case .failure(let err):
                                        errorText = "Fehler: \(err.localizedDescription)"
                                    }
                                }
                            }
                        }
                    } label: {
                        Text("Passwort vergessen?")
                            .underline()
                            .font(.footnote)
                    }
                    .buttonStyle(.plain)
                    .padding(.top, 4)
                }
                
                if let t = errorText {
                    Text(t).foregroundColor(.red).font(.footnote).multilineTextAlignment(.center)
                }
            }
            .animation(.easeInOut(duration: 0.15), value: focusedField) // ✨ hier
            .padding()
            .onSubmit {
                if focusedField == .email {
                    focusedField = .password
                } else if focusedField == .password && isSignUp {
                    focusedField = .name
                } else {
                    if isFormValid {
                        if isSignUp {
                            vm.signUp(userName: name, email: email, password: password) { handle($0) }
                        } else {
                            vm.signIn(email: email, password: password) { handle($0) }
                        }
                    }
                }
            }
        }
        .onTapGesture {
            focusedField = nil
        }
    }
    
    // Mini-Validation: beim Registrieren auch Name gefüllt + einfache Passwort-Policy
    private var isFormValid: Bool {
        if isSignUp {
            return !email.isEmpty && !password.isEmpty && !name.isEmpty && vm.isValidPassword(password)
        } else {
            return !email.isEmpty && !password.isEmpty
        }
    }
    
    private func handle(_ result: AuthResult) {
        switch result {
        case .success:
            errorText = nil
        case .error(let error):
            errorText = mapAuthError(error)
        }
    }
    
    private func mapAuthError(_ error: AuthError) -> String {
        switch error {
        case .emailAlreadyInUse: return "E‑Mail wird bereits verwendet."
        case .weakPassword: return "Passwort zu schwach (mind. 8 Zeichen, Großbuchst., Zahl, Sonderzeichen)."
        case .invalidEmailFormat: return "Ungültiges E‑Mail‑Format."
        case .invalidEmail: return "Kein Account mit dieser E‑Mail."
        case .wrongPassword: return "Falsches Passwort."
        case .networkError: return "Netzwerkfehler. Bitte später erneut versuchen."
        case .tooManyRequests: return "Zu viele Versuche. Bitte kurz warten."
        case .unknown(let m): return m ?? "Unbekannter Fehler."
        }
    }
}
