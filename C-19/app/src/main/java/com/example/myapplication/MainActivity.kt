package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun prevent(view:View){
      //  var context:Context?=null
        var intent  = Intent(this,prevention::class.java)
        startActivity(intent)
    }
    fun symptom(view:View){
        //  var context:Context?=null
        var intent  = Intent(this,Symptoms::class.java)
        startActivity(intent)
    }
    fun cases(view:View){
        var intent  = Intent(this,cases::class.java)
        startActivity(intent)
    }
    fun forum(view:View){
        var intent  = Intent(this,forum::class.java)
        startActivity(intent)
    }

}
