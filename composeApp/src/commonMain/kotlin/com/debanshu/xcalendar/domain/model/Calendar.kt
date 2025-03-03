package com.debanshu.xcalendar.domain.model

data class Calendar(
    val id: String,
    val name: String,
    val color: Int,
    val userId: String,
    val isVisible: Boolean = true,
    val isPrimary: Boolean = false
)