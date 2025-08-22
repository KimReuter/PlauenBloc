//
//  AuthError.swift
//  Bouldery
//
//  Created by Kim Reuter on 20.08.25.
//

import Foundation

enum AuthError: Error, Equatable {
    case emailAlreadyInUse
    case weakPassword
    case invalidEmailFormat
    case invalidEmail
    case wrongPassword
    case networkError
    case tooManyRequests
    case unknown(String?)
}
