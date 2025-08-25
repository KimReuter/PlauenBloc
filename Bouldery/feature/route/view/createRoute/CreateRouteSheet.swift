//
//  CreateRouteSheet.swift
//  Bouldery
//
//  Created by Kim Reuter on 22.08.25.
//

import SwiftUI

struct CreateRouteSheet: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var vm = CreateRouteViewModel()
    
    @AppStorage("lastHallSection") private var lastHallRaw: String = ""
    @AppStorage("lastSector")      private var lastSectorRaw: String = ""
    
    @State private var showMap = false
    
    private var defaultSetterNames: [String] { DefaultSetters.all.map(\.name) }
  
    var body: some View {
        NavigationStack {
            ScrollView {
                
                VStack(spacing: 16) {
                    
                    LabeledTextField(
                        label: "Routenname",
                        placeholder: "z. B. Plattenkönig",
                        text: $vm.name,
                        systemImage: "tag"
                    )
                    
                    LabeledPickerWithAdd(
                        label: "Schrauber",
                        options: defaultSetterNames,
                        value: $vm.setterName,
                        systemImage: "person.fill")
                    
                    
                    LabeledPicker(
                        label: "Nummer",
                        items: vm.availableNumbers,
                        selection: $vm.number,
                        display: { "\($0)" },
                        systemImage: "number"
                    )
                    .disabled(vm.availableNumbers.isEmpty)
                    
                    LabeledPicker(
                        label: "Hallenteil",
                        items: HallSection.allCases,
                        selection: $vm.hall,
                        display: { $0.label },
                        systemImage: "rectangle.3.group"
                    )
                    .onChange(of: vm.hall) { _ in
                        lastHallRaw = vm.hall.rawValue
                        if !vm.sectorOptions.contains(vm.sector) {
                            vm.sector = vm.sectorOptions.first ?? vm.sector
                        }
                        Task { await vm.refreshAvailableNumbers() }
                    }
                    
                    LabeledPicker(
                        label: "Sektor",
                        items: vm.sectorOptions,
                        selection: $vm.sector,
                        display: { $0.label },
                        systemImage: "square.grid.2x2"
                    )
                    .onChange(of: vm.sector) { _ in
                        lastSectorRaw = vm.sector.rawValue
                        Task { await vm.refreshAvailableNumbers() }
                    }
                    
                    LabeledColorPicker(
                        label: "Grifffarbe",
                        selection: $vm.holdColor
                    )
                    
                    LabeledColorPicker(
                        label: "Schwierigkeit",
                        selection: $vm.difficulty
                    )
                    
                    Button {
                        showMap.toggle()
                    } label: {
                        Text(showMap ? "Punkt übernehmen" : "Punkt auf Karte auswählen")
                    }
                    
                    if showMap {
                        EditableMapView(
                            routes: [],
                            hall: vm.hall,
                            draft: (vm.draftX != nil && vm.draftY != nil) ? (vm.draftX!, vm.draftY!) : nil,
                            draftColor: vm.difficulty.color,
                            draftLabel: "\(vm.number)",
                            onTapNormalized: { x, y in
                                vm.draftX = x; vm.draftY = y
                            }
                        )
                        .frame(height: 500)
                    }
                    
                    
                    LabeledTextField(
                        label: "Beschreibung",
                        placeholder: "Wie fühlt sich die Route an?",
                        text: $vm.description,
                        systemImage: "text.alignleft"
                    )
                    
                    Spacer()
                    
                    if let err = vm.error {
                        Text(err).font(.footnote).foregroundColor(.red)
                    }
                    
                    Button {
                        lastHallRaw = vm.hall.rawValue
                        lastSectorRaw = vm.sector.rawValue
                        Task {
                            if await vm.save() { dismiss() }
                        }
                    } label: {
                        Text("Speichern")
                            .font(.headline)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(vm.canSave ? AppTheme.Palette.green : Color.gray)
                            .foregroundColor(.white)
                            .cornerRadius(10)
                    }
                    .disabled(!vm.canSave || vm.isSaving)
                }
                .padding()
                .ignoresSafeArea(.keyboard, edges: .bottom)
                .navigationTitle("Neue Route erstellen")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .cancellationAction) {
                        Button("Schließen") { dismiss() }
                    }
                }
            }
            .background(AppTheme.Palette.bg.ignoresSafeArea())
            
            .toolbarBackground(AppTheme.Palette.bg, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
            .toolbarColorScheme(.dark, for: .navigationBar)
            .tint(AppTheme.Palette.green)
            
            .presentationBackground(AppTheme.Palette.bg)
            .task { await vm.refreshAvailableNumbers() }
        }
    }
}

#Preview("CreateRouteSheet") {
    CreateRouteSheet()
        .background(AppTheme.Palette.bg.ignoresSafeArea())
        .preferredColorScheme(.dark)
}
