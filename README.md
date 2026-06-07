# 🎵 Televised
### by Bentley Wagonjack

A clean, minimal MP3 player for Android built from the ground up.

---

## Features

- 🎵 Scans your device for all MP3 and music files automatically
- ▶️ Play, Pause, Next, and Previous controls
- ⏩ Seekbar with elapsed and total duration display
- 📋 Scrollable song list sorted A–Z
- 🔁 Auto-advances to the next track on completion
- 🌙 Dark theme with red accent

---

## Screenshots

> Coming soon

---

## Installation

1. Download `Televised.apk`
2. On your Android device go to **Settings → Install unknown apps** and enable it
3. Open the APK file and tap **Install**

---

## Build From Source

### Prerequisites

- Ubuntu (or Ubuntu chroot on Android via Termux)
- Java JDK 17
- Android SDK with build tools 34

### Steps

```bash
# Clone or download the project
cd ~/Televised

# Build the APK
./gradlew assembleDebug

# Output will be at:
app/build/outputs/apk/debug/app-debug.apk
```

---

## Project Structure

```
Televised/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/bentleywagonjack/televised/
│           │   └── MainActivity.java
│           ├── res/
│           │   ├── layout/activity_main.xml
│           │   └── values/
│           │       ├── strings.xml
│           │       └── styles.xml
│           └── AndroidManifest.xml
├── build.gradle
├── settings.gradle
└── gradlew
```

---

## Permissions

| Permission | Reason |
|---|---|
| `READ_MEDIA_AUDIO` | Read music files on Android 13+ |
| `READ_EXTERNAL_STORAGE` | Read music files on Android 12 and below |

---

## Tech Stack

- **Language:** Java
- **Min SDK:** Android 8.0 (API 26)
- **Target SDK:** Android 14 (API 34)
- **Build Tool:** Gradle 8.4

---

## License

MIT License — free to use, modify and distribute.

---

*Televised by Bentley Wagonjack*
