package com.delightroom.reminder.ui.alarm

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.delightroom.reminder.R
import com.delightroom.reminder.databinding.AlarmFragmentBinding
import com.delightroom.reminder.global.base.BaseFragment
import com.delightroom.reminder.global.util.MyApplication
import com.delightroom.reminder.repository.RemindRepository

class AlarmFragment : BaseFragment<AlarmFragmentBinding>() {
    override val layoutResourceId: Int = R.layout.alarm_fragment
    private val viewModel : AlarmViewModel by viewModels {
        AlarmViewModelFactory(
            RemindRepository((activity?.application as MyApplication).remindDatabase.remindDao()),
            requireActivity().application
        )
    }

    override fun initBinding() {
        activity?.let { hideSystemUI(it) }

        binding.viewModel = viewModel

        initArgsData()
        initObserve()
    }

    private fun initArgsData(){
        val args: AlarmFragmentArgs by navArgs()
        args.remind?.let { viewModel.initView(it) }
    }

    private fun initObserve(){
        viewModel.dismissEvent.observe(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.home, false)
        }
    }

    override fun onDestroyView() {
        activity?.let { showSystemUI(it) }
        viewModel.stopRingtone()
        super.onDestroyView()
    }
}