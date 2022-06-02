package com.example.rentisha

import android.app.Application
import com.example.rentisha.database.HouseDatabase
import timber.log.Timber

class BaseApplication: Application() {

    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    val database: HouseDatabase by lazy { HouseDatabase.getDatabase(this) }
}