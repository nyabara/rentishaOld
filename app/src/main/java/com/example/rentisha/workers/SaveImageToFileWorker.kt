package com.example.rentisha.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.rentisha.util.KEY_IMAGE_URIS
import java.io.File

private const val TAG = "SaveImageToFileWorker"
class SaveImageToFileWorker(ctx:Context,params:WorkerParameters): Worker(ctx,params) {
    override fun doWork(): Result {
        val appContext = applicationContext
        val resourceUris = inputData.getStringArray(KEY_IMAGE_URIS)
        makeStatusNotification("Saving images to temp file ",appContext)
        sleep()
        return try {
            if (TextUtils.isEmpty(resourceUris.toString())){
                throw IllegalArgumentException("Invalid input uri")
            }

            val pictureList = arrayListOf<Bitmap>()
            Log.d("resoucesUri",resourceUris.toString())
            if (resourceUris !=null){
                for (picture in resourceUris){
                    val resolver =appContext.contentResolver
                    val picture = BitmapFactory.decodeStream(resolver.openInputStream(Uri.fromFile(
                        File(picture))))
                    pictureList.add(picture)
                }
            }
            val outputUri = writeBitmapsToFile(appContext,pictureList)
            val outputData = workDataOf(KEY_IMAGE_URIS to outputUri.toString())
            Log.d(TAG,outputData.toString())
            Result.success(outputData)
        }catch (throwable: Throwable) {
            throwable.printStackTrace()
            Result.failure()
        }
    }
}