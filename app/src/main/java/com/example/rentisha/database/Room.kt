package com.example.rentisha.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HouseDao {
    @Query("select * from databasehouse")
    fun getHouses(): LiveData<List<DatabaseHouse>>

    @Query("SELECT * from databasehouse WHERE id = :id")
    fun getItem(id:Long): LiveData<DatabaseHouse>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll( houses: List<DatabaseHouse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHouse(house: DatabaseHouse)

    @Update
    fun update(house: DatabaseHouse)

    @Delete
    fun delete(house: DatabaseHouse)
}



@Database(entities = [DatabaseHouse::class], version = 2)
abstract class HouseDatabase: RoomDatabase() {
    abstract val houseDao: HouseDao
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


