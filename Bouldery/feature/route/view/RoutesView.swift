//
//  RoutesView.swift
//  Bouldery
//
//  Created by Kim Reuter on 21.08.25.
//

import SwiftUI

struct RouteView: View {
    @StateObject private var vm = MapViewModel()
    @State private var showSearch = false
    @State private var searchText = ""
    @FocusState private var searchFocused: Bool

    var body: some View {
        NavigationStack {
            ZStack {
                AppTheme.Palette.bg.ignoresSafeArea()

                VStack(spacing: 12) {
                    if showSearch {
                        HStack {
                            Image(systemName: "magnifyingglass")
                                .foregroundStyle(.secondary)

                            TextField("Suche nach Routen…", text: $searchText)
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
                        .padding(10)
                        .background(
                            RoundedRectangle(cornerRadius: 8)
                                .fill(Color(.systemGray6))
                        )
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(AppTheme.Palette.green.opacity(0.5), lineWidth: 1.5)
                        )
                        .transition(.move(edge: .top).combined(with: .opacity))
                        .onAppear { searchFocused = true }
                    }

                    ReadOnlyMapView(routes: vm.routes) { _ in }
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
                .padding()
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
                    } label: { Image(systemName: "magnifyingglass") }
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
                                // TODO: Neuer Route-Flow
                                print("Neue Route")
                            } label: {
                                Label("Neue Route", systemImage: "plus")
                            }
                        } label: {
                            Image(systemName: "ellipsis.vertical") 
                        }
                    }
            }
            .task { await vm.loadRoutes() }
        }
    }
}
