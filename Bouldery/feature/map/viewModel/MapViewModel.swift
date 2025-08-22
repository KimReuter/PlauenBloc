//
//  MapViewModel.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation

@MainActor
final class MapViewModel: ObservableObject {
    private let repo: RouteRepository = FirebaseRouteRepository()
    
    @Published var routes: [Route] = []
    @Published var draft: Route? = nil
    @Published var error: String?
    @Published var isLoading = false

    // ROUTEN LADEN
    func loadRoutes() async {
        isLoading = true; defer { isLoading = false }
        do {
            routes = try await repo.getRoutes()
        } catch {
            self.error = error.localizedDescription
        }
    }

    // NEUE ROUTE DRAFT STARTEN
    func startDraft(at x: Double, y: Double, hall: HallSection = .FRONT) {
        draft = Route(
            id: nil,
            name: "",
            hall: hall,
            sector: .SONNENPLATTE,
            holdColor: .YELLOW,
            difficulty: .GREEN,
            number: 0,
            description: "",
            setter: "",
            x: x,
            y: y,
            points: 0
        )
    }

    // DRAFT SPEICHERN
    func saveDraft(isFlash: Bool = false) async {
        guard var r = draft else { return }
        
        // Punkte kalkulieren
        r.points = PointsCalculator.points(for: r.difficulty, isFlash: isFlash)
        
        do {
            try await repo.addRoute(r)
            // Bei Firestore kommt die ID erst beim Snapshot wieder zurück.
            // Hier reload, damit die ID korrekt gesetzt wird:
            await loadRoutes()
            draft = nil
        } catch {
            self.error = error.localizedDescription
        }
    }

    // ROUTE LÖSCHEN
    func deleteRoute(_ id: String) async {
        do {
            try await repo.deleteRoute(id)
            routes.removeAll { $0.id == id }
        } catch {
            self.error = error.localizedDescription
        }
    }

    // ROUTE UPDATEN
    func updateRoute(_ route: Route) async {
        do {
            try await repo.updateRoute(route)
            if let idx = routes.firstIndex(where: { $0.id == route.id }) {
                routes[idx] = route
            }
        } catch {
            self.error = error.localizedDescription
        }
    }
}
