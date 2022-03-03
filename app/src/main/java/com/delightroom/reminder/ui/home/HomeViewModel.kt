package com.delightroom.reminder.ui.home

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(private val remindDao: RemindDao, application: Application): BaseViewModel(application) {
    val nextFragment = SingleLiveEvent<Any>()
    fun remindList() : Flow<List<Remind>> = remindDao.getAll()
    fun remindItem(remindId: Int) : LiveData<Remind> = remindDao.getRemindItem(remindId)
    private var alarmManager: AlarmManager? = null

    //리마인드 추가 버튼 클릭
    fun registerRemind(){
        nextFragment.call()
    }

    //체크박스 선택여부에 따른 리마인드 활성화 여부 DB 업데이트
    fun saveActivateState(remind: Remind) {
        CoroutineScope(Dispatchers.IO).launch {
            remindDao.update(remind)
        }
    }

    /**
     * 예정알람 시간보다 1분 이상 지연 되었을 경우 return false
     */
    fun isAlarmedAtTime(supposedTime: Long): Boolean{
        val current : Calendar = Calendar.getInstance()
        val diff: Long = current.timeInMillis - supposedTime

        return diff < 60000
    }

    /**
     * 리마인드 리스트의 체크박스 활성화 여부(isDone)에 따라 기 등록된 알림 취소/등록 처리
     */
    fun onChangeAlarm(context: Context, remind: Remind, isDone: Boolean){
        alarmManager?.let {
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra(RemindConsts.KEY_REMIND_ID, remind.id)
            val pIntent = PendingIntent.getBroadcast(context, remind.id, intent, 0)

            if (isDone) {
                it.cancel(pIntent)
                pIntent.cancel()
            }else{
                it.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, remind.time, pIntent)
            }
        }
    }
}

class HomeViewModelFactory(
    private val remindDao: RemindDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(remindDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

