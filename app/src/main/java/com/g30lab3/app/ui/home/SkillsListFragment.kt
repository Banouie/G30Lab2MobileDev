package com.g30lab3.app.ui.home


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.SkillsVM
import com.g30lab3.app.adaptors.SkillsAdapter


class SkillsListFragment : Fragment(R.layout.fragment_skills_list) {
    val skillsVM by viewModels<SkillsVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var emptyMessage: TextView = view.findViewById(R.id.empty_message_skills)
        val recyclerView:RecyclerView = view.findViewById(R.id.rv_skills)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        skillsVM.firebaseSkills.observe(requireActivity()){
            recyclerView.adapter= SkillsAdapter(it)
            //if the list of skills is empty a message is shown, shouldn't appear otherwise
            if (it.isEmpty()) {
                emptyMessage.visibility = View.VISIBLE
            } else {
                emptyMessage.visibility = View.GONE
            }
        }
    }
}