/*
 * // Copyright 2018 Beam Development
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License");
 * // you may not use this file except in compliance with the License.
 * // You may obtain a copy of the License at
 * //
 * //    http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS,
 * // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * // See the License for the specific language governing permissions and
 * // limitations under the License.
 */

package com.mw.beam.beamwallet.screens.addresses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mw.beam.beamwallet.R
import com.mw.beam.beamwallet.core.entities.WalletAddress
import com.mw.beam.beamwallet.core.helpers.Category

/**
 * Created by vain onnellinen on 2/28/19.
 */
class AddressesPagerAdapter(val context: Context, onAddressClickListener: AddressesAdapter.OnItemClickListener, categoryProvider: (address: String) -> Category?, private val type: AddressPagerType = AddressPagerType.FULL) : androidx.viewpager.widget.PagerAdapter() {
    private var touchListener: View.OnTouchListener? = null

    private val activeAdapter = AddressesAdapter(context, onAddressClickListener, categoryProvider, type == AddressPagerType.FULL)
    private val expiredAdapter = AddressesAdapter(context, onAddressClickListener, categoryProvider, type == AddressPagerType.FULL)
    private val contactsAdapter = AddressesAdapter(context, onAddressClickListener, categoryProvider, type == AddressPagerType.FULL)

    private var activeLayoutManager: LinearLayoutManager? = null
    private var expiredLayoutManager: LinearLayoutManager? = null
    private var contactsLayoutManager: LinearLayoutManager? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout = LayoutInflater.from(context).inflate(R.layout.item_list, container, false) as ViewGroup
        val recyclerView = layout.findViewById<RecyclerView>(R.id.list)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.apply {
            this.layoutManager = layoutManager

            adapter = if (type == AddressPagerType.FULL) {
                when (Tab.values()[position]) {
                    Tab.ACTIVE -> activeAdapter
                    Tab.EXPIRED -> expiredAdapter
                    Tab.CONTACTS -> contactsAdapter
                }
            } else {
                when (position) {
                    0 -> contactsAdapter
                    else -> activeAdapter
                }
            }

            if (type == AddressPagerType.SMALL) {
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            }

            setOnTouchListener { v, event -> touchListener?.onTouch(v, event) ?: false }
        }

        if (type == AddressPagerType.FULL) {
            when (Tab.values()[position]) {
                Tab.ACTIVE -> activeLayoutManager = layoutManager
                Tab.EXPIRED -> expiredLayoutManager = layoutManager
                Tab.CONTACTS -> contactsLayoutManager = layoutManager
            }
        } else {
            when (position) {
                0 -> contactsLayoutManager = layoutManager
                else -> activeLayoutManager = layoutManager
            }
        }

        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        if (type == AddressPagerType.FULL) {
            when (Tab.values()[position]) {
                Tab.ACTIVE -> activeLayoutManager = null
                Tab.EXPIRED -> expiredLayoutManager = null
                Tab.CONTACTS -> contactsLayoutManager = null
            }
        } else {
            when (position) {
                0 -> contactsLayoutManager = null
                else -> activeLayoutManager = null
            }
        }

        container.removeView(view as View)
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view == any
    }

    override fun getCount(): Int = if (type == AddressPagerType.FULL) Tab.values().count() else 2

    override fun getPageTitle(position: Int): CharSequence? {
        val stringId = if (type == AddressPagerType.FULL) {
            when (Tab.values()[position]) {
                Tab.ACTIVE -> R.string.addresses_tab_active
                Tab.EXPIRED -> R.string.addresses_tab_expired
                Tab.CONTACTS -> R.string.contacts
            }
        } else {
            when (position) {
                0 -> R.string.contacts
                else -> R.string.my_addresses
            }
        }

        return context.getString(stringId)
    }

    fun findFirstCompletelyVisibleItemPosition(currentItemPosition: Int): Int {
        val manager = if (type == AddressPagerType.FULL) {
            when (Tab.values()[currentItemPosition]) {
                Tab.ACTIVE -> activeLayoutManager
                Tab.EXPIRED -> expiredLayoutManager
                Tab.CONTACTS -> contactsLayoutManager
            }
        } else {
            when (currentItemPosition) {
                0 -> contactsLayoutManager
                else -> activeLayoutManager
            }
        }

        return manager?.findFirstCompletelyVisibleItemPosition() ?: 0
    }

    fun setData(tab: Tab, addresses: List<WalletAddress>) {
        when (tab) {
            Tab.ACTIVE -> activeAdapter.apply {
                setData(addresses.sortedByDescending { it.createTime })
                notifyDataSetChanged()
            }
            Tab.EXPIRED -> expiredAdapter.apply {
                setData(addresses.sortedByDescending { it.createTime + it.duration })
                notifyDataSetChanged()
            }
            Tab.CONTACTS -> contactsAdapter.apply {
                setData(addresses.sortedByDescending { it.createTime })
                notifyDataSetChanged()
            }
        }
    }

    fun setOnTouchListener(touchListener: View.OnTouchListener?) {
        this.touchListener = touchListener
    }
}

enum class AddressPagerType {
    FULL, SMALL
}

enum class Tab {
    ACTIVE, EXPIRED, CONTACTS;
}
