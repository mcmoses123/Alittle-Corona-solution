package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.SyncStateContract
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import com.google.android.youtube.player.*
import com.google.android.youtube.player.YouTubePlayerSupportFragment.newInstance
import kotlinx.android.synthetic.main.activity_prevention.*
import kotlinx.android.synthetic.main.activity_symptoms.*

class prevention : AppCompatActivity(), YouTubePlayer.OnInitializedListener {
    companion object {//the object to access all our files
    var Video_id:String="9Ay4u7OYOhA"
        var Youtube_API_Key:String="AIzaSyBO1Bno3uJTYzqPKFqDJU5yAizjHUUkZ4o"
    }
    //var listPoints=ArrayList<String>()
    //lateinit var youtubePlayerInit:YouTubePlayer.OnInitializedListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prevention)
        val youTubePlayerFragment = supportFragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerSupportFragment?
        btnPlay.setOnClickListener(View.OnClickListener{ v ->
            youTubePlayerFragment?.initialize(Youtube_API_Key,this)})


    }

    override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        p1: YouTubePlayer?,
        p2: Boolean
    ) {
        p1?.loadVideo(Video_id)
    }

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?
    ) {
        Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_LONG).show()
    }


}
