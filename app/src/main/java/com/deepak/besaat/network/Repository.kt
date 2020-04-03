package com.deepak.besaat.network

import com.deepak.besaat.model.buyerSignUpModel.BuyerSignUpResponse
import com.deepak.besaat.model.courierGuysModel.GetCourierGuysPojo
import com.deepak.besaat.model.courierGuysModel.GetDeliveryTypesPojo
import com.deepak.besaat.model.getServiceProviderModel.GetProvidersPojo
import com.deepak.besaat.model.getServicesModel.GetServicesResponse
import com.deepak.besaat.model.getStoresModel.GetStores
import com.deepak.besaat.model.getUserProfile.UserProfileResponse
import com.deepak.besaat.model.providerSignUpDetailToServer.ProviderSignUpDetailToServer
import com.deepak.besaat.model.providerSignupModel.ProviderSignUpResponse
import com.deepak.besaat.model.sendOtpModel.SendOtpResponse
import com.deepak.besaat.model.sendSignUpOtpModel.SendSignUpOtpResponse
import com.deepak.besaat.model.socialConfiguration.SocialConfiguration
import com.deepak.besaat.model.socialLoginModel.SocialLoginResponse
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.view.activities.customerSupport.model.CustomerSupportModel
import com.facebook.FacebookSdk.getCacheDir
import com.google.gson.JsonElement
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class Repository(interceptor: NetworkConnectionInterceptor) : SafeApiRequest() {
    var api: Api

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()

        // not working currently
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cache = Cache(getCacheDir(), cacheSize.toLong())

        /*httpClient.networkInterceptors().add(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.header("Content-Type", "application/json")
                return chain.proceed(requestBuilder.build())
            }
        })*/
        /* httpClient.addInterceptor(object : Interceptor {
             @Throws(IOException::class)
             override fun intercept(chain: Interceptor.Chain): Response<T> {
                 val requestBuilder = chain.request().newBuilder()
                 requestBuilder.header("Accept", "application/json")
                 return chain.proceed(requestBuilder.build())
             }
         })*/
        val headerApplicationInterceptor = object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.header("Accept", "application/json")
                var request = chain.request()

                return chain.proceed(requestBuilder.build())
                // val headers = request.headers().newBuilder().add("Authorization", authToken).build()
                //  request = request.newBuilder().headers(headers).build()
                // return chain.proceed(request)
            }
        }
        /*  val headerAuthorizationInterceptor = object : Interceptor {
              @Throws(IOException::class)
              override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                 // val requestBuilder = chain.request().newBuilder()
               //   requestBuilder.header("Accept", "application/json")
                  var request = chain.request()
                  //return chain.proceed(requestBuilder.build())
                   val headers = request.headers().newBuilder().add("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImM1YTIwZWFkMDcyMjgyZGRhODk1YzEyYzQ3YjhiZWFmZWJjMDhkYWU1MzI2NTdjMDA2OTk1ZjllNWU4MmYyNTAyNGUyYzU3ODlhYjllZWE5In0.eyJhdWQiOiIxIiwianRpIjoiYzVhMjBlYWQwNzIyODJkZGE4OTVjMTJjNDdiOGJlYWZlYmMwOGRhZTUzMjY1N2MwMDY5OTVmOWU1ZTgyZjI1MDI0ZTJjNTc4OWFiOWVlYTkiLCJpYXQiOjE1NzMwMjQxNjUsIm5iZiI6MTU3MzAyNDE2NSwiZXhwIjoxNjA0NjQ2NTY1LCJzdWIiOiIyNDkiLCJzY29wZXMiOltdfQ.tqCiCvPa5Qjgu1hbPz3nIK74U7EYesObKoaXpFeehQDvB6DK-IGLBOG_wBGkn_QleEgaLvMk6Ut-aXaprAo81uxbwe5rZpM3ZiGiZgtlyjtoJFl8nNnw-n97veMldCStZEphsGDewG7W32sKXBIuKLNemZRCk6nMtSgTY7pL-7c2NI_X-x9ZXQ4_Ie30j7TNTrqFKzavg3p58jdQw1KW8jwZ7TtaoYVHlcfPL9Pl9TSSyEjHReGuhGi4yigfR12n4errQ4Jm6W5k9mHuINMZuZJ4lC6eZM5dxed1JUhpbLw1RiWxY6-10W5JUESLBtFOjj_5ajX8i6Gz3D98xqPznPqOi8_O0apydDtrhFc-XQ9DPhpl2vp2mE9yxPhVhPK1nyHQcnVyuhMQrdnbj9ECt4s6tsPfqKHIKmiy7l9cNbsFj2dAPKWY0ZGzKuWHSsgQdOmsBNeMYjj8bec5x7p6EOHQpxff5mjqc905JnKx1tK3LMzU26XOfvafIoN7sLsGNkyRClpph_5heuiu-MnHeXFENZ0sKSbhtHvvDmlzxgVoeslQZYD6PWqHb5zJFm2WwZRbMA_SJl-1USrI92g2qokwxtsmW3Nv4wKbczsxcI35Zh-Pdi-ucCJBnhq9Fkrn-MKIzK-llX1gnLOFw1q6tY4U_xQxIUMKMVMHWNMJ1zw").build()
                   request = request.newBuilder().headers(headers).build()
                  return chain.proceed(request)
              }
          }*/
        httpClient.addInterceptor(headerApplicationInterceptor)
        //  httpClient.addInterceptor(headerAuthorizationInterceptor)
        httpClient.cache(cache)
