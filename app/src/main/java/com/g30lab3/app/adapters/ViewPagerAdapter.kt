package com.g30lab3.app.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.g30lab3.app.ui.PendingRequests.AcceptedPendingRequestsFragment
import com.g30lab3.app.ui.PendingRequests.AssignedPendingRequestsFragment
import com.g30lab3.app.ui.PendingRequests.IncomePendingRequestsFragment
import com.g30lab3.app.ui.PendingRequests.SentPendingRequestFragment
import com.g30lab3.app.ui.showProfile.ShowProfileFragment

@Suppress("DEPRECATION")
internal class ViewPagerAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                SentPendingRequestFragment()
            }
            1 -> {
                IncomePendingRequestsFragment()
            }
            2 -> {
                AcceptedPendingRequestsFragment()
            }
            3->{
                AssignedPendingRequestsFragment()
            }
            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}