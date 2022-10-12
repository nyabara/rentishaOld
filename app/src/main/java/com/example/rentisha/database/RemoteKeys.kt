package com.example.rentisha.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val houseId: Int,
    val prevKey:Int?,
    val nextKey:Int?) {
}