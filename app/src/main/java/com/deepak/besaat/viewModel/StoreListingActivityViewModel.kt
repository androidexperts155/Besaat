package com.deepak.besaat.viewModel

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.model.getStoresModel.GetStores
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.*
import org.json.JSONObject

class StoreListingActivityViewModel(activity: Activity, repository: Repository) : BaseViewModel() {
    val repository = repository
    var activity = activity
    var response: String = ""

    var successfullyGetStore: MutableLiveData<GetStores> = MutableLiveData()
    var stingResponse: MutableLiveData<String> = MutableLiveData()
    var jsonResponse: MutableLiveData<JSONObject> = MutableLiveData()
    var jsonResponse21: MutableLiveData<JSONObject> = MutableLiveData()
    var jsonResponse22: MutableLiveData<JSONObject> = MutableLiveData()
    var listMapIocnClick: MutableLiveData<Boolean> = MutableLiveData()
    var listMapIconClickChange: MutableLiveData<Boolean> = MutableLiveData()
    fun listorMapIconClick(view: View) {
        Log.e("MapIcon", "listpr map icon click  " + view)
        listMapIocnClick.value = true
        Log.e("MapIcon", "listpr map icon click value " + listMapIocnClick.value)
    }

    fun listorMapIconClickChange(view: View) {
        Log.e("MapIcon", "Current context change in view holder " + view)
        if (listMapIconClickChange.value == false) {
            listMapIconClickChange.value = true
        } else {
            listMapIconClickChange.value = false
        }
    }

