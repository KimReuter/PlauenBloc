//
//  FirebaseRouteRepository.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation
import FirebaseFirestore

final class FirebaseRouteRepository: RouteRepository {
    private let db = Firestore.firestore().collection("routes")

    func getRoutes() async throws -> [Route] {
        let snapshot = try await db.getDocuments()
        return snapshot.documents.compactMap { try? $0.data(as: Route.self) }
    }

    func addRoute(_ route: Route) async throws {
        _ = try db.addDocument(from: route)
    }

    func updateRoute(_ route: Route) async throws {
        guard let id = route.id else { return }
        try db.document(id).setData(from: route, merge: true)
    }

    func deleteRoute(_ routeId: String) async throws {
        try await db.document(routeId).delete()
    }
}
