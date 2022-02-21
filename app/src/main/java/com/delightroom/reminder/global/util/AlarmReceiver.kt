package com.delightroom.reminder.global.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.delightroom.reminder.ui.main.MainActivity

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("MJ_DEBUG", "Alarm Received!!")

        intent?.let {
            if (context != null) {
                showAlarm(context, it.getIntExtra(RemindConsts.KEY_REMIND_ID, -1))
            }
        }
    }

    private fun showAlarm(context: Context, remindId: Int){
        with(Intent(context, MainActivity::class.java)) {
            putExtra(RemindConsts.KEY_REMIND_ID, remindId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  //todo mj
            context.startActivity(this)
        }
    }
}