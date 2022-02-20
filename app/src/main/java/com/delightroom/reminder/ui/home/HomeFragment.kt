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
import kotlinx.coroutines.flow.collectLatest
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
        val listAdapter = RemindListAdapter(
            onItemClicked = {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeToRegister().setRemind(it)
                )
            },
            onCheckboxClicked = { remind, isChecked ->
                remind.apply {
                    isDone = !isChecked
                }

                viewModel.saveActivateState(remind)
            }
        )

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
            observeListUpdate()
        }
    }

    /**
     * 알람을 받으면 intent 를 통해 가져온 remindId를 이용해 DB 조회 후
     * 알람이 활성화 상태면 알람페이지로 이동한다.
     */
    private fun defineAlarmPage(){
        val remindId = activity?.intent?.getIntExtra(RemindConsts.KEY_REMIND_ID, -1)
        remindId?.let {
            if (remindId != -1) {
                lifecycle.coroutineScope.launch {
                    viewModel.remindItem(remindId).observe(viewLifecycleOwner) {
                        if (!it.isDone){
                            activity?.intent?.putExtra(RemindConsts.KEY_REMIND_ID, -1)
                            findNavController().navigate(
                                HomeFragmentDirections.actionHomeToAlarm().setRemind(it)
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * DB 변경 발생 시 리스트 업데이트
     */
    private fun observeListUpdate(){
        lifecycle.coroutineScope.launch {
            viewModel.remindList().collectLatest {
                (recyclerView.adapter as RemindListAdapter).submitList(it)
            }
        }
    }
}