package com.example.collegeschedule.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// 1. Функция для определения типа занятия по названию - ИСПРАВЛЕНА
fun getLessonIcon(subjectName: String?): ImageVector { // <-- Добавлено "?" после String
    val subject = subjectName?.lowercase() ?: "" // <-- Безопасный вызов
    return when {
        subject.contains("лекция") -> Icons.Default.Book // <-- Исправлено: Default вместо Filled
        subject.contains("практическая") || subject.contains("практич") -> Icons.Default.Edit
        subject.contains("лабораторная") || subject.contains("лаб.") -> Icons.Default.Science
        subject.contains("программирование") || subject.contains("прогр") -> Icons.Default.Code
        subject.contains("математика") || subject.contains("математ") -> Icons.Default.Calculate
        else -> Icons.Default.School
    }
}

// 2. Функция для цвета корпуса - ИСПРАВЛЕНА
fun getBuildingColor(buildingName: String?): Color { // <-- Добавлено "?" после String
    val firstChar = buildingName?.firstOrNull() // <-- Безопасный вызов
    return when (firstChar) {
        'A', 'а' -> Color(0xFF4CAF50)  // зеленый
        'B', 'б' -> Color(0xFF2196F3)  // синий
        'C', 'с' -> Color(0xFFFF9800)  // оранжевый
        else -> Color(0xFF5B5353)      // серый
    }
}