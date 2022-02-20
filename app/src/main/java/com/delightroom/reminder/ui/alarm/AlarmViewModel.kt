package com.delightroom.reminder.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.data.dao.RemindDao
import com.delightroom.reminder.global.base.BaseViewModel
import com.delightroom.reminder.global.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel(private val remindDao: RemindDao) : BaseViewModel() {
    val dismissEvent = SingleLiveEvent<Any>()

    fun dismissBtn() {
        dismissEvent.call()
    }

    //리마인드 데이터 수정
    fun modifyRemindData(remind: Remind) {
        CoroutineScope(Dispatchers.IO).launch {
            remindDao.update(remind)
        }
    }
}

class AlarmViewModelFactory(
    private val remindDao: RemindDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(remindDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}