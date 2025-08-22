//
//  CreateRouteSheet.swift
//  Bouldery
//
//  Created by Kim Reuter on 22.08.25.
//

import SwiftUI

struct CreateRouteSheet: View {
    @Environment(\.dismiss) private var dismiss
    
    private let routeRepo = FirebaseRouteRepository()
    
    @AppStorage("lastHallSection") private var lastHallRaw: String = ""
    @AppStorage("lastSector")      private var lastSectorRaw: String = ""
    
    @State private var routeName: String = ""
    @State private var routeNumber: Int = 1
    @State private var hall: HallSection = .FRONT
    @State private var sector : Sector  = .SONNENPLATTE
    @State private var setterString: String = DefaultSetters.all.first?.name ?? ""
    @State private var description: String = ""
    @State private var holdColor: HoldColor = .YELLOW
    @State private var difficulty: Difficulty = .GREEN
    @State private var draftX: Double? = nil
    @State private var draftY: Double? = nil
    @State private var nameError: String? = nil
    @State private var didInit = false
    
    @State private var takenNumbers: Set<Int> = []
    private let allNumbers = Array(1...25)
    
    private var availableNumbers: [Int] {
        allNumbers.filter { !takenNumbers.contains($0) }
    }
    
    private var defaultSetterNames: [String] { DefaultSetters.all.map(\.name) }
    private var sectorOptions: [Sector] { Sector.allCases.filter { $0.hall == hall }}
    
    private var canSave: Bool {
        !routeName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
        && draftX != nil && draftY != nil
        && !availableNumbers.isEmpty
    }
    
    var body: some View {
        NavigationStack {
            ScrollView {
                
                VStack(spacing: 16) {
                    
                    LabeledTextField(
                        label: "Routenname",
                        placeholder: "z. B. Plattenkönig",
                        text: $routeName,
                        systemImage: "tag",
                        errorMessage: nameError
                    )
                    
                    LabeledPickerWithAdd(
                        label: "Schrauber",
                        options: defaultSetterNames,
                        value: $setterString,
                        systemImage: "person.fill")
                    
                    
                    LabeledPicker(
                        label: "Nummer",
                        items: availableNumbers,
                        selection: $routeNumber,
                        display: { "\($0)" },
                        systemImage: "number"
                    )
                    
                    LabeledPicker(
                        label: "Hallenteil",
                        items: HallSection.allCases,
                        selection: $hall,
                        display: { $0.label },
                        systemImage: "rectangle.3.group"
                    )
                    
                    LabeledPicker(
                        label: "Sektor",
                        items: sectorOptions,
                        selection: $sector,
                        display: { $0.label },
                        systemImage: "square.grid.2x2"
                    )
                    
                    LabeledColorPicker(
                        label: "Grifffarbe",
                        selection: $holdColor
                    )
                    
                    LabeledColorPicker(
                        label: "Schwierigkeit",
                        selection: $difficulty
                    )
                    
                    EditableMapView(
                        routes: [],                  
                        hall: hall,                  
                        draft: (draftX != nil && draftY != nil) ? (draftX!, draftY!) : nil,
                        onTapNormalized: { x, y in
                            draftX = x; draftY = y
                        }
                    )
                    .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(.white.opacity(0.12), lineWidth: 1))
                    
                    
                    LabeledTextField(
                        label: "Beschreibung",
                        placeholder: "Wie fühlt sich die Route an?",
                        text: $description,
                        systemImage: "text.alignleft",
                        errorMessage: nameError
                    )
                    
                    Spacer()
                    
                    Button {
                        lastHallRaw = hall.rawValue
                        lastSectorRaw = sector.rawValue
                        dismiss()
                    } label: {
                        Text("Speichern")
                            .font(.headline)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(AppTheme.Palette.green)
                            .foregroundColor(.white)
                            .cornerRadius(10)
                    }
                    .disabled(true)
                }
                .padding()
                
                // Erstinitialisierung: letzte Auswahl wiederherstellen oder sinnvolle Defaults setzen
                .onAppear {
                    guard !didInit else { return }
                    didInit = true
                    applyDefaultsOrFallback()
                    Task { await loadTakenNumbers() }
                }
                // Wenn Hallenteil wechselt: Sektor gültig halten + Default aktualisieren
                .onChange(of: hall) { _ in
                    if !sectorOptions.contains(sector) {
                        sector = sectorOptions.first ?? sector
                    }
                    // Live „merken“, damit beim nächsten Sheet wieder da ist
                    lastHallRaw = hall.rawValue
                    lastSectorRaw = sector.rawValue
                }
                // Wenn Sektor wechselt: Default aktualisieren
                .onChange(of: sector) { _ in
                    lastSectorRaw = sector.rawValue
                }
                .onChange(of: takenNumbers) { _ in
                    if takenNumbers.contains(routeNumber) || !availableNumbers.contains(routeNumber) {
                        routeNumber = availableNumbers.first ?? 1
                    }
                }
                .ignoresSafeArea(.keyboard, edges: .bottom)
                .navigationTitle("Neue Route erstellen")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .cancellationAction) {
                        Button("Schließen") { dismiss() }
                    }
                }
            }
            // ⬇️ Hintergrund für das ganze Sheet (inkl. Safe Areas)
            .background(AppTheme.Palette.bg.ignoresSafeArea())
            
            // ⬇️ NavBar einfärben (auf den NavigationStack anwenden)
            .toolbarBackground(AppTheme.Palette.bg, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
            .toolbarColorScheme(.dark, for: .navigationBar)
            .tint(AppTheme.Palette.green)
            
            // ⬇️ iOS 17+: auch der Sheet‑Container selbst
            .presentationBackground(AppTheme.Palette.bg)
        }
    }
    
    // MARK: - Helpers
    private func applyDefaultsOrFallback() {
        // Versuche, gespeicherte Hall/Sector wiederherzustellen
        let storedHall   = HallSection(rawValue: lastHallRaw)
        let storedSector = Sector(rawValue: lastSectorRaw)
        
        if let h = storedHall {
            hall = h
            let opts = sectorOptions
            if let s = storedSector, opts.contains(s) {
                sector = s
            } else {
                // kein gültiger gespeicherter Sektor -> ersten gültigen nehmen
                sector = opts.first ?? sector
            }
        } else {
            // keine gespeicherten Werte -> Standards
            hall = .FRONT
            sector = sectorOptions.first ?? Sector.allCases.first! // erster gültiger Sektor
        }
    }
    
    private func loadTakenNumbers() async {
        do {
            let set = try await routeRepo.takenNumbers(hall: hall, sector: sector)
            await MainActor.run {
                takenNumbers = set
                // Wenn keine freie Nummer existiert, bleib bei aktuellem Wert
                if let firstFree = availableNumbers.first {
                    if !availableNumbers.contains(routeNumber) {
                        routeNumber = firstFree
                    }
                }
            }
        } catch {
            // Im Fehlerfall: lieber alle freigeben, damit der Flow nicht blockiert
            await MainActor.run {
                takenNumbers = []
            }
            print("⚠️ loadTakenNumbers failed: \(error.localizedDescription)")
        }
    }
}

#Preview("CreateRouteSheet") {
    CreateRouteSheet()
        .background(AppTheme.Palette.bg.ignoresSafeArea())
        .preferredColorScheme(.dark) // falls deine App dark ist
}