//        httpClient.addNetworkInterceptor(provideCacheInterceptor())
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.writeTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)
        httpClient.addInterceptor(interceptor)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build())
            .build()
        api = retrofit.create(Api::class.java)
    }

    private fun provideCacheInterceptor(): Interceptor? {
        return Interceptor { chain ->
            var request = chain.request()
            val originalResponse = chain.proceed(request)
            val cacheControl = originalResponse.header("Cache-Control")
            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains(
                    "no-cache"
                ) ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-stale=0")
            ) {
                val cc = CacheControl.Builder().maxStale(1, TimeUnit.DAYS).build()
                /*return originalResponse.newBuilder()
                                .header("Cache-Control", "public, max-stale=" + 60 * 60 * 24)
                                .build();*/request = request.newBuilder()
                    .cacheControl(cc)
                    .build()
                chain.proceed(request)
            } else {
                originalResponse
            }
        }
    }


    suspend fun getServices(): GetServicesResponse {
        return apiRequest { api.getServices() }
    }

    suspend fun getAllServices(): GetServicesResponse {
        return apiRequest { api.getAllServices() }
    }

    suspend fun getDeliveryTypes(authorization: String): GetDeliveryTypesPojo {
        return apiRequest { api.getDeliveryTypes(authorization) }
    }

    suspend fun getCourierGuys(
        url: String,
        authorization: String,
        sort: String,
        order: String,
        courierStatus: String,
        latitude: String,
        longitude: String,
        fromCountry: String,
        toCountry: String,
        search: String,
        radius: String
    ): GetCourierGuysPojo {
        return apiRequest {
            api.getCourierGuys(
                url,
                authorization,
                sort,
                order,
                courierStatus,
                latitude,
                longitude,
                fromCountry,
                toCountry,
                search,
                radius
            )
        }
    }

    suspend fun getCourierGuysOverSeas(
        url: String,
        authorization: String,
        sort: String,
        order: String,
        courierStatus: String,
        latitude: String,
        longitude: String,
        fromCountry: String,
        toCountry: String,
        search: String
    ): GetCourierGuysPojo {
        return apiRequest {
            api.getCourierGuysOverSeas(
                url,
                authorization,
                sort,
                order,
                courierStatus,
                latitude,
                longitude,
                fromCountry,
                toCountry,
                search
            )
        }
    }


    suspend fun getCancellationReasons(authorization: String): JsonElement {
        return apiRequest { api.getCancellationReasons(authorization) }
    }

    suspend fun getOrders(
        url: String,
        authorization: String,
        requestType: String,
        status: String
    ): JsonElement {
        return apiRequest {
            api.getOrders(
                url,
                authorization,
                requestType,
                status
            )
        }
    }

    suspend fun socialLogin(
        name: String,
        email: String,
        image: String,
        phone: String,
        instaId: String,
        googleId: String,
        fbId: String
    ): SocialLoginResponse {
        return apiRequest { api.socialLogin(name, email, image, phone, instaId, googleId, fbId) }
    }

    suspend fun createRequest(
        authorization: String,
        name: String,
        title: String,
        pickUpAddress: String,
        pickUpLatitute: String,
        pickUpLongitute: String,
        dropAddress: String,
        dropLatitute: String,
        dropLongitute: String,
        orderinfo: String,
        specialNote: String,
        charges: String,
        paymentMethod: String,
        requestType: String,
        providerID: String,
        image: File,
        driverSelectionType: String
    ): ResponseBody {
        var part: MultipartBody.Part? = null
        if (image.name.endsWith(".txt")) {
//                val requestBodies = RequestBody.create(MediaType.parse("multipart/form-data"), imagee)
            part = MultipartBody.Part.createFormData("image", " ")
        } else {
            val requestBodies =
                RequestBody.create(MediaType.parse("multipart/form-data"), image)
            part = MultipartBody.Part.createFormData("image", image.name, requestBodies)
        }


        val mProviderID = RequestBody.create(MediaType.parse("multipart/form-data"), providerID)
        val mName = RequestBody.create(MediaType.parse("multipart/form-data"), name)
        val mTitle = RequestBody.create(MediaType.parse("multipart/form-data"), title)
        val mPickupAddress =
            RequestBody.create(MediaType.parse("multipart/form-data"), pickUpAddress)
        val mPickUpLatitute =
            RequestBody.create(MediaType.parse("multipart/form-data"), pickUpLatitute)
        val mPickUpLongitute =
            RequestBody.create(MediaType.parse("multipart/form-data"), pickUpLongitute)
        val mDropAddress = RequestBody.create(MediaType.parse("multipart/form-data"), dropAddress)
        val mDropLatitute = RequestBody.create(MediaType.parse("multipart/form-data"), dropLatitute)
        val mDropLongitute =
            RequestBody.create(MediaType.parse("multipart/form-data"), dropLongitute)
        val mOrderinfo = RequestBody.create(MediaType.parse("multipart/form-data"), orderinfo)
        val mSpecialNote = RequestBody.create(MediaType.parse("multipart/form-data"), specialNote)
        val mCharges = RequestBody.create(MediaType.parse("multipart/form-data"), charges)
        val mPaymentMethod =
            RequestBody.create(MediaType.parse("multipart/form-data"), paymentMethod)
        val mRequestType = RequestBody.create(MediaType.parse("multipart/form-data"), requestType)
        val mDriverSelectionType = RequestBody.create(MediaType.parse("multipart/form-data"), driverSelectionType)
        val mTimeZone = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            TimeZone.getDefault().id.toString()
        )

        return apiRequest {
            api.createRequest(
                authorization,
                mName,
                mTitle,
                mPickupAddress,
                mPickUpLatitute,
                mPickUpLongitute,
                mDropAddress,
                mDropLatitute,
                mDropLongitute,
                mOrderinfo,
                mSpecialNote,
                mCharges,
                mPaymentMethod,
                mRequestType,
                mProviderID,
                part,
                mTimeZone,
                mDriverSelectionType
            )
        }
    }

    suspend fun createServiceRequest(
        authorization: String,
        providerID: String,
        name: String,
        title: String,
        pickUpAddress: String,
        pickUpLatitute: String,
        pickUpLongitute: String,
        dropAddress: String,
        dropLatitute: String,
        dropLongitute: String,
        orderinfo: String,
        specialNote: String,
        charges: String,
        paymentMethod: String,
        requestType: String,
        serviceTimeFrom: String,
        image: File
    ): JsonElement {
        var part: MultipartBody.Part? = null
        if (image.name.endsWith(".txt")) {
//                val requestBodies = RequestBody.create(MediaType.parse("multipart/form-data"), imagee)
            part = MultipartBody.Part.createFormData("image", " ")
        } else {
            val requestBodies =
                RequestBody.create(MediaType.parse("multipart/form-data"), image)
            part = MultipartBody.Part.createFormData("image", image.name, requestBodies)
        }


        val mProviderID = RequestBody.create(MediaType.parse("multipart/form-data"), providerID)
        val mName = RequestBody.create(MediaType.parse("multipart/form-data"), name)
        val mTitle = RequestBody.create(MediaType.parse("multipart/form-data"), title)
        val mPickupAddress =
            RequestBody.create(MediaType.parse("multipart/form-data"), pickUpAddress)
        val mPickUpLatitute =
            RequestBody.create(MediaType.parse("multipart/form-data"), pickUpLatitute)
        val mPickUpLongitute =
            RequestBody.create(MediaType.parse("multipart/form-data"), pickUpLongitute)
        val mDropAddress = RequestBody.create(MediaType.parse("multipart/form-data"), dropAddress)
        val mDropLatitute = RequestBody.create(MediaType.parse("multipart/form-data"), dropLatitute)
        val mDropLongitute =
            RequestBody.create(MediaType.parse("multipart/form-data"), dropLongitute)
        val mOrderinfo = RequestBody.create(MediaType.parse("multipart/form-data"), orderinfo)
        val mSpecialNote = RequestBody.create(MediaType.parse("multipart/form-data"), specialNote)
        val mCharges = RequestBody.create(MediaType.parse("multipart/form-data"), charges)
        val mPaymentMethod =
            RequestBody.create(MediaType.parse("multipart/form-data"), paymentMethod)
        val mRequestType = RequestBody.create(MediaType.parse("multipart/form-data"), requestType)
        val mServiceTimeFrom =
            RequestBody.create(MediaType.parse("multipart/form-data"), serviceTimeFrom)
        val mTimeZone = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            TimeZone.getDefault().id.toString()
        )

        return apiRequest {
            api.createServiceRequest(
                authorization,
                mProviderID,
                mName,
                mTitle,
                mPickupAddress,
                mPickUpLatitute,
                mPickUpLongitute,
                mDropAddress,
                mDropLatitute,
                mDropLongitute,
                mOrderinfo,
                mSpecialNote,
                mCharges,
                mPaymentMethod,
                mRequestType,
                mServiceTimeFrom,
                part,
                mTimeZone
            )
        }
    }


    suspend fun createCourierRequest(
        authorization: String,
        providerID: String,
        name: String,
        title: String,
        pickUpAddress: String,
        pickUpLatitute: String,
        pickUpLongitute: String,
        dropAddress: String,
        dropLatitute: String,
        dropLongitute: String,
        orderinfo: String,
        specialNote: String,
        charges: String,
        paymentMethod: String,
        requestType: String,
        deliveryTimeFrom: String,
        deliveryType: String,
        image: File
    ): JsonElement {
        var part: MultipartBody.Part? = null
        if (image.name.endsWith(".txt")) {
//                val requestBodies = RequestBody.create(MediaType.parse("multipart/form-data"), imagee)
            part = MultipartBody.Part.createFormData("image", " ")
        } else {
            val requestBodies =
                RequestBody.create(MediaType.parse("multipart/form-data"), image)
            part = MultipartBody.Part.createFormData("image", image.name, requestBodies)
        }


        val mProviderID = RequestBody.create(MediaType.parse("multipart/form-data"), providerID)
        val mName = RequestBody.create(MediaType.parse("multipart/form-data"), name)
        val mTitle = RequestBody.create(MediaType.parse("multipart/form-data"), title)
        val mPickupAddress =
            RequestBody.create(MediaType.parse("multipart/form-data"), pickUpAddress)
        val mPickUpLatitute =
            RequestBody.create(MediaType.parse("multipart/form-data"), pickUpLatitute)
        val mPickUpLongitute =
            RequestBody.create(MediaType.parse("multipart/form-data"), pickUpLongitute)
        val mDropAddress = RequestBody.create(MediaType.parse("multipart/form-data"), dropAddress)
        val mDropLatitute = RequestBody.create(MediaType.parse("multipart/form-data"), dropLatitute)
        val mDropLongitute =
            RequestBody.create(MediaType.parse("multipart/form-data"), dropLongitute)

        val mOrderinfo = RequestBody.create(MediaType.parse("multipart/form-data"), orderinfo)
        val mSpecialNote = RequestBody.create(MediaType.parse("multipart/form-data"), specialNote)
        val mCharges = RequestBody.create(MediaType.parse("multipart/form-data"), charges)
        val mPaymentMethod =
            RequestBody.create(MediaType.parse("multipart/form-data"), paymentMethod)
        val mRequestType = RequestBody.create(MediaType.parse("multipart/form-data"), requestType)
        val mServiceTimeFrom =
            RequestBody.create(MediaType.parse("multipart/form-data"), deliveryTimeFrom)
        val mDeliveryType = RequestBody.create(MediaType.parse("multipart/form-data"), deliveryType)
        val mTimeZone = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            TimeZone.getDefault().id.toString()
        )

        return apiRequest {
            api.createCourierRequest(
                authorization,
                mProviderID,
                mName,
                mTitle,
                mPickupAddress,
                mPickUpLatitute,
                mPickUpLongitute,
                mDropAddress,
                mDropLatitute,
                mDropLongitute,
                mOrderinfo,
                mSpecialNote,
                mCharges,
                mPaymentMethod,
                mRequestType,
                mServiceTimeFrom,
                mDeliveryType,
                part,
                mTimeZone
            )
        }
    }


    suspend fun uploadFile(
        authorization: String,
        image: File
    ): JsonElement {
        var part: MultipartBody.Part? = null
        if (image.name.endsWith(".txt")) {
//                val requestBodies = RequestBody.create(MediaType.parse("multipart/form-data"), imagee)
            part = MultipartBody.Part.createFormData("media_file", " ")
        } else {
            val requestBodies =
                RequestBody.create(MediaType.parse("multipart/form-data"), image)
            part = MultipartBody.Part.createFormData("media_file", image.name, requestBodies)
        }

        return apiRequest {
            api.uploadFile(
                authorization,
                part
            )
        }
    }


    suspend fun getInstagramLoginDetail(url: String): ResponseBody {
        return apiRequest { api.instagramLoginDetail(url) }
    }


    suspend fun getPlacesDetail(url: String): ResponseBody {
        return apiRequest { api.getPlaceDetails(url) }
    }

    suspend fun getImages(url: String): ResponseBody {
        return apiRequest { api.getImages(url) }
    }

    suspend fun getPlaceReview(url: String): ResponseBody {
        return apiRequest { api.getReviews(url) }
    }

    suspend fun getServiceProviders(
        url: String,
        authorization: String,
        sort: String,
        order: String,
        search: String,
        service_id: String,
        latitude: String,
        logitude: String,
        radius: String
    ): GetProvidersPojo {
        return apiRequest {
            api.getServiceProviders(
                url,
                authorization,
                sort,
                order,
                search,
                service_id,
                latitude,
                logitude,
                radius
            )
        }
    }

    suspend fun getFair(authorization: String): ResponseBody {
        return apiRequest {
            api.getFair(authorization)
        }
    }

    suspend fun addTrip(
        authorization: String,
        departureCountry: String?,
        departureDate: String,
        arrivalDate: String,
        arrivalCountry: String?,
        imagee: File?
    ): ResponseBody {
        var part: MultipartBody.Part? = null
        if (imagee != null) {
            if (imagee.name.endsWith(".txt")) {
//                val requestBodies = RequestBody.create(MediaType.parse("multipart/form-data"), imagee)
                part = MultipartBody.Part.createFormData("image", " ")
            } else {
                val requestBodies =
                    RequestBody.create(MediaType.parse("multipart/form-data"), imagee)
                part = MultipartBody.Part.createFormData("image", imagee.name, requestBodies)
            }
        }
        // val authorization = RequestBody.create(MediaType.parse("multipart/form-data"), authorization)
        val departureCountry =
            RequestBody.create(MediaType.parse("multipart/form-data"), departureCountry)
        val departureDate =
            RequestBody.create(MediaType.parse("multipart/form-data"), departureDate)
        val arrivalDate = RequestBody.create(MediaType.parse("multipart/form-data"), arrivalDate)
        val arrivalCountry =
            RequestBody.create(MediaType.parse("multipart/form-data"), arrivalCountry)
        val mTimeZone = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            TimeZone.getDefault().id.toString()
        )

        return apiRequest {
            api.addTrip(
                authorization,
                departureCountry,
                departureDate,
                arrivalDate,
                arrivalCountry,
                part,
                mTimeZone
            )
        }
    }

    suspend fun editTrip(
        authorization: String,
        tripId: String?,
        departureCountry: String?,
        departureDate: String,
        arrivalDate: String,
        arrivalCountry: String?,
        imagee: File?
    ): ResponseBody {
        var part: MultipartBody.Part? = null
        if (imagee != null) {
            if (imagee.name.endsWith(".txt")) {
//                val requestBodies = RequestBody.create(MediaType.parse("multipart/form-data"), imagee)
                part = MultipartBody.Part.createFormData("image", " ")
            } else {
                val requestBodies =
                    RequestBody.create(MediaType.parse("multipart/form-data"), imagee)
                part = MultipartBody.Part.createFormData("image", imagee.name, requestBodies)
            }
        }

        // val authorization = RequestBody.create(MediaType.parse("multipart/form-data"), authorization)
        val departureCountry =
            RequestBody.create(MediaType.parse("multipart/form-data"), departureCountry)
        val departureDate =
            RequestBody.create(MediaType.parse("multipart/form-data"), departureDate)
        val arrivalDate = RequestBody.create(MediaType.parse("multipart/form-data"), arrivalDate)
        val arrivalCountry =
            RequestBody.create(MediaType.parse("multipart/form-data"), arrivalCountry)

        val tripID =
            RequestBody.create(MediaType.parse("multipart/form-data"), tripId)
        val mTimeZone = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            TimeZone.getDefault().id.toString()
        )

        return apiRequest {
            api.editTrip(
                authorization,
                tripID,
                departureCountry,
                departureDate,
                arrivalDate,
                arrivalCountry,
                part,
                mTimeZone
            )
        }
    }

    suspend fun nearByProvider(
        authorization: String,
        latitude: String,
        longitude: String
    ): ResponseBody {

        return apiRequest {
            api.getNearByProvider(authorization, latitude, longitude)
        }

    }

    suspend fun pastTrips(authorization: String, id: Long?): ResponseBody {
        return apiRequest {
            api.pastTrips(authorization, id)
        }
    }

    suspend fun upcomigTrips(authorization: String, id: Long?): ResponseBody {

        return apiRequest {
            api.upcomingTrips(authorization, id)
        }
    }

    suspend fun updateAddress(
        authorization: String,
        address: String?,
        lat: String?,
        lng: String?
    ): ResponseBody {
        return apiRequest {
            api.updateAddress(authorization, address!!, lat!!, lng!!)
        }
    }

    suspend fun getChatHistory(
        authorization: String,
        address: String?
    ): JsonElement {
        return apiRequest {
            api.getChatHistory(authorization, address!!)
        }
    }

    suspend fun courierUserDetails(
        authorization: String,
        userId: String?,
        lat: String?,
        lng: String?
    ): ResponseBody {
        return apiRequest {
            api.courierUserDetail(authorization, userId!!, lat!!, lng!!)
        }
    }

    suspend fun deleteTrips(authorization: String, id: String?): CustomerSupportModel {
        return apiRequest {
            api.deleteTrip(authorization, id!!)
        }
    }

    suspend fun addSupportRequest(
        authorization: String,
        type: String?,
        id: String?,
        description: String?
    ): JsonElement {
        return apiRequest {
            api.addSupportRequest(authorization, type!!, id!!, description!!)
        }
    }

    suspend fun acceptOrRejectJob(
        authorization: String,
        requestId: String?,
        offer: String?,
        status: String?
    ): JsonElement {
        return apiRequest {
            api.acceptOrRejectJob(authorization, requestId!!,offer!!, status!!)
        }
    }

    suspend fun acceptOrRejectCancelRequest(
        authorization: String,
        requestId: String?,
        status: String?
    ): JsonElement {
        return apiRequest {
            api.acceptOrRejectCancelRequest(authorization, requestId!!, status!!)
        }
    }

    suspend fun rateUser(
        authorization: String,
        requestId: String?,
        receiverId: String?,
        rate: String?
    ): JsonElement {
        return apiRequest {
            api.rateUser(authorization, requestId!!, receiverId!!,rate!!)
        }
    }

    suspend fun changeOrderStatus(
        authorization: String,
        requestId: String?,
        requestType: String?,
        status: String?
    ): JsonElement {
        return apiRequest {
            api.changeOrderStatus(authorization, requestId!!, requestType!!,status!!)
        }
    }


    suspend fun getRequestDetails(
        authorization: String,
        requestId: String?,
        type: String?
    ): JsonElement {
        return apiRequest {
            api.getRequestDetails(authorization, requestId!!, type!!)
        }
    }

    suspend fun acceptOffer(
        authorization: String,
        offerId: String?,
        typeId: String?
    ): JsonElement {
        return apiRequest {
            api.acceptOffer(authorization, offerId!!,typeId!!)
        }
    }

    suspend fun cancelOrder(
        authorization: String,
        requestId: String?,
        reasonId: String?,
        remarks: String?
    ): JsonElement {
        return apiRequest {
            api.cancelOrder(authorization, requestId!!,reasonId!!,remarks!!)
        }
    }

    suspend fun getReqOffers(
        url:String,
        authorization: String,
        requestId: String
    ): JsonElement {
        return apiRequest {
            api.getReqOffers(url,authorization, requestId)
        }
    }

    suspend fun buyerSignUp(
        name: String,
        email: String,
        phone: String,
        instaId: String,
        googleId: String,
        fbId: String,
        image_url: String,
        image: File?
    ): BuyerSignUpResponse {
        var part: MultipartBody.Part? = null
        if (image != null) {
            val requestBodies = RequestBody.create(MediaType.parse("multipart/form-data"), image)
            part = MultipartBody.Part.createFormData("image", image.name, requestBodies)
        }
        val userName = RequestBody.create(MediaType.parse("multipart/form-data"), name)
        val email = RequestBody.create(MediaType.parse("multipart/form-data"), email)
        val phone = RequestBody.create(MediaType.parse("multipart/form-data"), phone)
        val instaId = RequestBody.create(MediaType.parse("multipart/form-data"), instaId)
        val googleId = RequestBody.create(MediaType.parse("multipart/form-data"), googleId)
        val fbId = RequestBody.create(MediaType.parse("multipart/form-data"), fbId)
        val image_url = RequestBody.create(MediaType.parse("multipart/form-data"), image_url)
        return apiRequest {
            api.buyerSignup(
                userName,
                email,
                phone,
                instaId,
                googleId,
                fbId,
                image_url,
                part
            )
        }
    }

    suspend fun getStores(authorization: String): GetStores {
        return apiRequest { api.getStores(authorization) }
    }


    suspend fun providerSignUp(providerSignUpDetailToServer: ProviderSignUpDetailToServer): ProviderSignUpResponse {
        var part: MultipartBody.Part? = null
        var licenseImage: MultipartBody.Part? = null
        if (providerSignUpDetailToServer.image != null) {
            val requestBodies = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                providerSignUpDetailToServer.image
            )
            part = MultipartBody.Part.createFormData(
                "image",
                providerSignUpDetailToServer.image!!.name,
                requestBodies
            )
        }

        if (providerSignUpDetailToServer.licenseImage != null) {
            val requestBodies = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                providerSignUpDetailToServer.licenseImage
            )
            licenseImage = MultipartBody.Part.createFormData(
                "license_image",
                providerSignUpDetailToServer.licenseImage!!.name,
                requestBodies
            )
        }

        if (providerSignUpDetailToServer.ratePerHour == null) {
            providerSignUpDetailToServer.ratePerHour = ""
        }

        val userName = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.name!!
        )
        val email = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.email!!
        )
        val phone = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.phone!!
        )
        val instaId = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.instagramId!!
        )
        val googleId = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.googleId!!
        )
        val fbId = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.facebookId!!
        )
        val providerRole = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.providerRole!!
        )
        val courierStatus = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.courierStatus!!
        )
        val licenseNum = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.licenseNum!!
        )
        val departureCountry = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.departureCountry!!
        )
        val departureDate = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.departureDate!!
        )
        val arrivalCountry = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.arrivalCountry!!
        )
        val arrivalDate = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.arrivalDate!!
        )
        val experienceYears = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.experienceYears!!
        )
        val ratePerHours = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.ratePerHour!!
        )
        val availableDays = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.availableDays!!
        )
        val serviceTimeFrom = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.serviceTimeFrom!!
        )
        val serviceTimeTo = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.serviceTimeTo!!
        )
        val serviceIds = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.serviceIds!!
        )
        val otherService = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.otherService!!
        )
        val image_url = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.image_url!!
        )
        val account_holder_name = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.account_holder_name!!
        )
        val account_number = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.account_number!!
        )
        val ifsc_code = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            providerSignUpDetailToServer.ifsc_code!!
        )
        val timeZone = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            TimeZone.getDefault().id.toString()
        )
        return apiRequest {
            api.providerSignUp(
                userName,
                email,
                phone,
                providerRole,
                courierStatus,
                licenseNum,
                fbId,
                googleId,
                instaId,
                departureCountry,
                departureDate,
                arrivalCountry,
                arrivalDate,
                experienceYears,
                ratePerHours,
                availableDays,
                serviceTimeFrom,
                serviceTimeTo,
                serviceIds,
                otherService,
                image_url,
                account_holder_name,
                account_number,
                ifsc_code,
                part,
                licenseImage,
                timeZone
            )
        }
    }


    suspend fun sendOtp(
        authorization: String,
        phoneNumber: String,
        coutryCode: String
    ): SendOtpResponse {
        return apiRequest { api.sendOtp(authorization, phoneNumber, coutryCode) }
    }

    suspend fun sendSignOtp(
        authorization: String,
        phoneNumber: String,
        coutryCode: String,
        email: String,
        isVerified: String
    ): SendSignUpOtpResponse {
        return apiRequest {
            api.sendSignUpOtp(
                authorization,
                phoneNumber,
                coutryCode,
                email,
                isVerified
            )
        }
    }

    suspend fun userProfile(authorization: String): UserProfileResponse {
        return apiRequest { api.userProfile(authorization) }
    }

    suspend fun socialConfiguration(): SocialConfiguration {
        return apiRequest { api.socialConfiguration() }
    }
}