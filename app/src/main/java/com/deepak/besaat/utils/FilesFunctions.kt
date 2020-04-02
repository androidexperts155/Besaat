package com.example.possibility.hr.utils

import android.app.Activity
import android.content.Context
import android.os.Environment
import java.io.File
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import android.content.Intent
import android.graphics.Matrix
import android.media.ExifInterface
import com.deepak.besaat.utils.Constants
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object FilesFunctions {
    lateinit var file: File
    fun createImageFile(): File {
        val imageFileName = Constants.APP_NAME
        val storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
        var image: File
        image = File.createTempFile(imageFileName, ".jpg", storageDir)
        return image
    }

    fun createImageFile2(activity: Activity): File {
        val imageFileName = Constants.APP_NAME
        val storageDir = activity.getExternalFilesDir(
            Environment.DIRECTORY_PICTURES)
        var image: File
        image = File.createTempFile(imageFileName, ".jpg", storageDir)
        return image
    }

    fun getResizedBitmap(image: Bitmap, bitmapWidth: Int, bitmapHeight: Int): Bitmap {
        var resizedBitMap = Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true)
        return resizedBitMap;
    }
    fun createFileFromBitMap(bitmap: Bitmap):File {
        val videoFileName = Constants.APP_NAME+"-"+ System.currentTimeMillis() + ".jpg"
        val myDirectory = File(Environment.getExternalStorageDirectory(), "IsabiTv")
        if (!myDirectory.exists()) {
            myDirectory.mkdir()
        }
        file = File(myDirectory, videoFileName)
        file.createNewFile()

        //Convert bitmap to byte array
        var bos = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        var bitmapData = bos.toByteArray()

        //write the bytes in file
        var fos = FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close()
        return file;
    }


    fun getPathFromData(context: Context, data: Intent): String {
        val selectedImageUri = data.data
        val filePath = arrayOf(MediaStore.Images.Media.DATA)
        val c = context.contentResolver.query(selectedImageUri!!, filePath, null, null, null)
        c!!.moveToFirst()
        val columnIndex = c.getColumnIndex(filePath[0])
        return c.getString(columnIndex)
    }




    fun changeImageOrientation(photoPath: String, bitmap: Bitmap): Bitmap {
        var ei = ExifInterface(photoPath);
        var orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        var rotatedBitmap: Bitmap? = null;
        when (orientation) {

            ExifInterface.ORIENTATION_ROTATE_90 ->
                rotatedBitmap = rotateImage(bitmap, 90.toFloat());


            ExifInterface.ORIENTATION_ROTATE_180 ->
                rotatedBitmap = rotateImage(bitmap, 180.toFloat());


            ExifInterface.ORIENTATION_ROTATE_270 ->
                rotatedBitmap = rotateImage(bitmap, 270.toFloat());


            ExifInterface.ORIENTATION_NORMAL ->
                rotatedBitmap = bitmap;
            else ->
                rotatedBitmap = bitmap
        }
        return rotatedBitmap!!;
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }


    /*fun getFolderSize(dir: File): Long {
        var size: Long = 0
        for (file in dir.listFiles()!!) {
            if (file.isFile) {
                // System.out.println(file.getName() + " " + file.length());
                size += file.length()
            } else
                size += getFolderSize(file)
        }
        return size
    }


    fun deleteFile(file: File?): Boolean {
        if (file != null) {
            if (file.isDirectory) {
                val children = file.list()
                for (i in children!!.indices) {
                    val success = deleteFile(File(file, children[i]))
                    if (!success) {
                        return false
                    }
                }
            }
            return file.delete()
        }
        return false
    }*/
}