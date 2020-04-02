package com.deepak.besaat.view.activities.chat.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import br.com.safety.audio_recorder.AudioListener
import br.com.safety.audio_recorder.AudioRecording
import br.com.safety.audio_recorder.RecordingItem
import com.deepak.besaat.R
import com.deepak.besaat.databinding.ActivityChatBinding
import com.deepak.besaat.model.chatModel.Chat
import com.deepak.besaat.model.chatModel.ChatHistoryPojo
import com.deepak.besaat.model.chatModel.UploadMediaPojo
import com.deepak.besaat.utils.CommonFunctions
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.FileUtil
import com.deepak.besaat.utils.SharedPref
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.chat.adapter.ChatHistoryListAdapter
import com.deepak.besaat.viewModel.ChatViewModel
import com.devlomi.record_view.OnRecordListener
import com.example.possibility.hr.utils.FilesFunctions
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chat.*
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : BaseActivity() {
    var mSocket: Socket? = null
    val sharedPref: SharedPref by inject()
    val commonFunctions: CommonFunctions by inject()
    var isFABOpen: Boolean = false
    val CHAT_MESSAGE = "chat_message"
    val CHAT_HISTORY = "chat"
    val RECEIVE_MESSAGE = "receive_message"
    var isSocketConnected: Boolean? = null
    var chatHistoryListAdapter: ChatHistoryListAdapter? = null
    var chatHistoryList: MutableList<Chat> = ArrayList()
    lateinit var binding: ActivityChatBinding

    var mAudioRecording: AudioRecording? = null
    var audioListener: AudioListener? = null
    val recorderPermission = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val viewModel: ChatViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
//        setContentView(R.layout.activity_chat)
        init()
        initObserver()
    }

    fun init() {
        customToolBarWithBack(this, toolBar)
        textViewTitleName.text = resources.getString(R.string.chat_)

        binding.viewModel = viewModel
        viewModel.name.set(intent.getStringExtra("name"))
        viewModel.title.set(intent.getStringExtra("name"))
        viewModel.receiverId.set(intent.getStringExtra("id"))
        viewModel.receiverImage.set(intent.getStringExtra("image"))

        viewModel.pickUpAddress.set(sharedPref.getString(Constants.ADDRESS))
        viewModel.pickUpLatitude.set(sharedPref.getString(Constants.latitude))
        viewModel.pickUpLogitude.set(sharedPref.getString(Constants.longitude))
        setAdapter()
        viewModel.getChatHistory()
        initSocket()
        setRecorder()
        closeFABMenu()
    }

    private fun initSocket() {
        var mOptions = IO.Options()
        mOptions.query = "user_id=" + sharedPref.getLong(Constants.USER_ID)
        mSocket = IO.socket(Constants.SERVER_URL + ":" + Constants.SERVER_PORT + "/", mOptions)

        mSocket?.connect()

        mSocket?.on(Socket.EVENT_CONNECT, onConnected)
        mSocket?.on(Socket.EVENT_ERROR, onError)
        mSocket?.on(Socket.EVENT_CONNECT_ERROR, onError)
        mSocket?.on(Socket.EVENT_DISCONNECT, onDisConnected)
        mSocket?.on(CHAT_MESSAGE, onSendNewMessage)
        mSocket?.on(RECEIVE_MESSAGE, onNewMessage)
        mSocket?.on(CHAT_HISTORY, onChatHistory)

        mSocket?.io()?.timeout(-1)

        getChatMessageHistory()
        getNewMessage()
    }

    private fun setAdapter() {
        chatHistoryListAdapter = ChatHistoryListAdapter(
            this,
            chatHistoryList,
            sharedPref.getLong(Constants.USER_ID).toInt(),
            viewModel.pickUpLatitude.get()!!, viewModel.pickUpLogitude.get()!!
        )
        rvChatHistory.adapter = chatHistoryListAdapter
    }

    private fun setRecorder() {
        recordView.visibility = View.INVISIBLE
        recordButton.setRecordView(recordView)
        recordView.setOnRecordListener(object : OnRecordListener {
            override fun onFinish(recordTime: Long) {
                Log.e("RecordView", "onFinish")
                recordView.visibility = View.INVISIBLE
                stopRecording()
            }

            override fun onLessThanSecond() {
                Log.e("RecordView", "onLessThanSecond")
                recordView.visibility = View.INVISIBLE
                cancelRecording()
            }

            override fun onCancel() {
                recordView.visibility = View.INVISIBLE
                Log.e("RecordView", "onCancel")
                cancelRecording()
            }

            override fun onStart() {
                if (checkPermission(recorderPermission) > 0) {
                    ActivityCompat.requestPermissions(
                        this@ChatActivity,
                        arrayOf(recorderPermission[0], recorderPermission[1]),
                        Constants.REQUEST_AUDIO_PERMISSION
                    )
                } else {
                    Log.e("RecordView", "onStart")
                    recordView.visibility = View.VISIBLE
                    startRecording()
                }
            }
        })

//        recordButton.isListenForRecord = false

        recordButton.setOnRecordClickListener {
            Toast.makeText(this@ChatActivity, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show()
            Log.e("RecordButton", "RECORD BUTTON CLICKED")
        }


        recordView.setOnBasketAnimationEndListener {
            Log.e("RecordView", "Basket Animation Finished")
        }

        recordView.cancelBounds = 8.0f//dp
        recordView.setSmallMicColor(Color.parseColor("#c2185b"))
        recordView.setSlideToCancelText("Cancel")
        //disable Sounds
        recordView.setSoundEnabled(false)
        //prevent recording under one Second (it's false by default)
        recordView.setLessThanSecondAllowed(false)
        //set Custom sounds onRecord
        //you can pass 0 if you don't want to play sound in certain state
        recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0)
        //change slide To Cancel Text Color
        recordView.setSlideToCancelTextColor(Color.parseColor("#ff0000"))
        //change slide To Cancel Arrow Color
        recordView.setSlideToCancelArrowColor(Color.parseColor("#ff0000"))
        //change Counter Time (Chronometer) color
        recordView.setCounterTimeColor(Color.parseColor("#ff0000"))
    }

    fun startRecording() {
        audioListener = object : AudioListener {
            override fun onCancel() {
                Toast.makeText(this@ChatActivity, "Cancel", Toast.LENGTH_SHORT).show()
            }

            override fun onError(e: Exception) {
                Toast.makeText(this@ChatActivity, "error" + e.message, Toast.LENGTH_SHORT).show()
            }

            override fun onStop(recordingItem: RecordingItem?) {
                Toast.makeText(this@ChatActivity, "Stop", Toast.LENGTH_SHORT).show()
//                AudioRecording(baseContext).play(recordingItem)
                if (recordingItem?.length != null && recordingItem.length < 1000) {
                    commonFunctions.showFeedbackMessage(rootLayout, "Too small voice message.")
                } else if (recordingItem?.filePath != null) {
                    mImageFile = File(recordingItem.filePath)
                    viewModel.file = mImageFile
                    viewModel.mediaRuntime.set((recordingItem.length / 1000).toString())
                    sendAudioAlertDialog()
                }
            }
        }

        mAudioRecording = AudioRecording(this)
            .setNameFile("/" + UUID.randomUUID() + "-audio.mp4")
            .start(audioListener)
    }

    fun stopRecording() {
        if (mAudioRecording != null) {
            mAudioRecording!!.stop(false)
        }
    }

    fun cancelRecording() {
        if (mAudioRecording != null) {
            mAudioRecording!!.stop(true)
        }
    }

    fun initObserver() {
        ed_message.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
            }
            false
        }

        viewModel.progressBar.observe(this, Observer<Boolean> {
            if (it) {
                commonFunctions.hideProgressBar()
                commonFunctions.showProgressBar(this, getString(R.string.loading))
            } else {
                commonFunctions.hideProgressBar()
            }
        })

        viewModel.feedBackMessage.observe(this, Observer {
            if (it != null)
                commonFunctions.showFeedbackMessage(rootLayout, it)
            else
                commonFunctions.showFeedbackMessage(rootLayout, "Something wrong.")
        })

        viewModel.attachmentClick.observe(this, Observer {
            if (it) {
                if (!isFABOpen) {
                    showFABMenu()
                } else {
                    closeFABMenu()
                }
            }
        })

        viewModel.onSendClick.observe(this, Observer {
            if (it) {
                sendMessage()
            }
        })

        viewModel.onMicClick.observe(this, Observer {
            if (it) {
                if (checkPermission(recorderPermission) > 0) {
                    ActivityCompat.requestPermissions(
                        this@ChatActivity,
                        arrayOf(recorderPermission[0], recorderPermission[1]),
                        Constants.REQUEST_AUDIO_PERMISSION
                    )
                } else {
                    commonFunctions.showFeedbackMessage(
                        rootLayout,
                        "Press and hold mic button to record voice message."
                    )
//                    setRecorder()
                }
            }
        })

        viewModel.onLocationClick.observe(this, Observer {
            if (it) {
                closeFABMenu()
                showSendCurrentLocationDialog()
            }
        })

        viewModel.onDocClick.observe(this, Observer {
            if (it) {
                closeFABMenu()
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "application/pdf"
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(intent, Constants.REQUEST_FILE)
            }
        })

        viewModel.onImageClick.observe(this, Observer {
            if (it) {
                val permission = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (checkPermission(permission) > 0) {
                    ActivityCompat.requestPermissions(
                        this@ChatActivity,
                        arrayOf(permission[0], permission[1]),
                        Constants.REQUEST_PERMISSION
                    )
                } else {
                    dialogForCameraGallery()
                }
            }
        })

        viewModel.successfullyUploadImage.observe(this, Observer {
            var pojo = Gson().fromJson(it, UploadMediaPojo::class.java)
            if (pojo.getStatus() != null && pojo.getStatus()!!) {
                viewModel.uploadedFileName.set(pojo.getFileName())
                attemptSend(viewModel.mediaType.get()!!)
            }
        })

        viewModel.successfullyGetHistory.observe(this, Observer {
            var pojo = Gson().fromJson(it, ChatHistoryPojo::class.java)
            if (pojo.getStatus() != null && pojo.getStatus()!!) {
                chatHistoryList.addAll(pojo.getChats() as MutableList<Chat>)
                chatHistoryListAdapter?.submitList(chatHistoryList)

                nScrollView.post { nScrollView.fullScroll(ScrollView.FOCUS_DOWN) }
            } else {
//                commonFunctions.showFeedbackMessage(rootLayout, it)
            }
        })
    }


    private fun showFABMenu() {
        fabDoc.visibility = View.VISIBLE
        fabLocation.visibility = View.VISIBLE
        isFABOpen = true
        fabLocation.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        fabDoc.animate().translationY(-resources.getDimension(R.dimen.standard_105))
    }

    private fun closeFABMenu() {
        isFABOpen = false
        fabDoc.animate().translationY(0.0f)
        fabLocation.animate().translationY(0.0f)
        fabDoc.visibility = View.GONE
        fabLocation.visibility = View.GONE
    }

    private fun sendMessage() {
        val message: String = ed_message.text.toString().trim()
        if (TextUtils.isEmpty(message)) {
            commonFunctions.showFeedbackMessage(rootLayout, "Can't send empty message")
        } else {
            viewModel.message.set(ed_message.text.toString().trim())
            viewModel.mediaType.set(Constants.MEDIA_CHAT)
            attemptSend(Constants.MEDIA_CHAT)
        }
    }

    private fun attemptSend(mediaType: String) {
        if (isSocketConnected != null && isSocketConnected!!) {
            val jsonObject = JSONObject()   // this object is to send data on server
            var chat = Chat()  // this object is to show data in chat history list
            chat.id = 0

            jsonObject.put("receiverId", viewModel.receiverId.get())
            chat.messageFrom = sharedPref.getLong(Constants.USER_ID).toInt()
            chat.messageTo = viewModel.receiverId.get()!!.toInt()

            //mediaType: 1=>chat, 2=>image, 3=>audio, 4=>video, 5=> Document
            jsonObject.put("mediaType", viewModel.mediaType.get())
            chat.mediaType = viewModel.mediaType.get()

            if (mediaType != Constants.MEDIA_AUDIO && mediaType != Constants.MEDIA_DOCUMENT) {
                jsonObject.put("message", viewModel.message.get())
                chat.message = viewModel.message.get()
            }


            if (mediaType == Constants.MEDIA_LOCATION) {
                jsonObject.put("latitude", viewModel.pickUpLatitude.get())
                jsonObject.put("longitude", viewModel.pickUpLogitude.get())
                jsonObject.put("address", viewModel.pickUpAddress.get())

                chat.latitude = viewModel.pickUpLatitude.get()
                chat.longitude = viewModel.pickUpLogitude.get()
                chat.address = viewModel.pickUpAddress.get()
            }

            if (mediaType != Constants.MEDIA_LOCATION && viewModel.mediaType.get() != Constants.MEDIA_CHAT) {
                jsonObject.put("media", viewModel.uploadedFileName.get())
                chat.media = viewModel.uploadedFileName.get()
                if (!viewModel.uploadedFileName.get()!!.contains("http")) {
                    chat.media = Constants.CHAT_MEDIA_URL + viewModel.uploadedFileName.get()
                }

                if (mediaType == Constants.MEDIA_AUDIO) {
                    jsonObject.put("mediaRuntime", viewModel.mediaRuntime.get())
                    chat.mediaRuntime = viewModel.mediaRuntime.get()
                }
            }

            clearData()
            mSocket?.emit(CHAT_MESSAGE, jsonObject)

            chat.createdAt = commonFunctions.getCurrentDateTime()
            chat.updatedAt = chat.createdAt
            chat.fromUserImage = sharedPref.getString(Constants.USER_IMAGE)
            chat.toUserImage = viewModel.receiverImage.get()

            chatHistoryList.add(chat)
            chatHistoryListAdapter?.submitList(chatHistoryList)
            chatHistoryListAdapter?.notifyDataSetChanged()

            nScrollView.post { nScrollView.fullScroll(ScrollView.FOCUS_DOWN) }

        } else
            commonFunctions.showFeedbackMessage(rootLayout, "Server not connected.")
    }


    fun sendAudioAlertDialog() {
        val builder = AlertDialog.Builder(this!!)
        builder.setTitle("Send voice message")
        builder.setMessage("Do you want to send voice message?")
        builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, i ->
            dialogInterface.dismiss()
            viewModel.mediaType.set(Constants.MEDIA_AUDIO)
            viewModel.uploadFile()
        }

        builder.setNegativeButton(getString(R.string.no)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

    private fun clearData() {
        ed_message.setText("")
        viewModel.message.set("")
        viewModel.mediaType.set("")
        viewModel.uploadedFileName.set("")
    }

    private fun getChatMessageHistory() {
        if (viewModel.receiverId.get() != null && viewModel.receiverId.get() != "") {
            val jsonObject = JSONObject()
            jsonObject.put("receiverId", viewModel.receiverId.get())
            mSocket?.emit(CHAT_HISTORY, jsonObject)
        }
    }

    private fun getNewMessage() {
        if (viewModel.receiverId.get() != null && viewModel.receiverId.get() != "") {
            val jsonObject = JSONObject()
            jsonObject.put("receiverId", viewModel.receiverId.get())
            mSocket?.emit(RECEIVE_MESSAGE, jsonObject)
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            try {
                var chatData = args[0] as JSONObject
                var chat = Gson().fromJson((args[0] as JSONObject).toString(), Chat::class.java)
                chat.id = 0
//                chat.messageFrom = viewModel.receiverId.get()!!.toInt()
//                chat.messageTo = sharedPref.getLong(Constants.USER_ID).toInt()
                chat.mediaType = chatData.optString("mediaType")
                if (chat.media != null && !chat?.media!!.contains("http")) {
                    chat.media = Constants.CHAT_MEDIA_URL + chat.media
                }

                chat.createdAt = CommonFunctions().getFormattedTimeOrDate(
                    chatData.optString("created_at"),
                    Constants.Pattern_YYYY_MM_DD_T_HH_MM_SS_SSSZ,
                    Constants.Pattern_YYYY_MM_DD_HH_MM_SS
                )

                chat.updatedAt = CommonFunctions().getFormattedTimeOrDate(
                    chatData.optString("created_at"),
                    Constants.Pattern_YYYY_MM_DD_T_HH_MM_SS_SSSZ,
                    Constants.Pattern_YYYY_MM_DD_HH_MM_SS
                )
                chat.fromUserImage = viewModel.receiverImage.get()
                chat.toUserImage = sharedPref.getString(Constants.USER_IMAGE)

                chatHistoryList.add(chat)
                chatHistoryListAdapter?.submitList(chatHistoryList)
                chatHistoryListAdapter?.notifyDataSetChanged()
                nScrollView.post { nScrollView.fullScroll(ScrollView.FOCUS_DOWN) }

            } catch (e: java.lang.Exception) {
                commonFunctions.showFeedbackMessage(
                    rootLayout,
                    "New message received. Please open chat again to refresh"
                )
                Log.e("newMsgExp", "" + e.message)
                return@Runnable
            }
            // add the message to view
//            Toast.makeText(this, "New Message", Toast.LENGTH_SHORT).show()
        })
    }

    private val onChatHistory = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            Toast.makeText(this@ChatActivity, "Message Received ", Toast.LENGTH_LONG).show()
            try {
                val data: JSONObject = args[0] as JSONObject
                val username: String
                val message: String
                username = data.getString("username")
                message = data.getString("message")
            } catch (e: JSONException) {
                Log.e("newMsgExp", "" + e.message)
                return@Runnable
            }
            // add the message to view
