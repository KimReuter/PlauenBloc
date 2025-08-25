//
//  RoutesView.swift
//  Bouldery
//
//  Created by Kim Reuter on 21.08.25.
//

import SwiftUI

struct RouteView: View {
    
    @StateObject private var vm = MapViewModel()
    @EnvironmentObject var authVM: AuthViewModel
    
    @State private var showSearch = false
    @State private var searchText = ""
    @FocusState private var searchFocused: Bool
    @State private var hall: HallSection = .FRONT
    @State private var showCreateSheet = false
    
    private var isOperator: Bool {
        authVM.userRole == .OPERATOR
    }
    
    var body: some View {
        NavigationStack {
            ZStack {
                AppTheme.Palette.bg.ignoresSafeArea()
                
                GeometryReader { proxy in
                    let availableH = proxy.size.height
                    let mapH = min(560, max(300, availableH - 220))
                    
                    VStack(spacing: 16) {
                        if showSearch {
                            HStack {
                                Image(systemName: "magnifyingglass")
                                    .foregroundStyle(.secondary)
                                TextField("Suche nach Routen…", text: $searchText)
                                    .foregroundStyle(.background)
                                    .textInputAutocapitalization(.never)
                                    .autocorrectionDisabled(true)
                                    .focused($searchFocused)
                                if !searchText.isEmpty {
                                    Button {
                                        searchText = ""
                                    } label: {
                                        Image(systemName: "xmark.circle.fill")
                                            .foregroundStyle(.secondary)
                                    }
                                    .buttonStyle(.plain)
                                }
                            }
                            .padding(.vertical, 10)
                            .padding(.horizontal, 24)
                            .background(RoundedRectangle(cornerRadius: 8).fill(Color(.systemGray6)))
                            .overlay(RoundedRectangle(cornerRadius: 8).stroke(AppTheme.Palette.green.opacity(0.5), lineWidth: 1.5))
                            .transition(.move(edge: .top).combined(with: .opacity))
                            .onAppear { searchFocused = true }
                        }
                        
                        HallSelectionButtons(selectedHall: $hall)
                            .padding(.top, 32)
                        
                        ReadOnlyMapView(routes: vm.routes, hall: hall) { _ in }
                            .frame(height: 500)
                            .padding(.top, 32)
                        
                        Spacer(minLength: 0)
                    }
                    .padding(.horizontal)
                }
            }
            .navigationTitle("Routen")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    
                    Button {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            showSearch.toggle()
                            if !showSearch { searchText = "" }
                        }
                    } label: {
                        Image(systemName: "magnifyingglass")
                    }
                }
                
                ToolbarItem(placement: .topBarTrailing) {
                    Menu {
                        Button {
                            // TODO: Filter öffnen
                            print("Filter")
                        } label: {
                            Label("Filter", systemImage: "line.3.horizontal.decrease.circle")
                        }
                        
                        Button {
                            // TODO: Routenliste anzeigen
                            print("Als Liste anzeigen")
                        } label: {
                            Label("Als Liste anzeigen", systemImage: "list.bullet")
                        }
                        
                        Button {
                            showCreateSheet = true
                        } label: {
                            Label("Neue Route", systemImage: "plus")
                        }
                        
                    } label: {
                        Image(systemName: "ellipsis").tint(.brandGreen)
                    }
                }
                
                
            }
            .sheet(isPresented: $showCreateSheet) {
                CreateRouteSheet()
            }
            .task { await vm.loadRoutes() }
        }
    }
}

