package com.deepak.besaat.model.myOrdersModel

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.deepak.besaat.R
import com.deepak.besaat.model.socialLoginModel.User
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import java.io.Serializable

class Request : Serializable {
    @SerializedName("id")
    @Expose
    private var id: Int? = null
    @SerializedName("user_id")
    @Expose
    private var userId: Int? = null
    @SerializedName("provider_id")
    @Expose
    private var providerId: Int? = null
    @SerializedName("title")
    @Expose
    private var title: String? = null
    @SerializedName("name")
    @Expose
    private var name: String? = null
    @SerializedName("pickup_address")
    @Expose
    private var pickupAddress: String? = null
    @SerializedName("pickup_latitude")
    @Expose
    private var pickupLatitude: String? = null
    @SerializedName("pickup_longitude")
    @Expose
    private var pickupLongitude: String? = null
    @SerializedName("drop_address")
    @Expose
    private var dropAddress: String? = null
    @SerializedName("drop_latitude")
    @Expose
    private var dropLatitude: String? = null
    @SerializedName("drop_longitude")
    @Expose
    private var dropLongitude: String? = null
    @SerializedName("order_info")
    @Expose
    private var orderInfo: String? = null
    @SerializedName("special_note")
    @Expose
    private var specialNote: String? = null
    @SerializedName("image")
    @Expose
    private var image: String? = null
    @SerializedName("service_time_from")
    @Expose
    private var serviceTimeFrom: String? = null
    @SerializedName("service_time_to")
    @Expose
    private var serviceTimeTo: String? = null
    @SerializedName("deliver_time_from")
    @Expose
    private var deliverTimeFrom: String? = null
    @SerializedName("deliver_time_to")
    @Expose
    private var deliverTimeTo: String? = null
    @SerializedName("driver_selection_type")
    @Expose
    private var driverSelectionType: String? = null
    @SerializedName("app_commission")
    @Expose
    private var appCommission: Int? = null
    @SerializedName("delivery_fee")
    @Expose
    private var deliveryFee: String? = null
    @SerializedName("charges")
    @Expose
    private var charges: String? = null
    @SerializedName("payment_method")
    @Expose
    private var paymentMethod: String? = null
    @SerializedName("request_type")
    @Expose
    private var requestType: String? = null
    @SerializedName("request_status")
    @Expose
    private var requestStatus: String? = null
    @SerializedName("cancellation_reason")
    @Expose
    private var cancellationReason: String? = null
    @SerializedName("cancellation_remarks")
    @Expose
    private var cancellationRemarks: String? = null
    @SerializedName("cancellation_status")
    @Expose
    private var cancellationStatus: Int? = null
    @SerializedName("created_at")
    @Expose
    private var createdAt: String? = null
    @SerializedName("accepted_at")
    @Expose
    private var acceptedAt: String? = null
    @SerializedName("updated_at")
    @Expose
    private var updatedAt: String? = null
    @SerializedName("delivery_type_id")
    @Expose
    private var deliveryTypeId: Int? = null
    @SerializedName("cancelledBy_id")
    @Expose
    private var cancelledById: Int? = null
    @SerializedName("working_status")
    @Expose
    private var working_status: String? = null

    @SerializedName("rate_by_customer")
    @Expose
    private var rateByCustomer: String? = null
    @SerializedName("rate_by_provider")
    @Expose
    private var rateByProvider: String? = null
    @SerializedName("request_sent_to")
    @Expose
    private var requestSentTo: List<RequestSentTo?>? = null

    @SerializedName("user") // for JOBs
    @Expose
    private var user: User? = null

    @SerializedName("provider")  // for Requests
    @Expose
    private var provider: User? = null

    fun getCancellationReason(): String? {
        return if (cancellationReason != null) {
            "Reason for Cancellation: $cancellationReason"
        } else
            cancellationReason
    }

    fun setCancellationReason(cancellationReason: String?) {
        this.cancellationReason = cancellationReason
    }

    fun getCancellationRemarks(): String? {
        return if (cancellationRemarks != null) {
            "Remarks : $cancellationRemarks"
        } else
            cancellationRemarks
    }

    fun setCancellationRemarks(cancellationRemarks: String?) {
        this.cancellationRemarks = cancellationRemarks
    }

    fun getWorkingStatus(): String? {
        return working_status
    }

    fun setWorkingStatus(working_status: String?) {
        this.working_status = working_status
    }


