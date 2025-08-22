//
//  CreateRouteSheet.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation
import SwiftUI

struct CreateRouteSheet: View {
    @State var draft: Route
    var onSave: (Route) -> Void
    var onCancel: () -> Void

    var body: some View {
        NavigationStack {
            Form {
                Section("Position") {
                    HStack {
                        Text("x").frame(width: 20, alignment: .leading)
                        Slider(value: $draft.x, in: 0...1)
                        Text(String(format: "%.2f", draft.x)).monospacedDigit()
                    }
                    HStack {
                        Text("y").frame(width: 20, alignment: .leading)
                        Slider(value: $draft.y, in: 0...1)
                        Text(String(format: "%.2f", draft.y)).monospacedDigit()
                    }
                }

                Section("Allgemein") {
                    TextField("Name", text: $draft.name)
                    Picker("Halle", selection: $draft.hall) {
                        ForEach(HallSection.allCases) { Text($0.label).tag($0) }
                    }
                    Picker("Sektor", selection: $draft.sector) {
                        ForEach(Sector.allCases) { Text($0.label).tag($0) }
                    }
                    Stepper(value: $draft.number, in: 0...999) {
                        HStack { Text("Nummer"); Spacer(); Text("\(draft.number)") }
                    }
                }

                Section("Eigenschaften") {
                    Picker("Griff-Farbe", selection: $draft.holdColor) {
                        ForEach(HoldColor.allCases) { Text($0.label).tag($0) }
                    }
                    Picker("Schwierigkeit", selection: $draft.difficulty) {
                        ForEach(Difficulty.allCases) { Text($0.label).tag($0) }
                    }
                    TextField("Setter:in", text: $draft.setter)
                    TextField("Beschreibung", text: $draft.description, axis: .vertical)
                        .lineLimit(3, reservesSpace: true)
                }
            }
            .navigationTitle("Route erstellen")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Abbrechen", action: onCancel)
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Speichern") {
                        var toSave = draft
                        if toSave.points == 0 {
                            toSave.points = PointsCalculator.points(for: toSave.difficulty)
                        }
                        onSave(toSave)
                    }
                    .disabled(draft.name.isEmpty)
                }
            }
        }
    }
}
