package com.vkm.healthmonitor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.vkm.healthmonitor.databinding.ActivityMainBinding
import com.vkm.healthmonitor.ui.InputFragment
import com.vkm.healthmonitor.ui.ChartsFragment
import com.vkm.healthmonitor.ui.HydrationFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_input -> switch(InputFragment())
                R.id.nav_charts -> switch(ChartsFragment())
                R.id.nav_hydration -> switch(HydrationFragment())
            }
            true
        }
        binding.bottomNav.selectedItemId = R.id.nav_input
    }
    private fun switch(f: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, f).commit()
    }
}
