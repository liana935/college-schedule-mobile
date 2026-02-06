package com.example.collegeschedule.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.collegeschedule.data.dto.GroupDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDropdown(
    groups: List<GroupDto>,
    selectedGroup: GroupDto?,
    onGroupSelected: (GroupDto) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    // Фильтруем группы по поисковому запросу
    val filteredGroups = if (searchText.isBlank()) {
        groups
    } else {
        groups.filter { group ->
            group.groupName.contains(searchText, ignoreCase = true) ||
                    group.specialtyName.contains(searchText, ignoreCase = true)
        }
    }

    Column(modifier = modifier) {
        // Поле для поиска и выбора
        OutlinedTextField(
            value = if (selectedGroup != null) selectedGroup.groupName else searchText,
            onValueChange = {
                searchText = it
                if (!expanded) expanded = true
            },
            label = { Text("Выберите группу") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Показать список")
                }
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Поиск")
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Выпадающий список
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            // Если есть поисковый запрос и нет результатов
            if (searchText.isNotBlank() && filteredGroups.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Группа не найдена") },
                    onClick = { }
                )
            } else {
                // Показываем отфильтрованные группы
                filteredGroups.forEach { group ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(group.groupName, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "Курс: ${group.course}, ${group.specialtyName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            onGroupSelected(group)
                            searchText = ""
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}