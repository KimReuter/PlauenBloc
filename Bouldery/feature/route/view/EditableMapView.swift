//
//  EditableMapView.swift
//  Bouldery
//
//  Created by Kim Reuter on 21.08.25.
//

import SwiftUI

struct EditableMapView: View {
    let routes: [Route]
    var onTapNormalized: ((Double, Double) -> Void)?

    var body: some View {
        GeometryReader { geo in
            Image("hall_plan")
                .resizable()
                .scaledToFit()
                .overlay {
                    ForEach(routes) { route in
                        Circle()
                            .fill(color(for: route.holdColor))
                            .frame(width: 16, height: 16)
                            .overlay(Circle().stroke(.white, lineWidth: 2))
                            .position(x: route.x * geo.size.width,
                                      y: route.y * geo.size.height)
                    }
                }
                .contentShape(Rectangle())
                .highPriorityGesture(
                    DragGesture(minimumDistance: 0).onEnded { g in
                        let f = geo.frame(in: .local)
                        let x = min(max(0, (g.location.x - f.minX) / f.width), 1)
                        let y = min(max(0, (g.location.y - f.minY) / f.height), 1)
                        onTapNormalized?(x, y)
                    }
                )
        }
        .aspectRatio(1.6, contentMode: .fit)
    }
}
