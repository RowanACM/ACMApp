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
    
    @IBOutlet weak var nameLbl: UILabel!
    @IBOutlet weak var meetingCountLbl: UILabel!
    override func viewDidLoad() {
        if(UserData.user != nil){
        BaseViewController.ref.child("members").child(UserData.user.uid).observe(FIRDataEventType.value, with: { (snapshot) in
            // Get user value
            let value = snapshot.value as? NSDictionary
            for v in (value?.allKeys)!{
                print(v)
            }
            let name = value?["name"] as? String ?? ""
            let meetingCount = value?["meeting_count"] as? Int ?? 0
        
            self.nameLbl.text = name
            self.meetingCountLbl.text = "Number of meetings attended: " + String(meetingCount)
            
            // ...
        })
        }
        
        
    }
    
    @IBAction func OpenSlackBtnPressed(_ sender: Any) {
        var url  = NSURL(string: "slack://open"); // Change the URL with your URL Scheme
     
            }
}
