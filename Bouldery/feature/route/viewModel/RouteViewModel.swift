//
//  RouteViewModel.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation

@MainActor
final class RouteViewModel: ObservableObject {
    private let repo: RouteRepository
    @Published var routes: [Route] = []

    init(repo: RouteRepository = FirebaseRouteRepository()) {
        self.repo = repo
    }

    func loadRoutes() {
        Task {
            do {
                routes = try await repo.getRoutes()
            } catch {
                print("❌ Fehler beim Laden der Routen: \(error)")
            }
        }
    }
}
