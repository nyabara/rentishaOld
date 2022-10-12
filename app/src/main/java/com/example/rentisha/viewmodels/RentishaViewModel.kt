package com.example.rentisha.viewmodels

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.savedstate.SavedStateRegistryOwner
import androidx.work.*
import com.example.rentisha.BaseApplication
import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.database.HouseDatabase.Companion.getDatabase
import com.example.rentisha.domain.ElectricityTypes
import com.example.rentisha.domain.House
import com.example.rentisha.domain.HouseTypes
import com.example.rentisha.api.NetworkHouse
import com.example.rentisha.api.Renter
import com.example.rentisha.api.RentishaNetwork
import com.example.rentisha.data.RentishaRepository
import com.example.rentisha.database.DatabaseImage
import com.example.rentisha.database.HousewithImages
import com.example.rentisha.util.IMAGE_MANIPULATION_WORK_NAME
import com.example.rentisha.util.KEY_IMAGE_URIS
import com.example.rentisha.util.TAG_OUTPUT
import com.example.rentisha.workers.CleanupWorker
import com.example.rentisha.workers.SaveImageToFileWorker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import java.io.IOException

enum class RentishaApiStatus { LOADING, ERROR, DONE }
class RentishaViewModel(private val rentishaRepository: RentishaRepository,
                        private val savedStateHandle: SavedStateHandle) : ViewModel() {

    //private val rentishaRepository = RentishaRepository(RentishaNetwork.rentishaApi,getDatabase(application))
    private val workManager = WorkManager.getInstance()


    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<RentishaApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<RentishaApiStatus> = _status






    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError


    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)


    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown
    private var _mapHouseTypes = MutableLiveData<HashMap<Int, HouseTypes>>()
    val mapHouseTypes = _mapHouseTypes
    private val _house = MutableLiveData<DatabaseHouse>()
    val house: LiveData<DatabaseHouse> = _house

    /**
     * init{} is called immediately when this ViewModel is created.
     */

    /**
     * Stream of immutable states representative of the UI.
     */
    val state: StateFlow<UiState>
    val pagingDataFlow: Flow<PagingData<UiModel>>
