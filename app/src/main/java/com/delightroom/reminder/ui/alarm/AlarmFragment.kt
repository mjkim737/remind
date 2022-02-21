package com.delightroom.reminder.ui.alarm

import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.delightroom.reminder.R
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.databinding.AlarmFragmentBinding
import com.delightroom.reminder.global.base.BaseFragment
import com.delightroom.reminder.global.util.MyApplication
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
    private lateinit var ringtoneSound: Ringtone

    override fun initBinding() {
        binding.viewModel = viewModel

        viewModel.dismissEvent.observe(viewLifecycleOwner) {
            modifyRemind?.let {
                modifyRemind!!.isDone = true
                viewModel.modifyRemindData(it)
            }
            findNavController().popBackStack(R.id.home, false)
        }

        initArgsData()
    }

    private fun initArgsData(){
        val args: AlarmFragmentArgs by navArgs()
        args.remind?.let {
            modifyRemind = it
            binding.txtName.text = it.name
            binding.txtTime.text = SimpleDateFormat("HH:mm", Locale.KOREA).format(Date(it.time))
            it.ringtoneUri?.let { uri -> playRingtone(uri) }
        }
    }

    private fun playRingtone(ringtoneUri: String) {
        ringtoneSound = RingtoneManager.getRingtone(requireContext(), Uri.parse(ringtoneUri))

        ringtoneSound.audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        ringtoneSound.play()
    }

    override fun onDestroyView() {
        ringtoneSound.stop()
        super.onDestroyView()
    }
}