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
    static func memberSignin(currentUser:FIRUser,completion:@escaping (String)->()) {
        
        var query = "https://2dvdaw7sq1.execute-api.us-east-1.amazonaws.com/prod/attendance?uid=" //server Url
        
        //append request to url
        query = query + currentUser.uid + "&name="
        let displayname =  currentUser.displayName!.replacingOccurrences(of: " ", with: "%20")
        query = query +  displayname + "&email="
        query = query +  currentUser.email!
        
        let url = URL(string: query)
        
        
        
        
        let task = URLSession.shared.dataTask(with: url!) { data, response, error in
            guard error == nil else {
                print(error)
                return
            }
            guard let data = data else {
                print("Data is empty")
                return
            }
            
            let result  = String(data: data, encoding: String.Encoding.utf8)
            completion(result!)
            print(result)
        }
        
        task.resume()
    }
}
