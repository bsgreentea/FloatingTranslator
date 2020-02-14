package com.greentea.floatingtranslator

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.greentea.floatingtranslator.Service.FloatingService

class MainActivity : AppCompatActivity() {

    private val OVERLAY_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse(" package:$packageName")
                )
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
            } else {
                startService()
            }
        } else {
            startService()
        }
    }

    fun startService(){
        val floatingService = Intent(applicationContext, FloatingService::class.java)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startService(floatingService)
        }
    }
}
