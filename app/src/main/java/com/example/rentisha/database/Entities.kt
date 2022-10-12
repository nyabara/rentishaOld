package com.example.rentisha.database

import androidx.room.*
import com.example.rentisha.domain.*

@Entity
data class DatabaseRenter constructor(
    @PrimaryKey
    val id: Int,
    val firstname: String,
    val middlename: String,
    val surname: String,
    val email: String,
    val phone: String,
    val password: String,
    val status: Int)
fun DatabaseRenter.asDomainRenterModel()=Renter(id=id,
    firstname=firstname,
    middlename=middlename,
    surname=surname,
    email=email,
    phone=phone,
    password=password,
    status=status)
@Entity
data class DatabaseHouseTypes constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name:String,
    val status:Int,
    val description:String)

fun List<DatabaseHouseTypes>.asDomainHouseTypesModel():List<HouseTypes>{
    return map{
        HouseTypes(id = it.id,name = it.name,status = it.status,description = it.description)
    }
}
@Entity
data class DatabaseElectricityTypes constructor(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: Int,
    val description: String
)
fun List<DatabaseElectricityTypes>.asDomainElectricityTypesModel():List<ElectricityTypes>{
    return map {
        ElectricityTypes(id = it.id,name = it.name,status = it.status,description = it.description)
    }
}
@Entity
data class DatabaseHouse constructor(
    @PrimaryKey
    val houseId :Int,
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
    val house_type_id:Int,
    val electricity_type_id:Int,
    val own_compound:Boolean,
    val status: Int)
@Entity
data class DatabaseImage constructor(
    @PrimaryKey
    val imageId: Int,
    val status: Int,
    val image_url:String,
    val house_id:Int
)

fun List<DatabaseImage>.asDomainImageModel():List<Image>{
    return map {
        Image(
            id = it.imageId,
            status = it.status,
            image_url = it.image_url,
            house_id = it.house_id
        )
    }
}

@Entity(primaryKeys = ["houseId", "imageId"])
data class HouseImageCrossRef(
    val houseId: Int,
    val imageId: Int
)

data class HousewithImages(
    @Embedded val house: DatabaseHouse,
    @Relation(
        parentColumn = "houseId",
        entityColumn = "imageId",
        associateBy = Junction(HouseImageCrossRef::class)
    )
    val images: List<DatabaseImage>
)

fun List<DatabaseHouse>.asDomainModel(): List<House> {
    return map {
        House(
            car_booking = it.car_booking,
            electricity_description = it.electricity_description,
            electricity_name = it.electricity_name,
            has_watchman = it.has_watchman,
             has_water = it.has_water,
            house_type = it.house_type,
            latitude = it.latitude,
            longitude = it.longitude,
            other_description = it.other_description,
            renter_id = it.renter_id,
            status = it.status,
            house_type_id =it.house_type_id,
            electricity_type_id = it.electricity_type_id,
            own_compound=it.own_compound,
            id = it.houseId
        )
    }

}
