package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Login: AppCompatActivity() {
    private var mAuth:FirebaseAuth?=null
    private var database= FirebaseDatabase.getInstance()
    private var myRef=database.reference//connect with the google database
    //var ListTweets=ArrayList<Ticket>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth=FirebaseAuth.getInstance()//initializing the authentication

        // FirebaseMessaging.getInstance().subscribeToTopic("weather")//you have subscribed to that channel and
        // this means any news coming to that channel i want to listen to it

    }
    fun LoginToFireBase(email:String,password:String){
        mAuth!!.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){task->
            var currentUser = mAuth!!.currentUser
            if(task.isSuccessful){
                if (currentUser != null) {
                    if (currentUser.isEmailVerified) {
                        Toast.makeText(applicationContext,"Succesful login",Toast.LENGTH_LONG).show()
                        LoadTweets()

                    }
                }


                }
                else{
                    Toast.makeText(applicationContext,"not succesful",Toast.LENGTH_LONG).show()
                }
            }
    }

    fun LoadTweets() {
        var currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            //save in database
            var intent = Intent(this, layout::class.java)
            intent.putExtra("email", currentUser.email)
           // intent.putExtra("username", UserView.text.toString())
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
        }
    }

   override fun onStart() {
        super.onStart()
        //starts if the user already logged in
        LoadTweets()
    }

    fun buLogin(view:View){
        LoginToFireBase(emailView.text.toString(),passwordView.text.toString())
    }
    fun newuser(view:View){
        var intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }
    fun reset(view:View){
        //todo have a new password
    }

}
