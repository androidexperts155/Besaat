package com.deepak.besaat.view.fragments.storeListingFragment.adapter

import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener
    (@NonNull layoutManager: LinearLayoutManager): RecyclerView.OnScrollListener() {
    @NonNull
    private val layoutManager:LinearLayoutManager
    init{
        this.layoutManager = layoutManager
    }
    override fun onScrolled(@NonNull recyclerView:RecyclerView, dx:Int, dy:Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = layoutManager.getChildCount()
        val totalItemCount = layoutManager.getItemCount()
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        if(!isLoading() && !isLastPage()){
       if((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&  firstVisibleItemPosition >= 0){
           loadMoreItems();

       }

        }

    }
    protected abstract fun loadMoreItems()

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean

    companion object {
        val PAGE_START = 1
        /**
         * Set scrolling threshold here (for now i'm assuming 10 item in one page)
         */
        private val PAGE_SIZE = 10
    }
}