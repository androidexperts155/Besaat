package com.deepak.besaat.view.activities.storeNearByListing.dbRepository

import com.deepak.besaat.dao.StoresDao
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails

class StoreDBRepository(private val storesDao: StoresDao) {
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.


    suspend fun getAllStores() : MutableList<SourceDetails>{
        return storesDao.getStores()

    }

    suspend fun insert(word: SourceDetails) {
        storesDao.insert(word)
    }

    suspend fun insertAll(word: MutableList<SourceDetails>) {
        storesDao.insertAll(word)
    }
}