# ♟️ Chess Game - Reorganized Monolithic Web Application

A full-featured chess game built from scratch with an HTML/CSS/JS frontend and a lightweight Java backend. Features multiple game modes, customizable options, advanced chess logic, and a fully functional real-time multiplayer room system on `localhost`.

---

## ✨ Features

### 🎮 Game Modes
- **Online Multiplayer (Real Backend)** – Host or join rooms using a 6-character room ID to play against others over your local network/localhost.
- **Local Multiplayer** – Play against a friend on the same device.
- **VS Computer** – Four difficulty levels (Easy to Expert) with AI opponents powered by the Minimax algorithm.
- **Training Mode** – Practice standard opening strategies, puzzles, and tactics with hints and scoring.

### 🚀 Core Features
- **Complete Chess Rules** – All 6 piece types with accurate movement patterns, checkmate, and check validation.
- **Time Controls** – Customizable from 1 minute (Bullet) to 30 minutes (Classical) or untimed.
- **Game Variants** – Standard Chess, Chess960 (Fischer Random), Atomic, King of the Hill, and Three Check.
- **Real-Time Multiplayer Chat** – Chat with your opponent in real-time within your multiplayer room.
- **Visual Feedback** – Highlighted moves, check indicators, move history, and captured pieces.
- **Sound System** – Audio feedback (Web Audio API) for moves, captures, checks, and checkmate.
- **Responsive Design** – Works across different screen sizes.

---

## 🎨 Customization & Themes
- **8 Board Themes** – Classic Brown, Dark (Green), Blue, Green, Purple, Gray, Ocean, and Sunset.
- **Independent Tab Themes** – Theme selections are kept separate per player/active tab.
- **Configurable Options** – Toggle sounds, move highlighting, and more.
- **Training Statistics** – Track performance with scores and accuracy metrics.

---

## 🛠️ Project Architecture Reorganization

### Reason for Reorganization
Previously, the project files (`ChessServe.java`, `index.html`, compiled class files, and duplicates) were flatly clustered in the root directory. To transform the codebase into a clean, professional, and manageable structure, we moved to a **Structured Monolith**:
1. **Separation of Concerns**: Moved Java source files to `backend/src` and web resources to `frontend` so that the client code and server code do not overlap.
2. **Directory Cleanliness**: Kept compiled class files and execution outputs inside their respective folders instead of cluttering the root workspace.
3. **Robust Lookup System**: Implemented a fallback file lookup in the Java backend. The server automatically locates `index.html` at the root, inside `frontend/`, or in parent directories, making compilation and execution robust to different working directories.

### Reorganized Monolithic Layout
```text
chess-game/
├── backend/
│   └── src/
│       └── ChessServe.java    # Java HTTP backend API & server
├── frontend/
│   └── index.html             # Unified HTML/CSS/JS frontend application
├── run.bat                    # One-click startup script (compiles & runs)
└── README.md                  # This documentation
```

---

## 🚀 How to Run the Project (Localhost)

### Requirements
- **Java Development Kit (JDK 8 or higher)** – Ensure that `javac` and `java` are installed and available on your system path.

### 1. Compile & Start the Server
* **Option A: Automatic Startup Script (Recommended for Windows)**
  Simply double-click the **`run.bat`** file in the root directory. It compiles the Java code and starts the server in one go.
  
* **Option B: Manual Command Line**
  Open your terminal inside the root directory (`d:\CHESS`) and run:
  ```powershell
  # Compile the Java backend source
  javac backend/src/ChessServe.java

  # Run the server from the compiled classpath
  java -cp backend/src ChessServe
  ```

Once started successfully, the terminal will print:
```text
Chess Server started successfully on port 9090
Server URL: http://localhost:9090
```

### 2. Open the Game in Your Browser
Open your browser and navigate to:
👉 **[http://localhost:9090](http://localhost:9090)**

### 3. How to Test Multiplayer Locally
1. In Browser Tab 1, go to `http://localhost:9090`. Click **Create Game**. Copy the generated 6-letter **Game ID** (e.g. `LQ2BUW`).
2. In Browser Tab 2 (or an incognito window), go to `http://localhost:9090`. Input the Game ID in the input box, and click **Join Game**.
3. Play moves, chat, change themes, or resign!

---

## 🎯 Key Bug Fixes & Achievements
- **Duplicate Usernames Collision**: Fixed a critical logic bug where players both using the default name `"Player"` would collide. The server now appends a suffix (e.g. `_2`) to the second player, which resolves turn-blocking and move mismatches.
- **King Moves Infinite Recursion (Stack Overflow)**: Fixed a runtime crash where checking if a square is threatened by a king would cause `kingMoves` and `attacked` to invoke each other infinitely. Added an `attackOnly` flag to bypass recursion.
- **Moves Delimiter Parsing**: Fixed turn synchronization where comma-separated values inside moves lists were parsed incorrectly by switching the backend delimiter to a semicolon `;` and updating the JavaScript parser.
- **Online Resignation & Real-time Chat**: Added `/resign` and `/sendChat` APIs with parameters url-decoding so that chats and forfeits synchronize instantly.

---

## 📊 Features Comparison

| Feature | Status | Notes |
| :--- | :--- | :--- |
| **Complete Chess Rules** | ✅ Fully Implemented | All piece movements, check/checkmate |
| **Computer AI** | ✅ 4 Difficulty Levels | From random moves to minimax algorithm |
| **Time Controls** | ✅ Customizable | 1-30 minutes or unlimited |
| **Game Variants** | ✅ 5 Variants | Including Chess960 |
| **Board Customization** | ✅ 8 Themes | Swappable themes per active tab |
| **Training Mode** | ✅ With Scoring | Puzzles and statistics |
| **Sound System** | ✅ Web Audio API | Configurable sound effects |
| **Chat System** | ✅ Fully Syncing | Sourced from backend database logs |
| **Online Multiplayer** | ✅ Fully Implemented | Real Java backend room integration |

---

## 🎓 Learning Outcomes
This project demonstrates proficiency in:
- **Full-Stack Development** – Clean monolithic integration between a Java HTTP backend and a Vanilla JS frontend.
- **Algorithm Design** – Recursive search (Minimax with alpha-beta pruning) and checkmate detection.
- **Concurrent Programming** – Thread-safe, multi-user game synchronization.
- **Problem Solving** – Real-time event polling and stack overflow diagnostics.
