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

package com.mw.beam.beamwallet.screens.create_password

import android.text.Editable
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.mw.beam.beamwallet.R
import com.mw.beam.beamwallet.base_screen.BaseFragment
import com.mw.beam.beamwallet.base_screen.BasePresenter
import com.mw.beam.beamwallet.base_screen.MvpRepository
import com.mw.beam.beamwallet.base_screen.MvpView
import com.mw.beam.beamwallet.core.helpers.WelcomeMode
import com.mw.beam.beamwallet.core.views.PasswordStrengthView
import com.mw.beam.beamwallet.core.watchers.TextWatcher
import kotlinx.android.synthetic.main.fragment_passwords.*


/**
 * Created by vain onnellinen on 10/23/18.
 */
class PasswordFragment : BaseFragment<PasswordPresenter>(), PasswordContract.View {
    private val passWatcher = object : TextWatcher {
        override fun afterTextChanged(password: Editable?) {
            presenter?.onPassChanged(password?.toString())
        }
    }

    private val confirmPassWatcher = object : TextWatcher {
        override fun afterTextChanged(password: Editable?) {
            presenter?.onConfirmPassChanged()
        }
    }

    private val onBackPressedCallback: OnBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            presenter?.onBackPressed()
        }
    }

    override fun onControllerGetContentLayoutId() = R.layout.fragment_passwords
    override fun getToolbarTitle(): String? = getString(R.string.change_password)

    override fun init(isModeChangePass: Boolean) {
        if (isModeChangePass) {
            description.text = getString(R.string.pass_screen_change_description)
            btnProceed.textResId = R.string.pass_save_new
            btnProceed.iconResId = R.drawable.ic_btn_save
        } else {
            description.text = getString(R.string.pass_screen_description)
            btnProceed.textResId = R.string.pass_proceed_to_wallet
            btnProceed.iconResId = R.drawable.ic_btn_proceed
            requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        }

        passLayout.typeface = ResourcesCompat.getFont(context!!, R.font.roboto_regular)
        confirmPassLayout.typeface = ResourcesCompat.getFont(context!!, R.font.roboto_regular)
    }

    override fun addListeners() {
        pass.addTextChangedListener(passWatcher)
        confirmPass.addTextChangedListener(confirmPassWatcher)

        btnProceed.setOnClickListener {
            presenter?.onProceed()
        }
    }

    override fun getSeed(): Array<String>? = arguments?.let { PasswordFragmentArgs.fromBundle(it).phrases }
    override fun getPass(): String = pass.text?.toString() ?: ""
    override fun isModeChangePass(): Boolean = arguments?.let { PasswordFragmentArgs.fromBundle(it).passChangeMode } ?: false
    override fun getWelcomeMode(): WelcomeMode? {
        return arguments?.let {
            val modeName = PasswordFragmentArgs.fromBundle(it).mode
            WelcomeMode.valueOf(modeName ?: return null)
        }
    }

    override fun proceedToWallet(mode: WelcomeMode, pass: String, seed: Array<String>) {
        findNavController().navigate(PasswordFragmentDirections.actionPasswordFragmentToWelcomeProgressFragment(pass, mode.name, seed))
    }
    override fun showSeedFragment() {
        findNavController().navigate(PasswordFragmentDirections.actionPasswordFragmentToWelcomeSeedFragment())
    }

    override fun completePassChanging() {
        findNavController().popBackStack()
    }

    override fun hasErrors(): Boolean {
        var hasErrors = false
        clearErrors()

        if (pass.text.isNullOrBlank()) {
            passError.visibility = View.VISIBLE
            passError.text = getString(R.string.password_can_not_be_empty)
            pass.isStateError = true
            hasErrors = true
        }

        if (!pass.text.isNullOrBlank() && pass.text.toString() != confirmPass.text.toString()) {
            passError.visibility = View.VISIBLE
            passError.text = getString(R.string.password_not_match)
            confirmPass.isStateError = true
            hasErrors = true
        }

        if (confirmPass.text.isNullOrBlank()) {
            passError.visibility = View.VISIBLE
            passError.text = getString(R.string.password_can_not_be_empty)
            confirmPass.isStateError = true
            hasErrors = true
        }

        return hasErrors
    }

    override fun showOldPassError() {
        passError.visibility = View.VISIBLE
        passError.text = getString(R.string.pass_old_pass_error)
        pass.isStateError = true
    }

    override fun clearErrors() {
        passError.visibility = View.GONE

        if (pass.isFocused) {
            pass.isStateAccent = true
        } else {
            pass.isStateNormal = true
        }

        if (confirmPass.isFocused) {
            confirmPass.isStateAccent = true
        } else {
            confirmPass.isStateNormal = true
        }
    }

    override fun showSeedAlert() {
        showAlert(message = getString(R.string.pass_return_seed_message),
                title = getString(R.string.pass_return_seed_title),
                btnConfirmText = getString(R.string.pass_return_seed_btn_create_new),
                btnCancelText = getString(R.string.cancel),
                onConfirm = { presenter?.onCreateNewSeed() })
    }

    override fun clearListeners() {
        pass.removeTextChangedListener(passWatcher)
        confirmPass.removeTextChangedListener(confirmPassWatcher)
        btnProceed.setOnClickListener(null)
    }

    override fun setStrengthLevel(strength: PasswordStrengthView.Strength) {
        strengthView.strength = strength
    }

    override fun initPresenter(): BasePresenter<out MvpView, out MvpRepository> {
        return PasswordPresenter(this, PasswordRepository(), PasswordState())
    }
}
