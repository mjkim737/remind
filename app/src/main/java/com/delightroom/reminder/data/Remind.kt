package com.delightroom.reminder.data

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Remind(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo var name: String?,
    @ColumnInfo var time: Long,
    @ColumnInfo var ringtone: String?, //todo mj
    @ColumnInfo var isDone: Boolean,
    ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeLong(time)
        parcel.writeString(ringtone)
        parcel.writeByte(if (isDone) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Remind> {
        override fun createFromParcel(parcel: Parcel): Remind {
            return Remind(parcel)
        }

        override fun newArray(size: Int): Array<Remind?> {
            return arrayOfNulls(size)
        }
    }
}
