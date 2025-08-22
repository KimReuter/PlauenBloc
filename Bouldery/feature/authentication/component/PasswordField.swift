//
//  PasswordField.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import SwiftUI

struct PasswordField: View {
    @Binding var password: String
    @Binding var isFocused: Bool     // <-- kommt von außen rein
    @State private var isSecure = true

    var body: some View {
        HStack(spacing: 8) {
            Group {
                if isSecure {
                    SecureField("Passwort", text: $password)
                        .textContentType(.password)
                        .submitLabel(.go)
                } else {
                    TextField("Passwort", text: $password)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled(true)
                        .textContentType(.password)
                        .submitLabel(.go)
                }
            }
            .padding(.vertical, 10)
            .padding(.leading, 12)

            Button {
                isSecure.toggle()
            } label: {
                Image(systemName: isSecure ? "eye.slash" : "eye")
                    .foregroundStyle(.secondary)
                    .padding(.trailing, 10)
            }
            .buttonStyle(.plain)
        }
        .background(
            RoundedRectangle(cornerRadius: 10, style: .continuous)
                .stroke(isFocused ? AppTheme.Palette.green : Color.gray.opacity(0.4), lineWidth: 2)
                .background(
                    RoundedRectangle(cornerRadius: 10, style: .continuous)
                        .fill(.thinMaterial)
                )
        )
        .contentShape(Rectangle())
        .onTapGesture { isFocused = true }
    }
}
