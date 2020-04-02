package com.deepak.besaat.view.activities.chat.adapter

import android.app.Activity
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepak.besaat.R
import com.deepak.besaat.databinding.AdapterCourierGuyzLocalBinding
import com.deepak.besaat.model.chatModel.Chat
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class ChatHistoryListAdapter(
    var activity: Activity,
    var chatHistoryList: MutableList<Chat>,
    var userId: Int,
    var lat: String, var lng: String
) :
    ListAdapter<Chat, ChatHistoryListAdapter.ViewHolder>(StoreDetailDiffCallback()) {
    lateinit var binding: AdapterCourierGuyzLocalBinding
    lateinit var itemList: OnItemClick2

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    var mediaPlayer: MediaPlayer? = null
    //    var isPlaying: Boolean = false
    var currentClickedplayPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View
        MapsInitializer.initialize(activity)
        createMediaPlayer()
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_customview_layout_right, parent, false)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_customview_layout_left, parent, false)
        }

        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).messageFrom == userId)
            VIEW_TYPE_MESSAGE_SENT
        else
            VIEW_TYPE_MESSAGE_RECEIVED

        return super.getItemViewType(position)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            Picasso.get().load(getItem(position).fromUserImage).fit()
                .placeholder(R.drawable.bd_blue_drawable)
