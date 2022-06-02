package com.example.rentisha.viewmodels

import androidx.lifecycle.*
import com.example.rentisha.BaseApplication
import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.database.HouseDatabase.Companion.getDatabase
import com.example.rentisha.domain.House
import com.example.rentisha.repository.HouseRepository
import kotlinx.coroutines.launch
import java.io.IOException
/**
 * RentishaViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during actiivty
 * or fragment lifecycle events.
 */
class RentishaViewModel(application: BaseApplication) : AndroidViewModel(application) {

    /**
     * The data source this ViewModel will fetch results from.
     */
    // A reference to the HouseRepository class
    private val houseRepository = HouseRepository(getDatabase(application))

    /**
     * A
     * list of houses that can be shown on the screen. This is private to avoid exposing a
     * way to set this value to observers.
     */
    val houselist = houseRepository.houses
    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    // return house clicked
    private val _house = MutableLiveData<House>()
    val house:LiveData<House> = _house

    /**
     * init{} is called immediately when this ViewModel is created.
     */

    init {
        // TODO: Replace with a call to the refreshDataFromRepository9) method
        refreshDataFromRepository()
    }

    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    // TODO: Replace with the refreshDataFromRepository() method
    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                houseRepository.refreshHouses()
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(houselist.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    fun onHouseClicked(house: House) {
        // set House object
        _house.value = house
    }

    fun getHouse(id:Long): LiveData<DatabaseHouse> {
        val houseDatabase = houseRepository.getHouse(id)
       return houseDatabase

    }

    fun addHouse(
        title: String,
        description: String,
        url: String,
        updated: String,
        name:String,
        isAvailable:Boolean,
        address: String,
        thumbnail: String
    ) {
        //val house = DatabaseHouse()
        val house = DatabaseHouse(
            name = name,
            address = address,
            isAvailable = isAvailable,
            description = description,
            title = title,
            url = url,
            updated = updated,
            thumbnail = thumbnail
        )

        // TODO: launch a coroutine and call the DAO method to add a Forageable to the database within it
        viewModelScope.launch {
            houseRepository.insertHouse(house)
        }

    }

    fun updateHouse(
        id:Long,
        title: String,
        description: String,
        url: String,
        updated: String,
        name:String,
        isAvailable:Boolean,
        address: String,
        thumbnail: String
    ) {
        val house = DatabaseHouse(
            id = id,
            title = title,
            name = name,
            address = address,
            isAvailable = isAvailable,
            description = description,
            url = url,
            updated = updated,
            thumbnail = thumbnail
        )
        viewModelScope.launch{

            houseRepository.updateHouse(house)
        }
    }

    fun deleteHouse(house: DatabaseHouse) {
        viewModelScope.launch {

            houseRepository.deleteHouse(house)
        }
    }

    fun isValidEntry(name: String, address: String): Boolean {
        return name.isNotBlank() && address.isNotBlank()
    }


    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: BaseApplication) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RentishaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RentishaViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
