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

package com.mw.beam.beamwallet.screens.welcome_screen.welcome_open

import com.mw.beam.beamwallet.base_screen.MvpPresenter
import com.mw.beam.beamwallet.base_screen.MvpRepository
import com.mw.beam.beamwallet.base_screen.MvpView

/**
 * Created by vain onnellinen on 10/19/18.
 */
interface WelcomeOpenContract {
    interface View : MvpView {
        fun init(shouldInitFingerprint : Boolean)
        fun hasValidPass(): Boolean
        fun getPass(): String
        fun openWallet(pass: String)
        fun changeWallet()
        fun showChangeAlert()
        fun showOpenWalletError()
        fun showFingerprintAuthError()
        fun clearError()
        fun clearFingerprintCallback()
    }

    interface Presenter : MvpPresenter<View> {
        fun onOpenWallet()
        fun onChangeWallet()
        fun onChangeConfirm()
        fun onPassChanged()
        fun onFingerprintError()
        fun onFingerprintSucceeded()
        fun onFingerprintFailed()
    }

    interface Repository : MvpRepository {
        fun isFingerPrintEnabled(): Boolean
    }
}
