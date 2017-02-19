//
//  BaseViewController.swift
//  ACM
//
//  Created by Johnathan Saunders on 1/21/17.
//  Copyright © 2017 Johnathan Saunders. All rights reserved.
//

import Foundation
import UIKit
import Firebase
import FirebaseDatabase

class BaseViewController: UIViewController {
    static var ref: FIRDatabaseReference! = FIRDatabase.database().reference()
    
}
