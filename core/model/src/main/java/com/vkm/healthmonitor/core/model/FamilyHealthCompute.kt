package com.vkm.healthmonitor.core.model

fun buildVitalsSummary(pwv: ProfileWithVitals): Map<String, VitalsSummaryItem> {
    return mapOf(
        "NORMAL" to VitalsSummaryItem("All good", "Keep doing regular checkups"),
        "WARNING" to VitalsSummaryItem("Pulse slightly high", "Take rest & monitor daily"),
        "CRITICAL" to VitalsSummaryItem("BP very high, SpO₂ low", "Visit doctor immediately")
    )
}

fun calculateHealthStatus(pwv: ProfileWithVitals): HealthStatus {
    var normal = 0
    var warning = 0
    var critical = 0

    pwv.vitals.forEach { v ->
        when {
            v.pulse in 60..100 -> normal++
            v.pulse in 50..59 || v.pulse in 101..120 -> warning++
            else -> critical++
        }
        when {
            v.bpSys in 100..129 && v.bpDia in 60..85 -> normal++
            v.bpSys in 90..139 || v.bpDia in 55..89 -> warning++
            else -> critical++
        }
        when {
            v.temperature in 36.5..37.5 -> normal++
            v.temperature in 37.6..38.5 -> warning++
            else -> critical++
        }
        when {
            v.spo2 >= 95 -> normal++
            v.spo2 in 90..94 -> warning++
            else -> critical++
        }
    }

    return HealthStatus(pwv.profile.name, normal, warning, critical)
}

fun computeFamilyHealthSummary(
    profilesWithVitals: List<ProfileWithVitals>
): List<FamilySliceSummary> {
    val perProfileSummaries = profilesWithVitals.map { pwv ->
        val hs = calculateHealthStatus(pwv)
        val vsMap = buildVitalsSummary(pwv)
        Triple(pwv, hs, vsMap)
    }

    return SliceType.values().map { slice ->
        val items = mutableListOf<ProfileSheetItem>()
        perProfileSummaries.forEach { (pwv, hs, vsMap) ->
            val label = slice.name
            val summaryItem = vsMap[label]
            if (summaryItem != null) {
                val countForSlice = when (slice) {
                    SliceType.NORMAL -> hs.normalCount
                    SliceType.WARNING -> hs.warningCount
                    SliceType.CRITICAL -> hs.criticalCount
                }
                if (countForSlice > 0) {
                    items += ProfileSheetItem(
                        profile = pwv.profile,
                        issues = summaryItem.issues,
                        recommendation = summaryItem.recommendation
                    )
                }
            }
        }
        FamilySliceSummary(slice, items.size, items)
    }
}
