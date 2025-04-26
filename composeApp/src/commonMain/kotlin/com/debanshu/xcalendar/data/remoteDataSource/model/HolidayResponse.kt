package com.debanshu.xcalendar.data.remoteDataSource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HolidayResponse(
    @SerialName("meta")
    val meta: Meta,
    @SerialName("response")
    val response: Response
)