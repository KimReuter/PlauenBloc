//
//  CreateRouteViewModel.swift
//  Bouldery
//
//  Created by Kim Reuter on 25.08.25.
//

import Foundation

@MainActor
final class CreateRouteViewModel: ObservableObject {
    private let service: RouteServiceing
    init(service: RouteServiceing = RouteService()) { self.service = service }

    // Form-State
    @Published var name = ""
    @Published var number = 1
    @Published var hall: HallSection = .FRONT {
        didSet {
            if !sectorOptions.contains(sector) {
                sector = sectorOptions.first ?? sector
            }
            Task { await refreshAvailableNumbers() }
        }
    }
    @Published var sector: Sector = .SONNENPLATTE {
        didSet { Task { await refreshAvailableNumbers() } }
    }
    var sectorOptions: [Sector] { Sector.options(for: hall) }
    @Published var setterName: String = DefaultSetters.all.first?.name ?? ""
    @Published var description = ""
    @Published var holdColor: HoldColor = .YELLOW
    @Published var difficulty: Difficulty = .GREEN
    @Published var draftX: Double?
    @Published var draftY: Double?

    @Published var availableNumbers: [Int] = Array(1...25)
    @Published var isSaving = false
    @Published var error: String?

    func refreshAvailableNumbers() async {
        do {
            availableNumbers = try await service.availableNumbers(hall: hall, sector: sector)
            if !availableNumbers.contains(number) { number = availableNumbers.first ?? 1 }
        } catch { self.error = error.localizedDescription }
    }

    var canSave: Bool {
        !name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty &&
        !description.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty &&
        draftX != nil && draftY != nil &&
        availableNumbers.contains(number)
    }

    func save() async -> Bool {
        guard let x = draftX, let y = draftY, canSave else { return false }
        isSaving = true; defer { isSaving = false }
        do {
            let input = CreateRouteInput(
                name: name,
                number: number,
                hall: hall,
                sector: sector,
                setterName: setterName,
                description: description,
                holdColor: holdColor,
                difficulty: difficulty,
                x: x, y: y
            )
            try await service.createRoute(input)
            return true
        } catch {
            self.error = error.localizedDescription
            return false
        }
    }
}
