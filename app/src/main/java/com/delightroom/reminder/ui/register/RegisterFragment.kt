package com.delightroom.reminder.ui.register

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.delightroom.reminder.R
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.databinding.RegisterFragmentBinding
import com.delightroom.reminder.global.base.BaseFragment
import com.delightroom.reminder.global.util.MyApplication

class RegisterFragment : BaseFragment<RegisterFragmentBinding>() {
    override val layoutResourceId: Int = R.layout.register_fragment
    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(
            (activity?.application as MyApplication).remindDatabase.remindDao()
        )
    }
    private var remindId: Int? = null

    override fun initBinding() {
        binding.viewModel = viewModel
        initArgsData()

        viewModel.homeFragment.observe(viewLifecycleOwner, Observer {
            findNavController().popBackStack(R.id.home, false) //todo mj

            val remind: Remind
            if (remindId == null) {
                remind = Remind(
                    name = binding.edtxtName.text.toString(),
                    time = 938291839221.toInt(),
                    ringtone = "ringtoooon",
                    isDone = true
                )
                viewModel.saveRemindData(remind)
            } else {
                remind = Remind(
                    id = remindId!!,
                    name = binding.edtxtName.text.toString(),
                    time = 938291839221.toInt(),
                    ringtone = "modified",
                    isDone = true
                )
                viewModel.modifyRemindData(remind)
            }
        })
    }

    private fun initArgsData() {
        val args: RegisterFragmentArgs by navArgs()
        val remind = args.remind
        remind?.let {
            remindId = it.id
            binding.edtxtName.setText(it.name)
            //todo 나머지 데이터도 set
        }
    }
}

