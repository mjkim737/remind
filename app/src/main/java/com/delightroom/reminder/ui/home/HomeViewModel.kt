package com.delightroom.reminder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.data.dao.RemindDao
import com.delightroom.reminder.global.base.BaseViewModel
import com.delightroom.reminder.global.util.SingleLiveEvent
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val remindDao: RemindDao): BaseViewModel() {
    val nextFragment = SingleLiveEvent<Any>()
    fun remindList() : Flow<List<Remind>> = remindDao.getAll()

    //리마인드 추가 버튼 클릭
    fun registerRemind(){
        nextFragment.call()
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

