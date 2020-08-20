package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_reply_main.*
import kotlinx.android.synthetic.main.add_notes.view.*
import kotlinx.android.synthetic.main.reply_main_ticket.view.*
import kotlinx.android.synthetic.main.tickets.view.*
import java.util.*
import kotlin.collections.ArrayList

class reply_main : AppCompatActivity() {
    var ListTweets = ArrayList<Ticket>()
    var adapter: reply_main.MyTweetAdapter? = null
    var date: String? = null
    var PrevText: String? = null
    var imageProfile: String? = null
    var userUsername: String? = null
    var UserUID: String? = null
    var myemail:String?=null
    var contexts: Context? = null
    private var mAuth: FirebaseAuth? = null
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference//connect with the google database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply_main)
        mAuth=FirebaseAuth.getInstance()//initializing the authentication
        var b: Bundle = intent.extras!!
        date = b.getString("date")
        PrevText = b.getString("PrevText")
        imageProfile = b.getString("profile")
        userUsername = b.getString("username")
        UserUID=b.getString("uid")
        myemail=b.getString("email")
        dateView.text = date
        txt_tweet.text = PrevText
        UserViewReply.text = userUsername
        Picasso.with(contexts).load(imageProfile).into(imageView)

       // ListTweets.add(Ticket("0", "him", "url", "add"))//dummy data
        adapter = MyTweetAdapter(this, ListTweets)
        listReplyView.adapter = adapter

        LoadPost()


    } //end of class

    inner class MyTweetAdapter : BaseAdapter {
        var listNotesAdapter = ArrayList<Ticket>()
        var context: Context? = null


        constructor(context: Context, listNotesAdapter: ArrayList<Ticket>) : super() {
            this.listNotesAdapter = listNotesAdapter
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
           // var currentUser = mAuth!!.currentUser//information about user email
            var mytweet = listNotesAdapter[position]
            //var keyd: String? = myRef.child("posts").push().key
            val time = Calendar.getInstance().time
            //load add ticket
            var myView = layoutInflater.inflate(R.layout.reply_main_ticket, null)
            sendMessageButton.setOnClickListener(View.OnClickListener {
                //upload to server,creating node with push assigning a unique ID
                Toast.makeText(applicationContext,("Second step complete"), Toast.LENGTH_SHORT).show()
                if (message.text.toString().trim().isNotEmpty()) {
                    // myView.txt_reply.text = message.text
                    // var hope=time

                    myRef.child("reply").child((SplitString(UserUID.toString()))) .setValue(PostInfo(
                        UserUID.toString(), message.text.toString(), myemail!!))
                    myView.txt_reply.setText(message.text)
                    message.setText("")
                    myView.dateReplyView.text= time.toString()
                    Toast.makeText(applicationContext,("Third step complete"), Toast.LENGTH_SHORT).show()
                    myRef.child("Users").child(UserUID!!.toString()).addValueEventListener(object:ValueEventListener{
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
                                       // Toast.makeText(applicationContext,("fourth step complete"), Toast.LENGTH_SHORT).show()
                                        Picasso.with(context).load(userinfo).into(myView.imageViewReply) //TODO not loading pic
                                    }else if (key.equals("username")){
                                        myView.UserReply.text = userinfo//it takes the personUID and display on the ticket

                                    }



                                }

                            }catch (ex:Exception){
                                Toast.makeText(applicationContext,("something went wrong"), Toast.LENGTH_SHORT).show()
                            }
                        }


                    })
                }
            })
            //--------------------
            myView.dView.setOnClickListener {

                // Toast.makeText(applicationContext,((SplitString(UserUID+myView.txt_tweet.text))), Toast.LENGTH_SHORT).show()
                var freezerItemsRef = myRef.child("reply")
                freezerItemsRef.child((SplitString(mytweet.tweetPersonUID!! + myView.txt_reply.text)))
                    .addValueEventListener(object : ValueEventListener {

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            try {
                                // Toast.makeText(applicationContext,"okay start", Toast.LENGTH_SHORT).show()
                                var td = p0.value as HashMap<String, Any>
                                for (key in td.keys) {
                                    //the database has a key which is userId and the values inside it
                                    //  Toast.makeText(applicationContext,"im still going", Toast.LENGTH_SHORT).show()
                                    var userinfo = td[key] as String
                                    if (key == "email") {
                                        // Toast.makeText(applicationContext,"reached this far", Toast.LENGTH_SHORT).show()
                                        if (userinfo == myemail) {
                                            // Toast.makeText(applicationContext,myemail, Toast.LENGTH_SHORT).show()
                                            freezerItemsRef.child((SplitString(UserUID+ myView.txt_reply.text)))
                                                .removeValue()

                                            listNotesAdapter.removeAt(position)

                                        }


                                    }
                                }
                                //Toast.makeText(applicationContext,"the end", Toast.LENGTH_SHORT).show()
                                adapter!!.notifyDataSetChanged()
                            } catch (ex: Exception) {

                            }
                        }

                    })
            }
            //add a reply setonclicklistener...and have intents to the new page

            return myView

        //-------------------

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
       // var currentUser=mAuth!!.currentUser//information about user email
        myRef.child("reply").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                try {
                    Toast.makeText(applicationContext,("first step complete"), Toast.LENGTH_SHORT).show()
                    //ListTweets.clear()
               //     ListTweets.add(Ticket("0","him","add","email"))
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
