//
//  RouteProperty.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation
import SwiftUI

protocol LabeledEnum {
    var label: String { get }
}

protocol ColorLabeledEnum: CaseIterable, Identifiable, Hashable {
    var label: String { get }
    var color: Color { get }
}

enum HoldColor: String, Codable, ColorLabeledEnum {
    var id: String { rawValue }
    case PINK, WHITE, YELLOW, BLUE, GREEN, RED, BROWN, TURQUOISE, GREY, PURPLE, BLACK

    var label: String {
        switch self {
        case .PINK: return "Rosa"
        case .WHITE: return "Weiß"
        case .YELLOW: return "Gelb"
        case .BLUE: return "Blau"
        case .GREEN: return "Grün"
        case .RED: return "Rot"
        case .BROWN: return "Braun"
        case .TURQUOISE: return "Turquoise"
        case .GREY: return "Grau"
        case .PURPLE: return "Lila"
        case .BLACK: return "Schwarz"
        }
    }

    var color: Color {
        switch self {
        case .PINK: return .pink
        case .WHITE: return .white
        case .YELLOW: return .yellow
        case .BLUE: return .blue
        case .GREEN: return .green
        case .RED: return .red
        case .BROWN: return .brown
        case .TURQUOISE: return .cyan
        case .GREY: return .gray
        case .PURPLE: return .purple
        case .BLACK: return .black
        }
    }
}

enum Difficulty: String, Codable, ColorLabeledEnum {
    var id: String { rawValue }
    case PINK, WHITE, YELLOW, BLUE, GREEN, RED, BROWN

    var label: String {
        switch self {
        case .PINK: return "Rosa"
        case .WHITE: return "Weiß"
        case .YELLOW: return "Gelb"
        case .BLUE: return "Blau"
        case .GREEN: return "Grün"
        case .RED: return "Rot"
        case .BROWN: return "Braun"
        }
    }

    var color: Color {
        switch self {
        case .PINK: return .pink
        case .WHITE: return .white
        case .YELLOW: return .yellow
        case .BLUE: return .blue
        case .GREEN: return .green
        case .RED: return .red
        case .BROWN: return .brown
        }
    }
}

enum HallSection: String, Codable, CaseIterable, Identifiable, LabeledEnum {
    var id: String { rawValue }
    case FRONT, BACK
    var label: String {
        switch self {
        case .FRONT: return "Vordere Halle"
        case .BACK:  return "Hintere Halle"
        }
    }
}

enum Sector: String, Codable, CaseIterable, Identifiable, LabeledEnum {
    
    var id: String { rawValue }
    
    case ROCO_DE_LA_FINESTRA, MASSONE, TREBENNA, SONNENPLATTE, GRAND_BROWEDA, DIEBESLOCH, MONTE_CUCCO, HÖLLENHUND
    
    var label: String {
        switch self {
        case .ROCO_DE_LA_FINESTRA: return "Roco de la Finestra"
        case .MASSONE:             return "Massone"
        case .TREBENNA:            return "Trebenna"
        case .SONNENPLATTE:        return "Sonnenplatte"
        case .GRAND_BROWEDA:       return "Grand Broweda"
        case .DIEBESLOCH:          return "Diebesloch"
        case .MONTE_CUCCO:         return "Monte Cucco"
        case .HÖLLENHUND:          return "Höllenhund"
        }
    }
    
    var hall: HallSection {
        switch self {
        case .SONNENPLATTE, .TREBENNA, .MASSONE, .ROCO_DE_LA_FINESTRA:
            return .FRONT
        case .DIEBESLOCH, .GRAND_BROWEDA, .MONTE_CUCCO, .HÖLLENHUND:
            return .BACK
        }
    }
}

struct RelativePosition: Codable, Equatable {
    var x: Double
    var y: Double
}

struct Person: Identifiable, Hashable {
    let id: String
    let name: String
    init(name: String) {
        self.name = name
        self.id = name
    }
}

enum DefaultSetters {
    static let all: [Person] = [
        Person(name: "Jens Grimm"),
        Person(name: "Jörg Band"),
        Person(name: "Jörg Schwert")
    ]
}

extension Sector {
    static func options(for hall: HallSection) -> [Sector] {
        allCases.filter { $0.hall == hall }
    }
}
