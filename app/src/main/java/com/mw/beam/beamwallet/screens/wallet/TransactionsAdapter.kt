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

package com.mw.beam.beamwallet.screens.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mw.beam.beamwallet.R
import com.mw.beam.beamwallet.core.entities.TxDescription
import com.mw.beam.beamwallet.core.helpers.TxSender
import com.mw.beam.beamwallet.core.helpers.convertToBeamWithSign
import com.mw.beam.beamwallet.core.utils.CalendarUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_transaction.*

/**
 * Created by vain onnellinen on 10/2/18.
 */
class TransactionsAdapter(private val context: Context, var data: List<TxDescription>, private val clickListener: OnItemClickListener) :
        androidx.recyclerview.widget.RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {
    private val beamResId = R.drawable.ic_beam
    private val notMultiplyColor = ContextCompat.getColor(context, R.color.wallet_adapter_not_multiply_color)
    private val multiplyColor = ContextCompat.getColor(context, R.color.wallet_adapter_multiply_color)
    private val receiveText = context.getString(R.string.receive)
    private val sendText = context.getString(R.string.send)
    private val currencyBeam = context.getString(R.string.currency_beam)
    private var privacyMode: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false)).apply {
        this.containerView.setOnClickListener {
            clickListener.onItemClick(data[adapterPosition])
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = data[position]

        holder.apply {
            val messageStatus = when (transaction.sender) {
                TxSender.RECEIVED -> receiveText
                TxSender.SENT -> sendText
            }

            message.text = "$messageStatus ${currencyBeam.toUpperCase()}" //TODO replace when multiply currency will be available

            itemView.setBackgroundColor(if (position % 2 == 0) notMultiplyColor else multiplyColor) //logically reversed because count starts from zero
            icon.setImageResource(beamResId)
            date.text = CalendarUtils.fromTimestamp(transaction.modifyTime)
            currency.setImageDrawable(transaction.currencyImage)

            sum.text = transaction.amount.convertToBeamWithSign(transaction.sender.value)
            sum.setTextColor(transaction.amountColor)

            status.setTextColor(transaction.statusColor)
            status.text = transaction.statusString

            val amountVisibility = if (privacyMode) View.GONE else View.VISIBLE
            sum.visibility = amountVisibility
            currency.visibility = amountVisibility
        }
    }

    override fun getItemCount(): Int = data.size

    fun setPrivacyMode(isEnable: Boolean) {
        if (privacyMode != isEnable) {
            privacyMode = isEnable
            notifyDataSetChanged()
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: TxDescription)
    }

    class ViewHolder(override val containerView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(containerView), LayoutContainer
}
