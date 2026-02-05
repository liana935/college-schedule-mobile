package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.utils.getWeekDateRange
import com.example.collegeschedule.ui.schedule.ScheduleList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen() {
    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var allGroups by remember { mutableStateOf<List<GroupDto>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Загружаем список групп
    LaunchedEffect(Unit) {
        try {
            allGroups = RetrofitInstance.api.getAllGroups()
        } catch (e: Exception) {
            error = "Ошибка загрузки групп: ${e.message}"
        }
    }

    // Загружаем расписание при выборе группы
    LaunchedEffect(selectedGroup) {
        if (selectedGroup.isNotBlank()) {
            loading = true
            error = null
            try {
                val (start, end) = getWeekDateRange()
                schedule = RetrofitInstance.api.getSchedule(selectedGroup, start, end)
            } catch (e: Exception) {
                error = "Ошибка загрузки расписания: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    val filteredGroups = if (searchText.isEmpty()) {
        allGroups
    } else {
        allGroups.filter { it.groupName.contains(searchText, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Выпадающий список с группами
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    if (!expanded) expanded = true
                },
                label = { Text("Поиск группы") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (filteredGroups.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Группа не найдена") },
                        onClick = { }
                    )
                } else {
                    filteredGroups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.groupName) },
                            onClick = {
                                selectedGroup = group.groupName
                                searchText = group.groupName
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Показываем выбранную группу
        if (selectedGroup.isNotBlank()) {
            Text(
                text = "Выбрана группа: $selectedGroup",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Отображение состояния
        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text(
                    text = "Ошибка: $error",
                    color = MaterialTheme.colorScheme.error
                )
            }
            selectedGroup.isBlank() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Выберите группу из списка выше")
                }
            }
            schedule.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Расписание не найдено")
                }
            }
            else -> {
                // Добавляем скроллинг
                ScheduleList(schedule)
            }
        }
    }
}