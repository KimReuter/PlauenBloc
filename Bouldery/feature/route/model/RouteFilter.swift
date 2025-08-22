//
//  RouteFilter.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation

enum FilterKey { case Color, Difficulty, Sector, Setter } 

struct RouteFilter: Codable, Equatable {
    var hall: HallSection? = nil
    var sector: Sector? = nil
    var number: Int? = nil
    var holdColor: HoldColor? = nil
    var difficulty: Difficulty? = nil
    var routeSetter: String? = nil
}

extension Array where Element == Route {
    func applying(_ f: RouteFilter) -> [Route] {
        self.filter { r in
            if let hall = f.hall, r.hall != hall { return false }
            if let sector = f.sector, r.sector != sector { return false }
            if let number = f.number, r.number != number { return false }
            if let color = f.holdColor, r.holdColor != color { return false }
            if let diff  = f.difficulty, r.difficulty != diff { return false }
            if let setter = f.routeSetter, !setter.isEmpty,
               !r.setter.localizedCaseInsensitiveContains(setter) { return false }
            return true
        }
    }
}
