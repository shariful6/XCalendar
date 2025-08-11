# XCalendar 📅

A modern, cross-platform calendar application built with **Kotlin Multiplatform** and **Compose Multiplatform**, featuring multiple calendar views, event management, and holiday integration.

## 🎯 What is XCalendar?

XCalendar is a feature-rich calendar application that provides multiple viewing modes (day, week, month, 3-day, and schedule views) with seamless event management capabilities. Built using modern Android development practices, it offers a native experience across both Android and iOS platforms.

## 📱 Screenshots & Demo

### **App Overview**
| Feature | Android | iOS |
|---------|---------|-----|
| **App Demo** | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/android.gif" width="200" height="400" alt="Android Demo"> | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/ios.gif" width="200" height="400" alt="iOS Demo"> |

### **Calendar Views**
| View | Android | iOS |
|------|---------|-----|
| **Month View** | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/android_month.png" width="200" height="400" alt="Android Month"> | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/ios_month.png" width="200" height="400" alt="iOS Month"> |
| **Week View** | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/android_week.png" width="200" height="400" alt="Android Week"> | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/ios_week.png" width="200" height="400" alt="iOS Week"> |
| **Day View** | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/android_day.png" width="200" height="400" alt="Android Day"> | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/ios_day.png" width="200" height="400" alt="iOS Day"> |
| **3-Day View** | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/android_three_day.png" width="200" height="400" alt="Android 3-Day"> | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/ios_three_day.png" width="200" height="400" alt="iOS 3-Day"> |
| **Schedule View** | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/android_schedule.png" width="200" height="400" alt="Android Schedule"> | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/ios_schedule.png" width="200" height="400" alt="iOS Schedule"> |

### **Event Management**
| Feature | Android | iOS |
|---------|---------|-----|
| **Add Event** | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/android_add_event.png" width="200" height="400" alt="Android Add Event"> | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/ios_add_event.png" width="200" height="400" alt="iOS Add Event"> |
| **View Event** | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/android_view_event.png" width="200" height="400" alt="Android View Event"> | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/ios_view_event.png" width="200" height="400" alt="iOS View Event"> |

### **Navigation & UI**
| Component | Android | iOS |
|-----------|---------|-----|
| **App Bar** | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/android_app_bar.png" width="200" height="400" alt="Android App Bar"> | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/ios_app_bar.png" width="200" height="400" alt="iOS App Bar"> |
| **Sidebar** | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/android_sidebar.png" width="200" height="400" alt="Android Sidebar"> | <img src="https://github.com/Debanshu777/XCalendar/raw/main/assets/screenshots/ios_sidebar.png" width="200" height="400" alt="iOS Sidebar"> |

## ✨ Key Features

- **Multiple Calendar Views**
  - 📅 Month View - Traditional monthly calendar layout
  - 📆 Week View - 7-day weekly schedule
  - 📋 Day View - Detailed single-day view
  - 📊 3-Day View - Compact 3-day overview
  - 📝 Schedule View - List-based event schedule

- **Event Management**
  - ➕ Create, edit, and delete events
  - 🎨 Multiple calendar support (Personal, Work, Family, Birthdays, Gym)
  - ⏰ Event reminders and notifications
  - 🔄 Recurring event support
  - 📍 Location-based events
  - 🌍 All-day event support

- **Calendar Integration**
  - 👥 Multi-user support
  - 🎯 Primary and secondary calendar management
  - 👁️ Calendar visibility controls
  - 🎨 Color-coded calendar categories

- **Holiday Support**
  - 🏛️ Built-in holiday data (currently supports India)
  - 📅 Holiday display across all views

- **Modern UI/UX**
  - 🎨 Material Design 3 implementation
  - 📱 Responsive design for different screen sizes
  - 🔄 Smooth animations and transitions
  - 📱 Native platform integration

## 🏗️ Architecture & Technical Stack

### **Technology Stack**
- **Frontend**: Compose Multiplatform (Jetpack Compose)
- **Backend**: Kotlin Multiplatform
- **Database**: Room Database with SQLite
- **Dependency Injection**: Koin
- **Networking**: Ktor Client
- **State Management**: Kotlin Flow + StateFlow
- **Navigation**: Compose Navigation
- **Build System**: Gradle with Kotlin DSL

### **Project Structure**
```
composeApp/src/commonMain/kotlin/com/debanshu/xcalendar/
├── 📱 ui/                          # UI components and screens
│   ├── components/                 # Reusable UI components
│   ├── screen/                     # Screen implementations
│   │   ├── dayScreen/             # Single day view
│   │   ├── weekScreen/            # Weekly view
│   │   ├── monthScreen/           # Monthly view
│   │   ├── threeDayScreen/        # 3-day view
│   │   └── scheduleScreen/        # Schedule/list view
│   ├── theme/                      # App theming and colors
│   └── CalendarViewModel.kt        # Main view model
├── 🏗️ domain/                      # Business logic layer
│   ├── model/                      # Data models
│   ├── repository/                 # Repository interfaces
│   └── states/                     # State management
├── 💾 data/                        # Data layer
│   ├── localDataSource/            # Local database (Room)
│   └── remoteDataSource/           # Remote API integration
└── 🔧 di/                          # Dependency injection (Koin)
```

### **Data Models**
- **Event**: Calendar events with title, description, location, time, reminders
- **Calendar**: User calendars with visibility and primary settings
- **User**: User account management
- **Holiday**: Holiday information and display

