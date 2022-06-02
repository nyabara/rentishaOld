package com.example.rentisha.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rentisha.domain.House

/**
 * Database entities go in this file. These are responsible for reading and writing from the
 * database.
 */


/**
 * DatabaseVideo represents a video entity in the database.
 */
@Entity
data class DatabaseHouse constructor(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val url: String,
    val updated: String,
    val title: String,
    val description: String,
    val name:String,
    @ColumnInfo(name = "is_available")
    val isAvailable:Boolean,
    val address: String,
    val thumbnail: String)


/**
 * Map DatabaseHouses to domain entities
 */
fun List<DatabaseHouse>.asDomainModel(): List<House> {
    return map {
        House(
            url = it.url,
            title = it.title,
            description = it.description,
            updated = it.updated,
            isAvailable = it.isAvailable,
            address = it.address,
            name = it.name,
            thumbnail = it.thumbnail,
            id = it.id
        )
    }

}
