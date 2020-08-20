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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SignUp: AppCompatActivity() {
    private var mAuth:FirebaseAuth?=null
    private var database= FirebaseDatabase.getInstance()
    private var myRef=database.reference//connect with the google database
   // var ListTweets=ArrayList<Ticket>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mAuth=FirebaseAuth.getInstance()//initializing the authentication
        imageView.setOnClickListener(View.OnClickListener {
            checkPermission()
        })
       // FirebaseMessaging.getInstance().subscribeToTopic("weather")//you have subscribed to that channel and
        // this means any news coming to that channel i want to listen to it

    }
    fun LoginToFireBase(email:String,password:String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                var currentUser = mAuth!!.currentUser
                if (currentUser != null) {
                    currentUser.sendEmailVerification()
                        .addOnCompleteListener(object : OnCompleteListener<Void> {

                            override fun onComplete(p0: Task<Void>) {
                                if (p0.isSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Verification email sent to " + currentUser.email,
                                        Toast.LENGTH_LONG
                                    ).show()
                                   // simpleProgressBar.visibility = View.VISIBLE
                                        SaveImageInFirebase()
                                        // mAuth!!.signOut()
                                        //
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Ohoo something went wrong",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    mAuth!!.signOut()
                                }
                            }
                        })
                }
                else{
                    Toast.makeText(applicationContext,"not successful",Toast.LENGTH_LONG).show()
                }
            }
    }
    fun SaveImageInFirebase(){
        var currentUser=mAuth!!.currentUser//information about user email
        val email:String= currentUser!!.email.toString()
        val storage=FirebaseStorage.getInstance()
        val storageRef=storage.getReferenceFromUrl("gs://covid-19-1a633.appspot.com")//the URL from storage
        val df=SimpleDateFormat("ddMMyyHHmmss")
        val dataobj=Date()
        val imagePath= SplitString(email)+"."+df.format(dataobj)+".jpg"//creating reference for my image,this means every image uploaded will have specific name
        val ImageRef=storageRef.child("images/"+imagePath)//have a folder named images and inside the folder add path
        // the images folder has to be created already on firebase storage
        imageView.isDrawingCacheEnabled=true
        imageView.buildDrawingCache()
        //take the image change to bitmapdrawable them to bitmap inorder to reduce the size of the image
        val drawable=imageView.drawable as BitmapDrawable
        val bitmap=drawable.bitmap
        val baos=ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)//compress the jpeg to 100 by 100 change to byte
        val data=baos.toByteArray()//getting the image
        val uploadTask=ImageRef.putBytes(data)//uploading the image
        uploadTask.addOnFailureListener{
            Toast.makeText(applicationContext,"fail to upload",Toast.LENGTH_LONG).show()
        }.addOnSuccessListener {taskSnapshot ->
            val DownloadURL=taskSnapshot.storage.downloadUrl.toString()//when done to get the downlodable url
            //steps in creating node for that user
            Toast.makeText(applicationContext,"starting to save values",Toast.LENGTH_LONG).show()
            myRef.child("Users").child(currentUser.uid).child("email").setValue(currentUser.email)
            myRef.child("Users").child(currentUser.uid).child("ProfileImage").setValue(DownloadURL)
            myRef.child("Users").child(currentUser.uid).child("username").setValue(UserViewReply.text.toString())
            mAuth!!.signOut()
            //the below method is fired after the person is logged in
           var intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)

        }
    }

   /* override fun onStart() {
        super.onStart()
        //starts if the user already logged in
        LoadTweets()
    }*/

    val READIMAGE:Int=253
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),READIMAGE)
                return
            }
        }
        loadImage()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            READIMAGE->{
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    loadImage()
                }else{
                    Toast.makeText(this,"Cannot access your image",Toast.LENGTH_LONG).show()
                }
            }
            else->super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    val PICK_IMAGE_CODE=123
    fun loadImage(){
        var intent=Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //Action_Pick cause you want to pick something, next arg is from the external storage
        startActivityForResult(intent,PICK_IMAGE_CODE)
    }

    //this function will fire automatically after startActivityForResult is done
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_CODE && data!=null && resultCode== Activity.RESULT_OK){
            val selectedImage=data.data//will have the information about the selected image
            val filePathColum= arrayOf(MediaStore.Images.Media.DATA)//the operation you wanted
            //the cursor gets the image you clicked hence sending the information to the application
            val cursor=contentResolver.query(selectedImage!!,filePathColum,null,null,null)
            cursor!!.moveToFirst()
            val columnIndex=cursor.getColumnIndex(filePathColum[0])
            val picturePath=cursor.getString(columnIndex)
            cursor.close()
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))

        }
    }
    fun signup(view:View){
        LoginToFireBase(EmailView.text.toString(),PassView.text.toString())
    }
    fun SplitString(email: String):String{
        val split=email.split("@")
        return split[0]
    }
}
