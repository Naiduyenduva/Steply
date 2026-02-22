package com.example.stepcounter.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.stepcounter.data.entity.UserStepsEntity

@Composable
fun MainScreen(
    steps: Int,
    history: List<UserStepsEntity>,
    isDark: Boolean,
    onThemeToggle: () -> Unit
) {
    var activeTab by remember { mutableStateOf(Tab.HOME) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    Tab.HOME -> HomeTab(steps = steps, history = history)
                    Tab.ACHIEVEMENTS -> AchievementsTab(history = history)
                    Tab.SETTINGS -> SettingsTab(
                        history = history,
                        isDark = isDark,
                        onThemeToggle = onThemeToggle
                    )
                }
            }
            AppNavigationBar(
                activeTab = activeTab,
                onTabChange = { activeTab = it }
            )
        }
    }
}
