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
import com.greentea.floatingtranslator.Utils.Constant

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startFloatingWidgetMaybe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constant.OVERLAY_PERMISSION_REQUEST_CODE && isDrawOverlayAllowed()){
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
        startActivityForResult(intent, Constant.OVERLAY_PERMISSION_REQUEST_CODE)
    }

    private fun isDrawOverlayAllowed(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)
}
