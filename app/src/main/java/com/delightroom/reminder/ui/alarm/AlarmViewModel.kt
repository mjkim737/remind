package com.delightroom.reminder.ui.alarm

import android.app.Application
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.global.base.BaseViewModel
import com.delightroom.reminder.global.util.SingleLiveEvent
import com.delightroom.reminder.repository.RemindRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AlarmViewModel(private val remindRepository: RemindRepository, application: Application) : BaseViewModel(application) {
    val dismissEvent = SingleLiveEvent<Any>()
    val remindName = SingleLiveEvent<String>()
    val remindTime = SingleLiveEvent<String>()
    private lateinit var ringtoneSound: Ringtone
    private lateinit var remindData : Remind

    fun dismissBtn() {
        modifyRemindData()
        dismissEvent.call()
    }

    //리마인드 데이터 수정
    private fun modifyRemindData() {
        CoroutineScope(Dispatchers.IO).launch {
            remindRepository.update(remindData)
        }
    }

    fun initView(remind: Remind) {
        remind.let {
            remindData = it
            remindName.value = it.name ?: ""
            remindTime.value = SimpleDateFormat("HH:mm", Locale.KOREA).format(Date(it.time))
            it.ringtoneUri?.let { uri -> playRingtone(uri) }
        }
    }

    private fun playRingtone(ringtoneUri: String) {
        ringtoneSound = RingtoneManager.getRingtone(getApplication(), Uri.parse(ringtoneUri))

        ringtoneSound.audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        ringtoneSound.play()
    }

    fun stopRingtone(){
        ringtoneSound.stop()
    }
}

class AlarmViewModelFactory(
    private val remindRepository: RemindRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(remindRepository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}