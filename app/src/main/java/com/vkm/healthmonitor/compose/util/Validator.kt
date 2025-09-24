package com.vkm.healthmonitor.compose.util

import com.vkm.healthmonitor.compose.data.model.VitalEntry


object Validator {
    fun plausible(v: VitalEntry): Pair<Boolean, String?> {
        if (v.pulse !in 30..220) return false to "Pulse seems implausible"
        if (v.bpSys !in 40..300) return false to "BP systolic seems implausible"
        if (v.bpDia !in 20..200) return false to "BP diastolic seems implausible"
        if (v.temperature !in 30f..45f) return false to "Temperature seems implausible"
        if (v.spo2 !in 30..100) return false to "SpO₂ seems implausible"
        return true to null
    }
}
