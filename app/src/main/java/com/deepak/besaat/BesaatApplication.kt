package com.deepak.besaat

import AppModule
import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import org.koin.core.context.startKoin


class BesaatApplication:Application() {
    companion object{

    }


    override fun onCreate() {
        super.onCreate()
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        startKoin { modules(AppModule.appModule(this@BesaatApplication)) }
    }
}