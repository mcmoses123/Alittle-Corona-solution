package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class forum : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)
    }
    fun login(view:View){
        var intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
    fun signup(view:View){
        var intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }
}
