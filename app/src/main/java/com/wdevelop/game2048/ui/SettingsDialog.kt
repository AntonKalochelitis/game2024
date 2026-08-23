package com.wdevelop.game2048.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wdevelop.game2048.R
import com.wdevelop.game2048.data.Achievement

@Composable
fun SettingsDialog(
    soundEnabled: Boolean,
    achievements: List<Achievement>,
    onSoundChanged: (Boolean) -> Unit,
    onClose: () -> Unit
) {

    val context = LocalContext.current

    AlertDialog(

        onDismissRequest = onClose,

        title = {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = stringResource(R.string.settings_title),
                    modifier =
                        Modifier.weight(1f)
                )

                IconButton(
                    onClick = onClose
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.Close,
                        contentDescription =
                            stringResource(R.string.settings_close_desc)
                    )
                }
            }
        },

        text = {

            Column {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = stringResource(R.string.sound_label),
                        modifier =
                            Modifier.weight(1f)
                    )

                    Switch(
                        checked = soundEnabled,
                        onCheckedChange =
                            onSoundChanged
                    )
                }

                TextButton(
                    onClick = {

                        val uri =
                            Uri.parse(
                                "market://details?id=com.wdevelop.game2048"
                            )

                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                uri
                            )

                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {

                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(
                                        "https://play.google.com/store/apps/details?id=com.wdevelop.game2048"
                                    )
                                )
                            )
                        }
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Star,
                        contentDescription = null
                    )

                    Text(
                        text = "  " + stringResource(R.string.rate_us_button)
                    )
                }

                HorizontalDivider(
                    modifier =
                        Modifier.padding(
                            vertical = 8.dp
                        )
                )

                Text(
                    text = stringResource(R.string.achievements_label)
                )

                LazyColumn {

                    items(
                        achievements,
                        key = {
                            it.id
                        }
                    ) { achievement ->

                        AchievementRow(
                            achievement
                        )
                    }
                }
            }
        },

        confirmButton = {}
    )
}

@Composable
private fun AchievementRow(
    achievement: Achievement
) {
    val context = LocalContext.current
    
    // Dynamically resolve title and description from resources using the keys stored in DB
    val title = try {
        val resId = context.resources.getIdentifier(achievement.title, "string", context.packageName)
        if (resId != 0) context.getString(resId) else achievement.title
    } catch (e: Exception) {
        achievement.title
    }

    val description = try {
        val resId = context.resources.getIdentifier(achievement.description, "string", context.packageName)
        if (resId != 0) context.getString(resId) else achievement.description
    } catch (e: Exception) {
        achievement.description
    }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 7.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector =
                if (achievement.unlocked) {
                    Icons.Default.Star
                } else {
                    Icons.Default.Lock
                },
            contentDescription = null
        )

        Column(
            modifier =
                Modifier.padding(
                    start = 10.dp
                )
        ) {

            Text(
                text = title
            )

            Text(
                text = description
            )
        }
    }
}