    fun getStoreDetails(
        sharedPref: SharedPref,
        keyword: String,
        google_key: String,
        radius: String,
        b: Boolean,
        rating: String,
        pageToken: String,
        value: String,
        lati: String?,
        longi: String?,
        allCategory: String?
    ) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread({
            try {
                var url: String
                if (!keyword.equals("All",true)&&!keyword.equals("",true)) {
                    url =
                        Constants.GET_LOCATION_IFO + sharedPref.getString(Constants.latitude) + "," + sharedPref.getString(
                            Constants.longitude
                        ) + "&radius=" + radius + "&keyword=" + keyword + "&key=" + google_key + "&pagetoken=" + pageToken
                } else {
                    url =
//                           Constants.GET_LOCATION_IFO + sharedPref.getString(Constants.latitude) + "," + sharedPref.getString(Constants.longitude) + "&radius=" + radius + "&keyword=Bar&&Airport&&Bicycle Store&&Bakery&&Bank&&Bus station&&Bicycle Store&&ATM&&Beauty salon" + "&key=" + google_key + "&pagetoken=" + pageToken
                        Constants.GET_LOCATION_IFO + sharedPref.getString(Constants.latitude) + "," + sharedPref.getString(
                            Constants.longitude
                        ) + "&radius=" + radius + "&keyword=" + allCategory + "&key=" + google_key + "&pagetoken=" + pageToken
                }

                var response = repository.getPlacesDetail(url)
                var resp = response.toString()

                var jsonObject = JSONObject(response.string())
                Log.e("response", "response on view model " + jsonObject.toString())
                Coroutines.mainThread {
                    //stingResponse.value=response.toString()
                    if (b) {
                        jsonResponse.value = jsonObject
                        progressBar.value = false
                    } else {
                        jsonResponse.value = null
                        progressBar.value = false
                    }
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
        })
    }

    fun getStoreFirstTimeUrl(url: String) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread({
            try {
                Log.e("response", "store setails url first time " + url)

                var response = repository.getPlacesDetail(url)
                var resp = response.toString()
                var jsonObject = JSONObject(response.string())
                Log.e("response", "response on view moodel " + jsonObject.toString())
                Coroutines.mainThread {
                    //stingResponse.value=response.toString()

                    jsonResponse.value = jsonObject
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
        })


    }

    fun getStoreDetailsFirstTime(
        sharedPref: SharedPref,
        keyword: String,
        google_key: String,
        radius: String,
        b: Boolean,
        rating: String,
        pageToken: String,
        value: String,
        lati: String?,
        longi: String?
    ) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread({
            try {

                var url: String
                url =
                    Constants.GET_LOCATION_IFO + sharedPref.getString(Constants.latitude) + "," + sharedPref.getString(
                        Constants.longitude
                    ) + "&radius=" + radius + "&keyword=Bar" + "&key=" + google_key + "&pagetoken=" + pageToken



                Log.e("response", "store setails url first time " + url)

                var response = repository.getPlacesDetail(url)
                var resp = response.toString()


                var jsonObject = JSONObject(response.string())
                Log.e("response", "response on view moodel " + jsonObject.toString())
                Coroutines.mainThread {
                    //stingResponse.value=response.toString()
                    if (b) {
                        jsonResponse.value = jsonObject
                        progressBar.value = false
                    } else {
                        jsonResponse.value = null
                        progressBar.value = false
                    }
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
        })
    }

    fun getStoreDetailsAnother(
        sharedPref: SharedPref,
        keyword: String,
        google_key: String,
        radius: String,
        b: Boolean,
        rating: String,
        pageToken: String,
        value: String,
        lati: String?,
        longi: String?,
        allCategory: String?
    ) {
        Coroutines.mainThread {
            progressBar.value = true
        }
        Coroutines.backThread({
            try {
                var url: String
                if (!keyword.equals("All",true)&&!keyword.equals("",true)){
                    url =
                        Constants.GET_LOCATION_IFO + sharedPref.getString(Constants.latChanged) + "," + sharedPref.getString(
                            Constants.longChanged
                        ) + "&radius=" + radius + "&keyword=" + keyword + "&key=" + google_key + "&pagetoken=" + pageToken

                } else {
                    url =
                        Constants.GET_LOCATION_IFO + sharedPref.getString(Constants.latChanged) + "," + sharedPref.getString(
                            Constants.longChanged
                        ) + "&radius=" + radius + "&keyword="+allCategory + "&key=" + google_key + "&pagetoken=" + pageToken
                }

                var response = repository.getPlacesDetail(url)
                var resp = response.toString()

                var jsonObject = JSONObject(response.string())
                Log.e("response", "response on view moodel " + jsonObject.toString())
                Coroutines.mainThread {
                    //stingResponse.value=response.toString()
                    if (b) {
                        jsonResponse.value = jsonObject
                        progressBar.value = false
                    } else {
                        jsonResponse.value = null
                        progressBar.value = false
                    }
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
        })
    }

    fun getStoreDetailsAnother(
        sharedPref: SharedPref,
        keyword: String,
        google_key: String,
        radius: String,
        b: Boolean,
        rating: String,
        pageToken: String
    ) {
        Coroutines.mainThread {
            progressBar.value = true
        }

        Coroutines.backThread({
            try {

                var url: String

                if (!keyword.equals("All")) {
                    url =
                        Constants.GET_LOCATION_IFO + sharedPref.getString(Constants.latitude) + "," + sharedPref.getString(
                            Constants.longitude
                        ) + "&radius=" + radius + "&keyword=" + keyword + "&key=" + google_key + "&pagetoken=" + pageToken

                } else {
                    url =
                        Constants.GET_LOCATION_IFO + sharedPref.getString(Constants.latitude) + "," + sharedPref.getString(
                            Constants.longitude
                        ) + "&radius=5000" + "&keyword=" + "Bar&&Airport&&Bicycle Store&&Bakery&&Bank&&Bus station&&Bicycle Store&&ATM&key=" + google_key + "&pagetoken=" + pageToken
                }
                Log.e("response", "store setails url  " + url)

                var response = repository.getPlacesDetail(url)
                var resp = response.toString()


                var jsonObject = JSONObject(response.string())
                Log.e("response", "response on view moodel " + jsonObject.toString())
                Coroutines.mainThread {
                    //stingResponse.value=response.toString()
                    if (b) {
                        jsonResponse22.value = jsonObject
                        progressBar.value = false
                    } else {
                        jsonResponse22.value = null
                        progressBar.value = false
                    }
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
        })
    }

    fun getStoreDetailsMore(
        sharedPref: SharedPref,
        keyword: String,
        google_key: String,
        radius: String,
        b: Boolean,
        rating: String,
        pageToken: String,
        allCategory: String?
    ) {
        Coroutines.mainThread {
            progressBar.value = true
        }

        Coroutines.backThread({
            try {

                var url: String

                if (!keyword.equals("All",true)&&!keyword.equals("",true)) {
                    url =
                        Constants.GET_LOCATION_IFO + sharedPref.getString(Constants.latitude) + "," + sharedPref.getString(
                            Constants.longitude
                        ) + "&radius=" + radius + "&keyword=" + keyword + "&key=" + google_key + "&pagetoken=" + pageToken
                } else {
                    url =
                        Constants.GET_LOCATION_IFO + sharedPref.getString(Constants.latitude) + "," + sharedPref.getString(
                            Constants.longitude
                        ) + "&radius="+radius + "&keyword=" + allCategory+"&key=" + google_key + "&pagetoken=" + pageToken
                }
                Log.e("response", "store setails url  " + url)

                var response = repository.getPlacesDetail(url)
                var resp = response.toString()


                var jsonObject = JSONObject(response.string())
                Log.e("response", "response on view moodel " + jsonObject.toString())
                Coroutines.mainThread {
                    //stingResponse.value=response.toString()
                    if (b) {
                        jsonResponse21.value = jsonObject
                        progressBar.value = false
                    } else {
                        jsonResponse21.value = null
                        progressBar.value = false
                    }
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
        })
    }

    fun getAllItems(
        sharedPref: SharedPref,
        keyword: String,
        google_key: String,
        radius: String,
        b: Boolean,
        rating: String
    ) {

        Coroutines.mainThread {
            progressBar.value = true
        }

        Coroutines.backThread({
            try {
                var url: String
                url =
                    Constants.GET_LOCATION_IFO + sharedPref.getString(Constants.latitude) + "," + sharedPref.getString(
                        Constants.longitude
                    ) + "&radius=5000" + "&keyword=" + "Bar|Airport|Bicycle Store|Bakery|Bank|Beauty salon|Bus station|Bicycle Store|ATM&key=" + google_key
                var response = repository.getPlacesDetail(url)
                var resp = response.toString()


                var jsonObject = JSONObject(response.string())
                Coroutines.mainThread {
                    //stingResponse.value=response.toString()
                    if (b) {
                        jsonResponse.value = jsonObject
                        progressBar.value = false
                    } else {
                        jsonResponse.value = null
                        progressBar.value = false
                    }
                }

            } catch (e: Exception) {
                Coroutines.mainThread {
                    //stingResponse.value=response.toString()
                    if (b) {
                        jsonResponse.value = null
                        progressBar.value = false
                    } else {
                        jsonResponse.value = null
                        progressBar.value = false
                    }
                }
            }


        })
    }

    fun getStores(authorization: String) {

        Coroutines.backThread {
            try {
                val response = repository.getStores(authorization)
                Coroutines.mainThread {
                    progressBar.value = false
                    successfullyGetStore.value = response
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
            }
        }
    }
}