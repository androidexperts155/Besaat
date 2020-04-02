package com.deepak.besaat.view.VisitActivity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.deepak.besaat.R
import com.deepak.besaat.network.AllLocalHandler
import com.deepak.besaat.utils.Constants
import com.deepak.besaat.utils.FragmentFucntions
import com.deepak.besaat.view.activities.BaseActivity
import com.deepak.besaat.view.activities.home.HomeActivity
import com.deepak.besaat.view.fragments.MyVisitFragment.MyVisitFragment
import com.example.possibility.hr.utils.FilesFunctions
import kotlinx.android.synthetic.main.activity_new_request_store.*
import org.koin.android.ext.android.inject
import java.io.File

class VisitActivity : BaseActivity(){
    var frameLayout:FrameLayout?=null
   // var imageViewTopLeft:ImageView?=null
    val fragmentFucntion: FragmentFucntions by inject()
    lateinit var file :File
    companion object{

        lateinit var imageViewTopLeft :ImageView
        lateinit var textTop:TextView

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit)
       var toolBar=findViewById<Toolbar>(R.id.toolBar)
       imageViewTopLeft=findViewById<ImageView>(R.id.imageCorner)
       textTop=findViewById<TextView>(R.id.textVisit)
        file=File("abnc")
      //  customToolBarWithMenu(this, toolBar)
        frameLayout=findViewById(R.id.frameContainer)
        var myVisitFragment= MyVisitFragment()
        fragmentFucntion.replaceFragment(this, myVisitFragment, R.id.frameContainer)
    }



    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@VisitActivity, HomeActivity::class.java))
        finishAffinity()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Constants.GALLERY_INTENT){
            if(resultCode== Activity.RESULT_OK){
                //   imageViewLicense.setImageBitmap(data?.extras?.get("data") as Bitmap)
                //  add_photo.setImageURI(data?.data)
                // Log.e("Data","data is  "+data?.data.toString() +"    "+data?.data.toString().reversed())
                var path = FilesFunctions.getPathFromData(this!!, data!!)
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this!!.getContentResolver(), data!!.data);
                var newBitMap = FilesFunctions.changeImageOrientation(path, bitmap)
                mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                file =mImageFile as File
                //viewModel.file = mImageFile
                add_photo.setImageBitmap(newBitMap)
                textCurrentAddress11.text=bitmap.toString().substring(bitmap.toString().indexOf("@"))

                AllLocalHandler.providerSignUpDetailToServer.requestImage=mImageFile

            }
        }
        if(requestCode == Constants.CAMERA_INTENT){
            val bitmap = BitmapFactory.decodeFile(mImageFile!!.getPath())
            var newBitMap = FilesFunctions.changeImageOrientation(mImageFile!!.path, bitmap)
            mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
            file =mImageFile as File

            //   viewModel.file = mImageFile
            add_photo.setImageBitmap(newBitMap)
            textCurrentAddress11.text=bitmap.toString().substring(bitmap.toString().indexOf("@")+2)
            AllLocalHandler.providerSignUpDetailToServer.requestImage=mImageFile

        }
    }
}
