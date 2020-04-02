package com.deepak.besaat.view.fragments.myOrders.interfaces

interface IOrderItemClick {
    fun onOrderItemClick(position:Int, type:Int, purpose: String, status:String)
}