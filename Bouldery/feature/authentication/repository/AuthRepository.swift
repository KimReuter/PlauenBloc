//
//  AuthRepository.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation
import Combine

protocol AuthRepository: AnyObject {
    
    var isLoggedInPublisher: AnyPublisher<Bool, Never> { get }
    var isInitializedPublisher: AnyPublisher<Bool, Never> { get }

    var isLoggedInValue: Bool { get }
    var isInitializedValue: Bool { get }

    func signUp(userName: String, email: String, password: String) async -> AuthResult
    func addUserToFirestore(userId: String, userName: String, role: UserRole) async
    func signIn(email: String, password: String) async -> AuthResult
    func fetchUserRole(userId: String) async -> UserRole?
    func sendPasswort(email: String) async -> Result<Void, Error>
    func getCurrentUserId() -> String?
    func signOut()
    
}
