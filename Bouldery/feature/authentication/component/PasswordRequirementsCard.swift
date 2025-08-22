//
//  PasswordRequirementsCard.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation
import SwiftUI

struct PasswordRequirementsCard: View {
    let password: String

    private var hasLen: Bool { password.count >= 8 }
    private var hasDigit: Bool { password.rangeOfCharacter(from: .decimalDigits) != nil }
    private var hasUpper: Bool { password.rangeOfCharacter(from: .uppercaseLetters) != nil }
    private var hasSpecial: Bool {
        password.rangeOfCharacter(from: .punctuationCharacters.union(.symbols)) != nil
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Passwortanforderungen")
                .font(.caption).fontWeight(.semibold)
                .foregroundColor(.secondary)

            VStack(spacing: 2) {
                PasswordRuleRow(text: "Mind. 8 Zeichen", valid: hasLen)
                PasswordRuleRow(text: "Mind. 1 Zahl", valid: hasDigit)
                PasswordRuleRow(text: "Mind. 1 Großbuchstabe", valid: hasUpper)
                PasswordRuleRow(text: "Mind. 1 Sonderzeichen", valid: hasSpecial)
            }
        }
        .padding(12)
        .background(.ultraThinMaterial)
        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .strokeBorder(Color.gray.opacity(0.15), lineWidth: 1)
        )
        .transition(.move(edge: .top).combined(with: .opacity))
        .animation(.easeInOut(duration: 0.2), value: password)
    }
}