### **Data Sources**
- **Static Data**: Events and calendars served from `assets/` directory
- **Dynamic Data**: Holiday information fetched from external API (when API key is configured)
- **Local Storage**: Room database for offline data persistence
- **API Integration**: Ktor client for remote data fetching

### **Key Components**
- **SwipeableCalendarView**: Interactive calendar with swipe gestures
- **BaseCalendarScreen**: Common calendar screen functionality
- **Event Management**: Add, edit, and delete event dialogs
- **Navigation Drawer**: Calendar and account management
- **Top App Bar**: Date navigation and view switching

## 🚀 Getting Started

### **Prerequisites**
- Android Studio Hedgehog or later
- Kotlin 1.9.0+
- JDK 21
- iOS development tools (for iOS builds)

### **Installation**

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/XCalendar.git
   cd XCalendar
   ```

2. **Configure API Key (Optional)**
   - Open `local.properties` file
   - Add your API key: `API_KEY=your_api_key_here`
   - Visit `https://calendarific.com` for API
   - This enables live holiday data fetching
   - Without API key, app falls back to static data

3. **Open in Android Studio**
   - Open the project in Android Studio
   - Sync Gradle files
   - Wait for dependencies to download

3. **Run on Android**
   - Connect an Android device or start an emulator
   - Click the "Run" button or use `./gradlew :composeApp:assembleDebug`

4. **Run on iOS** (requires macOS)
   - Open `iosApp/iosApp.xcodeproj` in Xcode
   - Select your target device/simulator
   - Build and run the project

**Note**: API key configuration is only available on Android builds. iOS builds will use static data.

### **Build Commands**

```bash
# Build for Android
./gradlew :composeApp:assembleDebug

# Build for iOS
./gradlew :composeApp:linkDebugFrameworkIosArm64

# Clean build
./gradlew clean
```

## 📱 App Flow & Navigation

### **Main Navigation Structure**
```
App Entry Point
├── CalendarApp (Main Container)
│   ├── Navigation Drawer
│   │   ├── User Accounts
│   │   ├── Calendar List
│   │   └── Settings
│   ├── Top App Bar
│   │   ├── Date Navigation
│   │   ├── View Selection
│   │   └── Action Buttons
│   └── Content Area
│       ├── Month Screen
│       ├── Week Screen
│       ├── Day Screen
│       ├── 3-Day Screen
│       └── Schedule Screen
```

### **Screen Interactions**
- **Month View**: Tap dates to navigate, swipe to change months
- **Week/Day Views**: Tap events for details, long press for options
- **Schedule View**: Scrollable list with month headers and event grouping
- **Event Management**: Floating action button opens add event dialog
- **Calendar Switching**: Drawer allows toggling calendar visibility

### **State Management**
- **DateStateHolder**: Manages current date, selected date, and view month
- **CalendarViewModel**: Handles UI state, events, calendars, and user data
- **Repository Pattern**: Clean separation between data sources and business logic

## 🔧 Configuration & Customization

### **Calendar Categories**
The app comes with pre-configured calendar categories:
- **Personal**: Personal events and appointments
- **Work**: Professional meetings and deadlines
- **Family**: Family-related events
- **Birthdays**: Birthday reminders and celebrations
- **Gym**: Fitness and workout schedules

### **Holiday Integration**
- Currently supports Indian holidays
- **Live API Integration**: Holiday data fetched from external API when `API_KEY` is configured in `local.properties`
- **Fallback Data**: Static holiday data stored in `assets/` directory for offline use
- **API Configuration**: Add `API_KEY=your_key_here` to `local.properties` to enable live data

### **Event Features**
- **Reminders**: Multiple reminder options (15min, 30min, 1hr, 1day, 1week)
- **Recurring Events**: Support for daily, weekly, monthly, and yearly patterns
- **Location**: Optional location information for events
- **All-day Events**: Mark events that span entire days

## 🧪 Testing

The project includes comprehensive testing infrastructure:
- **Test Tags**: UI components tagged for automated testing
- **State Testing**: ViewModel and state management testing
- **Component Testing**: Individual UI component testing

## 📊 Performance Features

- **Lazy Loading**: Efficient event loading with pagination
- **State Optimization**: Stable callbacks and memoization
- **Flow Management**: Optimized data streams with proper lifecycle management
- **Memory Management**: Efficient database queries and caching

## 🔮 Future Enhancements

- [ ] Calendar sync with Google Calendar, Outlook
- [ ] Push notifications for reminders
- [X] Dark/Light theme switching
- [ ] Custom calendar colors
- [ ] Event sharing capabilities
- [ ] Multi-language support
- [ ] Widget support
- [ ] Real-time calendar sync
- [ ] Multiple holiday region support

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### **Development Guidelines**
- Follow Kotlin coding conventions
- Use Compose best practices
- Maintain clean architecture principles
- Add appropriate test coverage

### **API Integration Notes**
- **Static Data**: Events and calendars are currently served from `assets/` directory
- **API Layer**: Remote data sources can be modified to integrate with actual calendar APIs
- **Holiday API**: Requires API key in `local.properties` for live holiday data
- **Fallback Strategy**: App gracefully falls back to static data when API is unavailable

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built with [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- UI powered by [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- Database management with [Room](https://developer.android.com/training/data-storage/room)
- Dependency injection using [Koin](https://insert-koin.io/)

---

**XCalendar** - Modern calendar experience across platforms 📱💻