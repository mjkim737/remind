package com.delightroom.reminder.ui.register

import com.delightroom.reminder.global.base.BaseViewModel
import com.delightroom.reminder.global.util.SingleLiveEvent

class RegisterViewModel : BaseViewModel() {
    val homeFragment = SingleLiveEvent<Any>()

    //리마인드 저장 버튼 클릭
    fun saveRemindBtn(){
        homeFragment.call()
    }
}