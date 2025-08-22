//
//  MainTabView.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation
import SwiftUI

struct MainTabView: View {
    @EnvironmentObject var authVM: AuthViewModel

    var body: some View {
        TabView {
            Tab("Dashboard", systemImage: "house") {
                HomeView()
            }
            
            Tab("Routen", systemImage: "map") {
                RouteView()
            }
        }
        .tint(AppTheme.Palette.green) 
    }
}
