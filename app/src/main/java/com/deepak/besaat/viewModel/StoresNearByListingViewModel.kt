package com.deepak.besaat.viewModel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.AppDatabase
import com.deepak.besaat.BesaatApplication
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails
import com.deepak.besaat.model.getStoresModel.GetStores
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import com.deepak.besaat.view.activities.storeNearByListing.dbRepository.StoreDBRepository
import org.json.JSONObject
import java.lang.Exception

class StoresNearByListingViewModel(
    val repository: Repository,
    val sharedPreferences: SharedPref
) :
    BaseViewModel() {
    var response: String = ""
    var successfullyGetStoreCategories: MutableLiveData<GetStores> = MutableLiveData()
    var successfullyGetStoresList: MutableLiveData<JSONObject> = MutableLiveData()
    var listMapIocnClick: MutableLiveData<Boolean> = MutableLiveData()
    var changeLocationClick: MutableLiveData<Boolean> = MutableLiveData()


    var sort: ObservableField<String> = ObservableField()
    var order: ObservableField<String> = ObservableField()
    var search: ObservableField<String> = ObservableField()
    var selectedStoreCat: ObservableField<String> = ObservableField()
    var selectedStoreCatID: ObservableField<Int> = ObservableField()
    var latitude: ObservableField<String> = ObservableField()
    var longitude: ObservableField<String> = ObservableField()
    var radius: ObservableField<String> = ObservableField()
    var rating: ObservableField<String> = ObservableField()
    var google_key: ObservableField<String> = ObservableField()
    var pageToken: ObservableField<String> = ObservableField()



    fun listOrMapIconClick(view: View) {
        listMapIocnClick.value = true
    }

    fun changeLocation(view: View) {
        changeLocationClick.value = true
    }


    fun getStoresListing() {
        Coroutines.mainThread {
            if (progressBar.value == false)
                progressBar.value = true
        }
        Coroutines.backThread {
            try {
//                var url: String = Constants.GET_NEARBY_STORES + latitude.get() + "," + longitude.get() + "&radius=" + radius.get()+"000" + "&keyword=" + selectedStoreCat.get() + "&key=" + google_key.get() + "&pagetoken=" + pageToken.get()  // nearby api without search
//                var url: String = Constants.GET_NEARBY_STORES_SEARCH + latitude.get() + "," + longitude.get() + "&radius=" + radius.get() + "&type=" + selectedStoreCat.get()+"&query="+search.get() + "&key=" + google_key.get() + "&pagetoken=" + pageToken.get()  // with query param
                var url: String =
                    Constants.GET_NEARBY_STORES_SEARCH + latitude.get() + "," + longitude.get() + "&radius=" + radius.get() + "&type=" + selectedStoreCat.get() + "&query=" + "&key=" + google_key.get() + "&pagetoken=" + pageToken.get()   // without query param

                var response = repository.getPlacesDetail(url)
                var jsonObject = JSONObject(response.string())
                Coroutines.mainThread {
                    successfullyGetStoresList.value = jsonObject
                    progressBar.value = false
                }
            } catch (e: ApiException) {
                Coroutines.mainThread {
                    feedBackMessage.value = e.message
                    progressBar.value = false
                }
            } catch (e: NoInternetException) {
                Coroutines.mainThread {
                    feedBackMessage.value = e.message
                    progressBar.value = false
                }
            }
        }
    }

    fun getStores() {
        Coroutines.backThread {
            try {
                val response = repository.getStores(
                    Constants.BEARER + " " + sharedPreferences.getString(Constants.TOKEN)
                )
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyGetStoreCategories.value = response
                }
            } catch (e: ApiException) {
                Coroutines.mainThread {
                    progressBar.value = false
                    feedBackMessage.value = e.message!!
                }
            } catch (e: NoInternetException) {
                Coroutines.mainThread {
                    progressBar.value = false
                    feedBackMessage.value = e.message!!
                }
            } catch (e: Exception) {
                Coroutines.mainThread {
                    progressBar.value = false
                    feedBackMessage.value = e.message!!
                }
            }
        }
    }
}