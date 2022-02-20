package com.delightroom.reminder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.data.dao.RemindDao
import com.delightroom.reminder.global.base.BaseViewModel
import com.delightroom.reminder.global.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val remindDao: RemindDao): BaseViewModel() {
    val nextFragment = SingleLiveEvent<Any>()
    fun remindList() : Flow<List<Remind>> = remindDao.getAll()
    fun remindItem(remindId: Int) : Flow<Remind> = remindDao.getRemindItem(remindId)
    val checkboxSaveCompleted = SingleLiveEvent<Any>()

    //리마인드 추가 버튼 클릭
    fun registerRemind(){
        nextFragment.call()
    }

    //체크박스 선택여부에 따른 리마인드 활성화 여부 DB 업데이트
    fun saveActivateState(remind: Remind) {
        CoroutineScope(Dispatchers.IO).launch {
            remindDao.update(remind)
            withContext(Dispatchers.Main) {
                checkboxSaveCompleted.call()
            }
        }
    }
}

class HomeViewModelFactory(
    private val remindDao: RemindDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(remindDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

