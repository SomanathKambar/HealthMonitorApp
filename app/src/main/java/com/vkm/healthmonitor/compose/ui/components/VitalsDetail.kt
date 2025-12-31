package com.vkm.healthmonitor.compose.ui.components

import com.vkm.healthmonitor.core.model.ProfileWithVitals
import com.vkm.healthmonitor.core.model.VitalsSummaryItem

fun buildVitalsSummary(profile: ProfileWithVitals): Map<String, VitalsSummaryItem> {
    val normalList = mutableSetOf<String>()
    val warningList = mutableSetOf<String>()
    val criticalList = mutableSetOf<String>()

    val warningRec = mutableSetOf<String>()
    val criticalRec = mutableSetOf<String>()

    profile.vitals.forEach { v ->
        // Pulse
        when {
            v.pulse in 60..100 -> normalList.add("Pulse ${v.pulse}")
            v.pulse in 50..59 || v.pulse in 101..120 -> {
                warningList.add("Pulse :  ${v.pulse}")
                warningRec.add("Monitor heart rate, consider light exercise and rest.")
            }
            else -> {
                criticalList.add("Pulse : ${v.pulse}")
                criticalRec.add("Seek medical help if irregular heartbeat persists.")
            }
        }

        // BP
        when {
            v.bpSys in 100..129 && v.bpDia in 60..85 -> normalList.add("BP ${v.bpSys}/${v.bpDia}")
            v.bpSys in 90..139 || v.bpDia in 55..89 -> {
                warningList.add("BP : ${v.bpSys}/${v.bpDia}")
                warningRec.add("Reduce salt intake, monitor daily.")
            }
            else -> {
                criticalList.add("BP : ${v.bpSys}/${v.bpDia}")
                criticalRec.add("Consult a doctor for high blood pressure.")
            }
        }

        // Temp
        when {
            v.temperature in 36.5..37.5 -> normalList.add("Temp ${v.temperature}")
            v.temperature in 37.6..38.5 -> {
                warningList.add("Temp : ${v.temperature}")
                warningRec.add("Drink fluids, rest, and monitor temperature.")
            }
            else -> {
                criticalList.add("Temp : ${v.temperature}")
                criticalRec.add("High fever! Seek immediate care.")
            }
        }

        // SpO₂
        when {
            v.spo2 >= 95 -> normalList.add("SpO₂ : ${v.spo2}")
            v.spo2 in 90..94 -> {
                warningList.add("SpO₂:  ${v.spo2}")
                warningRec.add("Practice breathing exercises and rest.")
            }
            else -> {
                criticalList.add("SpO₂ : ${v.spo2}")
                criticalRec.add("Critical oxygen level! Seek urgent care.")
            }
        }
    }
        val normal = if (normalList.isNotEmpty()) normalList.joinToString(", ") else "All good"
        val warning = if (warningList.isNotEmpty()) warningList.joinToString(", ") else "All good"
        val critical  = if (criticalList.isNotEmpty()) criticalList.joinToString(", ") else "All good"
    return mapOf(
        "Normal" to VitalsSummaryItem (normal, "Keep doing regular checkups"),
        "Warning" to VitalsSummaryItem(warning,  recommendation = if (warningRec.isNotEmpty()) warningRec.joinToString(" ") else "Monitor health closely."),
        "Critical" to VitalsSummaryItem( critical, recommendation = if (criticalRec.isNotEmpty()) criticalRec.joinToString(" ") else "Immediate medical consultation required!")
    )

}
