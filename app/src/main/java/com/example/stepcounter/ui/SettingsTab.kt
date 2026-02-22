package com.example.stepcounter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stepcounter.data.entity.UserStepsEntity
import com.example.stepcounter.ui.theme.Accent
import com.example.stepcounter.ui.theme.AccentContainer
import com.example.stepcounter.ui.theme.Primary
import com.example.stepcounter.ui.theme.PrimaryContainer

@Composable
fun SettingsTab(
    history: List<UserStepsEntity>,
    isDark: Boolean,
    onThemeToggle: () -> Unit
) {
    val totalSteps = history.sumOf { it.stepCount }
    val avgSteps = if (history.isNotEmpty()) totalSteps / history.size else 0
    val goalAchievements = history.count { it.stepCount >= 10_000 }
    val goalPct = if (history.isNotEmpty()) (goalAchievements * 100 / history.size) else 0

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
            Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Preferences & profile", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }

        // Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Primary.copy(alpha = 0.08f), Accent.copy(alpha = 0.08f))
                        )
                    )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Primary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Laxmu Naidu", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Premium Member", fontSize = 13.sp, color = Primary, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "%,d total steps".format(totalSteps),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Preferences Section
        SectionTitle("Preferences")
        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                // Dark Mode toggle
                PreferenceRow(
                    icon = if (isDark) Icons.Default.WbSunny else Icons.Default.Nightlight,
                    iconTint = if (isDark) Accent else Primary,
                    iconBg = if (isDark) AccentContainer else PrimaryContainer,
                    title = "Dark Mode",
                    subtitle = if (isDark) "On" else "Off",
                    action = {
                        DarkModeToggle(isDark = isDark, onToggle = onThemeToggle)
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                PreferenceRow(
                    icon = Icons.Default.TrackChanges,
                    iconTint = Accent,
                    iconBg = AccentContainer,
                    title = "Daily Goal",
                    subtitle = "10,000 steps",
                    action = {
                        Text("Edit", fontSize = 13.sp, color = Primary, fontWeight = FontWeight.Medium)
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                PreferenceRow(
                    icon = Icons.Default.Notifications,
                    iconTint = Primary,
                    iconBg = PrimaryContainer,
                    title = "Notifications",
                    subtitle = "Enabled",
                    action = {
                        Text("Manage", fontSize = 13.sp, color = Primary, fontWeight = FontWeight.Medium)
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Your Stats grid
        SectionTitle("Your Stats")
        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatGridItem(
                        modifier = Modifier.weight(1f),
                        label = "Avg Daily Steps",
                        value = "%,d".format(avgSteps),
                        valueColor = Primary
                    )
                    StatGridItem(
                        modifier = Modifier.weight(1f),
                        label = "Calories Burned",
                        value = "${(totalSteps * 0.04f).toInt()} kcal",
                        valueColor = Accent
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatGridItem(
                        modifier = Modifier.weight(1f),
                        label = "Distance",
                        value = "${"%.1f".format(totalSteps * 0.000762f)} km",
                        valueColor = Primary
                    )
                    StatGridItem(
                        modifier = Modifier.weight(1f),
                        label = "Goal Achievement",
                        value = "$goalPct%",
                        valueColor = Accent
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // About
        SectionTitle("About")
        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            PreferenceRow(
                icon = Icons.Default.Info,
                iconTint = Primary,
                iconBg = PrimaryContainer,
                title = "About StepTrack",
                subtitle = "Version 1.0.0",
                action = {}
            )
        }

        Spacer(Modifier.height(8.dp))
    }
    }
 } // end Scaffold

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun PreferenceRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
        }
        action()
    }
}

@Composable
private fun DarkModeToggle(isDark: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .width(44.dp)
            .height(26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(if (isDark) Primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            .clickable(onClick = onToggle),
        contentAlignment = if (isDark) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(3.dp)
                .size(20.dp)
                .background(Color.White, CircleShape)
        )
    }
}

@Composable
private fun StatGridItem(modifier: Modifier, label: String, value: String, valueColor: Color) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor)
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
