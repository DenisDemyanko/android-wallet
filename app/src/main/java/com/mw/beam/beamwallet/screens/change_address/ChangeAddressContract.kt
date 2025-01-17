package com.mw.beam.beamwallet.screens.change_address

import com.mw.beam.beamwallet.base_screen.MvpPresenter
import com.mw.beam.beamwallet.base_screen.MvpRepository
import com.mw.beam.beamwallet.base_screen.MvpView
import com.mw.beam.beamwallet.core.entities.OnAddressesData
import com.mw.beam.beamwallet.core.entities.OnTxStatusData
import com.mw.beam.beamwallet.core.entities.TxDescription
import com.mw.beam.beamwallet.core.entities.WalletAddress
import com.mw.beam.beamwallet.core.helpers.Category
import com.mw.beam.beamwallet.core.helpers.PermissionStatus
import com.mw.beam.beamwallet.core.helpers.TrashManager
import io.reactivex.Observable
import io.reactivex.subjects.Subject

interface ChangeAddressContract {
    interface View: MvpView {
        fun isFromReceive(): Boolean
        fun init(state: ViewState, generatedAddress: WalletAddress?)
        fun getGeneratedAddress(): WalletAddress?
        fun updateList(items: List<SearchItem>)
        fun back(walletAddress: WalletAddress?)
        fun setAddress(address: String)
        fun getSearchText(): String
        fun showNotBeamAddressError()
    }

    interface Presenter: MvpPresenter<View> {
        fun onChangeSearchText(text: String)
        fun onItemPressed(walletAddress: WalletAddress)
    }

    interface Repository: MvpRepository {
        fun getTxStatus(): Observable<OnTxStatusData>
        fun getAddresses(): Subject<OnAddressesData>
        fun getCategoryForAddress(address: String): Category?
        fun getTrashSubject(): Subject<TrashManager.Action>
        fun getAllAddressesInTrash(): List<WalletAddress>
        fun getAllTransactionInTrash(): List<TxDescription>
    }

    enum class ViewState {
        Send, Receive
    }
}