package com.example.rentisha.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rentisha.api.*
import com.example.rentisha.database.HouseDatabase
import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.database.RemoteKeys
import retrofit2.HttpException
import java.io.IOException

private const val RENTISHA_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class RentishaRemoteMediator(
    private val service: RentishaService,
    private val database:HouseDatabase): RemoteMediator<Int,DatabaseHouse>() {

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DatabaseHouse>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: RENTISHA_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        //val apiQuery = query + IN_QUALIFIER

        try {
            val apiResponse = service.getHouses(page, state.config.pageSize)
            val imagesOfHouse = mutableListOf<Image>()

            for (house in apiResponse){
                val houseimages = house.images
                imagesOfHouse.addAll(houseimages)
            }


            val endOfPaginationReached = apiResponse.isEmpty()
            database.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao.clearRemoteKeys()
                    database.houseDao.clearHouses()
                }
                val prevKey = if (page == RENTISHA_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = apiResponse.map {
                    RemoteKeys(houseId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao.insertAll(keys)
                database.houseDao.insertAll(apiResponse.asDatabaseModel())
                if (imagesOfHouse.isNotEmpty()){
                    database.houseDao.insertAllImages(imagesOfHouse.asDatabaseImageModel())
                    database.houseDao.insertHouseImageCrossRefs(imagesOfHouse.asHouseImageCrossRef())
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, DatabaseHouse>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { house ->
                // Get the remote keys of the last item retrieved
                database.remoteKeysDao.remoteKeysHouseId(house.houseId)

            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, DatabaseHouse>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { house ->
                // Get the remote keys of the first items retrieved
                database.remoteKeysDao.remoteKeysHouseId(house.houseId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, DatabaseHouse>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.houseId?.let { houseId ->
                database.remoteKeysDao.remoteKeysHouseId(houseId)
            }
        }
    }
}