//
//  ViewController.swift
//  ACM
//
//  Created by Johnathan Saunders on 1/20/17.
//  Copyright © 2017 Johnathan Saunders. All rights reserved.
//
import Foundation
import UIKit
class AnnouncementTableViewCell: UITableViewCell {
    @IBOutlet weak var committee: UILabel!
    @IBOutlet weak var body: UILabel!
    
    @IBOutlet var committeLogo: UIImageView!
    @IBOutlet var CardBackgroundView: UIView!
}
class ViewController: BaseViewController , UITableViewDelegate, UITableViewDataSource{
    
    
    
    var items: Array<Announcement> = Array<Announcement>();
    
    @IBOutlet weak var tableView: UITableView!
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
        
        self.title = "Announcements";
        let announcementRef =  BaseViewController.ref.child("announcements")
        // Listen for new comments in the Firebase database
        announcementRef.observe(.childAdded, with: { (snapshot) -> Void in
            let value = snapshot.value as? NSDictionary
            
            var announcement = Announcement()
            
            announcement.author = value?["author"] as? String ?? ""
            announcement.committee = value?["committee"] as? String ?? ""
            announcement.date = value?["date"] as? String ?? ""
            announcement.text = value?["text"] as? String ?? ""
            announcement.timestamp = value?["timestamp"] as? Int ?? 0
            announcement.title = value?["title"] as? String ?? ""
            self.items.append(announcement)
            self.items = self.items.sorted(by: { $0.timestamp > $1.timestamp })
            self.tableView.reloadData()
            
            print(announcement)
        })
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.items.count;
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell:AnnouncementTableViewCell = self.tableView.dequeueReusableCell(withIdentifier:"AnnouncementTableViewCell")! as! AnnouncementTableViewCell
        var announcement = self.items[indexPath.row]
        
        cell.committee.text = announcement.committee
        cell.body.text = announcement.text
        var card = cell.CardBackgroundView
        card?.layer.cornerRadius = 2.0
        card?.clipsToBounds = true
        card?.layer.masksToBounds = false
        card?.layer.shadowColor = UIColor.black.cgColor
        card?.layer.shadowOpacity = 0.5
        card?.layer.shadowOffset = CGSize(width: -1, height: 1)
        card?.layer.shadowRadius = 1
        
        cell = loadComitteeLogo(cell: cell, announcement: announcement)
        
        return cell
    }
    func loadComitteeLogo(cell: AnnouncementTableViewCell, announcement:Announcement) -> AnnouncementTableViewCell{
        print("committee:" + announcement.committee)
        switch announcement.committee {
        case "RU MAD (App)":
            cell.committeLogo.image = UIImage(named: "appLogo")
        case "Webdev":
            cell.committeLogo.image = UIImage(named: "webDevLogo")
        default:
            break
        }
        return cell
        
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let vc = UIStoryboard(name:"Main", bundle:nil).instantiateViewController(withIdentifier: "AnnouncementViewController") as? AnnouncementViewController
        vc?.announcement = items[indexPath.row]
        self.navigationController?.pushViewController(vc!, animated:true)
        
    }
    
    
    
}

