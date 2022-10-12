package com.example.rentisha.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface HouseDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertRenter(renter: DatabaseRenter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllHouseTypes(houseTypes: List<DatabaseHouseTypes>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHouseImageCrossRefs(houseImageCrossRefs: List<HouseImageCrossRef>)

    @Query("select * from DatabaseHouseTypes")
    fun getHouseTypes(): List<DatabaseHouseTypes>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllElectricityTypes(electricityTypes: List<DatabaseElectricityTypes>)
    @Query("select * from DatabaseElectricityTypes")
    fun getElectricityTypes():List<DatabaseElectricityTypes>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( houses: List<DatabaseHouse>)
    @Query(
        "SELECT * FROM DatabaseHouse WHERE " +
                "house_type LIKE :queryString OR other_description LIKE :queryString " +
                "ORDER BY house_type ASC"
    )
    fun houseByName(queryString: String): PagingSource<Int, DatabaseHouse>

    @Transaction
    @Query("SELECT * FROM DatabaseHouse WHERE houseId = :houseId")
    fun getHouseWithImages(houseId: Int):  HousewithImages

    @Transaction
    @Query("SELECT * FROM DatabaseHouse WHERE houseId = :houseId")
    fun getHouseWithImages1(houseId: Int):  PagingSource<Int,HousewithImages>





    @Query("SELECT * from databasehouse WHERE houseId = :id")
    fun getDatabaseHouse(id: Int): DatabaseHouse

    @Query("DELETE FROM databasehouse")
    suspend fun clearHouses()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllImages( images: List<DatabaseImage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHouse(house: DatabaseHouse)

    @Update
    fun update(house: DatabaseHouse)

    @Delete
    fun delete(house: DatabaseHouse)
}



@Database(entities = [DatabaseHouse::class,DatabaseImage::class,DatabaseHouseTypes::class,DatabaseElectricityTypes::class,HouseImageCrossRef::class,RemoteKeys::class], version = 2)
abstract class HouseDatabase: RoomDatabase() {
    abstract val houseDao: HouseDao
    abstract val remoteKeysDao:RemoteKeysDao
    companion object{
        @Volatile
        private lateinit var INSTANCE: HouseDatabase
        fun getDatabase(context: Context): HouseDatabase {
            synchronized(HouseDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        HouseDatabase::class.java,
                        "houses")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}


