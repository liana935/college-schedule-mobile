package com.example.collegeschedule.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.GroupDto
import com.example.collegeschedule.data.local.FavoritesManager
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun FavoritesScreen(
    allGroups: List<GroupDto>,
    onGroupClick: (GroupDto) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteGroups by FavoritesManager.favoriteGroups.collectAsState(emptySet())
    val coroutineScope = rememberCoroutineScope()

    // Находим группы, которые есть в избранном
    val favoriteGroupObjects = allGroups.filter { it.groupName in favoriteGroups }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (favoriteGroupObjects.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.StarOutline,
                        contentDescription = "Нет избранных",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Нет избранных групп",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            // Список избранных групп
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteGroupObjects) { group ->
                    // Карточка группы
                    Card(
                        onClick = {
                            // При клике вызываем колбэк
                            onGroupClick(group)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = group.groupName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Курс: ${group.course}, ${group.specialtyName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}