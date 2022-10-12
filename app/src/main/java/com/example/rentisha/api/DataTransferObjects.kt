package com.example.rentisha.api

import com.example.rentisha.database.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Renter(
    val id: Int=0,
    @SerializedName("firstname")
    @Expose
    val firstname: String,
    @SerializedName("middlename")
    @Expose
    val middlename: String,
    @SerializedName("surname")
    @Expose
    val surname: String,
    @SerializedName("email")
    @Expose
    val email: String,
    @SerializedName("phone")
    @Expose
    val phone: String,
    @SerializedName("password")
    @Expose
    val password: String,
    @SerializedName("status")
    @Expose
    val status: Int
)
fun Renter.asDatabaseRenterModel()=DatabaseRenter(id=id,
    firstname=firstname,
    middlename=middlename,
    surname=surname,
    email=email,
    phone=phone,
    password=password,
    status=status)


data class NetworkHouseTypes(val id: Int = 0,
                             val name:String,
                             val status:Int,
                             val description:String)

fun List<NetworkHouseTypes>.asDatabaseHouseTypesModel():List<DatabaseHouseTypes>{
    return map {
        DatabaseHouseTypes(id = it.id,name = it.name,status = it.status,description = it.description)
    }
}
data class NetworkElectricityTypes(val id: Int=0,
                                   val name: String,
                                   val status: Int,
                                   val description: String)
fun List<NetworkElectricityTypes>.asDatabaseElectricityTypesModel():List<DatabaseElectricityTypes>{
    return map {
        DatabaseElectricityTypes(id = it.id,name = it.name,status = it.status,description = it.description)
    }
}

data class NetworkHouse(
    @SerializedName("car_parking")
    @Expose
    val car_booking: Boolean,
    @SerializedName("electricity_description")
    @Expose
    val electricity_description: String,
    @SerializedName("electricity_name")
    @Expose
    val electricity_name: String,
    @SerializedName("has_watchman")
    @Expose
    val has_watchman: Boolean,
    @SerializedName("has_water")
    @Expose
    val has_water: Boolean,
    @SerializedName("house_type")
    @Expose
    val house_type: String,
    @SerializedName("id")
    @Expose
    val id :Int,
    @SerializedName("images")
    @Expose
    val images: List<Image> = emptyList(),
    @SerializedName("latitude")
    @Expose
    val latitude: Double,
    @SerializedName("longitude")
    @Expose
    val longitude: Double,
    @SerializedName("other_description")
    @Expose
    val other_description: String?,
    @SerializedName("own_compound")
    @Expose
    val own_compound: Boolean,
    @SerializedName("renter_id")
    @Expose
    val renter_id: Int,
    @SerializedName("house_type_id")
    @Expose
    val house_type_id:Int,
    @SerializedName("electricity_type_id")
    @Expose
    val electricity_type_id:Int,
    @SerializedName("status")
    @Expose
    val status: Int)

fun List<NetworkHouse>.asDatabaseModel(): List<DatabaseHouse> {
    return map {
        DatabaseHouse(
            car_booking = it.car_booking,
            electricity_description = it.electricity_description,
            electricity_name = it.electricity_name,
            has_watchman = it.has_watchman,
            has_water = it.has_water,
            house_type = it.house_type,
            houseId = it.id,
            latitude = it.latitude,
            longitude = it.longitude,
            other_description = it.other_description,
            renter_id = it.renter_id,
            house_type_id =it.house_type_id,
            electricity_type_id = it.electricity_type_id,
            own_compound = it.own_compound,
            status = it.status
        )
    }
}

data class Image(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("status")
    @Expose
    val status: Int,
    @SerializedName("image_url")
    @Expose
    val image_url:String,
    val house_id:Int

)
fun List<Image>.asDatabaseImageModel():List<DatabaseImage>{
    return map{
        DatabaseImage(
            imageId = it.id,
            status = it.status,
            image_url = it.image_url,
            house_id = it.house_id
        )
    }
}

fun List<Image>.asHouseImageCrossRef():List<HouseImageCrossRef>{
    return map{
        HouseImageCrossRef(houseId = it.house_id, imageId = it.id)
    }
}

