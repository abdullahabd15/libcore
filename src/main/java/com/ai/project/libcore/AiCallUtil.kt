package com.ai.project.libcore

import android.Manifest
import android.content.pm.PackageManager
import android.provider.CallLog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*

class AiCallUtil(internal val activity: AppCompatActivity) {

    companion object {
        const val READ_CALL_LOG_PERMISSION_CODE = 1234
    }

    fun getCallHistory(): MutableList<CallLogData>? {
        val callLogDataList = ArrayList<CallLogData>()
        if (ActivityCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            val cursor =
                activity.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)
            val number = cursor!!.getColumnIndex(CallLog.Calls.NUMBER)
            val type = cursor.getColumnIndex(CallLog.Calls.TYPE)
            val date = cursor.getColumnIndex(CallLog.Calls.DATE)
            val duration = cursor.getColumnIndex(CallLog.Calls.DURATION)
            while (cursor.moveToNext()) {
                val callLogData = CallLogData()
                callLogData.phoneNumber = cursor.getString(number)
                val callDate = cursor.getString(date)
                callLogData.callDateTime = Date(callDate.toLong())
                callLogData.callDurationInSeconds = cursor.getString(duration)
                val callType = cursor.getString(type)
                callLogData.callTypeAsInt = Integer.parseInt(callType)

                callLogDataList.add(callLogData)
            }
            cursor.close()
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_CALL_LOG),
                READ_CALL_LOG_PERMISSION_CODE
            )
        }
        return callLogDataList
    }
}