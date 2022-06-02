package com.example.rentisha.network

import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.domain.House
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 *
 * @see domain package for
 */

/**
 * VideoHolder holds a list of Videos.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "videos": []
 * }
 */
@JsonClass(generateAdapter = true)
data class NetworkHouseContainer(val videos: List<NetworkHouse>)

/**
 * Videos represent a devbyte that can be played.
 */
@JsonClass(generateAdapter = true)
data class NetworkHouse(
    val title: String,
    val description: String,
    val url: String,
    val updated: String,
    val thumbnail: String,
    val closedCaptions: String?)

/**
 * Convert Network results to database objects
 */
//fun NetworkHouseContainer.asDomainModel(): List<House> {
//    return videos.map {
//        House(
//            title = it.title,
//            description = it.description,
//            url = it.url,
//            updated = it.updated,
//            thumbnail = it.thumbnail)
//    }
//}
//
//
///**
// * Convert Network results to database objects
// */
//fun NetworkHouseContainer.asDatabaseModel(): List<DatabaseHouse> {
//    return videos.map {
//        DatabaseHouse(
//            title = it.title,
//            description = it.description,
//            url = it.url,
//            updated = it.updated,
//            thumbnail = it.thumbnail)
//    }
//}
