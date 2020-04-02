package com.deepak.besaat.view.activities.storeNearByListing.dbViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.AppDatabase
import com.deepak.besaat.BesaatApplication
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails
import com.deepak.besaat.utils.Coroutines
import com.deepak.besaat.view.activities.storeNearByListing.dbRepository.StoreDBRepository

class DBViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var repositoryDB: StoreDBRepository
    var storesList: MutableList<SourceDetails> = ArrayList()
    var notifyGetAllDataFromDB: MutableLiveData<Boolean> = MutableLiveData()

    init {
        // Gets reference to Dao from App RoomDatabase to construct
        // the correct StoreRepository.
        val wordsDao = AppDatabase.getDatabase(application).storesDao()
        repositoryDB = StoreDBRepository(wordsDao)
    }

    fun insertData(store: SourceDetails) {
        Coroutines.backThread {
            repositoryDB.insert(store)
            Coroutines.mainThread {
            }
        }
    }

    fun insertAllData(storesList: MutableList<SourceDetails>) {
        Coroutines.backThread {
            repositoryDB.insertAll(storesList)
            Coroutines.mainThread {

            }
        }
    }

    fun getAllDataFromDB() {
        Coroutines.backThread {
            storesList = repositoryDB.getAllStores()
            Coroutines.mainThread {
                notifyGetAllDataFromDB.value = true
            }
        }
    }
}