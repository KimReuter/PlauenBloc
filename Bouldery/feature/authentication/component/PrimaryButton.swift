//
//  GradientButtonStyle.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import SwiftUI

import SwiftUI

struct GradientButtonStyle: ButtonStyle {
    var gradient: LinearGradient = LinearGradient(
        colors: [AppTheme.Palette.green, AppTheme.Palette.orange],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    )
    var cornerRadius: CGFloat = 14
    var vPadding: CGFloat = 14
    var hPadding: CGFloat = 18

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.headline)
            .foregroundStyle(AppTheme.Palette.onPrimary)
            .padding(.vertical, vPadding)
            .padding(.horizontal, hPadding)
            .background(
                ZStack {
                    gradient
                    if configuration.isPressed {
                        Color.black.opacity(0.12)
                    }
                }
            )
            .clipShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
            .shadow(radius: configuration.isPressed ? 0 : 6, y: configuration.isPressed ? 0 : 3)
            .animation(.easeOut(duration: 0.12), value: configuration.isPressed)
            .contentShape(.rect)
            .accessibilityAddTraits(.isButton)
    }
}

struct PrimaryButton: View {
    let title: String
    let action: () -> Void
    var gradient: LinearGradient = LinearGradient(
        colors: [AppTheme.Palette.green, AppTheme.Palette.orange],
        startPoint: .topLeading, endPoint: .bottomTrailing
    )

    var body: some View {
        Button(action: action) {
            Text(title)
                .frame(maxWidth: .infinity)
        }
        .buttonStyle(GradientButtonStyle(gradient: gradient))
    }
}
