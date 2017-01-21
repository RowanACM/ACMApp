//
//  AnnouncementViewController.swift
//  ACM
//
//  Created by Johnathan Saunders on 1/21/17.
//  Copyright © 2017 Johnathan Saunders. All rights reserved.
//

import Foundation
import UIKit
class AnnouncementViewController : BaseViewController{
    @IBOutlet weak var committee: UILabel!
    @IBOutlet weak var timestamp: UILabel!
    @IBOutlet weak var body: UILabel!
    var announcement: Announcement!
    
    override func viewDidLoad() {
        self.title = "Announcement";

        committee.text = announcement.committee
       var date = NSDate(timeIntervalSince1970: TimeInterval(announcement.timestamp))
        timestamp.text = date.description
        body.text = announcement.text
        print(announcement)
    }
  
}
