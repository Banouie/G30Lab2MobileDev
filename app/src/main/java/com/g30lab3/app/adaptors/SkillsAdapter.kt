package com.g30lab3.app.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.google.android.material.card.MaterialCardView


class SkillsAdapter(val data: List<String>) :
    RecyclerView.Adapter<SkillsAdapter.ItemViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val skill: TextView = v.findViewById(R.id.skill_title)
        val card: MaterialCardView = v.findViewById(R.id.skill_card)
        val view:View = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vl = LayoutInflater.from(parent.context).inflate(R.layout.skill_layout, parent, false);
        return ItemViewHolder(vl);
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        //get the current skill and show the name in the text field of the holder
        val item = data[position]
        holder.skill.text = item
        //prepare a bundle in case of user selection of this skill
        var bundle = bundleOf("selected_skill" to item)
        //send the bundle to the fragment that shows the related timeSlot for the selected skill
        holder.card.setOnClickListener {
            holder.view.findNavController().navigate(R.id.action_skillsListFragment_to_nav_home,bundle)
        }
    }

    override fun getItemCount(): Int = data.size


}
