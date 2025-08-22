//
//  IdentifiableExtensions.swift
//  Bouldery
//
//  Created by Kim Reuter on 22.08.25.
//

import Foundation

extension Int: Identifiable { public var id: Int { self } }
extension String: Identifiable { public var id: String { self } }
