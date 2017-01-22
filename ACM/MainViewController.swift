//
//  MainViewController.swift
//  ACM
//
//  Created by Johnathan Saunders on 1/22/17.
//  Copyright © 2017 Johnathan Saunders. All rights reserved.
//
import UIKit
import Firebase
import GoogleSignIn
import FirebaseAuth
import Foundation
 class MainViewController: BaseViewController, GIDSignInUIDelegate {

    @IBOutlet weak var signInButton: GIDSignInButton!
    
    
    @IBAction func signInBtnPressed(_ sender: Any) {
    }
    
    override func viewDidLoad() {
        GIDSignIn.sharedInstance().uiDelegate = self
    }
    func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error?) {
        // ...
        if let error = error {
            // ...
            return
        }
        
        guard let authentication = user.authentication else { return }
        let credential = FIRGoogleAuthProvider.credential(withIDToken: authentication.idToken,
                                                          accessToken: authentication.accessToken)
        // ...
    }
}
