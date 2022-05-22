package com.g30lab3.app.ui.home


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.SkillsVM
import com.g30lab3.app.adaptors.SkillsAdapter
import com.g30lab3.app.ui.timeSlotEdit.createSnackBar
import com.google.android.material.floatingactionbutton.FloatingActionButton


class SkillsListFragment : Fragment(R.layout.fragment_skills_list) {

    val skillsVM by viewModels<SkillsVM>()
    private var doubleBackToExitPressedOnce = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var emptyMessage: TextView = view.findViewById(R.id.empty_message_skills)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_skills)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        skillsVM.firebaseSkills.observe(requireActivity()) {
            recyclerView.adapter = SkillsAdapter(it)
            //if the list of skills is empty a message is shown, shouldn't appear otherwise
            if (it.isEmpty()) {
                emptyMessage.visibility = View.VISIBLE
            } else {
                emptyMessage.visibility = View.GONE
            }
        }

        var addButton: FloatingActionButton = view.findViewById(R.id.floating_add_button_in_skillList)
        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_skillsListFragment_to_timeSlotEditFragment)
        }

        // Manage the BACK BUTTON: do nothing, avoid to come back to the login fragment
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(doubleBackToExitPressedOnce){
                        //close app
                        requireActivity().finish()
                    }
                    doubleBackToExitPressedOnce =  true
                    Toast.makeText(requireContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
                }
            })

    }

}