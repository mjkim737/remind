package com.delightroom.reminder.global.util

import android.app.Application
import com.delightroom.reminder.data.database.RemindDatabase

class MyApplication: Application() {
    val remindDatabase: RemindDatabase by lazy { RemindDatabase.getDatabase(this) }
}