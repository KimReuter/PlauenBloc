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
    var draft: (x: Double, y: Double)? = nil
    var draftColor: Color? = nil
    var draftLabel: String? = nil
    var onTapNormalized: ((Double, Double) -> Void)? = nil
    
    private var imageName: String {
        switch hall {
        case .FRONT: return "boulderhalle_grundriss_vordere_halle_klein"
        case .BACK:  return "boulderhalle_grundriss_hintereHalle_ klein"
        }
    }

    var body: some View {
        GeometryReader { geo in
            Image(imageName)
                .resizable()
                .scaledToFit()
                .frame(maxWidth: .infinity, alignment: .center)
                .overlay {
                    ForEach(routes) { route in
                        Circle()
                            .fill(route.holdColor.color)
                            .frame(width: 16, height: 16)
                            .overlay(Circle().stroke(.white, lineWidth: 2))
                            .position(x: route.x * geo.size.width,
                                      y: route.y * geo.size.height)
                    }
                    if let d = draft {
                        let fill = draftColor ?? .white.opacity(0.9)
                        ZStack {
                            Circle()
                                .fill(fill)
                                .frame(width: 28, height: 28)
                            if let label = draftLabel, !label.isEmpty {
                                
                                Text(label)
                                    .font(.system(size: 12, weight: .bold, design: .rounded))
                                    .foregroundStyle(Color.white)
                                    .shadow(radius: 1)
                            }
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
    }
}
