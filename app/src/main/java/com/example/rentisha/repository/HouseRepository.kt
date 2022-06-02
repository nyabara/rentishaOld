package com.example.rentisha.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.database.HouseDatabase
import com.example.rentisha.database.asDomainModel
import com.example.rentisha.domain.House
import com.example.rentisha.network.RentishaNetwork
//import com.example.rentisha.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HouseRepository(private val database: HouseDatabase) {
    suspend fun refreshHouses() {
        withContext(Dispatchers.IO) {
            val houselist = RentishaNetwork.houses.getRentlist()
            //database.houseDao.insertAll(houselist.asDatabaseModel())
        }

    }

    val houses: LiveData<List<House>> = Transformations.map(database.houseDao.getHouses()) {
        it.asDomainModel()
    }
    fun getHouse(id:Long):LiveData<DatabaseHouse>{
        return database.houseDao.getItem(id)
    }
    suspend fun insertHouse(house: DatabaseHouse){
        withContext(Dispatchers.IO){
            database.houseDao.insertHouse(house)
        }

    }
    suspend fun updateHouse(house: DatabaseHouse){
        withContext(Dispatchers.IO){
            database.houseDao.update(house)
        }

    }
    suspend fun deleteHouse(house: DatabaseHouse){
        withContext(Dispatchers.IO){
            database.houseDao.delete(house)
        }

    }


}