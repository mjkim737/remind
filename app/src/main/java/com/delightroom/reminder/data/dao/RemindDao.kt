package com.delightroom.reminder.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.delightroom.reminder.data.Remind
import kotlinx.coroutines.flow.Flow

@Dao
interface RemindDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(remind: Remind): Long

    @Update
    fun update(remind: Remind)

    @Query("SELECT * FROM remind WHERE id = :remindId")
    fun getRemindItem(remindId: Int): LiveData<Remind>

    @Query("SELECT * FROM remind")
    fun getAll(): Flow<List<Remind>>
}