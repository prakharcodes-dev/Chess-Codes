# Real-Time Chess & Chess Master Knowledge System

A full-featured, high-performance Chess application featuring a robust Java HTTP backend server and a responsive, modern HTML5/CSS3/JavaScript frontend client. The application supports multiple gameplay modes (including real-time online room multiplayer, local 2-player mode, AI computer opponent with 4 difficulty levels, interactive tactical practice modes, intelligent AI chat assistant, and a deep **Chess Master** strategy knowledge system with live position analysis).

---

## 🎮 Game Modes & Core Features

### 1. ♟️ Chess Master Knowledge & Strategy System (ENHANCED)
- **Deep Strategy & Tactics Knowledge Base**: A complete knowledge system covering 10 main categories:
  1. ⚔️ **Attack Techniques**: King-side, Queen-side, Pawn storm, Uncastled king, Opposite castling, Opening king position, Removing defenders, Queen+Bishop, Rook lift, Sacrificial attacks.
  2. 🎯 **Tactics**: Fork, Pin, Skewer, Discovered attack/check, Double attack, Double check, Deflection, Decoy, Removing defender, Overloading, Zwischenzug, Clearance, Interference, X-Ray attack, Trapping pieces.
  3. 🛡️ **Defense Techniques**: Defending attacks, Counterattack, Simplification, Trading attacking pieces, Blocking, Defensive sacrifice, Escape squares, Perpetual check, Fortress, Prophylaxis, Counterplay.
  4. 👑 **Checkmate Patterns**: Back-rank mate, Smothered mate, Anastasia's mate, Arabian mate, Boden's mate, Greek Gift attack, Queen+Bishop, Rook, Knight patterns.
  5. 🧠 **Positional Strategy**: Improving worst piece, Open/half-open files, Outposts, Weak squares, Passed pawns, Pawn structure, Space advantage, Good vs Bad Bishop, Bishop vs Knight, Rook on 7th, Minority attack, Restricting opponent.
  6. 💥 **Sacrifices**: Pawn, Piece, Exchange, Queen, Greek Gift, Deflection, Decoy, Clearance, Removing defender.
  7. 🏰 **King Safety**: When to castle, When not to castle, Pawn shield, Weakening king, Open files, Opposite castling, Escape squares, Attacking defenders.
  8. 🏆 **Winning Techniques**: Winning material, Trapping queen, Exploiting pinned pieces, Double attacks, Converting material advantage, Simplifying winning positions, Passed pawns, Removing counterplay, Converting winning endgames.
  9. 🔥 **Defending a Worse Position**: Perpetual check, Counterattack, Complications, Trading dangerous pieces, Fortress, Repetition, Stalemate tricks, Passed pawns, Tactical resources.
  10. 📖 **Opening Strategy**: Guides for 8 major openings (Ruy Lopez, Sicilian Defense, Queen's Gambit, French Defense, Italian Game, King's Indian Defense, Caro-Kann, English Opening) covering 8 structured points: Main Idea, Best Plans, Typical Tactics, Common Mistakes, King Safety, Middlegame Plans, How to Attack, How to Defend.
- **Three Structured Feature Fields for Every Package**:
  - 🌟 **Specialty**: Unique focus and strategic value of the technique.
  - ⚡ **What It Can Do**: Tactical and material advantages created.
  - 🎮 **How To Use In Match**: Step-by-step practical guide on when and how to apply the tactic in live play.
