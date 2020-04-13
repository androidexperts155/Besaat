package com.deepak.besaat.network

import com.deepak.besaat.model.buyerSignUpModel.BuyerSignUpResponse
import com.deepak.besaat.model.courierGuysModel.GetCourierGuysPojo
import com.deepak.besaat.model.courierGuysModel.GetDeliveryTypesPojo
import com.deepak.besaat.model.getServiceProviderModel.GetProvidersPojo
import com.deepak.besaat.model.getServicesModel.GetServicesResponse
import com.deepak.besaat.model.getStoresModel.GetStores
import com.deepak.besaat.model.getUserProfile.UserProfileResponse
import com.deepak.besaat.model.providerSignupModel.ProviderSignUpResponse
import com.deepak.besaat.model.sendOtpModel.SendOtpResponse
import com.deepak.besaat.model.sendSignUpOtpModel.SendSignUpOtpResponse
import com.deepak.besaat.model.socialConfiguration.SocialConfiguration
import com.deepak.besaat.model.socialLoginModel.SocialLoginResponse
import com.deepak.besaat.view.activities.customerSupport.model.CustomerSupportModel
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File

interface Api {
    @GET(NetworkConstants.GET_SERVICES)
    suspend fun getServices(): Response<GetServicesResponse>

    @GET(NetworkConstants.GET_ALL_SERVICES)
    suspend fun getAllServices(): Response<GetServicesResponse>

    @GET
    suspend fun instagramLoginDetail(@Url urt: String): Response<ResponseBody>

    @GET
    suspend fun getPlaceDetails(@Url urt: String): Response<ResponseBody>

    @GET
    suspend fun getImages(@Url urt: String): Response<ResponseBody>

    @GET
    suspend fun getReviews(@Url urt: String): Response<ResponseBody>

//    @FormUrlEncoded
//    @POST(NetworkConstants.CREATE_REQUEST)
//    suspend fun createRequest(
//        @Header("Authorization") authorization: String,
//        @Field("name") name: String,
//        @Field("title") title: String,
//        @Field("pickup_address") pickUpAddress: String,
//        @Field("pickup_latitude") pickUpLatitue: String,
//        @Field("pickup_longitude") pickUpLongitute: String,
//        @Field("drop_address") dropAddress: String,
//        @Field("drop_latitude") dropLatitute: String,
//        @Field("drop_longitude") dropLongitute: String,
//        @Field("order_info") orderInfo: String,
//        @Field("special_note") specialNote: String,
//        @Field("charges") charges: String,
//        @Field("payment_method") paymentMethod: String,
//        @Field("request_type") requestType: String,
//        @Field("image") image: File,
//        @Field("timezone") timezone: String,
//        @Field("driver_selection_type") driverSelectionType: String
//    ): Response<ResponseBody>


