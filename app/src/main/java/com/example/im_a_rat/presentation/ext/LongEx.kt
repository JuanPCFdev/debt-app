package com.example.im_a_rat.presentation.ext

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun Long?.millisToDate(): String {
    this ?: return ""
    return try {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = this@millisToDate
            add(Calendar.DAY_OF_MONTH, 1)
        }
        val sdf = SimpleDateFormat("EEEE dd MMMM", Locale.getDefault())
        sdf.format(calendar.time)
    } catch (e: Exception) {
        ""
    }
}