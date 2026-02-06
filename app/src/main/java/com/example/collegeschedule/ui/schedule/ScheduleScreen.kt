package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.utils.getWeekDateRange
import com.example.collegeschedule.ui.components.GroupDropdown
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.collegeschedule.data.local.FavoritesManager

@Composable
fun ScheduleScreen(
    // ДОБАВЛЯЕМ параметры
    selectedGroup: GroupDto? = null,
    onGroupSelected: (GroupDto) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var allGroups by remember { mutableStateOf<List<GroupDto>>(emptyList()) }

    // ДОБАВЛЯЕМ: внутреннее состояние для выбранной группы
    var localSelectedGroup by remember { mutableStateOf<GroupDto?>(selectedGroup) }

    val coroutineScope = rememberCoroutineScope()
    val favoriteGroups by FavoritesManager.favoriteGroups.collectAsState(emptySet())

    // ОБНОВЛЯЕМ: следим за изменением selectedGroup извне
    LaunchedEffect(selectedGroup) {
        if (selectedGroup != null) {
            localSelectedGroup = selectedGroup
        }
    }

    // Загружаем список всех групп при запуске
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            allGroups = RetrofitInstance.api.getAllGroups()
        } catch (e: Exception) {
            errorMessage = "Ошибка загрузки групп: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Загружаем расписание при выборе группы
    LaunchedEffect(localSelectedGroup) {
        if (localSelectedGroup != null) {
            isLoading = true
            errorMessage = null
            try {
                val (startDate, endDate) = getWeekDateRange()
                schedule = RetrofitInstance.api.getSchedule(
                    groupName = localSelectedGroup!!.groupName,
                    start = startDate,
                    end = endDate
                )
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки расписания: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        GroupDropdown(
            groups = allGroups,
            // ИСПОЛЬЗУЕМ localSelectedGroup
            selectedGroup = localSelectedGroup,
            onGroupSelected = { group ->
                // ОБНОВЛЯЕМ оба состояния
                localSelectedGroup = group
                onGroupSelected(group)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Индикатор выбранной группы
        if (localSelectedGroup != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Выбрана группа: ${localSelectedGroup!!.groupName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Курс: ${localSelectedGroup!!.course}, ${localSelectedGroup!!.specialtyName}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Кнопка "Избранное"
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                FavoritesManager.toggleFavoriteGroup(localSelectedGroup!!.groupName)
                            }
                        }
                    ) {
                        val isFavorite = localSelectedGroup!!.groupName in favoriteGroups
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Остальной код без изменений...
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Ошибка",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            localSelectedGroup == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Выбор группы",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Выберите группу из списка выше",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            schedule.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = "Нет занятий",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "На выбранную неделю занятий нет",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            else -> {
                ScheduleList(schedule)
            }
        }
    }
}