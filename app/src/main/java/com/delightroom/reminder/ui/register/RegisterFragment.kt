package com.delightroom.reminder.ui.register

import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.delightroom.reminder.R
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.databinding.RegisterFragmentBinding
import com.delightroom.reminder.global.base.BaseFragment
import com.delightroom.reminder.global.util.MyApplication
import java.util.*

class RegisterFragment : BaseFragment<RegisterFragmentBinding>() {
    override val layoutResourceId: Int = R.layout.register_fragment
    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(
            (activity?.application as MyApplication).remindDatabase.remindDao()
        )
    }
    private var modifyRemind: Remind? = null

    override fun initBinding() {
        binding.viewModel = viewModel
        initArgsData()

        viewModel.homeFragment.observe(viewLifecycleOwner, Observer {
            findNavController().popBackStack(R.id.home, false) //todo mj

            val remind: Remind
            if (modifyRemind == null) {
                remind = Remind(
                    name = binding.edtxtName.text.toString(),
                    time = getTime(),
                    ringtone = "ringtoooon",
                    isDone = false
                )
                viewModel.saveRemindData(remind)
            } else {
                remind = Remind(
                    id = modifyRemind!!.id,
                    name = binding.edtxtName.text.toString(),
                    time = getTime(),
                    ringtone = "modified",
                    isDone = modifyRemind!!.isDone
                )
                viewModel.modifyRemindData(remind)
            }
        })
    }

    private fun initArgsData() {
        val args: RegisterFragmentArgs by navArgs()
        val remind = args.remind
        remind?.let {
            modifyRemind = it

            val cal: Calendar = Calendar.getInstance()
            cal.timeInMillis = it.time

            binding.edtxtName.setText(it.name)
            binding.time.hour = cal.get(Calendar.HOUR_OF_DAY)
            binding.time.minute = cal.get(Calendar.MINUTE)

            //todo 나머지 데이터도 set
        }
    }

    private fun getTime(): Long {
        val tp: TimePicker = binding.time
        val cal: Calendar = Calendar.getInstance()

        cal.apply {
            set(Calendar.HOUR_OF_DAY, tp.hour)
            set(Calendar.MINUTE, tp.minute)
        }

        return cal.timeInMillis
    }
}

