package com.example.rentisha.domain

import com.example.rentisha.util.smartTruncate
import com.squareup.moshi.Json

/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 * @see database for objects that are mapped to the database
 * @see network for objects that parse or prepare network calls
 */

/**
 * Houses represent a House that can be rented.
 */
data class House(val id:Long,
                 val title: String,
                 val description: String,
                 val url: String,
                 val updated: String,
                 val name:String,
                 val isAvailable:Boolean,
                 val address: String,
                 val thumbnail: String) {

    /**
     * Short description is used for displaying truncated descriptions in the UI
     */
    val shortDescription: String
        get() = description.smartTruncate(200)
}

data class Renter(
    @Json(name = "firstname") val firstname: String,
    @Json(name = "middlename") val middlename: String,
    @Json(name = "surname") val surname: String,
    @Json(name = "email") val email: String,
    @Json(name = "phone") val phone: String,
    @Json (name = "password") val password: String
)