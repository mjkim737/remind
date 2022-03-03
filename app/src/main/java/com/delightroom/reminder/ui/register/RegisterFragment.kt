package com.delightroom.reminder.ui.register

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.delightroom.reminder.R
import com.delightroom.reminder.databinding.RegisterFragmentBinding
import com.delightroom.reminder.global.base.BaseFragment
import com.delightroom.reminder.global.util.MyApplication
import com.delightroom.reminder.repository.RemindRepository


class RegisterFragment : BaseFragment<RegisterFragmentBinding>() {
    override val layoutResourceId: Int = R.layout.register_fragment
    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(
            RemindRepository((activity?.application as MyApplication).remindDatabase.remindDao()),
            requireActivity().application
        )
    }

    override fun initBinding() {
        binding.viewModel = viewModel

        initArgsData()
        initObserve()
    }

    private fun initArgsData() {
        val args: RegisterFragmentArgs by navArgs()
        args.remind?.let { viewModel.initView(it) }
    }

    private fun initObserve(){
        viewModel.saveCompleted.observe(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.home, false) //todo mj
        }
    }
}

