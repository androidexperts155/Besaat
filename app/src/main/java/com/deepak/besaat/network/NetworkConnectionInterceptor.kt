package com.deepak.besaat.network

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.deepak.besaat.R
import com.deepak.besaat.utils.NoInternetException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.UndeclaredThrowableException
import java.net.SocketTimeoutException

class NetworkConnectionInterceptor(context: Context):Interceptor  {
    private val context=context.applicationContext
    @Throws(NoInternetException::class)

    override fun intercept(chain: Interceptor.Chain): Response {

        @Throws(NoInternetException::class)

       if(!isInternAvailable()){

               throw NoInternetException(context.getString(R.string.internet_connection))

       }else{
           try {
               return chain.proceed(chain.request())
           }catch (e : SocketTimeoutException){
               Log.e("NetworKIntercept","Intercert exception  in it  "+e)
                e.printStackTrace()
               throw NoInternetException(context.getString(R.string.internet_connection))
           }catch(e:IOException){
               e.printStackTrace()
               throw NoInternetException(context.getString(R.string.internet_connection))
           }catch(e:Exception){
               e.printStackTrace()
               throw NoInternetException(context.getString(R.string.internet_connection))
           }
       }
    }

    private fun isInternAvailable():Boolean{
      val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.activeNetworkInfo.also {
            return it!=null&&it.isConnected
        }
    }
}