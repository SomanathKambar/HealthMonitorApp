package com.vkm.healthmonitor.core.common.validator

import com.vkm.healthmonitor.core.model.HealthPlan
import com.vkm.healthmonitor.core.model.HealthStandard
import com.vkm.healthmonitor.core.model.VitalEntry

data class VitalInterpretation(val label: String, val message: String, val level: Int)

object Validator {
    private val defaults = mapOf(
        "pulse_min" to "60", "pulse_max" to "100",
        "bp_sys_min" to "90", "bp_sys_max" to "120",
        "bp_dia_min" to "60", "bp_dia_max" to "80",
        "temp_min" to "36.1", "temp_max" to "37.2",
        "spo2_min" to "95",
        "hydration_interval_hours" to "2",
        "hydration_max_ml" to "4000"
    )

    private fun mapFromStandards(s: List<HealthStandard>): Map<String, String> {
        val m = defaults.toMutableMap()
        s.forEach { m[it.healthKey] = it.value }
        return m
    }

    fun plausible(entry: VitalEntry): Pair<Boolean, String?> {
        if (entry.pulse !in 20..240) return false to "Pulse seems implausible — please recheck."
        if (entry.bpSys !in 30..300) return false to "Systolic BP seems implausible."
        if (entry.bpDia !in 20..200) return false to "Diastolic BP seems implausible."
        if (entry.temperature !in 30.0f..45.0f) return false to "Temperature seems implausible."
        if (entry.spo2 !in 30..100) return false to "Oxygen level seems implausible."
        return true to null
    }

    fun interpret(entry: VitalEntry, standards: List<HealthStandard>): List<VitalInterpretation> {
        val S = mapFromStandards(standards)
        val res = mutableListOf<VitalInterpretation>()

        val pulse = entry.pulse
        val pmin = S["pulse_min"]!!.toInt(); val pmax = S["pulse_max"]!!.toInt()
        if (pulse in pmin..pmax) res.add(VitalInterpretation("Pulse", "Normal ($pulse bpm)", 0))
        else if (pulse < pmin) res.add(VitalInterpretation("Pulse", "Low pulse ($pulse bpm). Rest and recheck.", 1))
        else res.add(VitalInterpretation("Pulse", "High pulse ($pulse bpm). Rest and consult if persists.", 2))

        val sys = entry.bpSys; val dia = entry.bpDia
        val smin = S["bp_sys_min"]!!.toInt(); val smax = S["bp_sys_max"]!!.toInt()
        val dmin = S["bp_dia_min"]!!.toInt(); val dmax = S["bp_dia_max"]!!.toInt()
        if (sys in smin..smax && dia in dmin..dmax) res.add(VitalInterpretation("Blood Pressure", "Normal ($sys/$dia mmHg)", 0))
        else if (sys > smax || dia > dmax) res.add(VitalInterpretation("Blood Pressure", "High BP. Reduce salt & consult if trend persists.", 2))
        else res.add(VitalInterpretation("Blood Pressure", "Low BP. If symptomatic, seek help.", 1))

        val temp = entry.temperature
        val tmin = S["temp_min"]!!.toFloat(); val tmax = S["temp_max"]!!.toFloat()
        if (temp in tmin..tmax) res.add(VitalInterpretation("Temperature", "Normal (${String.format("%.1f", temp)} °C)", 0))
        else if (temp > tmax) res.add(VitalInterpretation("Temperature", "Fever — rest & hydrate; consult if high.", 2))
        else res.add(VitalInterpretation("Temperature", "Low temperature — recheck & keep warm.", 1))

        val spo2 = entry.spo2
        val spmin = S["spo2_min"]!!.toInt()
        if (spo2 >= spmin) res.add(VitalInterpretation("Oxygen", "Good (${spo2}%)", 0))
        else res.add(VitalInterpretation("Oxygen", "Low oxygen — seek medical attention if breathless.", 2))

        return res
    }

    /**
     * Generate recommended health plans from interpretations. Returns simple plan templates.
     */
    fun generatePlansFromInterpretations(profileId: Int, interpretations: List<VitalInterpretation>): List<HealthPlan> {
        val plans = mutableListOf<HealthPlan>()
        interpretations.forEach { itp ->
            when (itp.label) {
                "Pulse" -> {
                    if (itp.level >= 1) {
                        plans.add(HealthPlan(profileId = profileId, title = "Rest & Breathing", description = "Take 5–10 minutes of seated deep breathing. Avoid stimulants.", repeatHours = 6))
                    }
                }
                "Blood Pressure" -> {
                    if (itp.level == 2) {
                        plans.add(HealthPlan(profileId = profileId, title = "Low-salt diet & Walk", description = "Reduce salt intake. Take a 20–30 minute brisk walk daily.", repeatHours = 24))
                    } else if (itp.level == 1) {
                        plans.add(HealthPlan(profileId = profileId, title = "Monitor BP", description = "Monitor BP twice daily for a week and record.", repeatHours = 12))
                    }
                }
                "Temperature" -> {
                    if (itp.level == 2) {
                        plans.add(HealthPlan(profileId = profileId, title = "Hydrate & Rest", description = "Hydrate, rest, and recheck temperature. Seek care for persistent high fever.", repeatHours = 6))
                    }
                }
                "Oxygen" -> {
                    if (itp.level == 2) {
                        plans.add(HealthPlan(profileId = profileId, title = "Seek Medical Advice", description = "Low oxygen detected. If breathless, seek immediate medical attention.", repeatHours = 1))
                    }
                }
            }
        }
        return plans
    }
}
