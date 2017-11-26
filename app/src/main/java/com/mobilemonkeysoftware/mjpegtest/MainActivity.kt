package com.mobilemonkeysoftware.mjpegtest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.math.BigInteger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Observable
                .create<ByteArray> {
                    try {

                        val response = OkHttpClient()
                                .newCall(
                                        Request.Builder()
                                                .url("http://iris.not.iac.es/axis-cgi/mjpg/video.cgi?resolution=320x240")
                                                .addHeader("Cache-Control", "no-cache")
                                                .build())
                                .execute()
                        Log.i("XXX", response.toString())
                        var bis = BufferedInputStream(response.body()?.source()?.inputStream())
                        do {
                            var array = ByteArray(64)
                            bis.read(array)
                            Log.i("XXX", BigInteger(1, array).toString(16))
                            it.onNext(ByteArray(0))
                        } while (true)


//                        // https://github.com/perthcpe23/android-mjpeg-view/blob/master/mjpegviewer/src/main/java/com/longdo/mjpegviewer/MjpegView.java
//                        var con = URL("http://iris.not.iac.es/axis-cgi/mjpg/video.cgi?resolution=320x240").openConnection() as HttpURLConnection
//                        con.doInput = true
//                        con.connect()
//                        val bis = BufferedInputStream(con.inputStream)
//                        do {
//                            var array = ByteArray(64)
//                            bis.read(array)
//                            Log.i("XXX", BigInteger(1, array).toString(16))
//                        } while (true)
                        it.onComplete()
                    } catch (e: Exception) {
                        it.onError(e)
                    }
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }
}
