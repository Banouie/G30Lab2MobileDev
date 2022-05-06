package com.g30lab3.app.ui.home
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.adaptors.TimeSlotAdapter
import com.g30lab3.app.databinding.FragmentHomeBinding
import com.g30lab3.app.models.timeSlot
import com.g30lab3.app.timeSlotVM
import com.g30lab3.app.ui.home.HomeViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    val vm by viewModels<timeSlotVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView:RecyclerView = view.findViewById(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        vm.all.observe(requireActivity()){
            // Data bind the recycler view
            recyclerView.adapter = TimeSlotAdapter(it)
        }



    }
}
