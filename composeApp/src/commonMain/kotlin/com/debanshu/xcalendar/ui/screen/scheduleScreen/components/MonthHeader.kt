package com.debanshu.xcalendar.ui.screen.scheduleScreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.debanshu.xcalendar.common.toSentenceCase
import com.debanshu.xcalendar.ui.YearMonth
import com.debanshu.xcalendar.ui.theme.XCalendarTheme
import com.skydoves.landscapist.coil3.CoilImage
import kotlinx.datetime.Month

private fun getMonthImageUrl(month: Month): String {
    return when (month) {
        Month.JANUARY -> "https://weandthecolor.com/wp-content/uploads/2014/11/1-January-Artwork-by-Lotta-Nieminen-for-Google-Calendar-app.jpg"
        Month.FEBRUARY -> "https://weandthecolor.com/wp-content/uploads/2014/11/2-February-Artwork-from-a-series-of-illustrations-for-the-Google-Calendar-app.jpg"
        Month.MARCH -> "https://weandthecolor.com/wp-content/uploads/2014/11/3-March-early-spring-landscape.jpg"
        Month.APRIL -> "https://weandthecolor.com/wp-content/uploads/2014/11/4-April-outdoor-activities-in-spring.jpg"
        Month.MAY -> "https://weandthecolor.com/wp-content/uploads/2014/11/5-May-enjoying-the-weather.jpg"
        Month.JUNE -> "https://weandthecolor.com/wp-content/uploads/2014/11/6-June-the-beginning-of-summer-fun-and-swimming.jpg"
        Month.JULY -> "https://weandthecolor.com/wp-content/uploads/2014/11/7-July-Camping-and-traveling.jpg"
        Month.AUGUST -> "https://weandthecolor.com/wp-content/uploads/2014/11/8-August-Beautiful-summertime.jpg"
        Month.SEPTEMBER -> "https://weandthecolor.com/wp-content/uploads/2014/11/9-September-the-beginning-of-autumn.jpg"
        Month.OCTOBER -> "https://weandthecolor.com/wp-content/uploads/2014/11/10-October-harvest-time.jpg"
        Month.NOVEMBER -> "https://weandthecolor.com/wp-content/uploads/2014/11/11-November-cold-and-rainy.jpg"
        Month.DECEMBER -> "https://weandthecolor.com/wp-content/uploads/2014/11/12-December-the-beginning-of-winter.jpg"
        else -> "https://weandthecolor.com/wp-content/uploads/2014/11/15-Additional-illustration.jpg"
    }
}

@Composable
fun MonthHeader(yearMonth: YearMonth) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        CoilImage(
            imageModel = { getMonthImageUrl(yearMonth.month) },
            modifier = Modifier.fillMaxSize()
        )

        // Month and year overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 64.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = "${yearMonth.month.name.toSentenceCase()} ${yearMonth.year}",
                style = XCalendarTheme.typography.headlineSmall,
                color = XCalendarTheme.colorScheme.inverseOnSurface
            )
        }
    }
}