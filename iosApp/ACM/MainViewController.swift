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
            RestUtils.memberSignin(currentUser: UserData.user, completion: { (response) in
                DispatchQueue.main.async {
                    print("This is run on the main queue, after the previous code in outer block")
                    
                    MainViewController.meetingSignInWaiting = false
                    self.meetingSignInBtn.isEnabled = true
                    self.meetingSignInBtn.isHidden = true
                    self.signInLbl.isHidden = false
                    var signInTxt = "Uh Oh, Sign-in was unsuccessful."
                    switch response {
                    case "100":
                        signInTxt  = "Welcome to ACM! \n Have a happy first meeting. 😄"
                    case "110":
                        signInTxt  = "Sign-in successful. 😎 \n Welcome Back!"
                        
                    case "120":
                        signInTxt  = "You are already signed in, no need to sign in again. 😁"
                        
                    case "130":
                        signInTxt  = "Response registered? 😕 Not sure what this is, someone ask tyler."
                        
                    case "200":
                        signInTxt  = "OH NO! 😱 Attendance sign in is disabled - Sorry."
                        
                    case "210":
                        signInTxt  = "Invalid Input \n 👽 Try your Rowan Email"
                        
                    case "220":
                        signInTxt  = "I HAVE NO IDEA WHAT HAPPENED!!!! 💔 \n But I can tell you it wasn't good."
                        
                    default:
                        break
                    }
                    self.signInLbl.text = signInTxt
                }
            })
            
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
