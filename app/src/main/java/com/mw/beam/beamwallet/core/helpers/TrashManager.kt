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

package com.mw.beam.beamwallet.core.helpers

import com.mw.beam.beamwallet.core.App
import com.mw.beam.beamwallet.core.entities.TxDescription
import com.mw.beam.beamwallet.core.entities.WalletAddress
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

object TrashManager {
    private val actions = HashMap<String, ActionData>()

    val subOnTrashChanged: Subject<Action> = PublishSubject.create()

    fun add(id: String, address: WalletAddress) {
        val actionData = ActionData(listOf(), listOf(address))
        actions[id] = actionData
        notifyChanged(TrashManager.ActionType.Added, actionData)
    }

    fun getAllData(): ActionData {
        val txDescriptions = HashMap<String, TxDescription>()
        val addresses = HashMap<String, WalletAddress>()
        actions.values.forEach {
            it.transactions.forEach { transaction -> txDescriptions[transaction.id] = transaction }
            it.addresses.forEach { address -> addresses[address.walletID] = address }
        }

        return ActionData(txDescriptions.values.toList(), addresses.values.toList())
    }

    fun add(id: String, txDescription: TxDescription) {
        val actionData = ActionData(listOf(txDescription), listOf())
        actions[id] = actionData
        notifyChanged(TrashManager.ActionType.Added, actionData)
    }

    fun add(id: String, data: ActionData) {
        actions[id] = data
        notifyChanged(TrashManager.ActionType.Added, data)
    }

    fun remove(id: String) {
        actions[id]?.apply {
            addresses.forEach {
                CategoryHelper.changeCategoryForAddress(it.walletID, null)
                App.wallet?.deleteAddress(it.walletID)
            }

            transactions.forEach {
                App.wallet?.deleteTx(it.id)
            }

            notifyChanged(TrashManager.ActionType.Removed, this)
        }

        actions.remove(id)
    }

    fun restore(id: String) {
        actions[id]?.let { notifyChanged(TrashManager.ActionType.Restored, it) }
        actions.remove(id)
    }

    private fun notifyChanged(type: ActionType, data: ActionData) {
        subOnTrashChanged.onNext(Action(type, data))
    }

    enum class ActionType {
        Added,
        Restored,
        Removed
    }

    data class Action(val type: ActionType, val data: ActionData)

    data class ActionData(val transactions: List<TxDescription>, val addresses: List<WalletAddress>)
}