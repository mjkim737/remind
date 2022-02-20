package com.delightroom.reminder.ui.alarm

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.delightroom.reminder.R
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.databinding.AlarmFragmentBinding
import com.delightroom.reminder.global.base.BaseFragment
import com.delightroom.reminder.global.util.MyApplication
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AlarmFragment : BaseFragment<AlarmFragmentBinding>() {
    override val layoutResourceId: Int = R.layout.alarm_fragment
    private val viewModel : AlarmViewModel by viewModels {
        AlarmViewModelFactory(
            (activity?.application as MyApplication).remindDatabase.remindDao()
        )
    }
    private var modifyRemind: Remind? = null

    override fun initBinding() {
        binding.viewModel = viewModel

        viewModel.dismissEvent.observe(viewLifecycleOwner, Observer {
            modifyRemind?.let {
                modifyRemind!!.isDone = true
                viewModel.modifyRemindData(it)
            }
            findNavController().popBackStack(R.id.home, false)
        })

        initArgsData()
    }

    private fun initArgsData(){
        val args: AlarmFragmentArgs by navArgs()

        lifecycle.coroutineScope.launch {
            viewModel.remindItem(args.remindId).collect{
                modifyRemind = it
                binding.txtName.text = it.name
                binding.txtTime.text = SimpleDateFormat("HH:mm", Locale.KOREA).format(Date(it.time))
            }
        }
    }
}