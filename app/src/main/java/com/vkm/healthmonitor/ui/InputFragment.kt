package com.vkm.healthmonitor.ui
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vkm.healthmonitor.data.FirestoreUtil
import com.vkm.healthmonitor.databinding.FragmentInputBinding
import com.vkm.healthmonitor.model.HealthEntry
class InputFragment : Fragment() {
    private var _b: FragmentInputBinding? = null
    private val b get() = _b!!
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentInputBinding.inflate(i, c, false); return b.root
    }
    override fun onViewCreated(v: View, s: Bundle?) {
        b.btnSave.setOnClickListener {
            val e = HealthEntry(
                heartRate = b.etHr.text.toString().toIntOrNull() ?: 0,
                bpSys = b.etBpSys.text.toString().toIntOrNull() ?: 0,
                bpDia = b.etBpDia.text.toString().toIntOrNull() ?: 0,
                temperature = b.etTemp.text.toString().toDoubleOrNull() ?: 0.0,
                weight = b.etWeight.text.toString().toDoubleOrNull() ?: 0.0,
                spo2 = b.etSpo2.text.toString().toIntOrNull() ?: 0
            )
            FirestoreUtil.addHealth(e)
            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
            b.etHr.text=null;b.etBpSys.text=null;b.etBpDia.text=null;b.etTemp.text=null;b.etWeight.text=null;b.etSpo2.text=null
        }
    }
    override fun onDestroyView(){super.onDestroyView();_b=null}
}
