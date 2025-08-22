//
//  MapView.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation
import SwiftUI

struct ReadOnlyMapView: View {
    let routes: [Route]
    var onSelectRoute: ((Route) -> Void)?

    var body: some View {
        GeometryReader { geo in
            Image("boulderhalle_grundriss_vordere_halle_klein")
                .resizable()
                .scaledToFit()
                .frame(maxWidth: .infinity)
                .overlay {
                    ForEach(routes) { route in
                        Circle()
                            .fill(color(for: route.holdColor))
                            .frame(width: 16, height: 16)
                            .overlay(Circle().stroke(.white, lineWidth: 2))
                            .position(x: route.x * geo.size.width,
                                      y: route.y * geo.size.height)
                            .onTapGesture { onSelectRoute?(route) }
                    }
                }
        }
    }

    private func color(for hold: HoldColor) -> Color {
        switch hold {
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
