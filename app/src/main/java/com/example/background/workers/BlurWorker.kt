package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R

private const val TAG = "BlurWorker"
class BlurWorker(val context: Context,val workerParams: WorkerParameters):Worker(context,workerParams) {
    override fun doWork(): Result {
        val appContext = context.applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        makeStatusNotification("Blur image", context)
        return try {
            if(TextUtils.isEmpty(resourceUri)){
                Log.e(TAG,"Inavlid input Uri")
            }
            val resolver = appContext.contentResolver
            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(
                    resourceUri
                ))
            )
            val output = blurBitmap(picture, appContext)
            val outputUri = writeBitmapToFile(appContext, output)
            makeStatusNotification("Output is $outputUri", appContext)
            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
            Result.success(outputData)
        }catch (e:Throwable){
            Result.failure()
        }
    }
}