    @Multipart
    @POST(NetworkConstants.CREATE_REQUEST)
    suspend fun createRequest(
        @Header("Authorization") authorization: String,
        @Part("name") name: RequestBody,
        @Part("title") title: RequestBody,
        @Part("pickup_address") pickUpAddress: RequestBody,
        @Part("pickup_latitude") pickUpLatitue: RequestBody,
        @Part("pickup_longitude") pickUpLongitute: RequestBody,
        @Part("drop_address") dropAddress: RequestBody,
        @Part("drop_latitude") dropLatitute: RequestBody,
        @Part("drop_longitude") dropLongitute: RequestBody,
        @Part("order_info") orderInfo: RequestBody,
        @Part("special_note") specialNote: RequestBody,
        @Part("charges") charges: RequestBody,
        @Part("payment_method") paymentMethod: RequestBody,
        @Part("request_type") requestType: RequestBody,
        @Part("provider_id") providerID: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("timezone") timezone: RequestBody,
        @Part("driver_selection_type") driverSelectionType: RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(NetworkConstants.CREATE_REQUEST)
    suspend fun createServiceRequest(
        @Header("Authorization") authorization: String,
        @Part("provider_id") providerID: RequestBody,
        @Part("name") name: RequestBody,
        @Part("title") title: RequestBody,
        @Part("pickup_address") pickUpAddress: RequestBody,
        @Part("pickup_latitude") pickUpLatitue: RequestBody,
        @Part("pickup_longitude") pickUpLongitute: RequestBody,
        @Part("drop_address") dropAddress: RequestBody,
        @Part("drop_latitude") dropLatitute: RequestBody,
        @Part("drop_longitude") dropLongitute: RequestBody,
        @Part("order_info") orderInfo: RequestBody,
        @Part("special_note") specialNote: RequestBody,
        @Part("charges") charges: RequestBody,
        @Part("payment_method") paymentMethod: RequestBody,
        @Part("request_type") requestType: RequestBody,
        @Part("service_time_from") serviceTimeFrom: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("timezone") timezone: RequestBody
    ): Response<JsonElement>


    @Multipart
    @POST(NetworkConstants.CREATE_REQUEST)
    suspend fun createCourierRequest(
        @Header("Authorization") authorization: String,
        @Part("provider_id") providerID: RequestBody,
        @Part("name") name: RequestBody,
        @Part("title") title: RequestBody,
        @Part("pickup_address") pickUpAddress: RequestBody,
        @Part("pickup_latitude") pickUpLatitue: RequestBody,
        @Part("pickup_longitude") pickUpLongitute: RequestBody,
        @Part("drop_address") dropAddress: RequestBody,
        @Part("drop_latitude") dropLatitute: RequestBody,
        @Part("drop_longitude") dropLongitute: RequestBody,
        @Part("order_info") orderInfo: RequestBody,
        @Part("special_note") specialNote: RequestBody,
        @Part("charges") charges: RequestBody,
        @Part("payment_method") paymentMethod: RequestBody,
        @Part("request_type") requestType: RequestBody,
        @Part("deliver_time_from") deliveryTimeFrom: RequestBody,
        @Part("delivery_type") deliveryType: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("timezone") timezone: RequestBody
    ): Response<JsonElement>

    @Multipart
    @POST(NetworkConstants.UPLOAD_MEDIA)
    suspend fun uploadFile(
        @Header("Authorization") authorization: String,
        @Part media_file: MultipartBody.Part?
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST(NetworkConstants.SOCIAL_LOGIN)
    suspend fun socialLogin(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("image_url") image: String,
        @Field("phone") phone: String,
        @Field("instagram_id") instagram_id: String,
        @Field("google_id") google_id: String,
        @Field("facebook_id") facebook_id: String
    ): Response<SocialLoginResponse>

    @Multipart
    @POST(NetworkConstants.BUYER_SIGN_UP)
    suspend fun buyerSignup(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("instagram_id") instagram_id: RequestBody,
        @Part("google_id") google_id: RequestBody,
        @Part("facebook_id") facebook_id: RequestBody,
        @Part("image_url") image_url: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Response<BuyerSignUpResponse>


    @GET(NetworkConstants.GET_STORE_CATEGORIES)
    suspend fun getStores(@Header("authorization") authorization: String): Response<GetStores>


    @GET(NetworkConstants.GET_PER_MILE_FAIR)
    suspend fun getFair(@Header("authorization") authorization: String): Response<ResponseBody>

    @FormUrlEncoded
    @POST(NetworkConstants.UPDATE_ADDRESS)
    suspend fun updateAddress(
        @Header("authorization") authorization: String,
        @Field("address") address: String,
        @Field("latitude") latitude: String,
        @Field("logitude") logitude: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST(NetworkConstants.CHAT_HISTORY)
    suspend fun getChatHistory(
        @Header("authorization") authorization: String,
        @Field("user_id") userId: String
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST(NetworkConstants.COURIER_DETAIL)
    suspend fun courierUserDetail(
        @Header("authorization") authorization: String,
        @Field("user_id") userId: String,
        @Field("latitude") latitude: String,
        @Field("longitude") logitude: String
    ): Response<ResponseBody>

    @Multipart
    @POST(NetworkConstants.PROVIDER_SIGN_UP)
    suspend fun providerSignUp(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("provider_role") provider_role: RequestBody,
        @Part("courier_status") courier_status: RequestBody,
        @Part("license_num") license_num: RequestBody,
        @Part("facebook_id") facebook_id: RequestBody,
        @Part("google_id") google_id: RequestBody,
        @Part("instagram_id") instagram_id: RequestBody,
        @Part("departure_country") departure_country: RequestBody,
        @Part("departure_date") departure_date: RequestBody,
        @Part("arrival_country") arrival_country: RequestBody,
        @Part("arrival_date") arrival_date: RequestBody,
        @Part("experience_years") experience_years: RequestBody,
        @Part("rate_per_hour") rate_per_hour: RequestBody,
        @Part("available_days") available_days: RequestBody,
        @Part("service_time_from") service_time_from: RequestBody,
        @Part("service_time_to") service_time_to: RequestBody,
        @Part("service_ids") service_ids: RequestBody,
        @Part("other_service") other_service: RequestBody,
        @Part("image_url") image_url: RequestBody,
        @Part("account_holder_name") account_holder_name: RequestBody,
        @Part("account_number") account_number: RequestBody,
        @Part("ifsc_code") ifsc_code: RequestBody,
        @Part profileImage: MultipartBody.Part?,
        @Part licenseImage: MultipartBody.Part?,
        @Part("timezone") timezone: RequestBody
    ): Response<ProviderSignUpResponse>


    @FormUrlEncoded
    @POST(NetworkConstants.SEND_OTP)
    suspend fun sendOtp(
        @Header("authorization") authorization: String,
        @Field("phone") phone: String,
        @Field("country_code") country_code: String
    ): Response<SendOtpResponse>


    @FormUrlEncoded
    @POST(NetworkConstants.SEND_SIGN_UP_OTP)
    suspend fun sendSignUpOtp(
        @Header("authorization") authorization: String,
        @Field("phone") phone: String,
        @Field("country_code") country_code: String,
        @Field("email") email: String,
        @Field("is_verified") is_verified: String
    ): Response<SendSignUpOtpResponse>


    @GET(NetworkConstants.USER_PROFILE)
    suspend fun userProfile(@Header("authorization") authorization: String): Response<UserProfileResponse>

    @GET(NetworkConstants.SOCIAL_CONFIGURATION)
    suspend fun socialConfiguration(): Response<SocialConfiguration>

    @FormUrlEncoded
    @POST(NetworkConstants.NEAR_BY_PROVIDERS)
    suspend fun getNearByProvider(
        @Header("authorization") authorization: String,
        @Field("latitude") latitude: String,
        @Field("logitude") longitude: String
    ): Response<ResponseBody>

    @Multipart
    @POST(NetworkConstants.ADD_TRIP)
    suspend fun addTrip(
        @Header("authorization") authorization: String,
        @Part("departure_country") departureCountry: RequestBody,
        @Part("departure_date") departure_date: RequestBody,
        @Part("arrival_date") arrival_date: RequestBody,
        @Part("arrival_country") arrival_country: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("timezone") timezone: RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(NetworkConstants.EDIT_TRIP)
    suspend fun editTrip(
        @Header("authorization") authorization: String,
        @Part("id") tripID: RequestBody,
        @Part("departure_country") departureCountry: RequestBody,
        @Part("departure_date") departure_date: RequestBody,
        @Part("arrival_date") arrival_date: RequestBody,
        @Part("arrival_country") arrival_country: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("timezone") timezone: RequestBody
    ): Response<ResponseBody>


    @GET("past-trips/{id}")
    suspend fun pastTrips(
        @Header("authorization") authorization: String,
        @Path("id") id: Long?
    ): Response<ResponseBody>

    @GET("upcoming-trips/{id}")
    suspend fun upcomingTrips(
        @Header("authorization") authorization: String,
        @Path("id") id: Long?
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST(NetworkConstants.ADD_SUPPORT_REQUEST)
    suspend fun addSupportRequest(
        @Header("authorization") authorization: String,
        @Field("support_type") supportType: String,
        @Field("support_type_id") supportTypeId: String,
        @Field("description") description: String
    ): Response<JsonElement>


    @FormUrlEncoded
    @POST(NetworkConstants.DELETE_TRIP)
    suspend fun deleteTrip(
        @Header("authorization") authorization: String,
        @Field("id") id: String
    ): Response<CustomerSupportModel>

    //////////////////////////////////////Service provider////////////////
    @FormUrlEncoded
    @POST
    suspend fun getServiceProviders(
        @Url url: String,
        @Header("authorization") authorization: String,
        @Field("sort_by") sort: String,
        @Field("order") order: String,
        @Field("search") search: String,
        @Field("service_id") service_id: String,
        @Field("latitude") latitude: String,
        @Field("logitude") logitude: String,
        @Field("radius") radius: String,
        @Field("rating") rating: String
    ): Response<GetProvidersPojo>


    ///////////////////////////Courier////////////////////////////

    @FormUrlEncoded
    @POST
    suspend fun getCourierGuys(
        @Url url: String,
        @Header("authorization") authorization: String,
        @Field("sort_by") sort_by: String,
        @Field("order") order: String,
        @Field("courier_status") courierStatus: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("from_country") fromCountry: String,
        @Field("to_country") toCountry: String,
        @Field("search") search: String,
        @Field("radius") radius: String,
        @Field("rating") rating: String
    ): Response<GetCourierGuysPojo>

    @FormUrlEncoded
    @POST
    suspend fun getCourierGuysOverSeas(
        @Url url: String,
        @Header("authorization") authorization: String,
        @Field("sort_by") sort_by: String,
        @Field("order") order: String,
        @Field("courier_status") courierStatus: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("from_country") fromCountry: String,
        @Field("to_country") toCountry: String,
        @Field("search") search: String,
        @Field("rating") rating: String
    ): Response<GetCourierGuysPojo>

    @GET(NetworkConstants.GET_DELIVERY_TYPES)
    suspend fun getDeliveryTypes(@Header("authorization") authorization: String): Response<GetDeliveryTypesPojo>

    /////////////////////////// Orders/ requests/ Jobs//////////////////////
    @FormUrlEncoded
    @POST
    suspend fun getOrders(
        @Url url: String,
        @Header("authorization") authorization: String,
        @Field("request_type") request_type: String,
        @Field("status") status: String
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST(NetworkConstants.ACCEPT_OR_REJECT_JOB)
    suspend fun acceptOrRejectJob(
        @Header("authorization") authorization: String,
        @Field("request_id") requestId: String,
        @Field("offer") offer: String,
        @Field("status") status: String
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST(NetworkConstants.ACCEPT_OR_REJECT_CANCEL_REQUEST)
    suspend fun acceptOrRejectCancelRequest(
        @Header("authorization") authorization: String,
        @Field("request_id") requestId: String,
        @Field("status") status: String
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST(NetworkConstants.RATE_USER)
    suspend fun rateUser(
        @Header("authorization") authorization: String,
        @Field("request_id") requestId: String,
        @Field("receiver_id") receiverId: String,
        @Field("rate") rate: String
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST(NetworkConstants.CHANGE_ORDER_STATUS)
    suspend fun changeOrderStatus(
        @Header("authorization") authorization: String,
        @Field("request_id") requestId: String,
        @Field("request_type") requestType: String,
        @Field("status") status: String
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST(NetworkConstants.REQUEST_DETAILS)
    suspend fun getRequestDetails(
        @Header("authorization") authorization: String,
        @Field("request_id") requestId: String,
        @Field("type") type: String
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST
    suspend fun getReqOffers(
        @Url url: String,
        @Header("authorization") authorization: String,
        @Field("request_id") requestId: String
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST(NetworkConstants.ACCEPT_OFFER)
    suspend fun acceptOffer(
        @Header("authorization") authorization: String,
        @Field("offer_id") offerId: String,
        @Field("type_id") typeId: String
    ): Response<JsonElement>

    @FormUrlEncoded
    @POST(NetworkConstants.CANCEL_REQUEST)
    suspend fun cancelOrder(
        @Header("authorization") authorization: String,
        @Field("request_id") requestId: String,
        @Field("reason_id") reasonId: String,
        @Field("remarks") remarks: String
    ): Response<JsonElement>

    @GET(NetworkConstants.REQUEST_CANCELLATION_REASONS)
    suspend fun getCancellationReasons(@Header("authorization") authorization: String): Response<JsonElement>

}
