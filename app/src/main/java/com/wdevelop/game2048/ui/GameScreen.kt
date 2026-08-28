package com.wdevelop.game2048.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.wdevelop.game2048.AdConfig
import com.wdevelop.game2048.GameViewModel
import com.wdevelop.game2048.R

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onShowInterstitial: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    if (state == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GameColors.Primary)
        }
        return
    }

    val currentState = state!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GameColors.Background)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Header(
            onSettings = viewModel::openSettings,
            onNewGame = {
                viewModel.newGame()
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = currentState.currentMaxTile.toString(),
            color = GameColors.Primary,
            fontSize = 54.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        ScorePanel(
            score = currentState.score,
            best = currentState.bestScore
        )

        Spacer(modifier = Modifier.height(18.dp))

        GameBoard(
            tiles = currentState.tiles,
            onMove = viewModel::move,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Рекламный баннер AdMob в самом низу (только если включена реклама)
        if (AdConfig.AD_ENABLED) {
            AdMobBanner()
        }
    }

    if (currentState.showSettings) {
        SettingsDialog(
            soundEnabled = currentState.soundEnabled,
            maxTile = currentState.maxTileRecord,
            maxTileDate = currentState.maxTileDate,
            onSoundChanged = viewModel::setSoundEnabled,
            onClose = viewModel::closeSettings
        )
    }

    if (currentState.showWinDialog) {
        WinDialog(
            onContinue = viewModel::continueAfterWin,
            onRestart = {
                onShowInterstitial()
                viewModel.newGame()
            }
        )
    }

    if (currentState.isGameOver) {
        GameOverDialog(
            onRestart = {
                onShowInterstitial()
                viewModel.newGame()
            }
        )
    }
}

@Composable
fun AdMobBanner() {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AdConfig.BANNER_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Composable
private fun Header(
    onSettings: () -> Unit,
    onNewGame: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onSettings,
            modifier = Modifier.size(54.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings_gear_desc),
                tint = GameColors.Primary,
                modifier = Modifier.size(34.dp)
            )
        }

        IconButton(
            onClick = onNewGame,
            modifier = Modifier.size(54.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.new_game_desc),
                tint = GameColors.Primary,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
private fun ScorePanel(
    score: Int,
    best: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScoreCard(
            title = stringResource(R.string.score_label),
            value = score,
            modifier = Modifier.weight(1f)
        )
        ScoreCard(
            title = stringResource(R.string.best_score_label),
            value = best,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ScoreCard(
    title: String,
    value: Int,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .background(GameColors.Surface, RoundedCornerShape(14.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = GameColors.Primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value.toString(),
            color = GameColors.TextDark,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun WinDialog(
    onContinue: () -> Unit,
    onRestart: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onContinue,
        title = { Text(stringResource(R.string.victory_title)) },
        text = { Text(stringResource(R.string.victory_message)) },
        confirmButton = {
            Button(onClick = onContinue) {
                Text(stringResource(R.string.continue_button))
            }
        },
        dismissButton = {
            Button(onClick = onRestart) {
                Text(stringResource(R.string.restart_button))
            }
        }
    )
}

@Composable
private fun GameOverDialog(
    onRestart: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(R.string.game_over_title)) },
        text = { Text(stringResource(R.string.game_over_message)) },
        confirmButton = {
            Button(onClick = onRestart) {
                Text(stringResource(R.string.restart_button))
            }
        }
    )
}
