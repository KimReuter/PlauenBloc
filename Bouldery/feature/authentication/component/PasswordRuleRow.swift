//
//  PasswordRuleRow.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import SwiftUI

struct PasswordRuleRow: View {
    let text: String
    let valid: Bool

    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: valid ? "checkmark.circle.fill" : "circle")
                .foregroundColor(valid ? .green : .gray)
            Text(text)
            Spacer(minLength: 0)
        }
        .font(.subheadline)
        .padding(.vertical, 4)
        .contentShape(Rectangle())
        .animation(.easeInOut(duration: 0.15), value: valid)
    }
}
