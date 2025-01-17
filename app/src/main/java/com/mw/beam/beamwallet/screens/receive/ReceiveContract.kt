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

package com.mw.beam.beamwallet.screens.receive

import androidx.lifecycle.LifecycleOwner
import com.mw.beam.beamwallet.base_screen.MvpPresenter
import com.mw.beam.beamwallet.base_screen.MvpRepository
import com.mw.beam.beamwallet.base_screen.MvpView
import com.mw.beam.beamwallet.core.entities.WalletAddress
import com.mw.beam.beamwallet.core.helpers.Category
import com.mw.beam.beamwallet.core.helpers.ExpirePeriod
import io.reactivex.subjects.Subject

/**
 * Created by vain onnellinen on 11/13/18.
 */
interface ReceiveContract {
    interface View : MvpView {
        fun init()
        fun initAddress(isGenerateAddress: Boolean, walletAddress: WalletAddress)
        fun getComment() : String?
        fun showQR(walletAddress: WalletAddress, amount: Long?, isAutogenerated: Boolean)
        fun shareToken(receiveToken: String)
        fun close()
        fun getAmountFromArguments(): Long
        fun getAmount(): Double?
        fun getTxComment(): String?
        fun setAmount(newAmount: Double)
        fun showChangeAddressFragment(generatedAddress: WalletAddress?)
        fun configCategory(currentCategory: Category?)
        fun handleExpandEditAddress(expand: Boolean)
        fun handleExpandAdvanced(expand: Boolean)
        fun showAddNewCategory()
        fun getLifecycleOwner(): LifecycleOwner
        fun getWalletAddressFromArguments(): WalletAddress?
        fun showSaveAddressDialog(nextStep: () -> Unit)
        fun showSaveChangesDialog(nextStep: () -> Unit)
    }

    interface Presenter : MvpPresenter<View> {
        fun onShareTokenPressed()
        fun onShowQrPressed()
        fun onExpirePeriodChanged(period : ExpirePeriod)
        fun onSelectedCategory(category: Category?)
        fun onAdvancedPressed()
        fun onEditAddressPressed()
        fun onChangeAddressPressed()
        fun onAddNewCategoryPressed()
        fun onSaveAddressPressed()
        fun onBackPressed()
        fun onAddressChanged(walletAddress: WalletAddress)
    }

    interface Repository : MvpRepository {
        fun generateNewAddress() : Subject<WalletAddress>
        fun saveAddress(address: WalletAddress)
        fun getCategory(address: String): Category?
        fun changeCategoryForAddress(address: String, category: Category?)
        fun updateAddress(address: WalletAddress)
    }
}
