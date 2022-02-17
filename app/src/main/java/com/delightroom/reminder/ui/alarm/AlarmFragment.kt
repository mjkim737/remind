package com.delightroom.reminder.ui.alarm

import androidx.fragment.app.viewModels
import com.delightroom.reminder.R
import com.delightroom.reminder.databinding.AlarmFragmentBinding
import com.delightroom.reminder.global.base.BaseFragment

class AlarmFragment : BaseFragment<AlarmFragmentBinding>() {
    override val layoutResourceId: Int = R.layout.alarm_fragment
    private val viewModel : AlarmViewModel by viewModels()

    override fun initBinding() {
        binding.viewModel = viewModel
    }
}