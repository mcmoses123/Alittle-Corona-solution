package com.example.myapplication
class Ticket{
    var tweetID:String?=null
    var tweetText:String?=null
    var tweetPersonUID:String?=null//person information
    var email:String?=null
    //var TID:String?=null

    constructor(tweetID:String,tweetText:String,tweetPersonUID:String,email:String){
        this.tweetID=tweetID
        this.tweetText=tweetText
        this.tweetPersonUID=tweetPersonUID
        this.email=email

    }
}