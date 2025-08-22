//
//  InputStyle.swift
//  Bouldery
//
//  Created by Kim Reuter on 22.08.25.
//

import SwiftUI

struct InputStyle: ViewModifier {
    func body(content: Content) -> some View {
        content
            .frame(height: 44)
            .frame(maxWidth: .infinity)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .fill(AppTheme.Palette.bg)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(Color.white.opacity(0.5), lineWidth: 1.5)
            )
    }
}

extension View {
    func inputStyle() -> some View {
        self.modifier(InputStyle())
    }
}
