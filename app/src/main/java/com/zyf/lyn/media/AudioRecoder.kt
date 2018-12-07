package com.zyf.lyn.media

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.VideoView
import kotlinx.android.synthetic.main.activity_audio_recoder.*
import java.io.IOException

class AudioRecoder : AppCompatActivity() {


    var mVideoPlayer: VideoView? = null
    var mFileName = ""

    var mRecorder: MediaRecorder? = null
    var mPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_recoder)
        mFileName = "${externalCacheDir.absolutePath}/audiorecordtest.3gp"
        bussize()
    }

    private fun bussize() {
        btn_start.setOnClickListener {
            permission()
        }

        btn_stop.setOnClickListener {
            stopRecording()
        }

        btn_play_start.setOnClickListener {
            playing()
        }

        btn_play_stop.setOnClickListener {
            stoping()
        }
    }

    private fun stoping() {
        mPlayer?.release()
        mPlayer = null

    }

    private fun playing() {
        mPlayer = MediaPlayer().apply {
            try {
                setDataSource(mFileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("", "play prepare failed")
            }
        }

    }

    fun permission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startRecorder()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE), 1111);
        }


    }

    private fun startRecorder() {
        mRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(mFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e("startRecorder", "prepare failed")
            }
            start()
        }
    }

    fun stopRecording() {
        mRecorder?.apply {
            stop()
            release()
        }
        mRecorder = null
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1111 -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startRecorder()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mRecorder?.release()
        mRecorder = null
        mPlayer?.release()
        mPlayer = null
    }
}
