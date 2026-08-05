# Real-Time Chess: Structured Monolithic Application

A full-featured, high-performance Chess application featuring a robust Java HTTP backend server and a responsive, modern HTML5/CSS3/JavaScript frontend client. The application supports multiple gameplay modes (including real-time online room multiplayer, local 2-player mode, AI computer opponent, and tactical practice modes), live room chats, customizable themes, game variants, and automated JDK auto-detection.

---

## 🎮 Game Modes & Features

### 1. Online Multiplayer Rooms
- **Room-Based Matching**: Host a match to generate a unique 6-character room ID, or enter an existing room ID to join a game.
- **Turn & Move Synchronization**: Real-time board state and turn updates powered by backend state polling.
- **Clocks & Timeouts**: Synchronized timers supporting bullet, blitz, classical, and untimed matches.
- **In-Game Chat**: Chat with your opponent in real-time within your multiplayer room.
- **Resignation Handling**: Clean resignation triggers that immediately conclude the game and notify both players.

### 2. VS Computer (AI Opponent)
- **Minimax Engine**: Features an AI opponent driven by the Minimax search algorithm with Alpha-Beta pruning.
- **Progressive Difficulty**: 4 difficulty levels (Easy, Medium, Hard, Expert) with varying search depths and positional evaluation strategies.

### 3. Chess Training & Practice Mode
- **Structured Practice**: Interactive scenarios including chess puzzles, openings, endgame practices, and tactical scenarios.
- **Hints & Scoring**: Displays legal moves, checks, and tracks correct vs. wrong moves in real-time.

### 4. Game Variants & Visual Options
- **Game Variants**: Supports Standard Chess, Chess960 (Fischer Random), Atomic Chess, King of the Hill, and Three Check.
- **Board Customization**: 8 built-in themes (Classic Brown, Dark Green, Blue, Green, Purple, Gray, Ocean, and Sunset).
- **Time Controls**: Flexible time limits including 1 min (Bullet), 3 min, 5 min (Default), 10 min, 15 min, 30 min (Classical), and Untimed.
- **Sound Effects & Legal Move Highlights**: Interactive audio feedback for moves, captures, checks, and checkmates, along with optional legal move highlights.

---

## 🛠️ Project Structure & Recent Improvements

### Project Layout
```text
/CHESS
  ├── backend/
  │   └── src/
  │       └── ChessServe.java  # Java HTTP server, REST endpoints, room state, & board logic
  ├── frontend/
  │   └── index.html           # Single-page HTML5/CSS3/JS UI layout, themes, & client logic
  ├── run.bat                  # Enhanced one-click Windows startup & JDK auto-detect script
  └── README.md                # Project documentation
```

### Key Changes & Fixes Made
1. **Backend Compilation & Method Overloading Fix**:
   - Fixed a method signature mismatch in [`backend/src/ChessServe.java`](file:///d:/CHESS/backend/src/ChessServe.java) by adding an overloaded `initializeBoard(String variant)` method. This allows board initialization with game variant parameters (e.g. Standard, Chess960, etc.) without breaking existing calls.
2. **Automatic JDK Detection in `run.bat`**:
   - Enhanced [`run.bat`](file:///d:/CHESS/run.bat) with automatic JDK discovery. If `javac` is not configured on the system `%PATH%`, the script automatically scans standard Windows installation locations (`C:\Program Files\Java\jdk*`, `C:\Program Files (x86)\Java\jdk*`, `%JAVA_HOME%`) and sets `%PATH%` dynamically.
3. **Comprehensive Backend REST Endpoints**:
   - Implemented and documented 14 backend HTTP handlers managing routing, room initialization, move validation, live state retrieval, health monitoring, and chat messaging.
4. **Duplicate Player Name Collisions**:
   - Prevents player name collisions by appending suffixes (e.g. `Player_2`) when both players join with identical names.
5. **Recursion & Stack Overflow Fixes**:
   - Guarded checkmate validation (`attacked()`) and king movement calculations (`kingMoves()`) with recursion depth flags to prevent stack overflows.

---

## 🚀 How to Run the Project (Localhost)

### Requirements
- **Java Development Kit (JDK 8 or higher)** installed on your machine.

### 1. Compile & Start the Server

* **Option A: Using the Windows Startup Script (Recommended)**
  Double-click **`run.bat`** in the project root directory. It automatically detects your JDK installation, compiles `backend/src/ChessServe.java`, and launches the HTTP server.

* **Option B: Manual Terminal Execution**
  Open PowerShell or Command Prompt in `d:\CHESS` and run:
  ```powershell
  # Compile the Java class
  javac backend/src/ChessServe.java

  # Start the HTTP server on port 9090
  java -cp backend/src ChessServe
  ```

Upon success, you will see the console output:
```text
=================================================================
                   Chess Server Startup Script
=================================================================

Searching for Java JDK installation...
Compiling Java Chess Backend...
[SUCCESS] Compiled successfully.

Starting Chess Server on http://localhost:9090 ...
Press Ctrl+C to stop the server.

Chess Server started successfully on port 9090
Server URL: http://localhost:9090
```

### 2. Open the Game in Your Browser
Navigate to:
👉 **[http://localhost:9090](http://localhost:9090)**

### 3. Testing Multiplayer Locally
1. Open `http://localhost:9090` in your browser. Click **Create Game** under Game Setup. A unique 6-character room ID (e.g. `LQ2BUW`) will be generated.
2. Open a second browser tab or incognito window at `http://localhost:9090`. Enter the room ID and click **Join Game**.
3. Board moves, clocks, and chat will synchronize in real-time between both windows.

---

## 💻 Technical Details

### Backend (Java)
- **Core HTTP Server**: Implemented using standard `com.sun.net.httpserver.HttpServer` with zero external dependencies.
- **Multithreading**: Uses a fixed thread pool of 50 worker threads for concurrent HTTP request processing.
- **State Management & Concurrency**: Uses `ConcurrentHashMap` and thread-safe lists for room storage, access timestamps, and chat history.
- **Automated Memory Cleanup**: Background daemon thread (`Auto-Cleanup-Thread`) runs every 5 minutes to purge inactive games exceeding the 2-hour idle threshold.
- **Available Backend Endpoints**:
  - `GET /` or `/index.html` - Serves the frontend single-page application.
  - `GET /health` - Server health check (uptime, memory, active games).
  - `GET /ping` - Server connectivity test.
  - `GET /create` - Creates a new game room with custom options.
  - `GET /join` - Joins an existing game room.
  - `GET /listGames` - Returns list of active game IDs.
  - `GET /sendMove` - Validates and executes a move.
  - `GET /getMoves` - Gets valid legal moves for a piece.
  - `GET /validateMove` - Validates move legality.
  - `GET /getState` - Polling endpoint for live board state, clocks, and turn.
  - `GET /sendChat` - Appends a message to room chat.
  - `GET /resign` - Processes player resignation.
  - `GET /resetGame` - Resets game state.
  - `GET /cleanup` - Triggers manual cleanup of inactive rooms.

### Frontend (HTML5 / CSS3 / Vanilla JS)
- **Glassmorphism Design System**: Modern dark UI featuring smooth gradients, cards, and tabbed controls.
- **Web Audio Synthesis**: Dynamic sound generation using `AudioContext` for move/capture/check audio without external file dependencies.
- **Polling Synchronization Loop**: Client polls state every 1 second to update board positions, timers, turn indicators, and room chat.
