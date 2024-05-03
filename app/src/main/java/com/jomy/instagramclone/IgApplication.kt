package com.jomy.instagramclone

import android.app.Application
import android.os.Build
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IgApplication : Application(){

    override fun onCreate() {
        super.onCreate()

                //FirebaseApp.initializeApp(this)
            }
        }
