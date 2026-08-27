package com.wdevelop.game2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.wdevelop.game2048.ui.GameScreen

class MainActivity :
    ComponentActivity() {

    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        // Initialize AdMob
        MobileAds.initialize(this) {}
        loadInterstitial()

        enableEdgeToEdge()

        setContent {

            val viewModel:
                GameViewModel =
                viewModel()

            GameScreen(
                viewModel = viewModel,
                onShowInterstitial = {
                    showInterstitial()
                }
            )
        }
    }

    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712", // Test ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    private fun showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd?.show(this)
            loadInterstitial() // Load next one
        } else {
            loadInterstitial()
        }
    }
}
