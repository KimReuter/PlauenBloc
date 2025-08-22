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
    
    func takenNumbers(hall: HallSection, sector: Sector) async throws -> Set<Int> {
            let snap = try await db
                .whereField("hall", isEqualTo: hall.rawValue)
                .whereField("sector", isEqualTo: sector.rawValue)
                .getDocuments()

            let nums = snap.documents.compactMap { $0.data()["number"] as? Int }
            return Set(nums)
        }
}
