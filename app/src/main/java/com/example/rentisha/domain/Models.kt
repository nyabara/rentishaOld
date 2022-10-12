package com.example.rentisha.domain

import com.example.rentisha.util.smartTruncate
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class Renter(   val id: Int=0,
                     val firstname: String,
                     val middlename: String,
                     val surname: String,
                     val email: String,
                     val phone: String,
                     val password: String,
                     val status: Int)

data class HouseTypes(val id: Int = 0,
                      val name:String,
                      val status:Int,
                      val description:String){
    override fun toString(): String {
        return name
    }
}

data class ElectricityTypes(val id: Int=0,
                            val name:String,
                            val status:Int,
                            val description:String){
    override fun toString(): String {
        return name
    }
}

/**
 * Houses represent a House that can be rented.
 */
data class House(  val id :Int=0,
                   val electricity_type_id:Int,
                   val house_type_id:Int,
                   val car_booking: Boolean,
                   val electricity_description: String,
                   val electricity_name: String,
                   val has_watchman: Boolean,
                   val has_water: Boolean,
                   val house_type: String,
                   val latitude: Double,
                   val longitude: Double,
                   val other_description: String?,
                   val renter_id: Int,
                   val own_compound:Boolean,
                   val status: Int) {

    /**
     * Short description is used for displaying truncated descriptions in the UI
     */
    val shortDescription: String?
        get() = other_description?.smartTruncate(200)

}

data class Image(val id: Int,
                  val status: Int,
                  val image_url:String,
                  val house_id:Int)
