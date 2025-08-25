//
//  HallSelectionButtons.swift
//  Bouldery
//
//  Created by Kim Reuter on 25.08.25.
//

import SwiftUI

struct HallSelectionButtons: View {
    @Binding var selectedHall: HallSection

    var body: some View {
        HStack(spacing: 8) {
            hallButton(.BACK,  title: "Hintere Halle")
            Spacer().frame(width: 24)
            hallButton(.FRONT, title: "Vordere Halle")
        }
    }

    @ViewBuilder
    private func hallButton(_ hall: HallSection, title: String) -> some View {
        Button {
            selectedHall = hall
        } label: {
            Text(title)
                .font(.headline)
                .padding(.vertical, 8)
                .frame(maxWidth: .infinity)
                .background(
                    RoundedRectangle(cornerRadius: 10)
                        .fill(selectedHall == hall ? AppTheme.Palette.green : Color(.systemGray5))
                )
                .foregroundStyle(selectedHall == hall ? .white : .primary)
        }
        .buttonStyle(.plain)
    }
}
