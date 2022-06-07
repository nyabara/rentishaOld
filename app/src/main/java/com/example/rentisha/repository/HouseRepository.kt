package com.example.rentisha.repository

import android.telecom.Call
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.database.HouseDatabase
import com.example.rentisha.database.asDomainModel
import com.example.rentisha.domain.House
import com.example.rentisha.domain.Renter
import com.example.rentisha.network.RentishaNetwork
//import com.example.rentisha.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import javax.security.auth.callback.Callback

class HouseRepository(private val database: HouseDatabase) {
    suspend fun refreshHouses() {
        withContext(Dispatchers.IO) {
            //val houselist = RentishaNetwork.houses.getRentlist()
            val renters = RentishaNetwork.rentishaApi.getRenters()
            //database.houseDao.insertAll(houselist.asDatabaseModel())
            Log.d("renters",renters.toString())
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
     fun addRenter(renter: Renter){
         try{
            RentishaNetwork.rentishaApi.addRenters(renter).enqueue(object : retrofit2.Callback<Renter>{
                override fun onResponse(call: retrofit2.Call<Renter>, response: Response<Renter>) {
                    if (response.isSuccessful){
                        Log.d("response","success")
                    }else{
                        Log.d("response",response.message())
                    }
                }

                override fun onFailure(call: retrofit2.Call<Renter>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
         }catch (e:IOException){

         }

    }

    suspend fun getRenters(){
        withContext(Dispatchers.IO){
            val renters = RentishaNetwork.rentishaApi.getRenters()
        }
    }


}