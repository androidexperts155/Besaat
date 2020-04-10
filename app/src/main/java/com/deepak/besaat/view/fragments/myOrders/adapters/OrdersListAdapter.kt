package com.deepak.besaat.view.fragments.myOrders.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.*
import com.deepak.besaat.model.myOrdersModel.Request
import com.deepak.besaat.network.NetworkConstants
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.view.fragments.myOrders.interfaces.IOrderItemClick

class OrdersListAdapter(
    var activity: Activity,
    var type: String,
    var subType: String,
    var currentUserId: Long
) :
    ListAdapter<Request, RecyclerView.ViewHolder>(StoreDetailDiffCallback()) {
    lateinit var binding: Any
    lateinit var itemList: OnItemClick2
    val VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_DELIVERY = 1
    val VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_SERVICE = 2
    val VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_COURIER = 3

    val VIEW_TYPE_JOB_SUB_VIEW_TYPE_DELIVERY = 4
    val VIEW_TYPE_JOB_SUB_VIEW_TYPE_SERVICE = 5
    val VIEW_TYPE_JOB_SUB_VIEW_TYPE_COURIER = 6

    var iOrderItemClick: IOrderItemClick? = null
    fun attachOrderItemClick(iOrderItemClick: IOrderItemClick) {
        this.iOrderItemClick = iOrderItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_DELIVERY) {
            binding = DataBindingUtil.inflate<AdapterOrderRequestDeliveryBinding>(
                inflater,
                R.layout.adapter_order_request_delivery,
                parent,
                false
            )
            return ViewHolderReqDelivery((binding as AdapterOrderRequestDeliveryBinding?)!!)
        } else if (viewType == VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_SERVICE) {
            binding = DataBindingUtil.inflate<AdapterOrderRequestServicesBinding>(
                inflater,
                R.layout.adapter_order_request_services,
                parent,
                false
            )
            return ViewHolderReqServices((binding as AdapterOrderRequestServicesBinding?)!!)
        } else if (viewType == VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_COURIER) {
            binding = DataBindingUtil.inflate<AdapterOrderRequestCourierBinding>(
                inflater,
                R.layout.adapter_order_request_courier,
                parent,
                false
            )
            return ViewHolderReqCourier((binding as AdapterOrderRequestCourierBinding?)!!)
        } else if (viewType == VIEW_TYPE_JOB_SUB_VIEW_TYPE_DELIVERY) {
            binding = DataBindingUtil.inflate<AdapterOrderDeliveryJobBinding>(
                inflater,
                R.layout.adapter_order_delivery_job,
                parent,
                false
            )
            return ViewHolderJobDelivery((binding as AdapterOrderDeliveryJobBinding?)!!)
        } else if (viewType == VIEW_TYPE_JOB_SUB_VIEW_TYPE_SERVICE) {
            binding = DataBindingUtil.inflate<AdapterOrderServicesJobBinding>(
                inflater,
                R.layout.adapter_order_services_job,
                parent,
                false
            )
            return ViewHolderJobServices((binding as AdapterOrderServicesJobBinding?)!!)
        } else {
            binding = DataBindingUtil.inflate<AdapterOrderCourierJobBinding>(
                inflater,
                R.layout.adapter_order_courier_job,
                parent,
                false
            )
            return ViewHolderJobCourier((binding as AdapterOrderCourierJobBinding?)!!)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (type == NetworkConstants.MY_REQUESTS && subType == Constants.REQUEST_TYPE_STORE)
            VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_DELIVERY
        else if (type == NetworkConstants.MY_REQUESTS && subType == Constants.REQUEST_TYPE_SERVICE)
            VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_SERVICE
        else if (type == NetworkConstants.MY_REQUESTS && subType == Constants.REQUEST_TYPE_COURIER)
            VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_COURIER
        else if (type == NetworkConstants.MY_JOBS && subType == Constants.REQUEST_TYPE_STORE)
            VIEW_TYPE_JOB_SUB_VIEW_TYPE_DELIVERY
        else if (type == NetworkConstants.MY_JOBS && subType == Constants.REQUEST_TYPE_SERVICE)
            VIEW_TYPE_JOB_SUB_VIEW_TYPE_SERVICE
        else if (type == NetworkConstants.MY_JOBS && subType == Constants.REQUEST_TYPE_COURIER)
            VIEW_TYPE_JOB_SUB_VIEW_TYPE_COURIER
        else
            VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_SERVICE     // this will be never used--> Dummy :)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_JOB_SUB_VIEW_TYPE_DELIVERY -> {
                (holder as ViewHolderJobDelivery).binding.currentUserID = currentUserId
                (holder as ViewHolderJobDelivery).binding.orderItem = getItem(position)

                if (getItem(position).getCancelledById() != null && getItem(position).getCancelledByIdLong() != currentUserId && getItem(
                        position
                    ).cancellationPanelVisibility() == 1
                ) {
                    holder.binding.llCancellationActions.visibility = View.VISIBLE
                    holder.binding.tvCancellationQuestion.visibility = View.VISIBLE
                } else {
                    holder.binding.llCancellationActions.visibility = View.GONE
                    holder.binding.tvCancellationQuestion.visibility = View.GONE
                }

                holder.binding.rootLayout.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.FILTER_ORDER_STATUS_VIEW,
                            Constants.FILTER_ORDER_STATUS_VIEW
                        )
                    }
                }

                holder.binding.tvAccept.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_ACCEPT,
                            Constants.JOB_ACCEPT
                        )
                    }
                }

                holder.binding.tvReject.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REJECT,
                            Constants.JOB_REJECT
                        )
                    }
                }

                holder.binding.tvCancelled.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_CANCELLED,
                            Constants.ORDER_STATUS_CANCELLED
                        )
                    }
                }

                holder.binding.tvAcceptCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.ACCEPT_CANCELLATION
                        )
                    }
                }

                holder.binding.tvRejectCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.REJECT_CANCELLATION
                        )
                    }
                }
            }
            //////////////////////////////////////////////////////////////////////////

            VIEW_TYPE_JOB_SUB_VIEW_TYPE_SERVICE -> {
                (holder as ViewHolderJobServices).binding.currentUserID = currentUserId
                (holder as ViewHolderJobServices).binding.orderItem = getItem(position)

                if (getItem(position).getCancelledById() != null && getItem(position).getCancelledByIdLong() != currentUserId && getItem(
                        position
                    ).cancellationPanelVisibility() == 1
                ) {
                    holder.binding.llCancellationActions.visibility = View.VISIBLE
                    holder.binding.tvCancellationQuestion.visibility = View.VISIBLE
                } else {
                    holder.binding.llCancellationActions.visibility = View.GONE
                    holder.binding.tvCancellationQuestion.visibility = View.GONE
                }

                holder.binding.rootLayout.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.FILTER_ORDER_STATUS_VIEW,
                            Constants.FILTER_ORDER_STATUS_VIEW
                        )
                    }
                }

                holder.binding.tvAccept.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_ACCEPT,
                            Constants.JOB_ACCEPT
                        )
                    }
                }

                holder.binding.tvReject.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REJECT,
                            Constants.JOB_REJECT
                        )
                    }
                }

                holder.binding.tvCancelled.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_CANCELLED,
                            Constants.ORDER_STATUS_CANCELLED
                        )
                    }
                }

                holder.binding.tvAcceptCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.ACCEPT_CANCELLATION
                        )
                    }
                }

                holder.binding.tvRejectCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.REJECT_CANCELLATION
                        )
                    }
                }
            }

            ///////////////////////////////////////////////////////////////////////

            VIEW_TYPE_JOB_SUB_VIEW_TYPE_COURIER -> {
                (holder as ViewHolderJobCourier).binding.currentUserID = currentUserId
                (holder as ViewHolderJobCourier).binding.orderItem = getItem(position)

                if (getItem(position).getCancelledById() != null && getItem(position).getCancelledByIdLong() != currentUserId && getItem(
                        position
                    ).cancellationPanelVisibility() == 1
                ) {
                    holder.binding.llCancellationActions.visibility = View.VISIBLE
                    holder.binding.tvCancellationQuestion.visibility = View.VISIBLE
                } else {
                    holder.binding.llCancellationActions.visibility = View.GONE
                    holder.binding.tvCancellationQuestion.visibility = View.GONE
                }

                holder.binding.rootLayout.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.FILTER_ORDER_STATUS_VIEW,
                            Constants.FILTER_ORDER_STATUS_VIEW
                        )
                    }
                }

                holder.binding.tvAccept.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_ACCEPT,
                            Constants.JOB_ACCEPT
                        )
                    }
                }

                holder.binding.tvReject.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REJECT,
                            Constants.JOB_REJECT
                        )
                    }
                }

                holder.binding.tvCancelled.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_CANCELLED,
                            Constants.ORDER_STATUS_CANCELLED
                        )
                    }
                }

                holder.binding.tvAcceptCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.ACCEPT_CANCELLATION
                        )
                    }
                }

                holder.binding.tvRejectCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.REJECT_CANCELLATION
                        )
                    }
                }
            }

            //////////////////////////////// Requests/////////////////////////////

            VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_DELIVERY -> {
                (holder as ViewHolderReqDelivery).binding.currentUserID = currentUserId
                (holder as ViewHolderReqDelivery).binding.orderItem = getItem(position)

                if (getItem(position).getCancelledById() != null && getItem(position).getCancelledByIdLong() != currentUserId && getItem(
                        position
                    ).cancellationPanelVisibility() == 1
                ) {
                    holder.binding.llCancellationActions.visibility = View.VISIBLE
                    holder.binding.tvCancellationQuestion.visibility = View.VISIBLE
                } else {
                    holder.binding.llCancellationActions.visibility = View.GONE
                    holder.binding.tvCancellationQuestion.visibility = View.GONE
                }

                holder.binding.rootLayout.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.FILTER_ORDER_STATUS_VIEW,
                            Constants.FILTER_ORDER_STATUS_VIEW
                        )
                    }
                }

                holder.binding.tvTrackStatus.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.FILTER_ORDER_STATUS_TRACK,
                            Constants.FILTER_ORDER_STATUS_TRACK
                        )
                    }
                }

                holder.binding.tvReorder.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_REORDER,
                            Constants.ORDER_STATUS_REQ_REORDER
                        )
                    }
                }

                holder.binding.tvCancelled.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_CANCELLED,
                            Constants.ORDER_STATUS_CANCELLED
                        )
                    }
                }

                holder.binding.tvAcceptCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.ACCEPT_CANCELLATION
                        )
                    }
                }

                holder.binding.tvRejectCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.REJECT_CANCELLATION
                        )
                    }
                }


            }

            ///////////////////////////////////////////////////////////////////////////////
            VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_SERVICE -> {
                (holder as ViewHolderReqServices).binding.currentUserID = currentUserId
                (holder as ViewHolderReqServices).binding.orderItem = getItem(position)
                if (getItem(position).getCancelledById() != null && getItem(position).getCancelledByIdLong() != currentUserId && getItem(
                        position
                    ).cancellationPanelVisibility() == 1
                ) {
                    holder.binding.llCancellationActions.visibility = View.VISIBLE
                    holder.binding.tvCancellationQuestion.visibility = View.VISIBLE
                } else {
                    holder.binding.llCancellationActions.visibility = View.GONE
                    holder.binding.tvCancellationQuestion.visibility = View.GONE
                }

                holder.binding.rootLayout.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.FILTER_ORDER_STATUS_VIEW,
                            Constants.FILTER_ORDER_STATUS_VIEW
                        )
                    }
                }

                holder.binding.tvTrackStatus.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.FILTER_ORDER_STATUS_TRACK,
                            Constants.FILTER_ORDER_STATUS_TRACK
                        )
                    }
                }

                holder.binding.tvReorder.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_REORDER,
                            Constants.ORDER_STATUS_REQ_REORDER
                        )
                    }
                }

                holder.binding.tvCancelled.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_CANCELLED,
                            Constants.ORDER_STATUS_CANCELLED
                        )
                    }
                }

                holder.binding.tvAcceptCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.ACCEPT_CANCELLATION
                        )
                    }
                }

                holder.binding.tvRejectCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.REJECT_CANCELLATION
                        )
                    }
                }
            }
            /////////////////////////////////////////////////////////////////////////////////////////
            VIEW_TYPE_REQUEST_SUB_VIEW_TYPE_COURIER -> {
                (holder as ViewHolderReqCourier).binding.currentUserID = currentUserId
                (holder as ViewHolderReqCourier).binding.orderItem = getItem(position)

                if (getItem(position).getCancelledById() != null && getItem(position).getCancelledByIdLong() != currentUserId && getItem(
                        position
                    ).cancellationPanelVisibility() == 1
                ) {
                    holder.binding.llCancellationActions.visibility = View.VISIBLE
                    holder.binding.tvCancellationQuestion.visibility = View.VISIBLE
                } else {
                    holder.binding.llCancellationActions.visibility = View.GONE
                    holder.binding.tvCancellationQuestion.visibility = View.GONE
                }

                holder.binding.rootLayout.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.FILTER_ORDER_STATUS_VIEW,
                            Constants.FILTER_ORDER_STATUS_VIEW
                        )
                    }
                }

                holder.binding.tvTrackStatus.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.FILTER_ORDER_STATUS_TRACK,
                            Constants.FILTER_ORDER_STATUS_TRACK
                        )
                    }
                }

                holder.binding.tvReorder.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_REORDER,
                            Constants.ORDER_STATUS_REQ_REORDER
                        )
                    }
                }

                holder.binding.tvCancelled.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_CANCELLED,
                            Constants.ORDER_STATUS_CANCELLED
                        )
                    }
                }

                holder.binding.tvAcceptCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.ACCEPT_CANCELLATION
                        )
                    }
                }

                holder.binding.tvRejectCancel.setOnClickListener {
                    if (iOrderItemClick != null) {
                        iOrderItemClick?.onOrderItemClick(
                            position,
                            holder.itemViewType,
                            Constants.ORDER_STATUS_REQ_CANCLLATION,
                            Constants.REJECT_CANCELLATION
                        )
                    }
                }
            }
        }
    }

    class ViewHolderJobDelivery(var binding: AdapterOrderDeliveryJobBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderJobServices(var binding: AdapterOrderServicesJobBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderJobCourier(var binding: AdapterOrderCourierJobBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderReqDelivery(var binding: AdapterOrderRequestDeliveryBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderReqServices(var binding: AdapterOrderRequestServicesBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderReqCourier(var binding: AdapterOrderRequestCourierBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class StoreDetailDiffCallback : DiffUtil.ItemCallback<Request>() {
        override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem.equals(newItem)
        }
    }

    interface OnItemClick2 {
        fun onPosClck(position: Int, name: String, value: String)
    }

    fun onClickListner(onItemClick: OnItemClick2) {
        this.itemList = onItemClick
    }
}