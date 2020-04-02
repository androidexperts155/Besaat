package com.deepak.besaat.utils

import org.koin.core.logger.MESSAGE

object Constants {
    val APP_NAME = "Besaat"
    val APP_DB_NAME = "Besaat.db"
    val Language = "Language"
    val BEARER = "Bearer"
    val SERVER_URL = "http://15.185.56.232"
    val SERVER_PORT = "3000"
    var BASE_URL = "$SERVER_URL/Beesat/public/api/"
    var CHAT_MEDIA_URL = "$SERVER_URL/Beesat/public/chat_media/"
    // val REDIRECT_URL="http://www.bissat.io"
    var BASE_URL_INSTA = "https://api.instagram.com/"
    //  var CLIENT_ID="cdedc0e2d9df45a2a5ad94be263abd14"
    var GET_INSTA_USER_INFO = "https://api.instagram.com/v1/users/self/?access_token="
    var GET_LOCATION_IFO = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
    var GET_NEARBY_STORES = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
    var GET_NEARBY_STORES_SEARCH =
        "https://maps.googleapis.com/maps/api/place/textsearch/json?location="
    var GET_PROFILE_URL = "https://maps.googleapis.com/maps/api/place/photo?photoreference="
    var GET_PLACE_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json?place_id="

    // Chat
    val GALLERY_INTENT = 1
    val CAMERA_INTENT = 2
    val REQUEST_PERMISSION_SETTING = 3
    var REQUEST_CHECK_LOCATION_STATUS = 4
    val REQUEST_PERMISSION = 111
    val REQUEST_LOCATION = 100
    val REQUEST_DROP_LOCATION = 101
    val REQUEST_FILE = 102
    val REQUEST_AUDIO_PERMISSION = 103
    val GOOGLE_SIGN_IN = 121

    // order related requests
    val SELECT_OFFER_REQ = 104
    val CANCEL_REQ = 105
    val VIEW_REQ = 106

    const val MENU_HOME = 1
    const val MENU_ORDER = 2
    const val MENU_NOTIFICATION = 3
    const val MENU_ME = 4
    const val MENU_WALLET = 5
    const val MENU_CARDS = 6
    const val MENU_VISITS = 7
    const val MENU_SETTINGS = 8

    // Order
    val REQUEST_TYPE_STORE = "1"
    val REQUEST_TYPE_SERVICE = "2"
    val REQUEST_TYPE_COURIER = "3"

    // for filter of order
    val FILTER_ORDER_STATUS_ALL = "0"
    val FILTER_ORDER_STATUS_PLACED = "1"
    val FILTER_ORDER_STATUS_ACCEPT = "2"
    val FILTER_ORDER_STATUS_REJECT = "3"
    val FILTER_ORDER_STATUS_IN_PROGRESS = "4"
    val FILTER_ORDER_STATUS_COMPLETED = "5"
    val FILTER_ORDER_STATUS_CANCELLED = "6"
    val FILTER_ORDER_STATUS_NEW_PENDING = "7"
    val FILTER_ORDER_STATUS_VIEW = "100"  //dummy
    val FILTER_ORDER_STATUS_REQ_VIEW_OFFERS = "101" //dummy



    // for status of order list  //1:placed,2:accept,3:reject,4:in_progress,5:completed,6:cancelled,7:requested for cancellation
    val ORDER_STATUS_PLACED = "1"
    val ORDER_STATUS_ACCEPT = "2"
    val ORDER_STATUS_REJECT = "3"
    val ORDER_STATUS_IN_PROGRESS = "4"
    val ORDER_STATUS_COMPLETED = "5"
    val ORDER_STATUS_CANCELLED = "6"
    val ORDER_STATUS_REQ_CANCLLATION = "7"



    // for accept and reject job to send in API
    val JOB_ACCEPT = "1"
    val JOB_REJECT = "0"

    // for accept and reject cancellation to send in API
    val ACCEPT_CANCELLATION = "1"
    val REJECT_CANCELLATION = "0"

    // to change status of JOB to send in API
    val CHANGE_STATUS_PICKUP = "0"
    val CHANGE_STATUS_DELAYED = "1"
    val CHANGE_STATUS_DROPPED = "2"
    val CHANGE_STATUS_START = "0"
    val CHANGE_STATUS_COMPLETE = "1"

    // for accept and reject cancellation request for cancellation_status( 0=>pending,1=>accepted,2=>rejected)
    val CANCELLATION_PENING = "0"
    val CANCELLATION_ACCEPT = "1"
    val CANCELLATION_REJECT = "2"

    val DETAILS_REQUEST = "0"
    val DETAILS_JOB = "1"

    val MESSAGE_CODE = "message_code"
    val MESSAGE_CODE_STRING = "message_code_string"

    val USER_ID = "userId"
    val USER_IMAGE = "userImage"
    val USER_NAME = "userName"

    val USER_Language = "userLanguage"
    val WALK_THROUGH = "walkThrough"
    val TOKEN = "token"
    val ROLE = "role"

    val ROLE_BUYER = "2"
//    val ROLE_PROVIDER = "1"

    //in chatting ----mediaType: 1=>chat, 2=>image, 3=>audio, 4=>video, 5=> Document
    const val MEDIA_CHAT = "1"
    const val MEDIA_IMAGE = "2"
    const val MEDIA_AUDIO = "3"
    const val MEDIA_VIDEO = "4"
    const val MEDIA_DOCUMENT = "5"
    const val MEDIA_LOCATION = "30"  /*for location media type will be 1 same as chat.
                                     In case of location this is dummy status
                                     to differentiate it from others in app*/
    val latitude = "latitude"
    val longitude = "longitude"
    val ADDRESS = "address"
    val COUNTRY_CODE = "country"
    val latChanged = "latChanged"
    val longChanged = "longChanged"

    // sorting and order->ascending and descending
    const val SORT_DISTANCE = "0"
    const val SORT_RATING = "1"
    const val ORDER_HIGHEST = "0"
    const val ORDER_LOWEST = "1"

    val COURIER_1 = "Local Courier"
    val COURIER_2 = "Overseas Courier"
    val COURIER_3 = "Local / Overseas Courier"


    ///driver_selection_type (1=>auto, 2=>manual   IN CASE OF STORE),
    val DRIVER_AUTO = "1"
    val DRIVER_MANUAL = "2"

    val Pattern_YYYY_MM_DD_HH_MM_A = "yyyy-MM-dd hh:mm a"
    val Pattern_YYYY_MM_DD_HH_MM_SS_A = "yyyy-MM-dd hh:mm:ss a"
    val Pattern_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    val Pattern_YYYY_MM_DD_T_HH_MM_SS_SSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val Pattern_DD_MMM_YY_HH_MM_AA = "dd MMM yy, hh:mm a"
    val Pattern_DD_MMM_YYYY_HH_MM = "dd MMM yyyy - HH:mm"
    val Pattern_HH_MM_SS = "HH:mm:ss"
    val Pattern_HH_MM_A = "hh:mm a"
    val Pattern_HH_MM_SS_A = "hh:mm:ss a"
    val Pattern_MMM_D_YYYY = "MMM d, yyyy"
    val Pattern_HH_MM = "HH:mm"


}