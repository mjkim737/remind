package com.delightroom.reminder.ui.register

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.delightroom.reminder.global.util.RemindConsts
import com.delightroom.reminder.ui.main.MainActivity

class RemindWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams){

    override fun doWork(): Result {
        val remindId : Int = inputData.getInt(RemindConsts.KEY_REMIND_ID, -1)
        Log.e("MJ_DEBUG", "Worker Working...! $remindId")

        showAlarm(remindId)

        return Result.success()
    }

    private fun showAlarm(remindId: Int){
        with(Intent(applicationContext, MainActivity::class.java)) {
            putExtra(RemindConsts.KEY_REMIND_ID, remindId)
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK  //todo mj
            applicationContext.startActivity(this)
        }
    }
}