package com.ilazar.myapp.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "items")
data class Item(@PrimaryKey var _id: String = "",
                var date: String = getCurrentDate(),
                var boolean: Boolean = false,
                var number: Int = 0,
                var text: String = "",
                var lat:Double=46.771862474087705,
                var lng:Double=23.62247195094824,
                var isSentToServer: Boolean=true,
                )

fun getCurrentDate(): String{
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    return "$year/${month + 1}/$day"
}