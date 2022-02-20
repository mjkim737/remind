package com.delightroom.reminder.ui.home

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delightroom.reminder.R
import com.delightroom.reminder.databinding.HomeFragmentBinding
import com.delightroom.reminder.global.base.BaseFragment
import com.delightroom.reminder.global.util.MyApplication
import com.delightroom.reminder.global.util.RemindConsts
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

        defineAlarmPage()
        initListAdapter()
    }

    private fun initListAdapter() {
        val listAdapter = RemindListAdapter {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeToRegister().setRemind(it)
            )
        }

        recyclerView = binding.list
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.divider_list_item
                        )!!
                    )
                }
            )
            adapter = listAdapter
        }

        lifecycle.coroutineScope.launch {
            viewModel.remindList().collect {
                listAdapter.submitList(it)
            }
        }
    }

    /**
     * 알람을 받으면 intent 를 통해 가져온 remindId를 이용해 DB 조회 후
     * 알람이 활성화 상태면 알람페이지로 이동한다.
     */
    private fun defineAlarmPage(){
        //알람을 받으면 intent 를 통해 remindId를 가져와서 알람페이지로 이동한다.
        val remindId = activity?.intent?.getIntExtra(RemindConsts.KEY_REMIND_ID, -1)
        remindId?.let {
            if (remindId != -1) {
                lifecycle.coroutineScope.launch {
                    viewModel.remindItem(remindId).collect {
                        if (!it.isDone)
                            findNavController().navigate(
                                HomeFragmentDirections.actionHomeToAlarm().setRemind(it)
                            )
                    }
                }
            }
        }
    }
}