package com.g30lab3.app.ui.PendingRequests

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.g30lab3.app.MainActivity
import com.g30lab3.app.R
import com.g30lab3.app.TimeSlotVM
import com.g30lab3.app.adapters.ViewPagerAdapter
import com.g30lab3.app.chatsVM
import com.google.android.material.tabs.TabLayout

/**This fragment shows all the requested time slots from the logged user, in other words the timeSlots he is interested in*/
class PendingRequestsFragment : Fragment(R.layout.fragment_pending_requests) {

    val pendingVM by viewModels<chatsVM>()
    val tsVM by viewModels<TimeSlotVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).supportActionBar?.subtitle=""
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tab: TabLayout = view.findViewById(R.id.tab)
        val vp: ViewPager = view.findViewById(R.id.vp)

        val adapter = ViewPagerAdapter(requireContext(),childFragmentManager,tab.tabCount)
        vp.adapter = adapter

        vp.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab))
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                vp.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


    }

}







