//
//  BoulderyApp.swift
//  Bouldery
//
//  Created by Kim Reuter on 19.08.25.
//

import SwiftUI
import Firebase

@main
struct BoulderyApp: App {
    
    init() {
        FirebaseConfiguration.shared.setLoggerLevel(.min)
        FirebaseApp.configure()
    }
    
    @StateObject private var authVM = AuthViewModel()
    
    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(authVM)
        }
    }
}
