//
//  RootView.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import SwiftUI

struct RootView: View {
    @EnvironmentObject var authVM: AuthViewModel
    
    var body: some View {
        Group {
            if !authVM.isInitialized {
                ProgressView("Lade...")
            } else if authVM.isLoggedIn {
                MainTabView()
            } else {
                LoginView()
            }
        }
    }
}

#Preview {
    RootView()
}