//
//    /**
//     * Processor of side effects from the UI which in turn feedback into [state]
//     */
    val accept: (UiAction) -> Unit

    init {
        val initialQuery: String = savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }

        pagingDataFlow = searches
            .flatMapLatest { searchHouse(queryString = it.query) }
            .cachedIn(viewModelScope)


        state = combine(
            searches,
            queriesScrolled,
            ::Pair
        ).map { (search, scroll) ->
            UiState(
                query = search.query,
                lastQueryScrolled = scroll.currentQuery,
                // If the search query matches the scroll query, the user has scrolled
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }

        refreshDataFromRepository()
        Log.d("houseImages",getHouseImages(1).toString())
    }



    private fun searchHouse(queryString: String): Flow<PagingData<UiModel>> =
        rentishaRepository.getSearchResultStream(queryString)
            .map { pagingData -> pagingData.map { UiModel.HouseItem(it) } }





    fun getHouseImages(houseId:Int):List<DatabaseImage>{
        val houseImages = rentishaRepository.getImages(houseId)

        return houseImages

    }
    fun getHouseImages1(houseId:Int): StateFlow<PagingData<List<DatabaseImage>>?> {
        val houseImages = rentishaRepository.getHouseImages(houseId).stateIn(scope=viewModelScope,started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),initialValue = null)

        return houseImages

    }

    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */

    private fun refreshDataFromRepository() {
        viewModelScope.launch {

            try {

                rentishaRepository.refreshApiData()

                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.

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


    fun getRenter(id: Int): Call<Renter> {
        val renter = rentishaRepository.getRenter(id)
        return renter
    }

    // Function to add Renter
    fun addRenter(
        firstname: String,
        middlename: String,
        surname: String,
        email: String,
        phone: String,
        password: String,
        status: Int,
    ) {

        val renter = Renter(
            firstname = firstname,
            middlename = middlename,
            surname = surname,
            email = email,
            phone = phone,
            password = password,
            status = status
        )
        viewModelScope.launch {
            rentishaRepository.addRenter(renter)
        }
    }

    // Function to update Renter
    fun updateRenter(
        id: Int,
        firstname: String,
        middlename: String,
        surname: String,
        email: String,
        phone: String,
        password: String,
        status: Int,
    ) {
        val renter = Renter(id = id,
            firstname = firstname,
            middlename = middlename,
            surname = surname,
            email = email,
            phone = phone,
            password = password,
            status = status)
        viewModelScope.launch {
            rentishaRepository.updateRenter(renter)
        }
    }


    fun addHouse(
        electricity_type_id: Int,
        house_type_id: Int,
        car_booking: Boolean,
        electricity_description: String,
        electricity_name: String,
        has_watchman: Boolean,
        has_water: Boolean,
        house_type: String,
        latitude: Double,
        longitude: Double,
        other_description: String,
        renter_id: Int,
        own_compound: Boolean,
        status: Int,
    ) {
        //val house = DatabaseHouse()
        val house = House(
            electricity_type_id = electricity_type_id,
            house_type_id = house_type_id,
            car_booking = car_booking,
            electricity_description = electricity_description,
            electricity_name = electricity_name,
            has_watchman = has_watchman,
            has_water = has_water,
            house_type = house_type,
            latitude = latitude,
            longitude = longitude,
            other_description = other_description,
            renter_id = renter_id,
            own_compound = own_compound,
            status = status
        )

        viewModelScope.launch {
            rentishaRepository.addHouse(house)
        }

    }

    fun updateHouse(
        id:Int,
        electricity_type_id: Int,
        house_type_id: Int,
        car_booking: Boolean,
        electricity_description: String,
        electricity_name: String,
        has_watchman: Boolean,
        has_water: Boolean,
        house_type: String,
        latitude: Double,
        longitude: Double,
        other_description: String,
        renter_id: Int,
        own_compound: Boolean,
        status: Int,
    ) {
        //val house = DatabaseHouse()
        val house = House(
            id = id,
            electricity_type_id = electricity_type_id,
            house_type_id = house_type_id,
            car_booking = car_booking,
            electricity_description = electricity_description,
            electricity_name = electricity_name,
            has_watchman = has_watchman,
            has_water = has_water,
            house_type = house_type,
            latitude = latitude,
            longitude = longitude,
            other_description = other_description,
            renter_id = renter_id,
            own_compound = own_compound,
            status = status
        )

        viewModelScope.launch {
            rentishaRepository.updateHouse(house)
        }

    }

    fun deleteHouse(house: DatabaseHouse) {
        viewModelScope.launch {

            rentishaRepository.deleteHouse(house)
        }
    }


    fun isValidEntry(
        latitude: Double,
        longitude: Double,
        house_type_id: Int,
        electricity_type_id: Int,
    ): Boolean {
        return latitude.toString().isNotBlank() && longitude.toString()
            .isNotBlank() && house_type_id != null && electricity_type_id != null
    }

    fun isValidUser(email: String, phone: String): Boolean {
        return email.isNotBlank() && phone.isNotBlank()
    }

    fun getHouseTypesAsMap(list: List<HouseTypes>): HashMap<Int, HouseTypes> {

        val mapHouseTypes = HashMap<Int, HouseTypes>()
        viewModelScope.launch {
            for (houseType in list) {
                mapHouseTypes.set(houseType.id, houseType)
            }
        }
        return mapHouseTypes

    }

    fun getElectricityTypeAsMap(list: List<ElectricityTypes>): HashMap<Int, ElectricityTypes> {

        val mapElectricityTypesTypes = HashMap<Int, ElectricityTypes>()
        viewModelScope.launch {
            for (electricityType in list) {
                mapElectricityTypesTypes.set(electricityType.id, electricityType)
            }
        }
        return mapElectricityTypesTypes

    }

    fun onHouseClicked(house: DatabaseHouse) {
        _house.value = house
    }

    fun getHouseTypeRepoAsync(): List<HouseTypes> {
        val listHouseTypeAsync = rentishaRepository.getHouseTypesAsync()
        return listHouseTypeAsync
    }

    fun getElectricityTypeRepoAsync(): List<ElectricityTypes> {
        val listElectricityTypeAsync = rentishaRepository.getElectricityTypesAsync()
        return listElectricityTypeAsync
    }

    fun getHouse(id: Int): DatabaseHouse {
        val houseDatabase = rentishaRepository.getHouse(id)
        return houseDatabase
    }

    internal fun saveImageToFile(arrayImage: Array<String>) {
        Log.d("arrayImages", arrayImage.toString())
        var continuation = workManager.beginUniqueWork(
            IMAGE_MANIPULATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(CleanupWorker::class.java)
        )

        val saveBuilderFile = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
            .setInputData(createInputDataForUri(arrayImage))
            .addTag(TAG_OUTPUT)
            .build()
        continuation = continuation.then(saveBuilderFile)
        continuation.enqueue()
    }


    fun createInputDataForUri(arrayListImage: Array<String>): Data {
        val builder = Data.Builder()
        arrayListImage?.let {
            builder.putStringArray(KEY_IMAGE_URIS, arrayListImage)
        }
        return builder.build()
    }

}

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)

sealed class UiModel {
    data class HouseItem(val house: DatabaseHouse) : UiModel()
}

private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "One bed room"
