package com.example.collegeschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.data.local.FavoritesManager
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.ui.favorites.FavoritesScreen
import com.example.collegeschedule.ui.schedule.ScheduleScreen
import com.example.collegeschedule.ui.theme.CollegeScheduleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Инициализируем менеджер избранного
        FavoritesManager.init(this)

        setContent {
            CollegeScheduleTheme {
                CollegeScheduleApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun CollegeScheduleApp() {
    var currentDestination by rememberSaveable {
        mutableStateOf(AppDestinations.HOME)
    }
    var allGroups by remember { mutableStateOf<List<GroupDto>>(emptyList()) }

    var selectedGroup by remember { mutableStateOf<GroupDto?>(null) }

    // Загружаем группы при старте
    LaunchedEffect(Unit) {
        try {
            allGroups = RetrofitInstance.api.getAllGroups()
        } catch (e: Exception) {
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> ScheduleScreen(
                    // ПЕРЕДАЕМ выбранную группу
                    selectedGroup = selectedGroup,
                    // И КОЛБЭК для обновления
                    onGroupSelected = { group ->
                        selectedGroup = group
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                AppDestinations.FAVORITES -> FavoritesScreen(
                    allGroups = allGroups,
                    onGroupClick = { group ->
                        // ПРИ КЛИКЕ НА ГРУППУ В ИЗБРАННОМ:
                        // 1. Запоминаем группу
                        selectedGroup = group
                        // 2. Переключаемся на главный экран
                        currentDestination = AppDestinations.HOME
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                AppDestinations.PROFILE -> Text(
                    "Профиль студента",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}