package com.debanshu.xcalendar.ui

sealed class CalendarView {
    data object Schedule : CalendarView()
    data object Day : CalendarView()
    data object ThreeDay : CalendarView()
    data object Week : CalendarView()
    data object Month : CalendarView()
}

sealed class TopBarCalendarView {
    data object NoView : TopBarCalendarView()
    data object Week : TopBarCalendarView()
    data object Month : TopBarCalendarView()
}