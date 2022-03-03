package com.delightroom.reminder.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
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
            (activity?.application as MyApplication).remindDatabase.remindDao(),
            requireActivity().application
        )
    }

    //배터리 최적화 요청 다이얼로그
    private val batteryOptimizeDlg : AlertDialog by lazy{
        val intent = Intent()
        intent.action =
            Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS

        val batteryOptimizeDlgBuilder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        batteryOptimizeDlgBuilder.setTitle("REMIND")
        batteryOptimizeDlgBuilder.setMessage("알림이 제대로 울리지 않았나요?\n배터리 최적화 목록에 추가하면 좋을 것 같아요.")
        batteryOptimizeDlgBuilder.setPositiveButton("확인") { _, _ ->
            startActivity(intent)
        }
        batteryOptimizeDlgBuilder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }
        batteryOptimizeDlgBuilder.create()
    }

    override fun initBinding() {
        binding.viewModel = viewModel

        viewModel.nextFragment.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_home_to_register)
        }

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
                viewModel.saveActivateState(remind.apply {
                    isDone = !isChecked
                })
               viewModel.onChangeAlarm(requireContext(), remind, isDone = !isChecked)
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
    private fun defineAlarmPage() {
        val remindId = activity?.intent?.getIntExtra(RemindConsts.KEY_REMIND_ID, -1)
        remindId?.let {
            if (remindId != -1) {
                lifecycle.coroutineScope.launch {
                    viewModel.remindItem(remindId).observe(viewLifecycleOwner) {
                        if (!it.isDone) {
                            activity?.intent?.putExtra(RemindConsts.KEY_REMIND_ID, -1)

                            if (viewModel.isAlarmedAtTime(it.time)) {
                                findNavController().navigate(
                                    HomeFragmentDirections.actionHomeToAlarm().setRemind(it)
                                )
                            } else {
                                showBatteryOptimizeAlert()
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 배터리 최적화 목록에 등록 안되어 있을 때 사용자가 등록하길 요청
     */
    private fun showBatteryOptimizeAlert(){
        val powerManager = activity?.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)
            && !batteryOptimizeDlg.isShowing
        )
            batteryOptimizeDlg.show()
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

    override fun onDestroyView() {
        if (batteryOptimizeDlg.isShowing) batteryOptimizeDlg.dismiss()
        super.onDestroyView()
    }
}