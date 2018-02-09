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
import Firebase
import FirebaseCore
import FirebaseInstanceID
import Foundation
class MainViewController: BaseViewController,  GIDSignInUIDelegate, GIDSignInDelegate {
    @IBOutlet weak var generalLabel: UILabel!
    
    @IBOutlet weak var signOutBtn: UIButton!
    @IBOutlet weak var googleSignInBtn: GIDSignInButton!
    @IBOutlet weak var signInLbl: UILabel!
    @IBOutlet weak var signInButton: GIDSignInButton!
    
    @IBOutlet weak var meetingSignInBtn: UIButton!
    static var meetingSignInWaiting = false
    
    @IBAction func signInBtnPressed(_ sender: Any) {
    }
    
    override func viewDidLoad() {
        GIDSignIn.sharedInstance().uiDelegate = self
        generalLabel.text = "What is ACM?\nACM is the programming club at Rowan University. I don\'t know what else to put here so please open a pull request and add more info here.\n\nWhen do you meet?\nEvery Friday at 2–4 PM in Robinson 201 a/b."
        GIDSignIn.sharedInstance().signIn()
        
        
    }
    override func viewDidAppear(_ animated: Bool) {
        
    }
    
    func sign(inWillDispatch signIn: GIDSignIn!, error: Error!) {
        print("work1")
        //put in delay because there isnt a delegate
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            print(FIRAuth.auth()?.currentUser == nil)
            
            self.signedIn()
        }
        
        
    }
    func sign(_ signIn: GIDSignIn!, dismiss viewController: UIViewController!) {
        print("work2")
    }
    
    
    
    @IBAction func meetingSignInPressed(_ sender: Any) {
        MainViewController.meetingSignInWaiting = true
        meetingSignInBtn.isEnabled = false
        if(MainViewController.meetingSignInWaiting){
            let currentUser = FIRAuth.auth()?.currentUser
            currentUser?.getTokenForcingRefresh(true) {idToken, error in
                if let error = error {
                    // Handle error
                    return;
                }
                
                
                RestUtils.memberSignin(idToken: idToken!, completion: { (responseDict) in
                    DispatchQueue.main.async {
                        print("This is run on the main queue, after the previous code in outer block")
                        let responseDict = Utils.convertToDictionary(text: responseDict)
                        if(responseDict?.keys.contains("message"))!{
                            let response =   responseDict?["message"]
                        
                            MainViewController.meetingSignInWaiting = false
                            self.meetingSignInBtn.isEnabled = true
                            self.meetingSignInBtn.isHidden = true
                            self.signInLbl.isHidden = false
                            var signInTxt = "Uh Oh, Sign-in was unsuccessful."
                            self.signInLbl.text = response as! String
                        }
                        
                    }
                })
                // Send token to your backend via HTTPS
                // ...
            }
            
        }
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
    
    
    
    func signedIn(){
        if(UserData.user != nil){
            googleSignInBtn.isHidden = true
            signOutBtn.isHidden = false
            meetingSignInBtn.isHidden =  false
        }
    }
    
    
    @IBAction func signOutBtnPressed(_ sender: Any) {
        let firebaseAuth = FIRAuth.auth()
        do {
            
            try firebaseAuth?.signOut()
            GIDSignIn.sharedInstance().signOut()
            GIDSignIn.sharedInstance().disconnect()
            
        } catch let signOutError as NSError {
            print ("Error signing out: %@", signOutError)
        }
        signInButton.isHidden = false
        signOutBtn.isHidden = true
        meetingSignInBtn.isHidden =  true
        signInLbl.isHidden = true
        
        
        
    }
}
