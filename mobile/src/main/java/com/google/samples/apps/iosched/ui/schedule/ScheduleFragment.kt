/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.iosched.ui.schedule

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.samples.apps.iosched.R
import com.google.samples.apps.iosched.databinding.FragmentScheduleBinding
import com.google.samples.apps.iosched.shared.util.TimeUtils.ConferenceDay
import com.google.samples.apps.iosched.shared.util.activityViewModelProvider
import com.google.samples.apps.iosched.ui.schedule.agenda.ScheduleAgendaFragment
import com.google.samples.apps.iosched.ui.schedule.day.ScheduleDayFragment
import com.google.samples.apps.iosched.ui.sessiondetail.SessionDetailActivity
import com.google.samples.apps.iosched.util.login.LoginHandler
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_schedule.*
import javax.inject.Inject

/**
 * The Schedule page of the top-level Activity.
 */
class ScheduleFragment : DaggerFragment() {

    companion object {
        val TAG: String = ScheduleFragment::class.java.simpleName
        val COUNT = ConferenceDay.values().size + 1 // Agenda
        val AGENDA_POSITION = COUNT - 1

        // requestCodes
        const val RC_LOG_IN = 1
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var loginHandler: LoginHandler

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreateView(inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = activityViewModelProvider(viewModelFactory)
        val binding: FragmentScheduleBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_schedule, container, false
        )

        // Set the layout variables
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        viewModel.navigateToSessionAction.observe(this, Observer { navigationEvent ->
            navigationEvent?.getContentIfNotHandled()?.let { sessionId ->
                openSessionDetail(sessionId)
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewpager.offscreenPageLimit = COUNT - 1
        viewpager.adapter = ScheduleAdapter(childFragmentManager)
        tabs.setupWithViewPager(viewpager)
    }

    private fun openSessionDetail(id: String) {
        startActivity(SessionDetailActivity.starterIntent(requireContext(), id))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(resultCode) {
            RC_LOG_IN -> loginHandler.handleLogin(resultCode, data) { loginResult ->
                viewModel.handleLoginResult(loginResult)
            }
        }
    }

    private fun requestLogin() {
        startActivityForResult(loginHandler.makeLoginIntent(), RC_LOG_IN)
    }

    /**
     * Adapter that build a page for each conference day.
     */
    inner class ScheduleAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount() = COUNT

        override fun getItem(position: Int): Fragment {
            return when(position) {
                AGENDA_POSITION -> ScheduleAgendaFragment()
                else -> ScheduleDayFragment.newInstance(ConferenceDay.values()[position])
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when(position) {
                AGENDA_POSITION -> getString(R.string.agenda)
                else -> ConferenceDay.values()[position].formatMonthDay()
            }
        }
    }
}
