package com.delightroom.reminder.ui.main

import android.os.Build
import android.view.WindowManager
import androidx.activity.viewModels
import com.delightroom.reminder.R
import com.delightroom.reminder.databinding.ActivityMainBinding
import com.delightroom.reminder.global.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutResourceId: Int = R.layout.activity_main
    private val viewModel : MainViewModel by viewModels()  //todo mj

    override fun initBinding() {
        binding.viewModel = viewModel

        popOnLockScreen()
    }

    private fun popOnLockScreen(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }else{
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
    }
}