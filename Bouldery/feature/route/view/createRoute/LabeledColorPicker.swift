//
//  LabeledColorPicker.swift
//  Bouldery
//
//  Created by Kim Reuter on 22.08.25.
//

import SwiftUI

struct LabeledColorPicker<T: ColorLabeledEnum>: View {
    let label: String
    @Binding var selection: T

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .font(.caption)
                .foregroundStyle(.white.opacity(0.8))

            HStack(spacing: 8) {
                colorDot(for: selection.color)

                Picker(selection: $selection) {
                    ForEach(Array(T.allCases)) { c in
                        HStack(spacing: 8) {
                            colorDot(for: c.color)
                            Text(c.label)
                        }
                        .tag(c)
                    }
                } label: {
                    HStack(spacing: 8) {
                        colorDot(for: selection.color)
                        Text(selection.label)
                        Spacer()
                        Image(systemName: "chevron.up.chevron.down")
                            .font(.footnote)
                            .foregroundStyle(.white.opacity(0.6))
                    }
                }
                .pickerStyle(.menu)
                .tint(.white)
            }
            .inputStyle()
        }
        .padding(.vertical, 4)
    }

    private func colorDot(for c: Color) -> some View {
        Circle()
            .fill(c)
            .frame(width: 14, height: 14)
            .overlay(Circle().stroke(Color.black.opacity(0.15), lineWidth: 1))
    }
}
