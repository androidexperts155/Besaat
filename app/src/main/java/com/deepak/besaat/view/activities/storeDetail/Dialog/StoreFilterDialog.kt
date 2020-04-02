package com.deepak.besaat.view.activities.storeDetail.Dialog

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.deepak.besaat.Interfaces.passValue
import com.deepak.besaat.R
import kotlinx.android.synthetic.main.rating_items.*


class StoreFilterDialog : DialogFragment() {
   var passValue :passValue?=null
    var seekBar:SeekBar ?=null
  lateinit var value:String
    lateinit var rating:String
    var distanceValue:String=""
    var ratingValue:String=""
    var radioButton1:RadioButton?=null
    var radioButton2:RadioButton?=null
    var radioButton3:RadioButton?=null
    var radioButton4:RadioButton?=null
    var radioButton5:RadioButton?=null
    var radioButtonDefault:RadioButton?=null
    override fun dismiss() {
        super.dismiss()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var myView = inflater!!.inflate(R.layout.search_pop_up_layout, container, false)
        var radioGroup=myView.findViewById<RadioGroup>(R.id.groupradio)
        var textViewContinue=myView.findViewById<TextView>(R.id.textViewContinue)
        radioButton1=myView.findViewById(R.id.radia_id1)
        radioButton2=myView.findViewById(R.id.radia_id2)
        radioButton3=myView.findViewById(R.id.radia_id3)
        radioButton4=myView.findViewById(R.id.radia_id4)
        radioButton5=myView.findViewById(R.id.radia_id5)
        radioButtonDefault=myView.findViewById(R.id.radia_id11)
        seekBar=myView.findViewById<SeekBar>(R.id.simpleSeekBar)

        if(!ratingValue.equals("")) {
            Handler().postDelayed({
                seekBar?.progress = ratingValue.toInt()
            }, 300)



        }
        if(ratingValue.equals("5")){
           // radioButton5?.setChecked(true);
            Handler().postDelayed({
               // radioGroup.check(R.id.radia_id5)
                radioButton5?.setChecked(true);
            }, 300)
            //radioGroup.check(R.id.radia_id5)

        }else if(ratingValue.equals("4")){
            Handler().postDelayed({
               // radioGroup.check(R.id.radia_id4)
                radioButton4?.setChecked(true);
            }, 300)

          //  radioButton4?.setChecked(true);

        }else if(ratingValue.equals("3")){
            Handler().postDelayed({
              //  radioGroup.check(R.id.radia_id3)
                radioButton3?.setChecked(true);
            }, 300)

          //  radioButton3?.setChecked(true);
        }else if(ratingValue.equals("2")){
            Handler().postDelayed({
              //  radioGroup.check(R.id.radia_id2)
                radioButton2?.setChecked(true);
            }, 300)

          //  radioButton2?.setChecked(true);

        }else if(ratingValue.equals("1")){
            Handler().postDelayed({
              //  radioGroup.check(R.id.radia_id1)
                radioButton1?.setChecked(true);
            }, 300)


         //   radioButton1?.setChecked(true);

        }else if(ratingValue.equals("")){
            Handler().postDelayed({
                radioGroup.check(R.id.radia_id11)
            }, 300)

          //  radioButtonDefault?.setChecked(true)
        }
      //  Window.clearFlags(ViewGroup.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
      //  {
            //this.setFinishOnTouchOutside(false);
      //  }
       // else
       // {
        //    .clearFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        //}*/
      //  var imageIcon=myView.findViewById<ImageView>(R.id.imageIcon)
       // imageIcon.setColorFilter(R.color.places_text_white_alpha_26)
         value="5000"
        rating=""
        radioGroup.setOnCheckedChangeListener(object :RadioGroup.OnCheckedChangeListener{

            override fun onCheckedChanged(group: RadioGroup?, viewID: Int) {
                if(viewID==R.id.radia_id11){
                   // value="5000"
                    rating=""
                   // passValue?.value("5000")
                }
                if(viewID == R.id.radia_id1){
                   // value="5000"
                    rating="5"
                   // passValue?.value("5000")
                }
                if(viewID == R.id.radia_id2){
                   // value="4000"
                    rating="4"
                  //  passValue?.value("4000")
                }

                if(viewID == R.id.radia_id3){
                   // value="3000"
                    rating="3"
                    //passValue?.value("3000")
                }
                if(viewID == R.id.radia_id4){
                    //value="2000"
                    rating="2"
                  //  passValue?.value("2000")
                }
                if(viewID == R.id.radia_id5){
                   // value="1000"
                    rating="1"
                   // passValue?.value("1000")
                }
            }
        })
        seekBar?.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.e("progressChange","progress change seek "+p1)
                seekbardetails.text=p1.toString() +" km"
                value=p1.toString()+"00"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        textViewContinue.setOnClickListener(object :View.OnClickListener{
            override fun onClick(view: View?) {

                passValue?.value(value,rating)
            }
        })

        return myView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun   newInstance(
            passValue: passValue,
            distanceValue: String,
            ratingString: String
        ) : StoreFilterDialog {
            var fragment : StoreFilterDialog = StoreFilterDialog()
            fragment.passValue=passValue
            fragment.distanceValue=distanceValue
            fragment.rating=ratingString
            Log.e("chekedValue","checked value is "+distanceValue+"  "+ratingString)
            return fragment
           // fragment.sourceDetails=sourceDetails
           // fragment.repository=repository


        }
        /* BlankFragment().apply {
             arguments = Bundle().apply {
                 putString(ARG_PARAM1, param1)
                 putString(ARG_PARAM2, param2)
             }
         }*/
    }




}