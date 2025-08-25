//
//  RouteService.swift
//  Bouldery
//
//  Created by Kim Reuter on 25.08.25.
//

import Foundation

struct CreateRouteInput {
    var name: String
    var number: Int
    var hall: HallSection
    var sector: Sector
    var setterName: String
    var description: String
    var holdColor: HoldColor
    var difficulty: Difficulty
    var x: Double
    var y: Double
}

protocol RouteServiceing {
    func listRoutes() async throws -> [Route]
    func availableNumbers(hall: HallSection, sector: Sector) async throws -> [Int]
    func createRoute(_ input: CreateRouteInput) async throws
}

final class RouteService: RouteServiceing {
    private let repo: RouteRepository
    init(repo: RouteRepository = FirebaseRouteRepository()) { self.repo = repo }

    func listRoutes() async throws -> [Route] {
        try await repo.getRoutes()
    }

    func availableNumbers(hall: HallSection, sector: Sector) async throws -> [Int] {
        let taken = try await repo.takenNumbers(hall: hall, sector: sector)
        return Array(1...25).filter { !taken.contains($0) }
    }

    func createRoute(_ input: CreateRouteInput) async throws {
        // Validierung
        guard !input.name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            throw ValidationError.emptyName
        }
        guard !input.description.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            throw ValidationError.emptyDescription
        }

        // Punkte
        let pts = PointsCalculator.points(for: input.difficulty, isFlash: false)

        // Domain-Objekt
        let route = Route(
            id: nil,
            name: input.name,
            hall: input.hall,
            sector: input.sector,
            holdColor: input.holdColor,
            difficulty: input.difficulty,
            number: input.number,
            description: input.description,
            setter: input.setterName,
            x: input.x, y: input.y,
            points: pts
        )

        try await repo.addRoute(route)
    }

    enum ValidationError: LocalizedError {
        case emptyName, emptyDescription
        var errorDescription: String? {
            switch self {
            case .emptyName:        return "Bitte einen Routenname eingeben."
            case .emptyDescription: return "Bitte eine Beschreibung eingeben."
            }
        }
    }
}
