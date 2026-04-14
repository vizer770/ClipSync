# 📱 ClipSync - Cross-Device Clipboard
A full-stack synchronization tool built 
ClipSync allows you to instantly share clipboard text between an Android device and a Web dashboard using Firebase cloud technology.

## 🚀 Features
- **Real-time Sync:** Uses Firebase Firestore Snapshots for sub-second text transfer.
- **Multi-User Isolation:** Each user has their own secure data "room" based on their UID.
- **Advanced UI:** Material Design 3 with "Card" windows for viewing and sending text.
- **Background Support:** Foreground service to keep the connection alive.

## 🛠 Tech Stack
- **Android:** Kotlin, Firebase Auth, Firestore, ClipboardManager API.
- **Web:** HTML5, CSS3, JavaScript (Vanilla), Firebase Web SDK.
- **Hosting:** Netlify (Web) & GitHub (Source Control).

## 📂 Project Structure
- `/Android-App`: Source code for the Kotlin mobile application.
- `/Web-App`: Dashboard for laptops/desktops (`index.html` & `script.js`).
- `/Releases`: Contains the installable `.apk` file.

## 📲 How to Install
1. **Android:** Download the `app-debug.apk` from the **Releases** section.
2. **Web:** Access the live dashboard here: https://clipsyncpc.netlify.app/ 
3. **Login:** 
   1. open app
   2. sign up ➔ use any email + password
   3. on desktop ➔ login with same email + password
   4. syncing ➔ data starts moving between devices automatically