//            Toast.makeText(this, "Chat history Message", Toast.LENGTH_SHORT).show()
        })
    }


    private val onSendNewMessage = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            Toast.makeText(this@ChatActivity, "Message Sent ", Toast.LENGTH_LONG).show()
            try {
                val data: JSONObject = args[0] as JSONObject
                // add the message to view
            } catch (e: JSONException) {
                return@Runnable
            }
        })
    }

    private val onConnected = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            isSocketConnected = true
//            Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show()
        })
    }

    private val onError = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            isSocketConnected = false
            mSocket?.connect()
            if (Gson().toJson(args).toString().contains("timeout")) {
                commonFunctions.showFeedbackMessage(rootLayout, "Poor connection")
            } else {
                commonFunctions.showFeedbackMessage(
                    rootLayout,
                    "Error:" + Gson().toJson(args).toString()
                )
            }
        })
    }

    private val onDisConnected = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            isSocketConnected = false
//            Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket?.disconnect()
        mSocket?.off(CHAT_MESSAGE, onSendNewMessage)
        mSocket?.off(CHAT_HISTORY, onChatHistory)
        mSocket?.off(RECEIVE_MESSAGE, onNewMessage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_LOCATION && resultCode == Activity.RESULT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                viewModel.pickUpLatitude.set(place?.latLng?.latitude?.toString())
                viewModel.pickUpLogitude.set(place?.latLng?.longitude?.toString())
                viewModel.pickUpAddress.set(place.address)

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        } else if (requestCode == Constants.REQUEST_FILE && resultCode == Activity.RESULT_OK) {
            val uri = Uri.parse(data!!.data!!.toString())
            mImageFile = File(FileUtil.getPath(uri, this))
            viewModel.file = mImageFile
            viewModel.mediaType.set(Constants.MEDIA_DOCUMENT)
            viewModel.uploadFile()
        } else if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.CAMERA_INTENT -> {
                    if (mImageFile != null) {
                        val bitmap = BitmapFactory.decodeFile(mImageFile!!.getPath())
                        var newBitMap =
                            FilesFunctions.changeImageOrientation(mImageFile!!.path, bitmap)
                        mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                        viewModel.file = mImageFile
                        showSendImageDialog(newBitMap)
//                        imageViewLicense.setImageBitmap(newBitMap)
                    } else {
                        if (data != null) {
                            val thumbnail = data!!.extras!!.get("data") as Bitmap
//                        circularImageView!!.setImageBitmap(thumbnail)
                            mImageFile = FilesFunctions.createFileFromBitMap(thumbnail)
                            viewModel.file = mImageFile
                            var resizedBitMap = FilesFunctions.getResizedBitmap(thumbnail, 200, 200)
//                            imageViewLicense.setImageBitmap(resizedBitMap)
                            showSendImageDialog(resizedBitMap)
                        } else {
                            commonFunctions.showFeedbackMessage(
                                rootLayout!!,
                                "Camera not supported. Please pic image from gallery."
                            )
                        }
                    }
                }

                Constants.GALLERY_INTENT -> {
                    var path = FilesFunctions.getPathFromData(this!!, data!!)
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(this!!.getContentResolver(), data!!.data);
                    var newBitMap = FilesFunctions.changeImageOrientation(path, bitmap)
                    mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                    viewModel.file = mImageFile
                    showSendImageDialog(newBitMap)
//                    imageViewLicense.setImageBitmap(newBitMap)
                }
            }
        }
    }

    private fun showSendImageDialog(bitmap: Bitmap) {
        val commonDialog = Dialog(this)
        val inflater = layoutInflater
        val vv = inflater.inflate(R.layout.popup_send_image, null, false)
        var ivImage = vv.findViewById<ImageView>(R.id.ivImage)
        var edMessage = vv.findViewById<EditText>(R.id.edMessage)
        var tvNo = vv.findViewById<TextView>(R.id.tvNo)
        var tvYes = vv.findViewById<TextView>(R.id.tvYes)

        ivImage.setImageBitmap(bitmap)

        tvYes.setOnClickListener {
            viewModel.mediaType.set(Constants.MEDIA_IMAGE)
            viewModel.message.set(edMessage.text.toString().trim())
            commonDialog.dismiss()
            viewModel.uploadFile()
        }

        tvNo.setOnClickListener {
            commonDialog.dismiss()
        }

        commonDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        commonDialog.setContentView(vv)
        commonDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        commonDialog.setCancelable(false)
//        commonDialog.window!!.attributes.windowAnimations = R.style.DialogTheme
        commonDialog.show()
    }

    private fun showSendCurrentLocationDialog() {
        val commonDialog = Dialog(this)
        val inflater = layoutInflater
        val vv = inflater.inflate(R.layout.popup_send_location, null, false)
        var edMessage = vv.findViewById<EditText>(R.id.edMessage)
        var tvNo = vv.findViewById<TextView>(R.id.tvNo)
        var tvYes = vv.findViewById<TextView>(R.id.tvYes)

        tvYes.setOnClickListener {
            viewModel.mediaType.set(Constants.MEDIA_CHAT)
            viewModel.message.set(edMessage.text.toString().trim())
            commonDialog.dismiss()
            attemptSend(Constants.MEDIA_LOCATION)
        }

        tvNo.setOnClickListener {
            commonDialog.dismiss()
        }

        commonDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        commonDialog.setContentView(vv)
        commonDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        commonDialog.setCancelable(false)
        commonDialog.show()
    }
}