- **Interactive Board Example Loader**: Every topic across all 10 categories has an assigned illustrative FEN position. Clicking **"♟ Load Example Position onto Board"** loads that position directly onto the interactive chess board for exploration and play.
- **“What Should I Do Here?” Interactive Position Analyzer**:
  - **Inputs**: Preset tactical setups (Greek Gift, Smothered Mate, Back-Rank Mate, Rook Endgame, Sicilian Dragon, Italian Game), Custom FEN input, or **"📥 Current Game Board Position"** loader.
  - **Live Evaluation Output**: Position evaluation score (+/- delta) & visual advantage bar, Ranked Candidate Moves (#1, #2, #3) with exact notation and evaluation deltas, Recommended Strategic Plan, Tactical Opportunities & Defensive Resources, and explanation of **WHY** the top move is best.
  - **Play Best Move on Board**: One-click button (**"▶ Play Top Recommended Move on Board"**) executes recommended candidate moves directly on the active board.

### 2. 🤖 VS Computer & Context-Aware Intelligent AI Chat
- **Minimax Engine**: Driven by Minimax search with Alpha-Beta pruning, positional evaluations, and king safety heuristics.
- **4 Difficulty Levels**: Easy (Beginner), Medium (Intermediate), Hard (Advanced), and Expert (Master).
- **Smart AI Chat Assistant**: In the Chat tab and VS Computer games, the Computer AI analyzes the player's message intent, keywords, and live board state in real time:
  - *Evaluation / Winning*: Calculates material lead (`White +4` / `Black +2` / `Even`) and comments on who has the advantage.
  - *Tactics & Openings*: Provides specific advice when asked about forks, pins, skewering, Ruy Lopez, Sicilian, gambits, or opening principles.
  - *Attack & Defense*: Gives actionable plans for launching attacks or building defenses.
  - *Endgame & Advice*: Offers guidance based on active turn and board state.
- **Automated Pawn Promotion**: Computer automatically promotes pawns to Queen on the 8th/1st rank.

### 3. 🌐 Online Multiplayer Rooms
- **Room-Based Matching**: Host a match to generate a unique 6-character room ID, or enter an existing room ID to join.
- **Turn & Move Synchronization**: Real-time board state and turn updates powered by backend polling.
- **Clocks & Timeouts**: Synchronized timers supporting bullet, blitz, classical, and untimed matches.
- **In-Game Chat**: Live room chat between players with AI companion integration.
- **Resignation Handling**: Clean resignation triggers with instant victory notifications.

### 4. 🎯 Chess Training & Practice Mode
- **Category Filters**: Practice Tactics, Checkmate Patterns, Endgame Techniques, and Opening Traps.
- **Hints & Scoring**: Show hints highlighting target squares, and track score, correct vs. wrong moves.

### 5. 🎨 Customization, Focus Mode & Game Enhancements
- **Persistent Board Themes**: 8 built-in themes (Classic Brown, Dark Green, Blue, Green, Purple, Gray, Ocean, Sunset) saved in `localStorage` and synchronized across all tab dropdowns.
- **Focus Mode & Collapsible Panel**: Toggle button (**`◧ Focus Mode (Hide Panel)`**) allows hiding the right control panel so the Chess Board expands into a grand **740px** centered view.
- **Checkmate Visual Display**: Floating checkmate banner (`"♔ CHECKMATE! [COLOR] WINS!"`), checkmated king glowing red aura (`.checkmate-king`), audio fanfare, and victory overlay modal.
- **Real-Time Live Timer**: Continuous 1-second interval tick loop (`setInterval`) ensuring clocks tick down live second-by-second (`04:59`, `04:58`, `04:57`...) without freezing.
- **Interactive Pawn Promotion Modal**: Modal picker for Queen ♕, Rook ♖, Bishop ♗, or Knight ♘ when a pawn reaches the 8th/1st rank.

---

## 🛠️ Project Structure & Architecture

### Project Layout
```text
/CHESS
  ├── backend/
  │   └── src/
  │       └── ChessServe.java  # Java HTTP server, REST endpoints, room state, & board logic
  ├── frontend/
  │   └── index.html           # Single-page HTML5/CSS3/JS UI layout, themes, Chess Master engine, & client logic
  ├── run.bat                  # One-click Windows startup & JDK auto-detect script
  ├── Dockerfile               # Production multi-stage Docker build config
  ├── .dockerignore            # Docker build ignore rules
  ├── .gitignore               # Git version control ignore rules
  ├── .env.example             # Environment variable configuration template
  └── README.md                # Project documentation
```

### Recent Key Updates & Improvements
1. **Interactive FEN Position Loader**: Added FEN position loading to every topic package in the Chess Master section.
2. **Three Feature Fields for Topics**: Added 🌟 Specialty, ⚡ What It Can Do, and 🎮 How To Use In Match to every topic detail view.
3. **Context-Aware AI Chatbot**: Upgraded chat engine to parse message intent, keywords, and live board material lead.
4. **Position Evaluator & Live Analyzer Enhancements**: Added current game board FEN importer and one-click candidate move execution.
5. **Board Theme Persistence & Dropdown Sync**: Saved themes in `localStorage` and synced all theme dropdown controls across tabs.
6. **Checkmate Visual Display & Audio**: Added floating checkmate banners, glowing red checkmate king highlights, checkmate sound effects, and victory modal overlay.
7. **Real-Time Live Timer Tick Loop**: Added a 1-second live interval tick loop so timers tick continuously second-by-second.
8. **Focus Mode & Collapsible Side Panel**: Added header toggle to collapse side controls for a grand full-screen board view.

---

## 🚀 How to Run the Project (Localhost)

### Requirements
- **Java Development Kit (JDK 8 or higher)** installed on your machine.

### 1. Start the Server

* **Option A: Using the Windows Startup Script (Recommended)**
  Double-click **`run.bat`** in the project root directory. It automatically detects your JDK, compiles `backend/src/ChessServe.java`, and launches the HTTP server.

* **Option B: Manual Terminal Execution**
  Open PowerShell or Command Prompt in `d:\CHESS` and run:
  ```powershell
  # Compile the Java class
  javac backend/src/ChessServe.java

  # Start the HTTP server on port 9090
  java -cp backend/src ChessServe
  ```

### 2. Open the Game in Your Browser
Navigate to:
👉 **[http://localhost:9090](http://localhost:9090)**

---

## 💻 Technical Details

### Backend (Java)
- **Core HTTP Server**: Built with `com.sun.net.httpserver.HttpServer` (zero external dependencies).
- **Multithreading**: Fixed thread pool of 50 worker threads for concurrent HTTP requests.
- **State Management & Concurrency**: Uses `ConcurrentHashMap` and thread-safe collections.
- **Automated Memory Cleanup**: Daemon thread purges inactive games exceeding 2 hours of inactivity every 5 minutes.

### Frontend (HTML5 / CSS3 / Vanilla JS)
- **Glassmorphism Design System**: Modern dark UI featuring smooth gradients and tabbed navigation.
- **Web Audio Synthesis**: Dynamic sound generation using Web Audio API (`AudioContext`).
- **Chess Master Engine & Live Analyzer**: Built-in minimax evaluator, material balance calculator, position analyzer, candidate move ranker, and FEN parser.
