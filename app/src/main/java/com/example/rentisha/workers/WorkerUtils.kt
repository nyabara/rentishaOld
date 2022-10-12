package com.example.rentisha.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.rentisha.R
import com.example.rentisha.util.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.Throws

private const val TAG = "WorkerUtils"

//make a notification so that you know when different steps of the background work chain are starting

fun makeStatusNotification(message:String,context: Context){

    //make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERSBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID,name,importance)
        channel.description = description

        //add the channel
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID,builder.build())

}
// method for sleeping for a fixed amount of time to emulate slower work
fun sleep(){
    try {
        Thread.sleep(DELAY_TIME_MILLIS,0)
    }catch (e:InterruptedException){
        Log.e(TAG,e.message.toString())
    }
}

// write array of bitmaps to a temporary file and return the uri for the file
@Throws(FileNotFoundException::class)
fun writeBitmapsToFile(applicationContext: Context,bitmaps: ArrayList<Bitmap>):Uri{
    val name = String.format("house-image-output-%s.png",UUID.randomUUID().toString())
    val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
    if (!outputDir.exists()){
        outputDir.mkdirs()
    }
    val outputFile = File(outputDir,name)
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(outputFile,true)
        for (bitmap in bitmaps){
            bitmap.compress(Bitmap.CompressFormat.PNG,0,out)
        }
    }
    finally {
        out?.let {
            try {
                it.close()
            }catch (ignore:IOException){

            }
        }
    }
    return Uri.fromFile(outputFile)

}