package com.delightroom.reminder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.delightroom.reminder.data.Remind

@Dao
interface RemindDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(remind: Remind)

    @Query("SELECT * FROM remind ORDER BY time ASC")
    fun getAll(): List<Remind>
}