package com.delightroom.reminder.ui.home

import com.delightroom.reminder.global.base.BaseViewModel
import com.delightroom.reminder.global.util.SingleLiveEvent

class HomeViewModel : BaseViewModel() {
    val nextFragment = SingleLiveEvent<Any>()

    //리마인드 추가 버튼 클릭
    fun registerRemind(){
        nextFragment.call()
    }
}