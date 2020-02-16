package com.greentea.floatingtranslator

import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.greentea.floatingtranslator.Service.FloatingService

class MainActivity : AppCompatActivity() {

    companion object{
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startFloatingWidgetMaybe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == OVERLAY_PERMISSION_REQUEST_CODE && isDrawOverlayAllowed()){
            Toast.makeText(this, "Granted Permission", Toast.LENGTH_SHORT).show()
            startFloatingWidgetMaybe()
        }
    }

    override fun onDestroy() {
        stopService(Intent(this, FloatingService::class.java))
        Toast.makeText(this, "Stopped widget", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

    private fun startFloatingWidgetMaybe(){
        if(isDrawOverlayAllowed()){
            startService(Intent(this@MainActivity, FloatingService::class.java))
            return
        }
        requestForDrawingOverAppsPermission()
    }

    private fun requestForDrawingOverAppsPermission(){
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }

    private fun isDrawOverlayAllowed(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)
}
