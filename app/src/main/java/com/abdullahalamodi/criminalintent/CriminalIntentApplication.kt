package com.abdullahalamodi.criminalintent

import android.app.Application
import com.abdullahalamodi.criminalintent.database.CrimeRepository

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(applicationContext); //applicationContext for long object life
    }
}