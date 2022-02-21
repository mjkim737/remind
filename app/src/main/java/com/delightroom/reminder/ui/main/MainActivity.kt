package com.delightroom.reminder.ui.main

import androidx.activity.viewModels
import com.delightroom.reminder.R
import com.delightroom.reminder.databinding.ActivityMainBinding
import com.delightroom.reminder.global.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutResourceId: Int = R.layout.activity_main
    private val viewModel : MainViewModel by viewModels()  //todo mj

    override fun initBinding() {
        binding.viewModel = viewModel
    }
}