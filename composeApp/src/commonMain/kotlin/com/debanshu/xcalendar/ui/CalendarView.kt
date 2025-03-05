package com.debanshu.xcalendar.ui

sealed class CalendarView {
    data object Schedule : CalendarView()
    data object Day : CalendarView()
    data object ThreeDay : CalendarView()
    data object Week : CalendarView()
    data object Month : CalendarView()
}