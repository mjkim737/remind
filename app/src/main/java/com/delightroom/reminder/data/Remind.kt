package com.delightroom.reminder.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Remind(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @NonNull @ColumnInfo var name: String,
    @NonNull @ColumnInfo var time: Int,
    @NonNull @ColumnInfo var ringtone: String, //todo mj
    @NonNull @ColumnInfo var isDone: Boolean,
    )
