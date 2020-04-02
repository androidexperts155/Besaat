package com.deepak.besaat.viewModel

import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.deepak.besaat.model.getStoresModel.GetStores
import com.deepak.besaat.model.getStoresModel.Store
import com.deepak.besaat.network.Repository
import com.deepak.besaat.utils.ApiException
import com.deepak.besaat.utils.Coroutines
import com.deepak.besaat.utils.FragmentFucntions
import com.deepak.besaat.utils.NoInternetException
import com.deepak.besaat.view.activities.courierGuysListing.CourierGuysListing
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.view.activities.servicesListing.ServicesListingActivity
import com.deepak.besaat.view.activities.storeNearByListing.activities.StoreNearByListingActivity
import com.deepak.besaat.view.activities.storesListing.StoreListingActivity

class HomeFragmentViewModel(repository: Repository, fragmentFucntions: FragmentFucntions) :
    BaseViewModel() {
    val repository = repository
    val fragmentFucntions = fragmentFucntions
    var successfullyGetStore: MutableLiveData<GetStores> = MutableLiveData()

    fun getStores(authorization: String) {
        Coroutines.mainThread {
            progressBar.value = true
        }
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


    fun storesClick(view: View) {
        Handler().postDelayed(Runnable {
            var Int: Intent = Intent(view.context, StoreNearByListingActivity::class.java)
            Int.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            view.context.startActivity(Int)

        }, 100)

    }


    fun servicesClick(view: View) {
        Handler().postDelayed(Runnable {
            var Int: Intent = Intent(view.context, ServicesListingActivity::class.java)
            Int.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            view.context.startActivity(Int)
        }, 100)

    }

    fun locationClick(view: View) {
        Handler().postDelayed(Runnable {
            //            var Int :Intent= Intent(view.context,SelectCourierType::class.java)
            var Int: Intent = Intent(view.context, CourierGuysListing::class.java)
            Int.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            view.context.startActivity(Int)
        }, 100)
    }

    fun categoryClick(view: View, storeDetail: Store) {
        //  Toast.makeText(view.context,storeDetail.name,Toast.LENGTH_LONG).show()

        Handler().postDelayed({
            //            var intent = Intent(view.context, StoreListingActivity::class.java)
//            Log.e("StoreClick", "Store get clciked  " + storeDetail.name)
//            intent.putExtra("storedetail", storeDetail.name)
//            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//            view.context.startActivity(intent)

            var intent = Intent(view.context, StoreNearByListingActivity::class.java)
            intent.putExtra("storeCat", storeDetail)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            view.context.startActivity(intent)

        }, 100)

    }
}