//
//  Route.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation
import FirebaseFirestore

struct Route: Identifiable, Codable, Equatable {
    @DocumentID var id: String?
    var name: String
    var setter: String
    var hall: HallSection
    var sector: Sector
    var holdColor: HoldColor
    var difficulty: Difficulty
    var number: Int
    var description: String
    var x: Double   // 0...1 normalisiert
    var y: Double   // 0...1 normalisiert
    var points: Int

    // Firestore braucht einen leeren init? Nicht zwingend, aber nice to have:
    init(id: String? = nil,
         name: String = "",
         hall: HallSection = .FRONT,
         sector: Sector = .SONNENPLATTE,
         holdColor: HoldColor = .YELLOW,
         difficulty: Difficulty = .GREEN,
         number: Int = 0,
         description: String = "",
         setter: String = "",
         x: Double = 0,
         y: Double = 0,
         points: Int = 0) {
        self.id = id
        self.name = name
        self.hall = hall
        self.sector = sector
        self.holdColor = holdColor
        self.difficulty = difficulty
        self.number = number
        self.description = description
        self.setter = setter
        self.x = x
        self.y = y
        self.points = points
    }
}
