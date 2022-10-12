package com.example.rentisha.data

import android.os.AsyncTask
import android.util.Log
import androidx.paging.*
import com.example.rentisha.database.*
import com.example.rentisha.domain.ElectricityTypes
import com.example.rentisha.domain.House
import com.example.rentisha.domain.HouseTypes
import com.example.rentisha.api.*
//import com.example.rentisha.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class RentishaRepository( private val service:RentishaService, private val database: HouseDatabase) {
    suspend fun refreshApiData() {
        withContext(Dispatchers.IO) {

            try {
                val renters = RentishaNetwork.rentishaApi.getRenters()
                val houseTypes = RentishaNetwork.rentishaApi.getHouseTypes()

                Log.d("HouseTypes", houseTypes.toString())
                if (houseTypes != null) {
                    database.houseDao.insertAllHouseTypes(houseTypes.asDatabaseHouseTypesModel())
                }
                val electricityTypes = RentishaNetwork.rentishaApi.getElectricityTypes()
                if (electricityTypes != null) {
                    database.houseDao.insertAllElectricityTypes(electricityTypes.asDatabaseElectricityTypesModel())
                }
                Log.d("electricityTypes", electricityTypes.toString())


            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

    }

    fun getRenter(id: Int): retrofit2.Call<Renter> {
        return RentishaNetwork.rentishaApi.getRenter(id)
    }

    fun addRenter(renter: Renter) {
        try {
            RentishaNetwork.rentishaApi.addRenters(renter)
                .enqueue(object : retrofit2.Callback<Renter> {
                    override fun onResponse(
                        call: retrofit2.Call<Renter>,
                        response: Response<Renter>,
                    ) {
                        if (response.isSuccessful) {
                            Log.d("response", "success")
                        } else {
                            Log.d("response", response.message())
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<Renter>, t: Throwable) {
                        Log.d("failure", t.printStackTrace().toString())
                    }

                })
        } catch (e: IOException) {

        }

    }

    fun updateRenter(renter: Renter) {
        try {
            RentishaNetwork.rentishaApi.updateRenter(renter.id)
                .enqueue(object : retrofit2.Callback<Renter> {
                    override fun onResponse(call: Call<Renter>, response: Response<Renter>) {
                        if (response.isSuccessful) {
                            Log.d("updateRenterResponse", "updated Renter Successfully")
                        } else if (response.code() == 405) {
                            Log.d("", response.message())

                        } else {
                            Log.d("updateRenterResponse", response.message())
                        }
                    }

                    override fun onFailure(call: Call<Renter>, t: Throwable) {
                        Log.d("ResponseFailure", t.printStackTrace().toString())
                    }

                })
        } catch (e: IOException) {

        }

    }




    fun getSearchResultStream(query: String): Flow<PagingData<DatabaseHouse>> {

        Log.d("HouseSearch", "New query: $query")
        // appending '%' so we can allow other characters to be before and after the query string
        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { database.houseDao.houseByName(dbQuery) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = RentishaRemoteMediator(
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
    fun getHouseImages(houseId:Int):Flow<PagingData<List<DatabaseImage>>>{
        val pagingSourceFactory = {database.houseDao.getHouseWithImages1(houseId)}
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagidata -> pagidata.map { databaseimage -> databaseimage.images } }
    }

    fun getImages(houseId:Int): List<DatabaseImage> {

        return GeHouseImagesAsyncTask(database.houseDao).execute(houseId).get()

    }


    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }


    suspend fun insertHouse(house: DatabaseHouse) {
        withContext(Dispatchers.IO) {
            try {
                database.houseDao.insertHouse(house)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    fun addHouse(house: House) {
        try {
            RentishaNetwork.rentishaApi.addHouse(house).enqueue(object : retrofit2.Callback<House> {
                override fun onResponse(call: retrofit2.Call<House>, response: Response<House>) {
                    if (response.isSuccessful) {
                        Log.d("response", "success")
                    } else {
                        Log.d("response", response.message())
                    }
                }

                override fun onFailure(call: retrofit2.Call<House>, t: Throwable) {
                    Log.d("failure", t.printStackTrace().toString())
                }

            })
        } catch (e: IOException) {

        }

    }

    fun updateHouse(house: House) {
        try {
            RentishaNetwork.rentishaApi.updateHouse(house)
                .enqueue(object : retrofit2.Callback<House> {
                    override fun onResponse(
                        call: retrofit2.Call<House>,
                        response: Response<House>,
                    ) {
                        if (response.isSuccessful) {
                            Log.d("response", "success")
                        } else {
                            Log.d("response", response.message())
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<House>, t: Throwable) {
                        Log.d("failure", t.printStackTrace().toString())
                    }

                })
        } catch (e: IOException) {

        }

    }

    suspend fun deleteHouse(house: DatabaseHouse) {
        withContext(Dispatchers.IO) {
            database.houseDao.delete(house)
        }

    }

    fun getHouseTypesAsync(): List<HouseTypes> {

        return GetHouseTypeAsyncTask(database.houseDao).execute().get().asDomainHouseTypesModel()
    }

    fun getElectricityTypesAsync(): List<ElectricityTypes> {

        return GetElectricityTypeAsyncTask(database.houseDao).execute().get()
            .asDomainElectricityTypesModel()
    }

    fun getHouse(id: Int): DatabaseHouse {
        return GetHouseAsyncTask(database.houseDao).execute(id).get()
    }

}

private class GetHouseTypeAsyncTask(private val houseDao: HouseDao) :
    AsyncTask<Void, Void, List<DatabaseHouseTypes>>() {
    override fun doInBackground(vararg p0: Void?): List<DatabaseHouseTypes> {
        val houseDatabaseTypes = houseDao.getHouseTypes()
        return houseDatabaseTypes
    }

    override fun onPostExecute(result: List<DatabaseHouseTypes>?) {
        super.onPostExecute(result)

    }

}

private class GetElectricityTypeAsyncTask(private val houseDao: HouseDao) :
    AsyncTask<Void, Void, List<DatabaseElectricityTypes>>() {
    override fun doInBackground(vararg p0: Void?): List<DatabaseElectricityTypes> {
        val electricityDatabaseTypes = houseDao.getElectricityTypes()
        return electricityDatabaseTypes
    }

    override fun onPostExecute(result: List<DatabaseElectricityTypes>?) {
        super.onPostExecute(result)

    }

}

private class GeHouseImagesAsyncTask(private val houseDao: HouseDao) :
    AsyncTask<Int, Void, List<DatabaseImage>>() {
    override fun doInBackground(vararg houseId: Int?): List<DatabaseImage>{
        val houseimages = houseId[0]?.let { houseDao.getHouseWithImages(it).images }
        return houseimages!!
    }

    override fun onPostExecute(result: List<DatabaseImage>?) {
        super.onPostExecute(result)

    }

}

private class GetHouseAsyncTask(private val houseDao: HouseDao) :
    AsyncTask<Int, Void, DatabaseHouse>() {
    override fun doInBackground(vararg houseId: Int?): DatabaseHouse? {
        val databaseHouse = houseId[0]?.let { houseDao.getDatabaseHouse(it) }
        return databaseHouse
    }

    override fun onPostExecute(result: DatabaseHouse?) {
        super.onPostExecute(result)

    }

}

