package com.debanshu.xcalendar.data.remoteDataSource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    @SerialName("code")
    val code: Int
)