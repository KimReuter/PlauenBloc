//
//  AppUser.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation
import FirebaseFirestore

struct AppUser: Codable {
    @DocumentID var uid: String?
    var userName: String
    var role: UserRole
    var totalPoints: Int
    var email: String?
}
