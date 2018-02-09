//
//  Utils.swift
//  ACM
//
//  Created by Johnathan Saunders on 2/9/18.
//  Copyright © 2018 Johnathan Saunders. All rights reserved.
//

import Foundation
class Utils {
    static func convertToDictionary(text: String) -> [String: Any]? {
        if let data = text.data(using: .utf8) {
            do {
                return try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            } catch {
                print(error.localizedDescription)
            }
        }
        return nil
    }

}
