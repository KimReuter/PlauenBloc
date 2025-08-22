//
//  EditableMapView.swift
//  Bouldery
//
//  Created by Kim Reuter on 21.08.25.
//

import SwiftUI

struct EditableMapView: View {
    let routes: [Route]
    var hall: HallSection = .FRONT
    var draft: (x: Double, y: Double)? = nil                 // ← NEU
    var onTapNormalized: ((Double, Double) -> Void)? = nil   // ← für Create-Sheet

    private var imageName: String {
        switch hall {
        case .FRONT: return "hall_plan_front"
        case .BACK:  return "hall_plan_back"
        }
    }

    var body: some View {
        GeometryReader { geo in
            Image(imageName)
                .resizable()
                .scaledToFit()
                .overlay {
                    // existierende Routen
                    ForEach(routes) { route in
                        Circle()
                            .fill(route.holdColor.color)
                            .frame(width: 16, height: 16)
                            .overlay(Circle().stroke(.white, lineWidth: 2))
                            .position(x: route.x * geo.size.width,
                                      y: route.y * geo.size.height)
                    }
                    // temporärer Marker (Draft)
                    if let d = draft {
                        ZStack {
                            Circle()
                                .strokeBorder(.white.opacity(0.9), lineWidth: 2)
                                .frame(width: 24, height: 24)
                            Circle()
                                .fill(.white.opacity(0.9))
                                .frame(width: 6, height: 6)
                        }
                        .shadow(radius: 2)
                        .position(x: d.x * geo.size.width,
                                  y: d.y * geo.size.height)
                    }
                }
                .contentShape(Rectangle())
                .highPriorityGesture(
                    DragGesture(minimumDistance: 0).onEnded { g in
                        guard let onTapNormalized else { return }
                        let f = geo.frame(in: .local)
                        let x = min(max(0, (g.location.x - f.minX) / f.width), 1)
                        let y = min(max(0, (g.location.y - f.minY) / f.height), 1)
                        onTapNormalized(x, y)
                    }
                )
        }
        .aspectRatio(1.6, contentMode: .fit)
    }
}
