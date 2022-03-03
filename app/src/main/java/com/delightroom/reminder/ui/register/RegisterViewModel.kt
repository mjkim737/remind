package com.delightroom.reminder.ui.register

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.text.TextUtils
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.delightroom.reminder.R
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.global.base.BaseViewModel
import com.delightroom.reminder.global.util.AlarmReceiver
import com.delightroom.reminder.global.util.RemindConsts
import com.delightroom.reminder.global.util.SingleLiveEvent
import com.delightroom.reminder.repository.RemindRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class RegisterViewModel(private val remindRepository: RemindRepository, application: Application) : BaseViewModel(application) {
    val saveCompleted = SingleLiveEvent<Long>()
    val hour = SingleLiveEvent<Int>()
    val minute = SingleLiveEvent<Int>()
    val remindName = SingleLiveEvent<String>()
    val ringtonePosition = SingleLiveEvent<Int>()
    val ringtoneAdapter = SingleLiveEvent<ArrayAdapter<String>>()
    private val ringtoneTitleList = arrayListOf<String>()
    private val deviceRingtoneMap : HashMap<String, String> by lazy { getRingtoneMap() }
    private var modifyRemindData: Remind? = null

    init {
        initRingtoneList()
    }

    fun initView(remind: Remind){
        modifyRemindData = remind

        modifyRemindData!!.let {
            val cal: Calendar = Calendar.getInstance()
            cal.timeInMillis = it.time

            remindName.value = it.name ?: ""
            hour.value = cal.get(Calendar.HOUR_OF_DAY)
            minute.value = cal.get(Calendar.MINUTE)
            it.ringtoneTitle?.let { ringtoneTitle -> setRingtoneSelection(ringtoneTitle) }
        }
    }

    //리마인드 저장 버튼 클릭
    fun saveRemindBtn(){
        lateinit var remind: Remind
        val selectedRingtoneTitle = ringtoneTitleList[ringtonePosition.value!!]
        val selectedRingtoneUri = deviceRingtoneMap[ringtoneTitleList[ringtonePosition.value!!]]

        if (modifyRemindData == null) {
            remind = Remind(
                name =  remindName.value,
                time =  getTimeCalendar().timeInMillis,
                ringtoneTitle = selectedRingtoneTitle,
                ringtoneUri = selectedRingtoneUri,
                isDone = false
            )
            saveRemindData(remind)

        }else{
            remind = Remind(
                id = modifyRemindData!!.id,
                name =  remindName.value,
                time =  getTimeCalendar().timeInMillis,
                ringtoneTitle = selectedRingtoneTitle,
                ringtoneUri = selectedRingtoneUri,
                isDone = false
            )
            modifyRemindData(remind)

        }

        registerAlarm(remindId = remind.id, time = remind.time)
        saveCompleted.call()
    }

    //리마인드 데이터 저장
    private fun saveRemindData(remind: Remind) =  CoroutineScope(Dispatchers.IO).launch {
        remindRepository.insert(remind)
    }

    //리마인드 데이터 수정
    private fun modifyRemindData(remind: Remind) = CoroutineScope(Dispatchers.IO).launch {
        remindRepository.update(remind)
    }

    /**
     * 디바이스 내 Ringtone 정보 가져오기
     * @return HashMap<Ringtone Title, Ringtone Uri>
     */
    private fun getRingtoneMap(): HashMap<String, String> {
        val context = getApplication<Application>()
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
    private fun registerAlarm(time: Long, remindId: Int){
        val context = getApplication<Application>()
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(RemindConsts.KEY_REMIND_ID, remindId)
        val pIntent = PendingIntent.getBroadcast(context, remindId, intent, 0)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pIntent)
    }

    /**
     * TimePicker 에 설정된 시간정보 Calendar 객체로 리턴
     */
    private fun getTimeCalendar(): Calendar {
        val cal: Calendar = Calendar.getInstance()

        cal.run {
            set(Calendar.HOUR_OF_DAY, hour.value!!)
            set(Calendar.MINUTE, minute.value!!)
            set(Calendar.SECOND, 0)
        }

        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.HOUR_OF_DAY, 24)
        }

        return cal
    }

    private fun initRingtoneList(){
        val context = getApplication<Application>()

        ringtoneTitleList.addAll(deviceRingtoneMap.keys)
        ringtoneAdapter.value = ArrayAdapter(context, R.layout.list_item_spinner, ringtoneTitleList)
    }

    /**
     * Ringtone 초기 선택 설정
     */
    private fun setRingtoneSelection(title: String) {
        ringtoneTitleList.forEachIndexed { index, keyTitle->
            if (TextUtils.equals(title, keyTitle)) {
                ringtonePosition.value = index
            }
        }
    }
}

class RegisterViewModelFactory(
    private val remindRepository: RemindRepository,
    private val application: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(remindRepository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}