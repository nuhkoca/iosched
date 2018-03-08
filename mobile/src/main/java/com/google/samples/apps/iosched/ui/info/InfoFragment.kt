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

package com.google.samples.apps.iosched.ui.info

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.samples.apps.iosched.R
import com.google.samples.apps.iosched.databinding.FragmentInfoBinding
import dagger.android.support.DaggerFragment

class InfoFragment : DaggerFragment() {

    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentInfoBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@InfoFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            viewpager.offscreenPageLimit = INFO_PAGES.size
            viewpager.adapter = InfoAdapter(childFragmentManager)
            tabs.setupWithViewPager(binding.viewpager)
        }
    }

    /**
     * Adapter that build a page for each info screen.
     */
    inner class InfoAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount() = INFO_PAGES.size

        override fun getItem(position: Int) = INFO_PAGES[position]()

        override fun getPageTitle(position: Int): CharSequence {
            return resources.getString(INFO_TITLES[position])
        }
    }

    companion object {
        fun newInstance() = InfoFragment()

        private val TAG: String = InfoFragment::class.java.simpleName
        private val INFO_TITLES = arrayOf(
                R.string.event_title,
                R.string.travel_title,
                R.string.faq_title,
                R.string.settings_title
        )
        private val INFO_PAGES = arrayOf(
                { EventFragment() },
                { TravelFragment() },
                { FaqFragment() },
                { SettingsFragment() }
        )
    }
}