//                .rotate(90.0f)
                .error(R.drawable.icn_no_image)
                .into(holder.cv_profile_image)

        holder.txtInfo.text = getItem(position).message

        if (getItem(position).message == null || getItem(position).message?.trim() == "")
            holder.rlBackground.visibility = View.GONE
        else
            holder.rlBackground.visibility = View.VISIBLE

        holder.tv_date.text = CommonFunctions().getFormattedTimeOrDate(
            getItem(position).createdAt!!,
            Constants.Pattern_YYYY_MM_DD_HH_MM_SS,
            Constants.Pattern_DD_MMM_YY_HH_MM_AA
        )

        if (getItem(position).mediaType != null && getItem(position).mediaType == Constants.MEDIA_IMAGE) {
            holder.image.visibility = View.VISIBLE
            holder.map.visibility = View.GONE
            holder.llDoc.visibility = View.GONE
            holder.llAudio.visibility = View.GONE

            if (getItem(position).media != null)
                Picasso.get().load(getItem(position).media).fit()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.icn_no_image)
                    .into(holder.image)
        } else if (getItem(position).mediaType != null && getItem(position).mediaType == Constants.MEDIA_CHAT) {
            if (getItem(position).longitude != null && getItem(position).longitude != "") {
                holder.image.visibility = View.GONE
                holder.map.visibility = View.VISIBLE
                holder.llDoc.visibility = View.GONE
                holder.llAudio.visibility = View.GONE
                holder.map.onCreate(null)
                holder.map.onResume()
                holder.map.getMapAsync {
                    var latLng = LatLng(
                        getItem(position).latitude!!.toDouble(),
                        getItem(position).longitude!!.toDouble()
                    )
                    it.addMarker(MarkerOptions().position(latLng).title(""))
                    it.moveCamera(CameraUpdateFactory.newLatLng(latLng))

                    it.setOnMapClickListener {
                        val intent = Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "geo:" + getItem(position).latitude!! + "," + getItem(position).longitude!! + "?q=" + lat +
                                        "," + lng + "(" + getItem(position).address + ")"
                            )
                        )
                        activity.startActivity(intent)
                    }
                }
            }
        } else if (getItem(position).mediaType != null && getItem(position).mediaType == Constants.MEDIA_DOCUMENT) {
            if (getItem(position).media != null) {
                holder.image.visibility = View.GONE
                holder.map.visibility = View.GONE
                holder.llDoc.visibility = View.VISIBLE
                holder.llAudio.visibility = View.GONE

                holder.llDoc.setOnClickListener {

                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getItem(position).media)
                    )
                    activity.startActivity(browserIntent)
                }
            }
        } else if (getItem(position).mediaType != null && getItem(position).mediaType == Constants.MEDIA_AUDIO) {
            if (getItem(position).media != null) {
                holder.image.visibility = View.GONE
                holder.map.visibility = View.GONE
                holder.llDoc.visibility = View.GONE
                holder.llAudio.visibility = View.VISIBLE
                var min: Int = 0
                var sec: Int = 0
                if (getItem(position).mediaRuntime != null) {
                    min = getItem(position).mediaRuntime!!.toInt() / 60
                    sec = getItem(position).mediaRuntime!!.toInt() % 60
                    holder.progress_bar.max =
                        getItem(position).mediaRuntime!!.toInt()
                }

                if (getItem(position).isPlaying)
                    holder.ivPlay.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_stop))
                else
                    holder.ivPlay.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_play))


                holder.tvTotalTime.text = String.format("%02d:%02d", min, sec)
                holder.ivPlay.setOnClickListener {
                    try {
                        var runnableCode = object : Runnable {
                            override fun run() {
                                if (getItem(position).isPlaying && currentClickedplayPosition != -1) {
                                    var seconds = (mediaPlayer?.currentPosition)!!.toInt() / 1000
                                    var minPro: Int = seconds.toString().toInt() / 60
                                    var secPro: Int = seconds.toString().toInt() % 60
                                    holder.tvProgress.text =
                                        String.format("%02d:%02d/", minPro, secPro)
                                    holder.progress_bar.progress = seconds
                                    getItem(position).handler.postDelayed(this, 100)
                                }
                            }
                        }

                        if (getItem(position).isPlaying && currentClickedplayPosition == position) {
                            mediaPlayer?.seekTo(0)
                            getItem(position).isPlaying = false
                            getItem(position).handler.removeCallbacks(runnableCode)
                            if (mediaPlayer?.isPlaying!!) {
                                mediaPlayer?.stop()
                                getItem(position).isPlaying = false
                                holder.ivPlay.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_play))
                            }
                            notifyItemChanged(position)
                        } else {
                            if (currentClickedplayPosition != -1) {
                                mediaPlayer?.seekTo(0)
                                getItem(currentClickedplayPosition).isPlaying = false
                                getItem(currentClickedplayPosition).handler.removeCallbacks(
                                    runnableCode
                                )
                                if (mediaPlayer?.isPlaying!!) {
                                    mediaPlayer?.stop()
                                    getItem(currentClickedplayPosition).isPlaying = false
                                }
                                notifyItemChanged(currentClickedplayPosition)
                            }
                            getItem(position).isPlaying = true
                            currentClickedplayPosition = position
                        }

                        if (getItem(position).isPlaying) {
                            createMediaPlayer()
                            mediaPlayer?.setDataSource(getItem(position).media)
                            mediaPlayer?.prepare()
                            mediaPlayer?.start()
                            holder.ivPlay.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_stop))
                            mediaPlayer?.setOnCompletionListener {
                                getItem(position).isPlaying = false
                                currentClickedplayPosition = -1
                                holder.ivPlay.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_play))
                            }
                            getItem(position).handler?.postDelayed(runnableCode, 100)
                        }
//                        else{
//                            getItem(position).handler.removeCallbacks(runnableCode)
//                        }

                    } catch (e: Exception) {
                        Log.e("playerExp", "" + e.message)
                    }
                }
            }
        }
    }

    private fun createMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder().setContentType(
                AudioAttributes.CONTENT_TYPE_MUSIC
            ).build()
        )
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        internal var txtInfo: TextView = itemView.findViewById(R.id.txtInfo)
        internal var tv_date: TextView = itemView.findViewById(R.id.tv_date)
        internal var tvTotalTime: TextView = itemView.findViewById(R.id.tvTotalTime)
        internal var tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        internal var image: ImageView = itemView.findViewById(R.id.image)
        internal var progress_bar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        internal var ivPlay: ImageView = itemView.findViewById(R.id.ivPlay)
        internal var llDoc: LinearLayout = itemView.findViewById(R.id.llDoc)
        internal var llAudio: LinearLayout = itemView.findViewById(R.id.llAudio)
        internal var rlBackground: RelativeLayout = itemView.findViewById(R.id.rlBackground)
        internal var map: MapView = itemView.findViewById(R.id.map)
        internal var cv_profile_image: CircleImageView =
            itemView.findViewById(R.id.cv_profile_image)
    }

    private class StoreDetailDiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
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