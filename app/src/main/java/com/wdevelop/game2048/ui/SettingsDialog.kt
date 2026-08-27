package com.wdevelop.game2048.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wdevelop.game2048.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsDialog(
    soundEnabled: Boolean,
    maxTile: Int,
    maxTileDate: Long,
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
                    text = stringResource(R.string.best_score_label) + " (Tile)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                if (maxTile > 0) {
                    val dateStr = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(Date(maxTileDate))
                    
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = maxTile.toString(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GameColors.Primary
                        )
                        Text(
                            text = dateStr,
                            fontSize = 14.sp,
                            color = GameColors.TextDark.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    Text(
                        text = "-",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },

        confirmButton = {}
    )
}
