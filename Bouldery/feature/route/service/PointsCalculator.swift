//
//  PointsCalculator.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation

struct PointsCalculator {
    static func points(for difficulty: Difficulty, isFlash: Bool = false) -> Int {
        let base: Int
        switch difficulty {
        case .PINK:  return 0
        case .WHITE: return 10
        case .YELLOW:return 20
        case .BLUE:  return 30
        case .GREEN: return 40
        case .RED:   return 50
        case .BROWN: return 60
        }
        return (isFlash && base > 0) ? base + 10 : base
    }
}
