package com.deepak.besaat.view.activities.storeDetail

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.Observer

import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.deepak.besaat.R
import com.deepak.besaat.model.reviewDetails.reviewDetails
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.newRequestStore.NewRequestStore
import com.deepak.besaat.view.activities.storeDetail.adapter.ReviewsAdapter
import com.deepak.besaat.viewModel.CustomerProfileViewModell
import kotlinx.android.synthetic.main.activity_new_request_store.toolBar
import kotlinx.android.synthetic.main.activity_store_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.koin.android.ext.android.inject
import org.json.JSONObject

class StoreDetailActivity : BaseActivity() {
    var reviewList: MutableList<reviewDetails> = ArrayList()
    val viewModel: CustomerProfileViewModell by inject()
    var adapter: ReviewsAdapter? = null
    var mPopupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_detail)
        var title: String = intent.extras?.get("title") as String
        var imageUrl: String = intent.extras?.get("imageurl") as String

        var latitute: Double = intent.extras?.get("latitute") as Double
        var longitute: Double = intent.extras?.get("longitute") as Double
        var address: String = intent.extras?.get("location") as String
        var palceId: String = intent.extras?.get("placeid") as String
        var rating: Float = intent.extras?.get("rating") as Float
        var noTotalRatingUsers: Int = intent.extras?.get("no_rating_users") as Int

        var textTitle: TextView = findViewById(R.id.textView26)
        var textBelowRating: TextView = findViewById(R.id.textView29)
        var imageProfile: ImageView = findViewById(R.id.imageProfile)
        var textRating: TextView = findViewById(R.id.textRating)
        var ratingBar: RatingBar = findViewById(R.id.ratingBar)
        var textRatingOutOf: TextView = findViewById(R.id.ratingTextOutOf)
        var textAddress: TextView = findViewById(R.id.textView27)
        var textRequest: TextView = findViewById(R.id.textViewRequest)

        textTitle.text = title
        textAddress.text = address
        textBelowRating.text = String.format("%d Ratings", noTotalRatingUsers)
        textRating.text = rating.toString()
        textRatingOutOf.text = rating.toString()
        ratingBar.rating = rating

        viewModel.getReviews(palceId)
        initObserver()

        textView56.setOnClickListener {
            showPopUpGoogleInfoWindow(it)
        }

        textRequest.setOnClickListener {
            var int: Intent = Intent(this@StoreDetailActivity, NewRequestStore::class.java)
            int.putExtra("latitute", latitute)
            int.putExtra("longitute", longitute)
            int.putExtra("location", address)
            int.putExtra("title", title)
            int.putExtra("from", "")
            int.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(int)
        }

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.icn_no_image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageProfile.setImageResource(R.drawable.icn_no_image)
                    // holder.progress.visibility=View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    //   holder.progress.visibility=View.VISIBLE
                    return false
                }
            })
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(imageProfile)

        customToolBarWithBack(this, toolBar)
        textViewTitleName.text = getString(R.string.store_detail)
        setAdapter()
    }

    private fun showPopUpGoogleInfoWindow(view: View) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val customView = LayoutInflater.from(this@StoreDetailActivity)
            .inflate(R.layout.pop_window_text_info, null, false)
        val textView = customView.findViewById<TextView>(R.id.textViewInfo)

        textView.text = resources.getString(R.string.google_rating_info)

        mPopupWindow = PopupWindow(
            customView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        mPopupWindow!!.isOutsideTouchable = true

        val displayMetrics = resources.displayMetrics
        val height = displayMetrics.heightPixels * 2 / 3
        if (location[1] > height) {
            mPopupWindow!!.showAsDropDown(view, 0, -320);
        } else {
            mPopupWindow!!.showAsDropDown(view, 0, 0)
        }
    }

    private fun initObserver() {
        viewModel.jsonResponse.observe(this, Observer {
            handleResponse(it)
        })
    }

    private fun handleResponse(it: JSONObject?) {
        reviewList = ArrayList()
        Log.e("Results", "results values  " + it)
        //  var reviewDetails = reviewDetails()
        //  reviewDetails.profilePhotoUrl = ""
        // reviewDetails.textComment = "xyz"
        // reviewList.add(reviewDetails)
        //  Log.e("Results","review list is "+reviewList.get(0).textComment)

        try {
            var jsonObj = it?.getJSONObject("result")

            var jsonArray: JSONArray = jsonObj?.getJSONArray("reviews")!!
            for (item: JSONObject in jsonArray) {

                var reviewDetails = reviewDetails()

                reviewDetails.profilePhotoUrl = item.optString("profile_photo_url")
                reviewDetails.textComment = item.optString("text")
                reviewDetails.rating = item.optInt("rating")
                reviewList.add(reviewDetails)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //setAdapter()
        adapter?.reviewList(reviewList)
        adapter?.notifyDataSetChanged()

        ratingBarCalculation()
    }

    private fun setAdapter() {
        adapter = ReviewsAdapter(this, reviewList)
        recyclerViewReviews.layoutManager = LinearLayoutManager(this)
//        recyclerViewReviews.addItemDecoration(
//            DividerItemDecoration(
//                ContextCompat.getDrawable(
//                    this@StoreDetailActivity,
//                    R.drawable.divider
//                )
//            )
//        )
        recyclerViewReviews.adapter = adapter
    }

    fun ratingBarCalculation() {
        if (intent.extras?.get("no_rating_users") as Int <= 5 && reviewList.size <= 5) {
            var user1Rating = 0
            var user2Rating = 0
            var user3Rating = 0
            var user4Rating = 0
            var user5Rating = 0

            for (i in 0 until reviewList.size) {
                when (reviewList[i].rating) {
                    5 -> {
                        user5Rating++
                    }
                    4 -> {
                        user4Rating++
                    }
                    3 -> {
                        user3Rating++
                    }
                    2 -> {
                        user2Rating++
                    }
                    1 -> {
                        user1Rating++
                    }
                }
            }

            progress5.max = reviewList.size
            progress5.progress = user5Rating

            progress4.max = reviewList.size
            progress4.progress = user4Rating

            progress3.max = reviewList.size
            progress3.progress = user3Rating

            progress2.max = reviewList.size
            progress2.progress = user2Rating

            progress1.max = reviewList.size
            progress1.progress = user1Rating
        }
    }

    private operator fun JSONArray?.iterator(): Iterator<JSONObject> =
        (0 until this!!.length()).asSequence().map { get(it) as JSONObject }.iterator()
}
