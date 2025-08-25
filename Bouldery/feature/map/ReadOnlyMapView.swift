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
    var hall: HallSection
    var onSelectRoute: ((Route) -> Void)?
    
    private var imageName: String {
        switch hall {
        case .FRONT: return "boulderhalle_grundriss_vordere_halle_klein"
        case .BACK:  return "boulderhalle_grundriss_hintereHalle_ klein"
        }
    }
    
    private var visibleRoutes: [Route] {
        routes.filter { $0.hall == hall }
    }
    
    var body: some View {
            Image(imageName)
                .resizable()
                .scaledToFit()
                .overlay {
                    GeometryReader { ig in
                        ForEach(visibleRoutes) { route in
                            Circle()
                                .fill(route.difficulty.color)
                                .frame(width: 16, height: 16)
                                .overlay(Circle().stroke(.white, lineWidth: 2))
                                .position(x: route.x * ig.size.width,
                                          y: route.y * ig.size.height)
                                .onTapGesture { onSelectRoute?(route) }
                        }
                    }
                }
                .clipped()
        }
}
