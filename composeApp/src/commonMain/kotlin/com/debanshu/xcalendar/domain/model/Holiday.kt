package com.debanshu.xcalendar.domain.model

data class Holiday(
    val id: String,
    val name: String,
    val date: Long,
    val countryCode: String
)