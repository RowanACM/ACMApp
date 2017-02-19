//
//  RestUtils.swift
//  ACM
//
//  Created by Johnathan Saunders on 1/22/17.
//  Copyright © 2017 Johnathan Saunders. All rights reserved.
//

import Foundation
import FirebaseAuth
class RestUtils{
    static func memberSignin(currentUser:FIRUser ) {
        
        let displayname =  currentUser.displayName!.replacingOccurrences(of: " ", with: "%20")
        
        var query = "https://2dvdaw7sq1.execute-api.us-east-1.amazonaws.com/prod/attendance"
        query += "?uid=" + currentUser.uid
        query += "&name=" + displayname
        query += "&email=" +  currentUser.email!
        query += "&method=" + "ios"
        
        let url = URL(string: query)
        print(url!)
        
        let task = URLSession.shared.dataTask(with: url!) { data, response, error in
            guard error == nil else {
                print(error!)
                return
            }
            guard let data = data else {
                print("Data is empty")
                return
            }
            
            let result = String(data: data, encoding: String.Encoding.utf8)
            print(result!)
        }
        
        task.resume()
    }
}
