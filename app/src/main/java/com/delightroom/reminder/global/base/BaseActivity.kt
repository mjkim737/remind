package com.delightroom.reminder.global.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<VB: ViewDataBinding> : AppCompatActivity(){
    lateinit var binding: VB
    abstract val layoutResourceId: Int

    abstract fun initBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResourceId)
        binding.lifecycleOwner = this

        initBinding()

        requestPermission()
    }

    //todo mj
    private fun requestPermission(){
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")
            )
            systemAlertRequestResult.launch(intent)
        }
    }

    private val systemAlertRequestResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //TODO mj 성공했을 때
            } else {
                //TODO mj 실패했을 때
            }
        }
}