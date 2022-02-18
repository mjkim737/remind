package com.delightroom.reminder.ui.home

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delightroom.reminder.R
import com.delightroom.reminder.databinding.HomeFragmentBinding
import com.delightroom.reminder.global.base.BaseFragment
import com.delightroom.reminder.global.util.MyApplication
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<HomeFragmentBinding>() {
    override val layoutResourceId: Int = R.layout.home_fragment
    private lateinit var recyclerView: RecyclerView
    private val viewModel: HomeViewModel by viewModels {  //todo mj
        HomeViewModelFactory(
            (activity?.application as MyApplication).remindDatabase.remindDao()
        )
    }

    override fun initBinding() {
        binding.viewModel = viewModel

        viewModel.nextFragment.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_home_to_register)
        })

        initListAdapter()
    }

    private fun initListAdapter() {
        recyclerView = binding.list
        recyclerView.layoutManager = LinearLayoutManager(context)

        val listAdapter = RemindListAdapter {
            Log.d("MJ_DEBUG", "YES! : ${it.name}")
            findNavController().navigate(HomeFragmentDirections.actionHomeToRegister().setRemind(it))
        }

        recyclerView.adapter = listAdapter
        lifecycle.coroutineScope.launch {
            viewModel.remindList().collect {
                listAdapter.submitList(it)
            }
        }
    }
}