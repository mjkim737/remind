package com.delightroom.reminder.ui.register

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.data.dao.RemindDao
import com.delightroom.reminder.global.base.BaseViewModel
import com.delightroom.reminder.global.util.AlarmReceiver
import com.delightroom.reminder.global.util.RemindConsts
import com.delightroom.reminder.global.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(private val remindDao: RemindDao) : BaseViewModel() {
    val saveEvent = SingleLiveEvent<Any>()
    val saveCompleted = SingleLiveEvent<Long>()

    //리마인드 저장 버튼 클릭
    fun saveRemindBtn(){
        saveEvent.call()
    }

    //리마인드 데이터 저장
    fun saveRemindData(remind: Remind){
        CoroutineScope(Dispatchers.IO).launch {
            saveCompleted.postValue(remindDao.insert(remind))
        }
    }

    //리마인드 데이터 수정
    fun modifyRemindData(remind: Remind) {
        CoroutineScope(Dispatchers.IO).launch {
            remindDao.update(remind)
            withContext(Dispatchers.Main){
                saveCompleted.call()
            }
        }
    }

    /**
     * 디바이스 내 Ringtone 정보 가져오기
     * @return HashMap<Ringtone Title, Ringtone Uri>
     */
    fun getRingtoneMap(context: Context): HashMap<String, String> {
        val ringtoneManager = RingtoneManager(context)
        ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE)
        val cursor = ringtoneManager.cursor
        val ringtoneMap = hashMapOf<String, String>()

        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val uri = ringtoneManager.getRingtoneUri(cursor.position)

            ringtoneMap[title] = uri.toString()
        }
        cursor.close()

        return ringtoneMap
    }

    /**
     * AlarmManger에 리마인드 알림 등록
     */
    fun registerAlarm(context:Context, time: Long, remindId: Int){
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(RemindConsts.KEY_REMIND_ID, remindId)
        val pIntent = PendingIntent.getBroadcast(context, remindId, intent, 0)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pIntent)
    }
}

class RegisterViewModelFactory(
    private val remindDao: RemindDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(remindDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}