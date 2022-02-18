package com.delightroom.reminder.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.delightroom.reminder.data.Remind
import com.delightroom.reminder.data.dao.RemindDao

@Database(entities = [Remind::class], version = 1)
abstract class RemindDatabase : RoomDatabase() {
    abstract fun remindDao(): RemindDao

    //mj RoomDatabase 인스턴스는 리소스를 많이 소비하고, DB중복 생성 방지를 위해 싱글톤
    companion object {
        @Volatile   //todo mj https://developer.android.google.cn/codelabs/basic-android-kotlin-training-intro-room-flow?hl=ko#6
        private var INSTANCE: RemindDatabase? = null

        fun getDatabase(context: Context): RemindDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    RemindDatabase::class.java,
                    "db_remind")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}