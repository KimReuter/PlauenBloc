//
//  RouteViewModel.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation

@MainActor
final class RouteViewModel: ObservableObject {
    
    private let service: RouteServiceing
    private let repo: RouteRepository
    
    @Published var routes: [Route] = []
    @Published var isLoading = false
    @Published var error: String?
    
    init(repo: RouteRepository = FirebaseRouteRepository(), service: RouteServiceing = RouteService()) {
        self.repo = repo
        self.service = service
    }
    
    func loadRoutes() async {
        isLoading = true; defer { isLoading = false }
        do { routes = try await service.listRoutes() }
        catch { self.error = error.localizedDescription }
    }
}
