package com.ai.project.libcore

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

abstract class AiCallStateReceiver: BroadcastReceiver() {
    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var isIncoming: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.listen(AiPhoneStateListener(context),
                PhoneStateListener.LISTEN_CALL_STATE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    protected abstract fun onOutgoingCallStarted(context: Context, phoneNumber: String)

    protected abstract fun onOutgoingCallEnded(context: Context, phoneNumber: String)

    protected abstract fun onIncomingCallReceived(context: Context, phoneNumber: String)

    protected abstract fun onIncomingCallAnswered(context: Context, phoneNumber: String)

    protected abstract fun onIncomingCallEnded(context: Context, phoneNumber: String)

    private inner class AiPhoneStateListener constructor(internal var context: Context) : PhoneStateListener() {

        override fun onCallStateChanged(state: Int, phoneNumber: String) {
            super.onCallStateChanged(state, phoneNumber)
            if (lastState == state) {
                return
            }
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    isIncoming = true
                    onIncomingCallReceived(context, phoneNumber)
                }
                TelephonyManager.CALL_STATE_OFFHOOK ->
                    if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                        isIncoming = false
                        onOutgoingCallStarted(context, phoneNumber)
                    } else {
                        isIncoming = true
                        onIncomingCallAnswered(context, phoneNumber)
                    }
                TelephonyManager.CALL_STATE_IDLE ->
                    if (isIncoming) {
                        onIncomingCallEnded(context, phoneNumber)
                    } else {
                        onOutgoingCallEnded(context, phoneNumber)
                    }
            }
            lastState = state
        }
    }
}