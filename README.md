# Real-Time Chess: Structured Monolithic Application
A full-featured Chess application featuring a robust Java HTTP backend server and a responsive HTML5/CSS3/JavaScript frontend client. The application supports multiple gameplay modes (including real-time online rooms, local multiplayer, and minimax computer AI), live room chats, and customizable visual themes.
---
## 🎮 Game Modes & Features
### 1. Online Multiplayer Rooms
- **Room-Based Matching**: Host a match to generate a unique 6-character room ID, or enter an existing room ID to join a game.
- **Turn & Move Synchronization**: Real-time board state and turn updates powered by backend polling.
- **Clocks & Timeouts**: Synchronized timers supporting bullet, blitz, and classical matches.
- **In-Game Chat**: Chat with your opponent in real-time within your multiplayer room.
- **Resignation Handling**: Clean resignation triggers that immediately end the game and update both players' UI overlays.
### 2. VS Computer (AI Opponent)
- **Minimax Engine**: Features an AI opponent driven by the Minimax search algorithm with Alpha-Beta pruning.
- **Progressive Difficulty**: 4 difficulty levels (Easy, Medium, Hard, Expert) with varying search depths and positional evaluation strategies.
### 3. Chess Training Mode
- **Structured Practice**: Interactive scenarios including chess puzzles, openings, endgame practices, and tactical scenarios.
- **Hints & Scoring**: Displays legal moves, checks, and tracks correct vs. wrong moves.
### 4. Local & Custom Settings
- **Local Game**: Play a standard match against a friend sharing the same screen.
- **Board Customization**: 8 built-in themes (Classic Brown, Dark Green, Blue, Green, Purple, Gray, Ocean, and Sunset) that can be adjusted independently per tab.
- **Sound Effects**: Audio notifications for moves, captures, checks, and checkmate.
---
## 🛠️ Architecture and Reorganization
### Why We Reorganized the Project
Originally, the project had a flat file structure where backend source files, frontend web code, compiled class files, and temporary scripts were mixed together in the root directory. To establish a clean, standard monolithic repository, we reorganized the layout:
1. **Separation of Concerns**: Java server files are isolated in the `backend/src` folder, and frontend web resources reside in the `frontend` folder.
2. **Preventing Class Pollution**: Compiled `.class` files are placed inside the backend source directory, keeping the workspace root clean and readable.
3. **Robust Serving Path Resolution**: The backend router utilizes a fallback search array to locate `index.html` (checking the current directory, `./frontend/`, and relative paths). This ensures the server starts correctly regardless of where the launch command is executed.
4. **Duplicate Player Name Collisions**: Resolved an issue where both players joining with the default name `"Player"` would clash. The server dynamically appends a suffix (e.g. `Player_2`) to keep player scopes unique.
5. **Stack Overflow Resolution**: Fixed a recursion bug between checkmate validation (`attacked()`) and king movements (`kingMoves()`) by introducing an recursion-limiting flag.
### Reorganized Directory Tree
```text
/CHESS
  ├── backend/
  │   └── src/
  │       └── ChessServe.java  # Java HTTP server, routing, and room state logic
  ├── frontend/
  │   └── index.html           # Chess board UI layout, styling, and client script
  ├── run.bat                  # One-click Windows compile-and-run helper script
  └── README.md                # Project documentation
```
---
## 🚀 How to Run the Project (Localhost)
### Requirements
- **Java Development Kit (JDK 8 or higher)**: Make sure `javac` and `java` are configured on your system's environmental `PATH`.
### 1. Compile & Start the Server
* **Option A: Using the Windows Startup Script (Recommended)**
  Double-click **`run.bat`** in the project root directory. It compiles the Java files and starts the server automatically.
  
* **Option B: Manual Terminal Execution**
  Open your terminal in the workspace root directory (`d:\CHESS`) and run the following commands:
  ```powershell
  # Compile the Java class
  javac backend/src/ChessServe.java
  # Start the HTTP server on port 9090
  java -cp backend/src ChessServe
  ```
Upon success, you will see the console message:
```text
Chess Server started successfully on port 9090
Server URL: http://localhost:9090
```
### 2. Open the Game in Your Browser
Open any modern web browser and navigate to:
👉 **[http://localhost:9090](http://localhost:9090)**
### 3. Testing Multiplayer Locally
1. Open a browser window at `http://localhost:9090`. Click **Create Game** under Game Setup. A unique 6-character room ID (e.g. `LQ2BUW`) will appear.
2. Open another browser window (or an incognito tab) at the same URL. Enter the room ID and click **Join Game**.
3. The board, chat, and clocks will automatically synchronize.
---
## 💻 Technical Details
### Backend (Java)
- **Core Server**: Implemented using standard `com.sun.net.httpserver.HttpServer`. No external frameworks are required.
- **Query Parameter Decoding**: Incorporates `java.net.URLDecoder` to parse query parameters (e.g. space characters in usernames or chats).
- **Concurrency**: Thread-safe collections (`ConcurrentHashMap` and synchronized lists) to safely manage active rooms and chat logs concurrently.
- **Memory Cleanup**: A background daemon thread periodically cleans up inactive rooms that exceed the idle threshold (2 hours).
### Frontend (HTML/CSS/JS)
- **Custom CSS Theme Grid**: Styles tables, hover states, selection outlines, and transition animations.
- **Web Audio Context API**: Dynamically synthesizes beep frequencies for chess sounds without requesting external audio files.
- **Polling Loop**: Periodically polls the server state every 1 second to update clock timers, fetch new moves, and append incoming chats.
