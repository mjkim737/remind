package com.delightroom.reminder.global.base

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel(){

    /**
     * Full Screen Mode 설정
     */
    fun hideSystemUI(activity: Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window?.setDecorFitsSystemWindows(false)
            activity.window?.insetsController?.apply {
                hide(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }else {
            activity.window?.decorView?.apply {
                systemUiVisibility =
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }
    }


    /**
     * Full Screen Mode 해제
     */
    fun showSystemUI(activity: Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window?.setDecorFitsSystemWindows(true)
            activity.window?.insetsController?.apply {
                show(WindowInsets.Type.navigationBars())
                show(WindowInsets.Type.statusBars())
            }
        }else{
            activity.window?.decorView?.apply {
                systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }
}