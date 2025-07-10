# 🧗 PlauenBloc

A mobile app for the **Boulderhalle Plauen** – built with love, Kotlin, and Jetpack Compose.  
This app is part of a real-world collaboration and also serves as a final project for an app development course.

---

## 🎯 Purpose

The app supports both customers and operators of the climbing gym by providing a digital and interactive experience:

- Users (customers) can discover, rate, and track boulder problems.
- Operators can create and manage routes and share detailed info.
- The app aims to bring community, structure, and fun into one interface.

---

## 🛠️ Tech Stack

- **Kotlin Multiplatform (KMP)**
- **Jetpack Compose** (Android)
- **Firebase Authentication** (User & Operator distinction)
- **Firebase Firestore** (for Boulder data and user input)
- **Clean Architecture** approach (UI, ViewModel, Repository layers)
- **StateFlow**, **Coroutines**, **Compose Destinations** for navigation

---

## 🔑 Key Features (MVP)

### For Users:
- 🔐 Login with Firebase
- 🗺️ Interactive 2D map of the boulder gym
- 📍 List view of all routes (in addition to map view)
- 📍 Tap on markers to see Boulder details
- ⭐ Rate how fun/beautiful a boulder is
- 📝 Leave comments or attempt notes (e.g. “Sent on 3rd try!”)

### For Operators:
- ➕ Create new Boulder routes
- 🛠️ Add/edit route info (grade, style, setter, color, etc.)
- ✏️ Update or delete existing problems

---

## 🧩 Planned Features

- 👥 Community: share routes & beta with friends
- 📁 Collections: users can organize boulders (e.g. warm-up, projects)
- 📊 Track progress over time
- 🔔 Notifications for new routes or events

---

## 🧪 Project Status

> In development – core MVP features are actively being implemented. Firebase Auth, role-based UI, and first screens are in place.

### ✅ Completed
- [x] Defined core **User Personas** (climber & operator)
- [x] Created detailed **User Stories**
- [x] Outlined initial app structure and feature scope
- [x] Integrated **Firebase Authentication**
- [x] Set up **user role distinction** (User vs Operator)
- [x] Connected to **Firebase Firestore**
- [x] Created base **UI layout & navigation**
- [x] Implemented **AuthScreen** with animated UI

### 🧩 In Progress / Planned
- [ ] Creating UI prototypes in **Figma**
- [ ] Firebase Authentication setup (including user role distinction)
- [ ] UI layout & navigation flow with Jetpack Compose
- [ ] Interactive map with markers for boulders
- [ ] Boulder Detail View (bottom sheet or screen)
- [ ] User rating & comment functionality
- [ ] Boulder creation/editing for operators
      
---

## 🤝 Contributing

This is a real project with a real use case. If you're interested in bouldering, mobile development, or community-driven apps – feel free to reach out or fork the repo!

---

## 📸 Screenshots

*(To be added when UI is more complete)*

---

## 📍 About

Built as part of an app development training & a collaboration with the [Boulderhalle Plauen](http://boulderhalle-plauen.de/) team. 
We believe in movement, community, and clean code. 💪

---

> Made in Plauen, with chalky fingers and Kotlin ❤️
