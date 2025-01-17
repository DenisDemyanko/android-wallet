package com.mw.beam.beamwallet.screens.app_activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.navigation.AnimBuilder
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.mw.beam.beamwallet.R
import com.mw.beam.beamwallet.base_screen.BaseActivity
import com.mw.beam.beamwallet.base_screen.BasePresenter
import com.mw.beam.beamwallet.base_screen.MvpRepository
import com.mw.beam.beamwallet.base_screen.MvpView
import com.mw.beam.beamwallet.core.App
import com.mw.beam.beamwallet.screens.transaction_details.TransactionDetailsFragmentArgs

class AppActivity : BaseActivity<AppActivityPresenter>(), AppActivityContract.View {

    companion object {
        const val TRANSACTION_ID = "TRANSACTION_ID"
    }

    override fun onControllerGetContentLayoutId(): Int = R.layout.activity_app

    override fun getToolbarTitle(): String? = null

    override fun showOpenFragment() {
        val navController = findNavController(R.id.nav_host)
        navController.navigate(R.id.welcomeOpenFragment, null, navOptions {
            popUpTo(R.id.navigation) { inclusive = true }
            launchSingleTop = true
            anim(buildTransitionAnimation())
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.isAppRunning = true
        super.onCreate(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        App.isAppRunning = true
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onDestroy() {
        App.isAppRunning = false
        super.onDestroy()
    }

    override fun onControllerCreate(extras: Bundle?) {
        super.onControllerCreate(extras)
        App.intentTransactionID = extras?.getString(TRANSACTION_ID)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        presenter?.onNewIntent(intent?.extras?.getString(TRANSACTION_ID))
    }

    override fun showWalletFragment() {
        val navController = findNavController(R.id.nav_host)
        navController.navigate(R.id.walletFragment, null, navOptions {
            popUpTo(R.id.navigation) { inclusive = true }
            launchSingleTop = true
            anim(buildTransitionAnimation())
        })
    }

    override fun showTransactionDetailsFragment(txId: String) {
        findNavController(R.id.nav_host).navigate(R.id.transactionDetailsFragment, TransactionDetailsFragmentArgs(txId).toBundle(), navOptions {
            popUpTo(R.id.walletFragment) {}
            anim(buildTransitionAnimation())
        })
    }

    private fun buildTransitionAnimation(): AnimBuilder.() -> Unit = {
        enter = R.anim.fade_in
        popEnter = R.anim.fade_in
        exit = R.anim.fade_out
        popExit = R.anim.fade_out
    }

    fun pendingSend(info: PendingSendInfo) {
        presenter?.onPendingSend(info)
    }

    override fun startNewSnackbar(onUndo: () -> Unit, onDismiss: () -> Unit) {
        showSnackBar(getString(R.string.wallet_sent_message), onDismiss, onUndo)
    }

    override fun ensureState(): Boolean = true

    override fun initPresenter(): BasePresenter<out MvpView, out MvpRepository> {
        return AppActivityPresenter(this, AppActivityRepository())
    }
}