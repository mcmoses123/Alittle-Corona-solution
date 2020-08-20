package com.example.myapplication

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cases.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class cases : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cases)
    }
    fun GenerateData(view:View){
        var Country=etCountryName.text.toString()
        val url="https://api.covid19api.com/summary"
        MyAsyncTask().execute(url)//sending to the assync class
    }
    inner class MyAsyncTask: AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            try{
                var url=URL(params[0])
                var urlConnect=url.openConnection() as HttpURLConnection
                var inString=ConvertStreamToString(urlConnect.inputStream)//converting the results into string
                publishProgress(inString)//cant access the UI ,needs to be sent to OnProgressUpdate

            }catch (ex:Exception){

            }
            return ""
        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
                var json=JSONObject(values[0])
                if(etCountryName.text.toString().toLowerCase()=="global"){
                    val cal = Calendar.getInstance()
                    var global=json.getJSONObject("Global")
                    var NewConfirmed=global.getString("NewConfirmed")
                    var TotalConfirmed=global.getString("TotalConfirmed")
                    var NewDeaths=global.getString("NewDeaths")
                    var TotalDeaths=global.getString("TotalDeaths")
                    var NewRecovered=global.getString("NewRecovered")
                    var TotalRecovered=global.getString("TotalRecovered")

                    NcTextview.text="New Confirmed: "+NewConfirmed
                    TcTextview.text="Total Confirmed: "+TotalConfirmed
                    NdTextview.text="New Deaths: "+NewDeaths
                    TdTextview.text="Total Deaths: "+TotalDeaths
                    NrTextview.text="New Recovered: "+NewRecovered
                    TrTextview.text="Total Recovered: "+TotalRecovered
                    DTextview.text="Date Recorded: "+cal.time.toString()

                }
                else{
                    var Countries=json.getJSONArray("Countries")
                    try {
                        var count=0
                        do {
                            var Counters = Countries.getJSONObject(count)
                            var slug = Counters.getString("Slug")
                            if (slug.toLowerCase() == (etCountryName.text.toString().toLowerCase())) {
                                var NewC = Counters.getString("NewConfirmed")
                                var TotalC = Counters.getString("TotalConfirmed")
                                var NewD = Counters.getString("NewDeaths")
                                var TotalD = Counters.getString("TotalDeaths")
                                var NewR = Counters.getString("NewRecovered")
                                var TotalR = Counters.getString("TotalRecovered")
                                var Date=Counters.getString("Date")

                                NcTextview.text = "New Confirmed: $NewC"
                                TcTextview.text = "Total Confirmed: "+TotalC
                                NdTextview.text = "New Deaths: "+NewD
                                TdTextview.text = "Total Deaths: "+TotalD
                                NrTextview.text = "New Recovered: "+NewR
                                TrTextview.text = "Total Recovered: "+TotalR
                                DTextview.text = "Date Recorded: "+Date
                                count=-2
                            }
                            count++
                        }while ((count<Countries.length())||(count!=-1))
                    }catch (ex:Exception){

                    }
                }


            }catch (ex:Exception){

            }
        }

        override fun onPostExecute(result: String?) {
        }

        override fun onPreExecute() {

        }
        fun ConvertStreamToString(inputStream: InputStream):String{
            var bufferReader=BufferedReader(InputStreamReader(inputStream))
            var line:String
            var AllString:String=""
            try{
                do{
                    line=bufferReader.readLine()
                    if(line!=null){
                        AllString+=line
                    }
                }while(line!=null)
                inputStream.close()

            }catch(ex:Exception){

            }
            return AllString
        }

    }

}
