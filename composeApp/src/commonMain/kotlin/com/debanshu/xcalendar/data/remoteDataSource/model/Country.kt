package com.debanshu.xcalendar.data.remoteDataSource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String
)