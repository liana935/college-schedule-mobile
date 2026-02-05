package com.example.collegeschedule.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.*

// Функция для цвета корпуса
fun getBuildingColor(building: String): Color = when (building) {
    "Главный корпус" -> Color(0xFF4CAF50)
    "Учебный корпус №2" -> Color(0xFF2196F3)
    "Лабораторный корпус" -> Color(0xFF9C27B0)
    "Спортивный корпус" -> Color(0xFFF44336)
    "Библиотечный центр" -> Color(0xFFFF9800)
    "Институт ИТ" -> Color(0xFF009688)
    "Физический корпус" -> Color(0xFF673AB7)
    "Корпус инженерии" -> Color(0xFF3F51B5)
    "Корпус Экономики" -> Color(0xFFE91E63)
    "Корпус Психологии" -> Color(0xFF795548)
    else -> Color(0xFF757575)
}

// Функция для иконки предмета (исправленная версия)
fun getSubjectIcon(subject: String): ImageVector = when {
    subject.contains("английский", ignoreCase = true) -> Icons.Default.Language
    subject.contains("программирование", ignoreCase = true) -> Icons.Default.Code

    // Используем отдельные проверки вместо Regex
    subject.contains("информатика", ignoreCase = true) ||
            subject.contains("базы данных", ignoreCase = true) ||
            subject.contains("алгоритмы", ignoreCase = true) ||
            subject.contains("веб", ignoreCase = true) ||
            subject.contains("мобильная", ignoreCase = true) -> Icons.Default.Computer

    subject.contains("математика", ignoreCase = true) ||
            subject.contains("статистика", ignoreCase = true) -> Icons.Default.Calculate

    subject.contains("физика", ignoreCase = true) ||
            subject.contains("химия", ignoreCase = true) ||
            subject.contains("биология", ignoreCase = true) -> Icons.Default.Science

    subject.contains("история", ignoreCase = true) ||
            subject.contains("литература", ignoreCase = true) -> Icons.Default.HistoryEdu

    subject.contains("физическая культура", ignoreCase = true) -> Icons.Default.DirectionsRun
    subject.contains("экономика", ignoreCase = true) -> Icons.Default.AttachMoney

    subject.contains("психология", ignoreCase = true) ||
            subject.contains("социология", ignoreCase = true) -> Icons.Default.Psychology

    subject.contains("география", ignoreCase = true) -> Icons.Default.Public

    subject.contains("правоведение", ignoreCase = true) ||
            subject.contains("этика", ignoreCase = true) ||
            subject.contains("философия", ignoreCase = true) -> Icons.Default.Gavel

    subject.contains("дизайн", ignoreCase = true) -> Icons.Default.Brush

    subject.contains("электротехника", ignoreCase = true) ||
            subject.contains("робототехника", ignoreCase = true) -> Icons.Default.Build

    else -> Icons.Default.School
}

@Composable
fun LessonCard(lesson: LessonDto) {
    // Определяем цвет корпуса
    val buildingColor = lesson.groupParts.values
        .firstNotNullOfOrNull { it?.building }
        ?.let(::getBuildingColor)
        ?: Color(0xFF757575)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.dp, buildingColor)
    ) {
        Column(Modifier.padding(12.dp)) {
            // Заголовок
            LessonHeader(lesson, buildingColor)

            Spacer(Modifier.height(8.dp))

            // Содержимое
            LessonContent(lesson.groupParts, buildingColor)
        }
    }
}

@Composable
private fun LessonHeader(lesson: LessonDto, color: Color) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "${lesson.lessonNumber} пара",
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = lesson.time,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

@Composable
private fun LessonContent(groupParts: Map<LessonGroupPart, LessonPartDto?>, cardColor: Color) {
    when {
        // Занятие для всей группы
        groupParts[LessonGroupPart.FULL] != null -> {
            LessonInfoView(groupParts[LessonGroupPart.FULL]!!)
        }

        // Занятие с подгруппами
        groupParts[LessonGroupPart.SUB1] != null || groupParts[LessonGroupPart.SUB2] != null -> {
            Column {
                // Подгруппа 1
                groupParts[LessonGroupPart.SUB1]?.let { lessonPart ->
                    SubgroupCard(LessonGroupPart.SUB1, lessonPart, cardColor)

                    // Добавляем отступ только если есть вторая подгруппа
                    if (groupParts[LessonGroupPart.SUB2] != null) {
                        Spacer(Modifier.height(8.dp))
                    }
                }

                // Подгруппа 2
                groupParts[LessonGroupPart.SUB2]?.let { lessonPart ->
                    SubgroupCard(LessonGroupPart.SUB2, lessonPart, cardColor)
                }
            }
        }

        else -> {
            Text(
                text = "Нет занятий",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun SubgroupCard(type: LessonGroupPart, lessonPart: LessonPartDto, cardColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor.copy(alpha = 0.1f)
        )
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(
                text = when (type) {
                    LessonGroupPart.SUB1 -> "Подгруппа 1"
                    LessonGroupPart.SUB2 -> "Подгруппа 2"
                    else -> ""
                },
                style = MaterialTheme.typography.labelSmall,
                color = cardColor
            )
            LessonInfoView(lessonPart)
        }
    }
}

@Composable
fun LessonInfoView(lessonPart: LessonPartDto) {
    val buildingColor = getBuildingColor(lessonPart.building)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Иконка предмета
        Icon(
            imageVector = getSubjectIcon(lessonPart.subject),
            contentDescription = "Тип занятия",
            tint = buildingColor,
            modifier = Modifier.size(24.dp)
        )

        // Информация о занятии
        Column {
            Text(
                text = lessonPart.subject,
                style = MaterialTheme.typography.titleSmall,
                color = buildingColor
            )

            Text(
                text = "${lessonPart.teacher} (${lessonPart.teacherPosition})",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Аудитория",
                    tint = buildingColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${lessonPart.building}, ауд. ${lessonPart.classroom}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = buildingColor
                )
            }

            Text(
                text = lessonPart.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}