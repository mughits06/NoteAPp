package com.mughitszufar.notes2.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mughitszufar.notes2.data.Priority
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "todo_table")
@Parcelize
class TodoData (
        @PrimaryKey(autoGenerate = true)
    var id: Int,
        var title: String,
        var priority: Priority,
        var description: String

):Parcelable