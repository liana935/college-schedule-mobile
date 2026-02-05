package com.example.collegeschedule.ui.schedule

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.utils.getWeekDateRange

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
            modifier = Modifier.menuAnchor()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
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

    // Показываем выбранную группу
    if (selectedGroup.isNotBlank()) {
        Text("Выбрана группа: $selectedGroup")
    }

    when {
        loading -> CircularProgressIndicator()
        error != null -> Text("Ошибка: $error")
        selectedGroup.isBlank() -> Text("Выберите группу из списка")
        schedule.isEmpty() -> Text("Расписание не найдено")
        else -> ScheduleList(schedule)
    }
}