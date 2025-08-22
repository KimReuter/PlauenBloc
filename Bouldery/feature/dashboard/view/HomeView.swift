//
//  HomeView.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import SwiftUI

struct HomeView: View {
    @EnvironmentObject var vm: AuthViewModel
    
    var body: some View {
        ZStack {
            AppTheme.Palette.bg.ignoresSafeArea()
            
            VStack(spacing: 24) {
                Text("🏡 Willkommen in Bouldery")
                    .font(.title.bold())
                
                Button(role: .destructive) {
                    vm.signOut()
                } label: {
                    Text("Ausloggen")
                        .font(.headline)
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(AppTheme.Palette.orange)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .padding(.horizontal, 40)
            }
        }
    }
}
