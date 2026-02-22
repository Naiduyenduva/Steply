package com.example.stepcounter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stepcounter.data.entity.UserStepsEntity
import com.example.stepcounter.ui.theme.Accent
import com.example.stepcounter.ui.theme.AccentContainer
import com.example.stepcounter.ui.theme.AccentContainerDark
import com.example.stepcounter.ui.theme.AccentLight
import com.example.stepcounter.ui.theme.Primary
import com.example.stepcounter.ui.theme.PrimaryContainer
import com.example.stepcounter.ui.theme.PrimaryContainerDark
import com.example.stepcounter.ui.theme.PrimaryLight

data class Badge(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val unlocked: Boolean
)

data class Milestone(
    val label: String,
    val target: Int
)

@Composable
fun AchievementsTab(history: List<UserStepsEntity>) {
    val totalSteps = history.sumOf { it.stepCount }
    val currentStreak = computeStreak(history)
    val longestStreak = 28

    val badges = listOf(
        Badge("First Steps", "Walk 1,000 steps in a day", Icons.Default.Star, totalSteps >= 1_000),
        Badge("On Fire", "Maintain a 7-day streak", Icons.Default.Favorite, currentStreak >= 7),
        Badge("Marathon", "Walk 20,000 steps in a day", Icons.Default.DirectionsRun, history.any { it.stepCount >= 20_000 }),
        Badge("Heart Healthy", "Reach 100K total steps", Icons.Default.FavoriteBorder, totalSteps >= 100_000),
        Badge("Champion", "Maintain a 30-day streak", Icons.Default.EmojiEvents, currentStreak >= 30),
    )

    val milestones = listOf(
        Milestone("10K Steps", 10_000),
        Milestone("50K Steps", 50_000),
        Milestone("100K Steps", 100_000),
        Milestone("250K Steps", 250_000),
        Milestone("500K Steps", 500_000),
        Milestone("1M Steps", 1_000_000),
    )

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text("Achievements", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Your progress & milestones",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
            val primaryBg = if (isDark) PrimaryContainerDark else PrimaryContainer
            val accentBg = if (isDark) AccentContainerDark else AccentContainer
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StreakCard(
                    modifier = Modifier.weight(1f),
                    title = "Current Streak",
                    value = "$currentStreak",
                    unit = "days",
                    gradient = Brush.linearGradient(listOf(primaryBg, primaryBg.copy(alpha = 0.5f))),
                    borderColor = Primary.copy(alpha = if (isDark) 0.4f else 0.3f),
                    valueColor = if (isDark) PrimaryLight else Primary,
                    icon = Icons.Default.Favorite,
                    iconTint = if (isDark) AccentLight else Accent
                )
                StreakCard(
                    modifier = Modifier.weight(1f),
                    title = "Longest Streak",
                    value = "$longestStreak",
                    unit = "days",
                    gradient = Brush.linearGradient(listOf(accentBg, accentBg.copy(alpha = 0.5f))),
                    borderColor = Accent.copy(alpha = if (isDark) 0.4f else 0.3f),
                    valueColor = if (isDark) AccentLight else Accent,
                    icon = Icons.Default.EmojiEvents,
                    iconTint = if (isDark) AccentLight else Accent
                )
            }

            Spacer(Modifier.height(24.dp))

            val unlockedCount = badges.count { it.unlocked }
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Badges ($unlockedCount/${badges.size})", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    badges.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { badge ->
                                BadgeItem(badge = badge, modifier = Modifier.weight(1f))
                            }
                            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Milestones", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    milestones.forEach { milestone ->
                        MilestoneRow(milestone = milestone, totalSteps = totalSteps)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StreakCard(
    modifier: Modifier,
    title: String,
    value: String,
    unit: String,
    gradient: Brush,
    borderColor: Color,
    valueColor: Color,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(value, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = valueColor)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        unit,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(badge: Badge, modifier: Modifier) {
    val bgColor = if (badge.unlocked) MaterialTheme.colorScheme.surface
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    val borderColor = if (badge.unlocked) Primary.copy(alpha = 0.3f)
    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    val iconBg = if (badge.unlocked) Primary.copy(alpha = 0.15f)
    else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)

    val iconTint = if (badge.unlocked) Primary
    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (badge.unlocked) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier.size(36.dp).background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(badge.icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                badge.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (badge.unlocked) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
            Text(
                badge.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (badge.unlocked) 0.5f else 0.3f),
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun MilestoneRow(milestone: Milestone, totalSteps: Int) {
    val fraction = (totalSteps / milestone.target.toFloat()).coerceIn(0f, 1f)
    val completed = totalSteps >= milestone.target

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(milestone.label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
            if (completed) {
                Text("✓", fontSize = 14.sp, color = Primary, fontWeight = FontWeight.Bold)
            } else {
                Text("${(fraction * 100).toInt()}%", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            }
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = if (completed) Primary else Primary.copy(alpha = 0.5f),
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    }
}

private fun computeStreak(history: List<UserStepsEntity>): Int {
    if (history.isEmpty()) return 0
    val sorted = history.sortedByDescending { it.date }
    var streak = 0
    var expectedDate = java.time.LocalDate.now()
    for (entry in sorted) {
        val entryDate = try { java.time.LocalDate.parse(entry.date) } catch (e: Exception) { break }
        if (entryDate == expectedDate) {
            streak++
            expectedDate = expectedDate.minusDays(1)
        } else {
            break
        }
    }
    return streak
}

private fun androidx.compose.ui.graphics.Color.luminance(): Float =
    0.2126f * red + 0.7152f * green + 0.0722f * blue
