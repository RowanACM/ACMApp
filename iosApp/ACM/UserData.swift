//
//  UserData.swift
//  ACM
//
//  Created by Johnathan Saunders on 2/18/17.
//  Copyright © 2017 Johnathan Saunders. All rights reserved.
//
import FirebaseAuth
import Foundation
class UserData{
    static var signinStatus:Int = 0; //response from restUtil
    static var user:FIRUser! = nil //firebase/google signed in user
}
