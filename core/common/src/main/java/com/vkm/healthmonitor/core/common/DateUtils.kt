package com.vkm.healthmonitor.core.common

import android.text.format.DateFormat
import java.util.Date

object DateUtils {
    fun currentDateString(): String =
        DateFormat.format("yyyy-MM-dd", Date()).toString()

    fun timeStringFrom(ts: Long): String =
        DateFormat.format("HH:mm", Date(ts)).toString()
}


//object Validator {
//    fun plausible(v: VitalEntry): Pair<Boolean, String?> {
//        if (v.pulse !in 30..220) return false to "Pulse looks implausible"
//        if (v.bpSys !in 30..300) return false to "Systolic BP looks implausible"
//        if (v.bpDia !in 20..200) return false to "Diastolic BP looks implausible"
//        if (v.temperature !in 30.0f..45.0f) return false to "Temperature looks implausible"
//        if (v.spo2 !in 30..100) return false to "SpO₂ looks implausible"
//        return true to null
//    }
//}