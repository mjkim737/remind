package com.delightroom.reminder.ui.register

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.delightroom.reminder.R
import com.delightroom.reminder.databinding.RegisterFragmentBinding
import com.delightroom.reminder.global.base.BaseFragment

class RegisterFragment : BaseFragment<RegisterFragmentBinding>() {
    override val layoutResourceId: Int = R.layout.register_fragment
    private val viewModel : RegisterViewModel by viewModels()

    override fun initBinding() {
        binding.viewModel = viewModel
        
        viewModel.homeFragment.observe(viewLifecycleOwner, Observer {
            findNavController().popBackStack(R.id.home, false) //todo mj
            //todo register db
            
        })
    }
}