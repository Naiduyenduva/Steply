package com.example.stepcounter.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import android.util.Log
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import com.example.stepcounter.data.entity.UserStepsEntity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

fun getCalendarWeek(): List<LocalDate> {
    val today = LocalDate.now()
    val startDay = today.minusDays(3)
    
    // Create a list of 7 days starting from Sunday
    return (0..6).map { startDay.plusDays(it.toLong()) }
}

@Composable
fun HealthDashboardScreen(steps: Int, history: List<UserStepsEntity>) {
    Scaffold(
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Header()
            Spacer(Modifier.height(16.dp))
            DateCards()
            Spacer(Modifier.height(16.dp))
            StepsCard(steps = steps)
            Spacer(Modifier.height(16.dp))
            StepHistoryList(history)
            Spacer(Modifier.height(16.dp))

        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
            modifier = Modifier
            .size(42.dp)
            .background(Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center //centers the icon inside the circle
            ) {
                Icon (
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Profile",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text("Welcome", fontSize = 12.sp, color = Color.Gray)
                Text("Laxmu Naidu", fontWeight = FontWeight.Bold)
            }
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null
        )
    }
}

@Composable
fun DateCards () {
    val weekDates = remember { getCalendarWeek() }
    val today = remember { LocalDate.now() }
    // This "SideEffect" runs after every successful recomposition
    SideEffect {
        Log.d("HealthApp", "Today is: $today")
        Log.d("HealthApp", "Week Dates: ${weekDates.joinToString(", ")}")
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDates.forEach { date -> 
                val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                // Format the date (e.g., "12")
                val dayNumber = date.dayOfMonth.toString()
                DateItem(
                day = dayName,
                date = dayNumber, 
                isSelected = date == today
            )
        }
    }

}

@Composable
fun DateItem (day: String, date: String, isSelected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(30.dp)).background(if(isSelected) Color(0xFF1ABC9C) else Color.White).padding(vertical = 12.dp, horizontal = 8.dp)
            .width(30.dp) // Fixed width ensures they are uniform
    ) {
        Text(
            text = day,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = date,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
    }

}

@Composable
fun StepHistoryList(history: List<UserStepsEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Gap between cards
    ) {
        items(history) { dayEntry ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp), // Slight breathing room from screen edges
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp), // Padding inside the card
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = dayEntry.date,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "${dayEntry.stepCount} steps",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2196F3) // A nice blue color for the steps
                    )
                }
            }
        }
    }
}

@Composable
fun StepProgressCircle(
    steps: Int,
    targetSteps: Int = 15000,
    modifier: Modifier = Modifier,
    progressColor: Color = Color(0xFF6C63FF), // your app color
    backgroundColor: Color = Color(0xFFE0E0E0)
) {
    val progress = (steps / targetSteps.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            val strokeWidth = 14.dp.toPx()
            val radius = size.minDimension / 2 - strokeWidth

            // Background ring
            drawCircle(
                color = backgroundColor,
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Glow effect
            drawCircle(
                color = progressColor.copy(alpha = 0.25f),
                radius = radius,
                style = Stroke(width = strokeWidth + 6.dp.toPx())
            )

            // Progress arc
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        // Center text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = steps.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Steps",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun StepsCard(steps: Int) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            StepProgressCircle(
                steps = steps,
                modifier = Modifier
            )

            Spacer(Modifier.width(20.dp))

            Column {
                Text("Today", fontWeight = FontWeight.Bold)
                Text("Target 15,000 steps", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

