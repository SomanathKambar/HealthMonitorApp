package com.vkm.healthmonitor.ui
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.vkm.healthmonitor.data.FirestoreUtil
import com.vkm.healthmonitor.model.HealthEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.ktx.toObject
import com.vkm.healthmonitor.databinding.FragmentChartsBinding

class ChartsFragment: Fragment() {
    private var _b: FragmentChartsBinding? = null
    private val b get() = _b!!
    private val items = mutableListOf<HealthEntry>()
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentChartsBinding.inflate(i, c, false); return b.root
    }
    override fun onViewCreated(v: View, s: Bundle?) {
        FirestoreUtil.healthQuery().addSnapshotListener { snap, _ ->
            items.clear(); snap?.documents?.forEach { d -> d.toObject<HealthEntry>()?.let(items::add) }; render()
        }
    }
    private fun render() {
        val ds = LineDataSet(items.mapIndexed { idx, h -> Entry(idx.toFloat(), h.heartRate.toFloat()) }, "Heart Rate (bpm)")
        b.chart.data = LineData(ds); b.chart.invalidate()
    }
    override fun onDestroyView(){super.onDestroyView();_b=null}
}
