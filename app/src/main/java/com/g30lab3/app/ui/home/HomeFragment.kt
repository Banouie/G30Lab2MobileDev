package com.g30lab3.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.g30lab3.app.R
import com.g30lab3.app.databinding.FragmentHomeBinding
import com.g30lab3.app.timeSlotVM

class HomeFragment : Fragment(R.layout.fragment_home) {

    val vm by viewModels<timeSlotVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var text:TextView = view.findViewById(R.id.textView2)
        var button:Button = view.findViewById(R.id.button)

        vm.items.observe(requireActivity()){
            text.text = it.toString()
        }

        button.setOnClickListener{
            vm.add()
        }
    }
}
