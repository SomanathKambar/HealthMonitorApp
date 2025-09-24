package com.vkm.healthmonitor.ui
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.vkm.healthmonitor.data.FirestoreUtil
import com.vkm.healthmonitor.databinding.FragmentHydrationBinding
import com.vkm.healthmonitor.model.HydrationEntry
import java.util.Calendar
class HydrationFragment: Fragment() {
    private var _b: FragmentHydrationBinding? = null
    private val b get() = _b!!
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentHydrationBinding.inflate(i, c, false); return b.root
    }
    override fun onViewCreated(v: View, s: Bundle?) {
        b.btnAdd250.setOnClickListener{ add(250) }
        b.btnAdd500.setOnClickListener{ add(500) }
        refresh()
    }
    private fun boundsToday(): Pair<Long,Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY,0);cal.set(Calendar.MINUTE,0);cal.set(Calendar.SECOND,0);cal.set(Calendar.MILLISECOND,0)
        val start = cal.timeInMillis; cal.add(Calendar.DAY_OF_MONTH,1); val end = cal.timeInMillis; return start to end
    }
    private fun add(amount:Int){ FirestoreUtil.addHydration(HydrationEntry(amount)); refresh() }
    private fun refresh(){
        val (s,e)=boundsToday()
        FirestoreUtil.hydrationQueryToday(s,e).addSnapshotListener{ snap, _ ->
            var total=0; snap?.forEach { d -> total += (d.getLong("amountMl")?:0L).toInt() }
            b.tvTotal.text = "${total} ml"
        }
    }
    override fun onDestroyView(){super.onDestroyView();_b=null}
}
