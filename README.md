#ACM Attendance Lambda
Gets run every time someone signs in to the meeting

###API Url: https://2dvdaw7sq1.execute-api.us-east-1.amazonaws.com/prod/attendance

Properties are: uid, name, and email.  
uid = the unique idenitifier for the user  
name = name of user  
email = users email (must end with rowan.edu or it is not valid)  
 
###Example request

     https://2dvdaw7sq1.execute-api.us-east-1.amazonaws.com/prod/attendance?uid=abc123&name=John%20Smith&email=example@students.rowan.edu
     
   (Notice that example name has "%20" where there should be a space. The API does not accept spaces in the query, depending on what you use to GET the request you may not need to worry about spaces in your request.)
    
###Response codes 
100 = Suceeded and this is the users first time ever signing in   
110 = Suceeded and this user has signed in before  
120 = The user is already signed into the meeting  
130 = The user signed up for ACM between meetings  
200 =  Attendance sign in is currently disabled  
210 = Input is not valid  // could be that the users email does not end in "rowan.edu"  
220 = An unknown error  

###CURL example

    curl https://2dvdaw7sq1.execute-api.us-east-1.amazonaws.com/prod/attendance?uid=abc123&name=John%20Smith&email=example@students.rowan.edu"
  
  response: 210

