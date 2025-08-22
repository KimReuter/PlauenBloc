//
//  LabeledPickerWithAdd.swift
//  Bouldery
//
//  Created by Kim Reuter on 22.08.25.
//

import SwiftUI

private enum SetterMode {
    case list                // vordefinierte Optionen (Picker)
    case manualEditing       // Textfeld + ✅
    case manualReadOnly      // bestätigter manueller Text, read-only + ✏️
}

struct LabeledPickerWithAdd: View {
    let label: String
    let options: [String]                   // vordefinierte Werte (z. B. Schrauber)
    @Binding var value: String              // finaler String-Wert
    var systemImage: String? = "person.fill"

    @State private var mode: SetterMode = .list
    @State private var manualText = ""
    @FocusState private var manualFocused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .font(.caption)
                .foregroundStyle(.white.opacity(0.8))

            HStack(spacing: 8) {
                if let systemImage {
                    Image(systemName: systemImage)
                        .foregroundStyle(.white.opacity(0.7))
                        .padding(.leading, 4) // requested horizontal padding
                }

                switch mode {
                case .list:
                    Picker("", selection: $value) {
                        ForEach(options, id: \.self) { opt in
                            Text(opt).tag(opt)
                        }
                    }
                    .pickerStyle(.menu)
                    .tint(.white)

                    Spacer(minLength: 4)

                    // ➕: wechselt in manuellen Bearbeitungsmodus
                    Button {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            manualText = value
                            mode = .manualEditing
                            DispatchQueue.main.async { manualFocused = true }
                        }
                    } label: {
                        Image(systemName: "plus.circle.fill")
                            .imageScale(.large)
                            .foregroundStyle(.white)
                            .padding(.horizontal, 4) // requested horizontal padding
                    }
                    .buttonStyle(.plain)

                case .manualEditing:
                    HStack(spacing: 4) {
                        TextField("Manuell eingeben…", text: $manualText)
                            .textInputAutocapitalization(.words)
                            .autocorrectionDisabled(false)
                            .foregroundStyle(.white)
                            .focused($manualFocused)
                            .onSubmit { commitManual() }
                            .onChange(of: manualText) { _ in
                                value = manualText
                            }

                        // ✅ bestätigen
                        Button {
                            commitManual()
                            manualFocused = false
                            withAnimation(.easeInOut(duration: 0.2)) {
                                mode = .manualReadOnly
                            }
                        } label: {
                            Image(systemName: "checkmark.circle.fill")
                                .imageScale(.large)
                                .foregroundStyle(.green)
                                .padding(.horizontal, 8) // requested horizontal padding
                        }
                        .buttonStyle(.plain)
                    }

                    Spacer(minLength: 4)

                    // ⨉ abbrechen -> zurück zur Liste (Wert bleibt wie vorher)
                    Button {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            // Falls der aktuelle value aus den Optionen stammt, zurück zur Liste,
                            // sonst in ReadOnly bleiben (damit nichts verloren geht).
                            if options.contains(value) {
                                mode = .list
                            } else {
                                mode = .manualReadOnly
                            }
                            manualFocused = false
                        }
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .imageScale(.large)
                            .foregroundStyle(.white)
                            .padding(.horizontal, 8) // requested horizontal padding
                    }
                    .buttonStyle(.plain)

                case .manualReadOnly:
                    // Read-Only Darstellung: Text + Edit-Button
                    Text(value.isEmpty ? "—" : value)
                        .foregroundStyle(.white)
                        .lineLimit(1)
                        .truncationMode(.tail)
                        .contentShape(Rectangle())
                        .onTapGesture {
                            // Tap auf Text öffnet wieder Bearbeitung
                            withAnimation(.easeInOut(duration: 0.2)) {
                                manualText = value
                                mode = .manualEditing
                                DispatchQueue.main.async { manualFocused = true }
                            }
                        }

                    Spacer(minLength: 4)

                    // ✏️ bearbeiten
                    Button {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            manualText = value
                            mode = .manualEditing
                            DispatchQueue.main.async { manualFocused = true }
                        }
                    } label: {
                        Image(systemName: "pencil")
                            .imageScale(.medium)
                            .foregroundStyle(.white.opacity(0.9))
                            .padding(.horizontal, 8)
                    }
                    .buttonStyle(.plain)

                    // 🔁 zurück zur Liste
                    Button {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            // Wenn der manuelle Wert einer Option entspricht, übernehmen
                            if options.contains(value) == false, let first = options.first {
                                // optional: auf erste Option zurückfallen
                                value = first
                            }
                            mode = .list
                        }
                    } label: {
                        Image(systemName: "arrow.triangle.2.circlepath")
                            .imageScale(.medium)
                            .foregroundStyle(.white.opacity(0.9))
                            .padding(.horizontal, 8)
                    }
                    .buttonStyle(.plain)
                }
            }
            .inputStyle() // gleicher Look/Höhe/Breite
        }
        .padding(.vertical, 8)
        .onAppear {
            // Initiales Mode‑Setup
            if !value.isEmpty, !options.contains(value) {
                // gespeicherter Wert ist custom -> read-only anzeigen
                mode = .manualReadOnly
            } else {
                // Wert aus Optionen oder leer -> starte mit Liste
                if value.isEmpty { value = options.first ?? "" }
                mode = .list
            }
        }
    }

    private func commitManual() {
        value = manualText.trimmingCharacters(in: .whitespacesAndNewlines)
    }
}

#Preview {
    @State var setter = "Jens Grimm"
    return VStack(spacing: 16) {
        LabeledPickerWithAdd(
            label: "Schrauber",
            options: ["Jens Grimm", "Jörg Band", "Jörg Schwert"],
            value: $setter
        )
        Text("Wert: \(setter)")
            .foregroundStyle(.white)
    }
    .padding()
    .background(Color.black)
}
