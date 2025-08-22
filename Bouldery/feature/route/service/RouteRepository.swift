//
//  RouteRepository.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation

protocol RouteRepository {
    func getRoutes() async throws -> [Route]
    func addRoute(_ route: Route) async throws
    func updateRoute(_ route: Route) async throws
    func deleteRoute(_ routeId: String) async throws
    func takenNumbers(hall: HallSection, sector: Sector) async throws -> Set<Int>
}
