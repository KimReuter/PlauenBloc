//
//  LabeledTextField.swift
//  Bouldery
//
//  Created by Kim Reuter on 22.08.25.
//

import SwiftUI

struct LabeledTextField: View {
    var label: String?
    var placeholder: String = ""
    @Binding var text: String
    var systemImage: String? = nil
    var errorMessage: String? = nil
    var keyboard: UIKeyboardType = .default
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            // Label oben
            if let label = label {
                Text(label)
                    .font(.caption)
                    .foregroundColor(errorMessage == nil ? .secondary : .red)
            }
            
            HStack {
                // Optionales Icon
                if let systemImage = systemImage {
                    Image(systemName: systemImage)
                        .foregroundColor(.brandOnPrimary.opacity(0.7))
                        .padding(.horizontal, 8)
                }
                
                // Textfeld
                TextField(placeholder, text: $text)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled(true)
                    .keyboardType(keyboard)
                    .foregroundStyle(.brandOnPrimary)
            }
            .inputStyle()
            
            // Fehlernachricht
            if let error = errorMessage {
                Text(error)
                    .font(.caption2)
                    .foregroundColor(.red)
                    .padding(.leading, 4)
            }
        }
    }
}

#Preview("LabeledTextField") {
    @State var text = "Test"
    return VStack {
        LabeledTextField(
            label: "Routenname",
            placeholder: "Gib den Namen ein",
            text: $text,
            systemImage: "pencil"
        )
        
        LabeledTextField(
            label: "Routenname",
            placeholder: "Pflichtfeld",
            text: $text,
            systemImage: "pencil",
            errorMessage: "Bitte ausfüllen!"
        )
    }
    .padding()
}
