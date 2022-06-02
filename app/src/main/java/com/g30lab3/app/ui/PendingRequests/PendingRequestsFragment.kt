package com.g30lab3.app.ui.PendingRequests

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.g30lab3.app.MainActivity
import com.g30lab3.app.R
import com.g30lab3.app.TimeSlotVM
import com.g30lab3.app.adapters.ViewPagerAdapter
import com.g30lab3.app.chatsVM
import com.google.android.material.tabs.TabLayout


class PendingRequestsFragment : Fragment(R.layout.fragment_pending_requests) {

    val pendingVM by viewModels<chatsVM>()
    val tsVM by viewModels<TimeSlotVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).supportActionBar?.title= "Sent by you" //initialize the title to this because when created the tab implicitly selected is sent
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tab: TabLayout = view.findViewById(R.id.tab)
        val vp: ViewPager = view.findViewById(R.id.vp)

        tab.tabSelectedIndicator

        val adapter = ViewPagerAdapter(requireContext(),childFragmentManager,tab.tabCount)
        vp.adapter = adapter

        // set badges
        var badgeSent = tab.getTabAt(0)?.orCreateBadge
        var badgeIncome = tab.getTabAt(1)?.orCreateBadge
        var badgeAccepted = tab.getTabAt(2)?.orCreateBadge
        var badgeAssigned = tab.getTabAt(3)?.orCreateBadge
        pendingVM.loggedUserPendingRequests.observe(requireActivity()){
            val number = it.size
            if (number!=0) {
                badgeSent?.number = it.size
            } else {
                tab.getTabAt(0)?.removeBadge()
            }
        }
        pendingVM.loggedUserIncomeRequests.observe(requireActivity()){
            val number = it.size
            if (number!=0) {
                badgeIncome?.number = it.size
            } else {
                tab.getTabAt(1)?.removeBadge()
            }
        }
        pendingVM.loggedUserAcceptedRequests.observe(requireActivity()){
            val number = it.size
            if (number!=0) {
                badgeAccepted?.number = it.size
            } else {
                tab.getTabAt(2)?.removeBadge()
            }
        }
        pendingVM.loggedUserAssignedRequests.observe(requireActivity()){
            val number = it.size
            if (number!=0) {
                badgeAssigned?.number = it.size
            } else {
                tab.getTabAt(3)?.removeBadge()
            }
        }
        // end set badges

        //viewpager section
        vp.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab))
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                vp.currentItem = tab.position
                when(tab.position){
                    0 ->{
                        (requireActivity() as MainActivity).supportActionBar?.title= "Sent by you"
                    }
                    1 ->{
                        (requireActivity() as MainActivity).supportActionBar?.title= "Sent to you"
                    }
                    2 ->{
                        (requireActivity() as MainActivity).supportActionBar?.title= "Accepted by you"
                    }
                    3 ->{
                        (requireActivity() as MainActivity).supportActionBar?.title= "Assigned to you"
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }



}







