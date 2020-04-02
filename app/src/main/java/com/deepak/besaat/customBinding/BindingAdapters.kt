package com.rv.isabitv.customBinding

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.deepak.besaat.R
import de.hdodenhof.circleimageview.CircleImageView


@BindingAdapter("imageFromBitmap")
fun bindimageFromBitmap(imageView: CircleImageView, bitmap: Bitmap?) {
    if (bitmap != null) {
        imageView.setImageBitmap(bitmap)
    }
}

@BindingAdapter("imageFromDrawable")
fun setImageDrawable(view: ImageView, drawable: Drawable?) {
    view.setImageDrawable(drawable)
}

@BindingAdapter("imageFromResource")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}


@BindingAdapter("imageFromBitmapRectangle")
fun bindimageFromBitmapRectangle(imageView: ImageView, bitmap: Bitmap?) {
    if (bitmap != null) {
        imageView.setImageBitmap(bitmap)
    }
}


@BindingAdapter("imageFromUrl")
fun bindimageFromUrl(imageView: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .into(imageView)
    } else {
        Glide.with(imageView.context)
            .load(R.drawable.icn_no_image)
            .into(imageView)
    }
}

@BindingAdapter("imageFromUrl")
fun bindCircularimageFromUrl(imageView: CircleImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .into(imageView)
    }
}

/*
@BindingAdapter("movie")
fun bindimageFromUrl2(imageView: ImageView, movie: Movie?) {
    var imageUrl = ""
    if (movie!!.folder.equals("tv_serails")||movie!!.folder.equals("episodes")) {
        imageUrl = Constants.MEDIA_URL + movie.folder + "/" + movie.serial_id + "/" + movie.image
    } else {
        imageUrl = Constants.MEDIA_URL + movie.folder + "/" + movie.image
    }
    if (imageUrl != "") {
        Glide.with(imageView.context)
                .load(imageUrl)
                .into(imageView)
    }
}

@BindingAdapter("circleImageFromUrl")
fun circleImageFromUrl(imageView: CircleImageView, imageUrl: String?) {
    if (!imageUrl!!.contains("null")) {
        Glide.with(imageView.context)
                .load(imageUrl)
                .into(imageView)
    } else {
        imageView.setImageResource(R.color.colorPrimary)
    }
}


@BindingAdapter("imageUrl", "type")
fun loadProfileImage(imageView: CircleImageView, imageUrl: String?, type: String) {
    if (type.equals("Normal")) {
        if (!imageUrl!!.contains("null") && !imageUrl!!.equals("")) {
            var requestOptions = RequestOptions()
            requestOptions.placeholder(R.color.colorPrimary)
            Glide.with(imageView.context)
                    .load(Constants.MEDIA_URL + "/" + Constants.PROFILE + "/" + imageUrl).apply(requestOptions)
                    .into(imageView)
        } else {
            imageView.setImageResource(R.color.colorPrimary)
        }
    } else {
        if (!imageUrl!!.contains("null") && !imageUrl!!.equals("")) {
            Glide.with(imageView.context)
                    .load(imageUrl)
                    .into(imageView)
        } else {
            imageView.setImageResource(R.color.colorPrimary)
        }
    }


}
*/