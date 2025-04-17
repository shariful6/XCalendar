package com.debanshu.xcalendar

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.debanshu.xcalendar.ui.theme.XCalendarTheme

class MainActivity : ComponentActivity() {
    private var isDarkMode = false
    private val transparentBarStyle = SystemBarStyle.auto(
        lightScrim = Color.TRANSPARENT,
        darkScrim = Color.TRANSPARENT,
        detectDarkMode = { isDarkMode },
    )

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isDarkMode != newConfig.isNightModeActive) {
            isDarkMode = newConfig.isNightModeActive
            enableEdgeToEdge(transparentBarStyle, transparentBarStyle)
        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkMode = resources.configuration.isNightModeActive
        enableEdgeToEdge(transparentBarStyle, transparentBarStyle)
        setContent {
            App()
        }
    }
}
