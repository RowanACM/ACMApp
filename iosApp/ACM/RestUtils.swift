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
    static func memberSignin(idToken:String,completion:@escaping (String)->()) {
        
        var query = "https://api.rowanacm.org/prod/sign-in?token=" //server Url
        
        //append request to url
        query = query + idToken
        print(query)
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
