package com.delightroom.reminder.data.dao

import androidx.room.*
import com.delightroom.reminder.data.Remind
import kotlinx.coroutines.flow.Flow

@Dao
interface RemindDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(remind: Remind)

    @Update
    fun update(remind: Remind)

    @Query("SELECT * FROM remind ORDER BY time ASC")
    fun getAll(): Flow<List<Remind>>
}