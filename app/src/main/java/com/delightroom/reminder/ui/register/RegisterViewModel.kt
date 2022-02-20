package com.delightroom.reminder.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.data.dao.RemindDao
import com.delightroom.reminder.global.base.BaseViewModel
import com.delightroom.reminder.global.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(private val remindDao: RemindDao) : BaseViewModel() {
    val saveEvent = SingleLiveEvent<Any>()

    //리마인드 저장 버튼 클릭
    fun saveRemindBtn(){
        saveEvent.call()
    }

    //리마인드 데이터 저장
    fun saveRemindData(remind: Remind){
        CoroutineScope(Dispatchers.IO).launch {
            remindDao.insert(remind)
        }
    }

    //리마인드 데이터 수정
    fun modifyRemindData(remind: Remind) {
        CoroutineScope(Dispatchers.IO).launch {
            remindDao.update(remind)
        }
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