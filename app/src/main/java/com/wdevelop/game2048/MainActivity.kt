package com.wdevelop.game2048

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.wdevelop.game2048.ui.GameScreen

class MainActivity : ComponentActivity() {

    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Инициализируем перехватчик ПЕРВЫМ делом
        CrashHandler.init(applicationContext)
        
        super.onCreate(savedInstanceState)

        val lastCrash = CrashHandler.getLatestCrash(applicationContext)

        if (AdConfig.AD_ENABLED) {
            try {
                MobileAds.initialize(this) { status ->
                    Log.d("AdMob", "Initialization status: $status")
                }
                loadInterstitial()
            } catch (e: Exception) {
                Log.e("AdMob", "Critical error during AdMob initialization", e)
            }
        }

        enableEdgeToEdge()

        setContent {
            val showErrorDialog = remember { mutableStateOf(lastCrash != null) }
            
            if (showErrorDialog.value && lastCrash != null) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog.value = false },
                    title = { Text("DEBUG: Last Crash Detected") },
                    text = { Text(lastCrash) },
                    confirmButton = {
                        TextButton(onClick = { showErrorDialog.value = false }) {
                            Text("OK")
                        }
                    }
                )
            }

            val viewModel: GameViewModel = viewModel()

            GameScreen(
                viewModel = viewModel,
                onShowInterstitial = {
                    showInterstitial()
                }
            )
        }
    }

    private fun loadInterstitial() {
        if (!AdConfig.AD_ENABLED) return

        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                this,
                AdConfig.INTERSTITIAL_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        Log.d("AdMob", "Interstitial Ad Loaded")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        Log.e("AdMob", "Interstitial Ad failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("AdMob", "Error while loading Interstitial", e)
        }
    }

    private fun showInterstitial() {
        if (!AdConfig.AD_ENABLED) return

        try {
            if (!isFinishing && !isDestroyed && interstitialAd != null) {
                interstitialAd?.show(this)
                interstitialAd = null
                loadInterstitial()
            } else {
                Log.d("AdMob", "Interstitial not ready or Activity state invalid")
                loadInterstitial()
            }
        } catch (e: Exception) {
            Log.e("AdMob", "Error while showing Interstitial", e)
            loadInterstitial()
        }
    }
}
