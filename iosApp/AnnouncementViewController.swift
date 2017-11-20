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
        let date = NSDate(timeIntervalSince1970: TimeInterval(announcement.timestamp))
        let relativeDate = timeAgoSinceDate(date: date)
        timestamp.text = relativeDate
        body.text = announcement.text
        print(announcement)
    }
    
    // Get the relative time between a given date and the current time
    func timeAgoSinceDate(date:NSDate) -> String {
        let calendar = NSCalendar.current
        let unitFlags: Set<Calendar.Component> = [.minute, .hour, .day, .weekOfYear, .month, .year, .second]
        let now = NSDate()
        let earliest = now.earlierDate(date as Date)
        let latest = (earliest == now as Date) ? date : now
        let components = calendar.dateComponents(unitFlags, from: earliest as Date,  to: latest as Date)
        
        if (components.year! >= 2) {
            return "\(components.year!) years ago"
        } else if (components.year! >= 1){
            return "1 year ago"
        } else if (components.month! >= 2) {
            return "\(components.month!) months ago"
        } else if (components.month! >= 1){
            return "1 month ago"
        } else if (components.weekOfYear! >= 2) {
            return "\(components.weekOfYear!) weeks ago"
        } else if (components.weekOfYear! >= 1){
            return "1 week ago"
        } else if (components.day! >= 2) {
            return "\(components.day!) days ago"
        } else if (components.day! >= 1){
            return "Yesterday"
        } else if (components.hour! >= 2) {
            return "\(components.hour!) hours ago"
        } else if (components.hour! >= 1){
            return "1 hour ago"
        } else if (components.minute! >= 2) {
            return "\(components.minute!) minutes ago"
        } else if (components.minute! >= 1){
            return "1 minute ago"
        } else if (components.second! >= 3) {
            return "\(components.second!) seconds ago"
        } else {
            return "Just now"
        }
        
    }
  
}
