package com.greentea.floatingtranslator.Service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.greentea.floatingtranslator.FloatingView.FloatingHeadWindow

class FloatingService : Service() {

    lateinit var floatingHeadWindow: FloatingHeadWindow
    private var mBinder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        init()
        return super.onStartCommand(intent, flags, startId)
    }

    fun init(){
        if(!::floatingHeadWindow.isInitialized){
            floatingHeadWindow = FloatingHeadWindow(applicationContext).apply {
                create()
                createLayoutParams()
                show()
            }
        }
    }

    inner class LocalBinder: Binder(){
        fun getService(): FloatingService{
            return this@FloatingService
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
