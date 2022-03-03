package com.delightroom.reminder.repository

import androidx.lifecycle.LiveData
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.data.dao.RemindDao
import kotlinx.coroutines.flow.Flow

class RemindRepository (private val dao: RemindDao){
    fun insert(remind: Remind): Long = dao.insert(remind)
    fun update(remind: Remind) = dao.update(remind)
    fun getRemindItem(remindId: Int): LiveData<Remind> = dao.getRemindItem(remindId)
    fun getAll(): Flow<List<Remind>> = dao.getAll()
}