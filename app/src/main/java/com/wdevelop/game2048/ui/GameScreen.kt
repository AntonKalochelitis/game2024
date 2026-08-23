package com.wdevelop.game2048.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wdevelop.game2048.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel
) {

    val state by viewModel.state.collectAsState()

    var achievements by remember {
        mutableStateOf(
            viewModel.getAchievements()
        )
    }

    LaunchedEffect(
        state.score,
        state.tiles
    ) {
        achievements =
            viewModel.getAchievements()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                GameColors.Background
            )
            .padding(
                horizontal = 20.dp,
                vertical = 18.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Header(
            onSettings =
                viewModel::openSettings,
            onNewGame =
                viewModel::newGame
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        Text(
            text = "2048",
            color = GameColors.Primary,
            fontSize = 54.sp,
            fontWeight =
                FontWeight.ExtraBold
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        ScorePanel(
            score = state.score,
            best = state.bestScore
        )

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )

        GameBoard(
            tiles = state.tiles,
            onMove = viewModel::move,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
        )

        Spacer(
            modifier =
                Modifier.weight(1f)
        )

        Button(
            onClick =
                viewModel::newGame
        ) {

            Icon(
                imageVector =
                    Icons.Default.Refresh,
                contentDescription =
                    null
            )

            Text(
                text = "  НОВАЯ ИГРА"
            )
        }
    }

    if (state.showSettings) {

        SettingsDialog(
            soundEnabled =
                state.soundEnabled,
            achievements =
                achievements,
            onSoundChanged =
                viewModel::setSoundEnabled,
            onClose =
                viewModel::closeSettings
        )
    }

    if (state.showWinDialog) {

        WinDialog(
            onContinue =
                viewModel::continueAfterWin,
            onRestart =
                viewModel::newGame
        )
    }

    if (state.isGameOver) {

        GameOverDialog(
            onRestart =
                viewModel::newGame
        )
    }
}

@Composable
private fun Header(
    onSettings: () -> Unit,
    onNewGame: () -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        IconButton(
            onClick = onSettings,
            modifier =
                Modifier.size(54.dp)
        ) {

            Icon(
                imageVector =
                    Icons.Default.Settings,
                contentDescription =
                    "Настройки",
                tint =
                    GameColors.Primary,
                modifier =
                    Modifier.size(34.dp)
            )
        }

        IconButton(
            onClick = onNewGame,
            modifier =
                Modifier.size(54.dp)
        ) {

            Icon(
                imageVector =
                    Icons.Default.Add,
                contentDescription =
                    "Новая игра",
                tint =
                    GameColors.Primary,
                modifier =
                    Modifier.size(34.dp)
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
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        ScoreCard(
            title = "ОЧКИ",
            value = score,
            modifier =
                Modifier.weight(1f)
        )

        ScoreCard(
            title = "ЛУЧШИЙ",
            value = best,
            modifier =
                Modifier.weight(1f)
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
        modifier =
            modifier
                .background(
                    GameColors.Surface,
                    RoundedCornerShape(14.dp)
                )
                .padding(14.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = title,
            color =
                GameColors.Primary,
            fontWeight =
                FontWeight.Bold
        )

        Text(
            text = value.toString(),
            color =
                GameColors.TextDark,
            fontSize = 30.sp,
            fontWeight =
                FontWeight.ExtraBold
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

        title = {
            Text("ПОБЕДА!")
        },

        text = {
            Text(
                "Вы собрали плитку 2048!"
            )
        },

        confirmButton = {
            Button(
                onClick = onContinue
            ) {
                Text("ПРОДОЛЖИТЬ")
            }
        },

        dismissButton = {
            Button(
                onClick = onRestart
            ) {
                Text("ЗАНОВО")
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

        title = {
            Text("ИГРА ОКОНЧЕНА")
        },

        text = {
            Text(
                "Доступных ходов больше нет."
            )
        },

        confirmButton = {
            Button(
                onClick = onRestart
            ) {
                Text("ЗАНОВО")
            }
        }
    )
}
