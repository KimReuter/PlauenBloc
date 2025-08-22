//
//  LabeledPicker.swift
//  Bouldery
//
//  Created by Kim Reuter on 22.08.25.
//

import SwiftUI

struct LabeledPicker<Item: Identifiable & Hashable>: View {
    let label: String
    let items: [Item]
    @Binding var selection: Item
    var display: (Item) -> String
    var systemImage: String? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .font(.caption)
                .foregroundColor(.white.opacity(0.8))

            HStack {
                if let systemImage { Image(systemName: systemImage).foregroundStyle(.white.opacity(0.7)) }

                Picker("", selection: $selection) {
                    ForEach(items) { item in
                        Text(display(item))
                            .tag(item)
                    }
                }
                .pickerStyle(.menu)
                .tint(.white)
            }
            .inputStyle()
            .foregroundStyle(.white)          
        }
        .padding(.vertical, 4)
    }
}
