package com.example.myapplication

//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.AdView
//import kotlinx.android.synthetic.main.add_ticket.view.*
//import kotlinx.android.synthetic.main.ads_ticket.view.*
//import kotlinx.android.synthetic.main.tweets_ticket.view.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_layout.*
import kotlinx.android.synthetic.main.add_notes.view.*
import kotlinx.android.synthetic.main.tickets.view.*
import java.util.*
import kotlin.collections.ArrayList


class layout : AppCompatActivity() {
    var ListTweets=ArrayList<Ticket>()
    var adapter:MyTweetAdapter?=null
    var myemail:String?=null
    var UserUID:String?=null
    var userUsername:String?=null
    var imageProfile:String?=null
    private var mAuth: FirebaseAuth?=null
    private var database= FirebaseDatabase.getInstance()
    private var myRef=database.reference//connect with the google database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout)
        var b:Bundle= intent.extras!!
        myemail=b.getString("email")
        UserUID=b.getString("uid")

        //dummy data
        ListTweets.add(Ticket("0","him","add","email"))
        /*ListTweets.add(Ticket("0","him","url","hussein"))
        ListTweets.add(Ticket("0","him","url","hussein"))
        ListTweets.add(Ticket("0","him","url","hussein"))*/
        adapter=MyTweetAdapter(this,ListTweets)
        lvTweets.adapter=adapter
        LoadPost()
    }
    inner class MyTweetAdapter: BaseAdapter {//this class already implemented and responsible for working with listview and the format implemented at the top
    var listNotesAdapter=ArrayList<Ticket>()
        var context: Context?=null
        constructor(context: Context, listNotesAdapter:ArrayList<Ticket>):super(){
            this.listNotesAdapter=listNotesAdapter
            this.context=context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var mytweet=listNotesAdapter[position]
            //var keyd: String? = myRef.child("posts").push().key
            val time= Calendar.getInstance().time

            if(mytweet.tweetPersonUID.equals("add")){
                //load add ticket
                var myView=layoutInflater.inflate(R.layout.add_notes,null)
                myView.iv_post.setOnClickListener(View.OnClickListener {
                    //upload to server,creating node with push assigning a unique ID

                   // var hope=time
                    myRef.child("posts").child((SplitString(UserUID+myView.etPost.text))).setValue(
                        PostInfo(UserUID!!, myView.etPost.text.toString(), myemail!!)
                    )
                    myView.etPost.setText("")
                })
                return myView
            }
            else{
                var myView=layoutInflater.inflate(R.layout.tickets,null)
                //load tweet ticket
                myView.txt_tweet.text = mytweet.tweetText//it takes the text input by the user and display
                // myView.txtUserName.text = mytweet.tweetPersonUID//it takes the personUID and display on the ticket
                // picasso from the internet takes the url and loads into the object you want to add
                // Picasso.with(context).isLoggingEnabled = true
               // Picasso.with(context).load(mytweet.tweetImageURL).into(myView.imageView) //TODO not loading pic
                myView.dateView.text= time.toString()
                myView.deleteView.setOnClickListener {

                   // Toast.makeText(applicationContext,((SplitString(UserUID+myView.txt_tweet.text))), Toast.LENGTH_SHORT).show()
                    var freezerItemsRef=myRef.child("posts")
                    freezerItemsRef.child((SplitString(UserUID+myView.txt_tweet.text))).addValueEventListener(object:ValueEventListener{

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                           try{
                              // Toast.makeText(applicationContext,"okay start", Toast.LENGTH_SHORT).show()
                               var td= p0.value as HashMap<String,Any>
                               for(key in td.keys){
                                   //the database has a key which is userId and the values inside it
                                 //  Toast.makeText(applicationContext,"im still going", Toast.LENGTH_SHORT).show()
                                   var userinfo=td[key] as String
                                   if(key == "email"){
                                      // Toast.makeText(applicationContext,"reached this far", Toast.LENGTH_SHORT).show()
                                      if(userinfo==myemail){
                                          // Toast.makeText(applicationContext,myemail, Toast.LENGTH_SHORT).show()
                                           freezerItemsRef.child((SplitString(UserUID+myView.txt_tweet.text))).removeValue()

                                           listNotesAdapter.removeAt(position)

                                       }


                                   }
                               }
                               //Toast.makeText(applicationContext,"the end", Toast.LENGTH_SHORT).show()
                               adapter!!.notifyDataSetChanged()
                           }catch(ex:Exception){}
                        }

                    })
                }
                //add a reply setonclicklistener...and have intents to the new page

                myRef.child("Users").child(mytweet.tweetPersonUID!!).addValueEventListener(object:ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        try {

                            var td= p0.value as HashMap<String,Any>
                            for(key in td.keys){
                                //the database has a key which is userId and the values inside it
                                var userinfo=td[key] as String
                                if(key.equals("ProfileImage")){
                                    Picasso.with(context).load(userinfo).into(myView.imageView) //TODO not loading pic
                                    imageProfile=userinfo
                                }else if (key.equals("username")){
                                    myView.UserViewReply.text = userinfo//it takes the personUID and display on the ticket
                                    userUsername=userinfo

                                }
 


                            }

                        }catch (ex:Exception){

                        }
                    }


                })

                return myView

            }



        }

        override fun getItem(position: Int): Any {
            //returning the position of the arraylist item
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size //it returns the size of the array it specifies how many times the getview should be called when the class is invoked
        }
    }
    fun SplitString(ID: String):String{
        val re = Regex("[^A-Za-z0-9 ]")

        return re.replace(ID, "")
    }
    fun LoadPost(){
        myRef.child("posts").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                try {

                    ListTweets.clear()
                    ListTweets.add(Ticket("0","him","add","email"))
                //    ListTweets.add(Ticket("0","him","url","ads"))
                    var td= p0.value as HashMap<String,Any>
                    for(key in td.keys){
                        //the database has a key which is userId and the values inside it
                        var post=td[key] as HashMap<String,Any>
                        ListTweets.add(Ticket(key,post["text"] as String,
                            post["userUID"] as String, post["email"].toString()
                        ))



                    }
                    adapter!!.notifyDataSetChanged()//it notifies the adapter there is a list change

                }catch (ex:Exception){

                }
            }


        })
    }
}
