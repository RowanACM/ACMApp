//
//  ProfileViewController.swift
//  ACM
//
//  Created by Johnathan Saunders on 2/21/17.
//  Copyright © 2017 Johnathan Saunders. All rights reserved.
//
import Foundation
import FirebaseDatabase
import UIKit

class ProfileViewController:BaseViewController {
    
    @IBOutlet var slackBtn: UIButton!
    @IBOutlet weak var nameLbl: UILabel!
    @IBOutlet weak var meetingCountLbl: UILabel!
    override func viewDidLoad() {
        
        
        
    }
    override func viewDidAppear(_ animated: Bool) {
        if(UserData.user != nil){
            BaseViewController.ref.child("members").child(UserData.user.uid).observe(FIRDataEventType.value, with: { (snapshot) in
                // Get user value
                let value = snapshot.value as? NSDictionary
                for v in (value?.allKeys)!{
                    print(v)
                }
                let name = value?["name"] as? String ?? ""
                self.navigationItem.title = name
                let meetingCount = value?["meeting_count"] as? Int ?? 0
                let hasSlack = value?["meeting_count"] as? Bool ?? false
                
                self.nameLbl.text = name
                self.meetingCountLbl.text = "Number of meetings attended: " + String(meetingCount)
                
                if(hasSlack){
                    self.slackBtn.isHidden = false;
                }
                // ...
            })
        }
    }
    @IBAction func OpenSlackBtnPressed(_ sender: Any) {
        var url  = URL(string: "slack://open"); // Change the URL with your URL Scheme
        if #available(iOS 10.0, *) {
            
            if UIApplication.shared.canOpenURL(url! as URL) == true
            {
                print(true)
                UIApplication.shared.open(url! as URL, options: [:], completionHandler: { (success) in
                    print(success)
                    if(!success){
                        self.slackInItunes()
                    }
                    
                })
 
            }else {
                slackInItunes()
            }
        } else {
            let alert = UIAlertController(title: "Sorry", message: "Under iOS 10 not supported", preferredStyle: UIAlertControllerStyle.alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
            self.present(alert, animated: true, completion: nil)
            // Fallback on earlier versions
        }
        
    }
    
    func slackInItunes(){
        if let itunesUrl = NSURL(string: "https://appsto.re/us/5mE4K.i"), UIApplication.shared.canOpenURL(itunesUrl as URL) {
            if #available(iOS 10.0, *) {
                UIApplication.shared.open(itunesUrl as URL, options: [:], completionHandler: { (success) in
                })
            } else {
                // Fallback on earlier versions
            }
            
        }
    }
    
    
}
