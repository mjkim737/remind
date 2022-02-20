package com.delightroom.reminder.ui.register

import android.widget.TimePicker
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.*
import com.delightroom.reminder.R
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.databinding.RegisterFragmentBinding
import com.delightroom.reminder.global.util.RemindConsts
import com.delightroom.reminder.global.base.BaseFragment
import com.delightroom.reminder.global.util.MyApplication
import java.util.*
import java.util.concurrent.TimeUnit

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

        viewModel.saveEvent.observe(viewLifecycleOwner, Observer {
            if (modifyRemind == null) registerNewRemind() else modifyRemind()
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

    private fun getTimeCalendar(): Calendar {
        val tp: TimePicker = binding.time
        val cal: Calendar = Calendar.getInstance()

        cal.apply {
            set(Calendar.HOUR_OF_DAY, tp.hour)
            set(Calendar.MINUTE, tp.minute)
            set(Calendar.SECOND, 0)
        }

        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.HOUR_OF_DAY, 24)
        }

        return cal
    }

    private fun registerWorker(currentCal: Calendar, remindId: Int) {
        val remindData: Data = workDataOf(RemindConsts.KEY_REMIND_ID to remindId)

        val workRequest = OneTimeWorkRequestBuilder<RemindWorker>()
            .setInitialDelay(getTimeDiff(currentCal), TimeUnit.MILLISECONDS)
            .setInputData(remindData)
            .build()

        WorkManager.getInstance(requireContext()).enqueue(workRequest)   //todo mj
    }

    private fun getTimeDiff(currentCal: Calendar): Long{
        val currentDate = Calendar.getInstance()
        return currentCal.timeInMillis - currentDate.timeInMillis
    }

    /**
     * 새로운 리마인드 등록
     */
    private fun registerNewRemind() {
        val currentCal = getTimeCalendar()
        val remind =  Remind(
            name = binding.edtxtName.text.toString(),
            time = currentCal.timeInMillis,
            ringtone = "ringtoooon",
            isDone = false
        )
        viewModel.saveRemindData(remind)

        viewModel.saveCompleted.observe(viewLifecycleOwner, Observer {
            registerWorker(currentCal, it.toInt())
            findNavController().popBackStack(R.id.home, false) //todo mj
        })
    }

    /**
     * 등록된 리마인드 수정
     */
    private fun modifyRemind() {
        val currentCal = getTimeCalendar()
        val remind = Remind(
            id = modifyRemind!!.id,
            name = binding.edtxtName.text.toString(),
            time = currentCal.timeInMillis,
            ringtone = "modified",
            isDone = modifyRemind!!.isDone
        )
        viewModel.modifyRemindData(remind)

        registerWorker(currentCal, remind.id)
        findNavController().popBackStack(R.id.home, false) //todo mj
    }
}