    fun getCancelledById(): Int? {
        return cancelledById
    }

    fun setCancelledById(cancelledById: Int?) {
        this.cancelledById = cancelledById
    }

    fun getRateByCustomer(): String? {
        return rateByCustomer
    }

    fun setRateByCustomer(rateByCustomer: String?) {
        this.rateByCustomer = rateByCustomer
    }

    fun getRateByProvider(): String? {
        return rateByProvider
    }

    fun setRateByProvider(rateByProvider: String?) {
        this.rateByProvider = rateByProvider
    }

    fun getCancellationStatus(): Int? {
        return cancellationStatus
    }

    fun setCancellationStatus(cancellationStatus: Int?) {
        this.cancellationStatus = cancellationStatus
    }

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getUserId(): Int? {
        return userId
    }

    fun setUserId(userId: Int?) {
        this.userId = userId
    }

    fun getProviderId(): Int? {
        return providerId
    }

    fun setProviderId(providerId: Int?) {
        this.providerId = providerId
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getPickupAddress(): String? {
        return pickupAddress
    }

    fun setPickupAddress(pickupAddress: String?) {
        this.pickupAddress = pickupAddress
    }

    fun getPickupLatitude(): String? {
        return pickupLatitude
    }

    fun setPickupLatitude(pickupLatitude: String?) {
        this.pickupLatitude = pickupLatitude
    }

    fun getPickupLongitude(): String? {
        return pickupLongitude
    }

    fun setPickupLongitude(pickupLongitude: String?) {
        this.pickupLongitude = pickupLongitude
    }

    fun getDropAddress(): String? {
        return dropAddress
    }

    fun setDropAddress(dropAddress: String?) {
        this.dropAddress = dropAddress
    }

    fun getDropLatitude(): String? {
        return dropLatitude
    }

    fun setDropLatitude(dropLatitude: String?) {
        this.dropLatitude = dropLatitude
    }

    fun getDropLongitude(): String? {
        return dropLongitude
    }

    fun setDropLongitude(dropLongitude: String?) {
        this.dropLongitude = dropLongitude
    }

    fun getOrderInfo(): String? {
        return orderInfo
    }

    fun setOrderInfo(orderInfo: String?) {
        this.orderInfo = orderInfo
    }

    fun getSpecialNote(): String? {
        return specialNote
    }

    fun setSpecialNote(specialNote: String?) {
        this.specialNote = specialNote
    }

    fun getImage(): String? {
        return image
    }

    fun setImage(image: String?) {
        this.image = image
    }

    fun getServiceTimeFrom(): String? {
        return serviceTimeFrom
    }

    fun setServiceTimeFrom(serviceTimeFrom: String?) {
        this.serviceTimeFrom = serviceTimeFrom
    }

    fun getServiceTimeTo(): String? {
        return serviceTimeTo
    }

    fun setServiceTimeTo(serviceTimeTo: String?) {
        this.serviceTimeTo = serviceTimeTo
    }

    fun getDeliverTimeFrom(): String? {
        return deliverTimeFrom
    }

    fun setDeliverTimeFrom(deliverTimeFrom: String?) {
        this.deliverTimeFrom = deliverTimeFrom
    }

    fun getDeliverTimeTo(): String? {
        return deliverTimeTo
    }

    fun setDeliverTimeTo(deliverTimeTo: String?) {
        this.deliverTimeTo = deliverTimeTo
    }

    fun getDriverSelectionType(): String? {
        return driverSelectionType
    }

    fun setDriverSelectionType(driverSelectionType: String?) {
        this.driverSelectionType = driverSelectionType
    }

    fun getAppCommission(): Int? {
        return appCommission
    }

    fun setAppCommission(appCommission: Int?) {
        this.appCommission = appCommission
    }

    fun getDeliveryFee(): String? {
        return deliveryFee
    }

    fun setDeliveryFee(deliveryFee: String?) {
        this.deliveryFee = deliveryFee
    }

    fun getCharges(): String? {
        return charges
    }

    fun setCharges(charges: String?) {
        this.charges = charges
    }

    fun getPaymentMethod(): String? {
        return paymentMethod
    }

    fun setPaymentMethod(paymentMethod: String?) {
        this.paymentMethod = paymentMethod
    }

    fun getRequestType(): String? {
        return requestType
    }

    fun setRequestType(requestType: String?) {
        this.requestType = requestType
    }

    fun getRequestStatus(): String? {
        return requestStatus
    }

    fun setRequestStatus(requestStatus: String?) {
        this.requestStatus = requestStatus
    }

    fun getCreatedAt(): String? {
        return createdAt
    }

    fun setCreatedAt(createdAt: String?) {
        this.createdAt = createdAt
    }

    fun getAcceptedAt(): String? {
        return acceptedAt
    }

    fun setAcceptedAt(acceptedAt: String?) {
        this.acceptedAt = acceptedAt
    }

    fun getUpdatedAt(): String? {
        return updatedAt
    }

    fun setUpdatedAt(updatedAt: String?) {
        this.updatedAt = updatedAt
    }

    fun getDeliveryTypeId(): Int? {
        return deliveryTypeId
    }

    fun setDeliveryTypeId(deliveryTypeId: Int?) {
        this.deliveryTypeId = deliveryTypeId
    }

    fun getUser(): User? {
        return user
    }

    fun setUser(user: User?) {
        this.user = user
    }

    fun getProvider(): User? {
        return provider
    }

    fun setProvider(user: User?) {
        this.provider = provider
    }

    fun getRequestSentTo(): List<RequestSentTo?>? {
        return requestSentTo
    }

    fun setRequestSentTo(requestSentTo: List<RequestSentTo?>?) {
        this.requestSentTo = requestSentTo
    }

    fun getAcceptedAtString(): String? {
        return String.format(
            "Accepted at: %s", CommonFunctions().getFormattedTimeOrDate(
                acceptedAt!!,
                Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
                Constants.Pattern_DD_MMM_YY_HH_MM_AA
            )
        )
    }

    fun retRequestSentToSize(): Int? {
        if (requestSentTo != null) {
            return requestSentTo!!.size
        } else
            return 0
    }

    fun getChargesInCurrency(): String? {
        if (charges != null) {
            return String.format("$%.2f", charges!!.toFloat())
        } else
            return charges
    }

    fun getOrderDateFormatted(): String {
        return CommonFunctions().getFormattedTimeOrDate(
            createdAt!!,
            Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
            Constants.Pattern_DD_MMM_YY_HH_MM_AA
        )
    }

    fun getOrderDateOnlyFormatted(): String {
        return CommonFunctions().getFormattedTimeOrDate(
            createdAt!!,
            Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
            Constants.Pattern_MMM_D_YYYY
        )
    }

    fun getOrderTimeOnlyFormatted(): String {
        return CommonFunctions().getFormattedTimeOrDate(
            createdAt!!,
            Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
            Constants.Pattern_HH_MM_A
        )
    }

    fun getOrderServiceDateOnlyFormatted(): String {
        return if (serviceTimeFrom != null) {
            CommonFunctions().getFormattedTimeOrDate(
                serviceTimeFrom!!,
                Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
                Constants.Pattern_MMM_D_YYYY
            )
        } else {
            "N/A"
        }
    }

    fun getOrderServiceTimeOnlyFormatted(): String {
        return if (serviceTimeFrom != null) {
            CommonFunctions().getFormattedTimeOrDate(
                serviceTimeFrom!!,
                Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
                Constants.Pattern_HH_MM_A
            )
        } else {
            "N/A"
        }
    }

    fun getOrderDeliveryDateOnlyFormatted(): String {
        return if (deliverTimeFrom != null) {
            CommonFunctions().getFormattedTimeOrDate(
                deliverTimeFrom!!,
                Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
                Constants.Pattern_MMM_D_YYYY
            )
        } else {
            "N/A"
        }
    }

    fun getOrderDeliveryTimeOnlyFormatted(): String {
        return if (deliverTimeFrom != null) {
            CommonFunctions().getFormattedTimeOrDate(
                deliverTimeFrom!!,
                Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
                Constants.Pattern_HH_MM_A
            )
        } else {
            "N/A"
        }
    }

    fun cancellationPanelVisibility(): Int? {
        if (cancellationStatus != null) {
            if (cancellationStatus == 0) {
                return 1
            } else {
                return 0
            }
        } else {
            return 0
        }
    }

    fun getBackgroundColor(): String? {
        return if (requestStatus == "1" || requestStatus == "2" || requestStatus == "4") {
            "#FFFFFF"   // white
        } else if (requestStatus == "3" || requestStatus == "6" || requestStatus == "7") {
            "#F8EEEE"   // light red
        } else if (requestStatus == "5") {
            "#F2FCFC"    // light blue
        } else {
            "#FFFFFF"    // white
        }
    }

    fun getImageDrawableStage1(): Int? {
        return if (requestStatus == "1" || requestStatus == "2") {
            R.drawable.radio_button_selected_yellow
        } else if (provider != null) {
            R.drawable.ic_check_circle_blue
        } else if (requestStatus == "6" || requestStatus == "7" && provider == null) {
            R.drawable.radio_button_selected_red
        } else {
            R.drawable.bg_transparent_stroke_gray_circle
        }
    }

    fun getImageDrawableStage2(): Int? {
        return if (requestStatus == "1" && provider == null) {
            R.drawable.bg_transparent_stroke_gray_circle
        } else if (requestStatus == "6" || requestStatus == "7") {
            R.drawable.radio_button_selected_red
        } else if (requestStatus == "4") {
            R.drawable.radio_button_selected_yellow
        } else if (requestStatus == "5") {
            R.drawable.radio_button_selected_light_blue
        } else {
            R.drawable.bg_transparent_stroke_gray_circle
        }
    }

    fun getImageDrawableStage3(): Int? {
        return if (requestStatus == "1" || requestStatus == "4") {
            R.drawable.bg_transparent_stroke_gray_circle
        } else if (requestStatus == "6" || requestStatus == "7") {
            R.drawable.ic_cancel_order_status
        } else if (requestStatus == "5") {
            R.drawable.ic_check_circle_blue
        } else {
            R.drawable.bg_transparent_stroke_gray_circle
        }
    }

    fun getTextStage1(): String? {
        return if (requestStatus == "1" || requestStatus == "2") {
            "Waiting..."
        } else if (provider != null) {
            "Accepted"
        } else if (requestStatus == "6" || requestStatus == "7" || requestStatus == "3") {
            "Cancelled"
        } else {
            "Accepted"
        }
    }

    fun getTextColorStage1(): String? {
        return if (requestStatus == "1" || requestStatus == "2") {
            "#E5B253"  // yellow
        } else if (provider != null) {
            "#64BFCD"  // blue
        } else if (requestStatus == "6" || requestStatus == "7") {
            "#DD8B89"  //red
        } else if (requestStatus == "3") {
            "#E1E1ED"  //red
        } else {
            "#64BFCD"  // blue
        }
    }

    fun getTextColorStage2(): String? {
        return if (requestStatus == "1") {
            "#E1E1ED"  // grey
        } else if (requestStatus == "6" || requestStatus == "7") {
            "#DD8B89"  //red
        } else if (requestStatus == "4") {
            "#E5B253"  // yellow
        } else if (requestStatus == "5") {
            "#64BFCD"  // blue
        } else {
            "#E1E1ED"  // grey
        }
    }

    fun getTextStage3(): String? {
        return if (requestStatus == "6" || requestStatus == "7") {
            "Cancelled"
        } else {
            "Completed"
        }
    }

    fun getTextColorStage3(): String? {
        return if (requestStatus == "6" || requestStatus == "7") {
            "#DD8B89"  //red
        } else if (requestStatus == "4") {
            "#DD8B89"  // grey
        } else if (requestStatus == "5") {
            "#64BFCD"  // blue
        } else {
            "#E1E1ED"  // grey
        }
    }

    fun getColorStage1_2(): String? {
        if (requestStatus == "1") {
            return "#E1E1ED" // grey
        } else if (provider != null) {
            return "#64BFCD"  // blue
        } else if (requestStatus == "6" || requestStatus == "7" && provider == null) {
            return "#DD8B89"  //red
        } else {
            return "#E1E1ED" // grey
        }
    }

    fun getColorStage2_3(): String? {
        if (requestStatus == "1" || requestStatus == "4") {
            return "#E1E1ED" // grey
        } else if (requestStatus == "6" || requestStatus == "7") {
            return "#DD8B89"  //red
        } else if (requestStatus == "5") {
            return "#64BFCD"  // blue
        } else {
            return "#E1E1ED" // grey
        }
    }

//    companion object DataBindingAdapter : Serializable {
//        @BindingAdapter("bind:image_url")
//        @JvmStatic
//        fun loadImage(imageView: ImageView, image_url: String) {
////            if (image_url.isNotEmpty())
//            Picasso.get().load(image_url).fit()
//                .placeholder(R.drawable.bd_blue_drawable)
//                .error(R.drawable.icn_no_image)
//                .into(imageView)
//        }
//    }
}