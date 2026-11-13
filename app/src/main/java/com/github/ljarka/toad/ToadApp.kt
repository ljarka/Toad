package com.github.ljarka.toad

import android.app.Application
import com.github.ljarka.toad.data.species.SpeciesSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ToadApp : Application() {

    @Inject lateinit var speciesSeeder: SpeciesSeeder

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            speciesSeeder.seedIfEmpty()
        }
    }
}
