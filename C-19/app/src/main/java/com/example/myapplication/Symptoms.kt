package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import kotlinx.android.synthetic.main.activity_prevention.*
import kotlinx.android.synthetic.main.activity_symptoms.*

class Symptoms :AppCompatActivity(), YouTubePlayer.OnInitializedListener {

    companion object {//the object to access all our files
    var Video_id:String="YAc9NabBJzg"
        var Youtube_API_Key:String="AIzaSyBO1Bno3uJTYzqPKFqDJU5yAizjHUUkZ4o"
    }
   // lateinit var youtubePlayerInit:YouTubePlayer.OnInitializedListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptoms)
        val youTubePlayerFragment = supportFragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerSupportFragment?
        buPlaySymptom.setOnClickListener(View.OnClickListener{ v ->
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
