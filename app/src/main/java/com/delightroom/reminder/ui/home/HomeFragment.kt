package com.delightroom.reminder.ui.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.delightroom.reminder.R
import com.delightroom.reminder.databinding.HomeFragmentBinding
import com.delightroom.reminder.global.base.BaseFragment

class HomeFragment : BaseFragment<HomeFragmentBinding>() {
    override val layoutResourceId: Int = R.layout.home_fragment
    private val viewModel: HomeViewModel by viewModels()

    override fun initBinding() {
        binding.viewModel = viewModel

        viewModel.nextFragment.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_home_to_register)
        })
    }
}