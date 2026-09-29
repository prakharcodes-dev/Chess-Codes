# Real-Time Chess & Chess Master Knowledge System

🚀 **Live Project Demo:** [https://chess-codes.onrender.com]

A full-featured, high-performance Chess application featuring a robust Java HTTP backend server and a responsive, modern HTML5/CSS3/JavaScript frontend client. The application supports multiple gameplay modes (including real-time online room multiplayer, local 2-player mode, AI computer opponent with 4 difficulty levels, interactive tactical practice modes, intelligent AI chat assistant, and a deep **Chess Master** strategy knowledge system with live position analysis).

---

### 0. 🎮 Mode Selection Dashboard & Landing UX Redesign (NEW)
- **Visual Mode Selection Cards**: Instant 1-click landing cards replacing raw form setup with intuitive visual cards:
  - 🤖 **VS Computer (AI)**: Launch AI games with 4 difficulty levels (Easy, Medium, Hard, Expert).
  - 👥 **Local 2-Player**: Pass & play side-by-side on the same device with live timers.
  - 🌐 **Online Room Match**: Host or Join a 6-digit multiplayer room code.
  - ♟️ **Chess Master Academy**: 10 strategy categories, tactical patterns & live position evaluator.
  - 🎯 **Tactical Training**: Solve curated tactical puzzles & level up accuracy.
- **Collapsible Customization Accordion**: Organized settings (**Time Control, Game Variant, Board Theme, Piece Set, Piece Visual Style, Sound Effects, Highlight Legal Moves**) inside a neat **⚙️ Match & Board Customization** accordion.

### 1. 🤖 AI Chess Coach & Real-Time Move Analysis (NEW)
- **Live Move-by-Move Evaluation**: Evaluates every move made during a match using Piece-Square Tables (PST) and centipawn material balance.
- **Move Quality Classification**: Detects and badges every move into 6 clear tactical tiers:
  - ✨ **Brilliant**: Game-changing tactical sacrifices or decisive checkmate execution.
  - ⭐ **Best Move**: Top computer engine recommendation with optimal square control.
  - 👍 **Good Move**: Solid, stable move maintaining positional harmony.
  - ⚠️ **Inaccuracy**: Slight loss of tempo or suboptimal square placement.
  - ❌ **Mistake**: Significant tactical error conceding counterplay or central pressure.
  - 💥 **Blunder**: Severe tactical blunder losing material or compromising king safety.
- **Actionable Tactical Explanations**: Plain-English explanations explaining *why* a move was great or where it fell short.
- **Better Move Suggestions & Board Highlighting**: Suggests the optimal engine continuation with an interactive **"👁️ Show on Board"** toggle that highlights the exact source and target squares on the chessboard.
- **Live Position Evaluation Meter**: Numerical centipawn score (e.g. `+1.85`, `-0.75`, `0.00`) and a smooth visual advantage bar with White vs Black win chance percentages.
- **Dedicated AI Coach Tab & Live In-Game Ticker**: Includes both a dedicated **"🤖 AI Coach"** tab in the main controls and a compact **Live AI Coach Ticker** inside the active Game panel.
- **Comprehensive Post-Game Analysis & Report**:
  - **Player Accuracy Percentages**: Scientific accuracy scoring for both White and Black.
  - **Move Quality Breakdown Counters**: Total counts for Brilliant, Best, Good, Inaccuracies, Mistakes, and Blunders.
  - **Key Turning Point of the Match**: Pinpoints the single biggest swing move of the game with evaluation delta and best continuation.
  - **Personalized Weaknesses & Training Plan**: Automatically diagnoses error patterns (e.g. Tactical Blunder Proneness, Opening Inaccuracies, Endgame Technique) and provides **1-click action buttons** that directly launch the corresponding puzzle drill or Master Academy lesson.

### 2. 🖥️ Balanced Desktop Layout & Professional Chess Platform UX (NEW)
- **Balanced 52/48 Grid**: Left chessboard column (52%) and right control panel (48%) are proportionally balanced with `grid-template-columns: minmax(0, 52fr) minmax(0, 48fr)`.
- **Top Alignment (`align-items: start;`)**: Prevents vertical over-stretching or visual distortion between the chessboard and control cards.
- **Compact Card Sizing**: Right-side cards, buttons, and accordions are streamlined with reduced vertical spacing to eliminate empty gaps and maintain a clean, compact interface.
- **Responsive Mobile Layout**: Automatically transitions to a single-column layout on screens under 1080px without sacrificing any feature or functionality.

### 3. ♟️ Chess Master Knowledge & Strategy System (ENHANCED)
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

### 4. 🤖 VS Computer & Context-Aware Intelligent AI Chat
- **Minimax Engine**: Driven by Minimax search with Alpha-Beta pruning, positional evaluations, and king safety heuristics.
- **4 Difficulty Levels**: Easy (Beginner), Medium (Intermediate), Hard (Advanced), and Expert (Master).
- **Smart AI Chat Assistant**: In the Chat tab and VS Computer games, the Computer AI analyzes the player's message intent, keywords, and live board state in real time:
  - *Evaluation / Winning*: Calculates material lead (`White +4` / `Black +2` / `Even`) and comments on who has the advantage.
  - *Tactics & Openings*: Provides specific advice when asked about forks, pins, skewering, Ruy Lopez, Sicilian, gambits, or opening principles.
  - *Attack & Defense*: Gives actionable plans for launching attacks or building defenses.
  - *Endgame & Advice*: Offers guidance based on active turn and board state.
- **Automated Pawn Promotion**: Computer automatically promotes pawns to Queen on the 8th/1st rank.

### 5. 🌐 Online Multiplayer Rooms
- **Room-Based Matching**: Host a match to generate a unique 6-character room ID, or enter an existing room ID to join.
- **Turn & Move Synchronization**: Real-time board state and turn updates powered by backend polling.
- **Clocks & Timeouts**: Synchronized timers supporting bullet, blitz, classical, and untimed matches.
- **In-Game Chat**: Live room chat between players with AI companion integration.
- **Resignation Handling**: Clean resignation triggers with instant victory notifications.

### 6. 🎯 Chess Training & Practice Mode
- **Category Filters**: Practice Tactics, Checkmate Patterns, Endgame Techniques, and Opening Traps.
- **Hints & Scoring**: Show hints highlighting target squares, and track score, correct vs. wrong moves.

### 7. 🎨 Customization, Piece Sets, Piece Styles & Interaction Enhancements
- **Multiple Piece Sets**: Selectable complete piece sets: **Classic Set (Staunton)**, **Modern Set (Merida)**, **Fantasy Set (Horsey)**, and **Minimal Set (California)**. Saved in `localStorage` and applied across all piece views.
- **Multiple Piece Visual Styles**: Selectable visual styles: **Classic Vector**, **Modern Vector**, **3D Rendered Depth** (perspective depth filter & drop-shadows), and **Neon Glow Cyber** (glowing cyan/rose cyberpunk drop-shadow filters).
- **Smooth Animations & Capture Effects**: Smooth piece scale transitions, capture burst animations (`@keyframes captureBurst`), king check pulsating danger aura (`.highlight`), checkmate golden trophy aura (`.checkmate-king`), and invalid move shake feedback (`@keyframes shakeCell`).
- **Interactive Piece Movement & Drag-and-Drop**: Native HTML5 Drag and Drop piece movement (`draggable="true"`, `ondragstart`, `ondragover`, `ondrop`), legal move target dots (`.possible-move`), capture target rings (`.capture-move`), last-move highlight (`.last-move`), and selected-piece outline (`.selected-cell`).
- **Persistent Board Themes**: 14 built-in themes (Classic Brown, Dark Green, Blue, Green, Purple, Gray, Ocean, Sunset, Walnut Wood, Cyberpunk Neon, Emerald Forest, Crimson Ruby, Glacier Ice, Luxury Gold) saved in `localStorage`.
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
9. **Mode Selection Dashboard & Landing UX Redesign**: Added intuitive 1-click game mode cards (VS Computer, Local 2-Player, Online Room, Chess Master Academy, Tactical Training) and a collapsible match customization accordion for an ultra-clean, beginner-friendly onboarding experience.
10. **Refined Board UI Spacing & Rounded Corners**: Enhanced `.board-container` padding and smooth rounded corner framing (`border-radius: 24px` / `16px`) for an elegant glassmorphism board design.

---

## 🚀 How to Run the Project (Localhost)

You can run this project using any of the following convenient methods:

---

### Option 1: ⚡ 1-Click Startup (Windows - Recommended)
1. Double-click the **`run.bat`** file located in the project root directory.
2. The script will automatically:
   - Search and detect your Java JDK installation (`JAVA_HOME` or system paths).
   - Compile `backend/src/ChessServe.java`.
   - Start the HTTP backend server on **`http://localhost:9090`**.
3. Open your browser and go to:
   👉 **[http://localhost:9090](http://localhost:9090)**

---

### Option 2: 💻 Command Line (Windows, macOS, Linux)

#### Prerequisites:
* **Java Development Kit (JDK 8 or higher)** installed on your machine (`javac -version`).

#### Steps:
1. Open your terminal (PowerShell, Command Prompt, or Bash) in the project root directory:
   ```bash
   cd d:\CHESS
   ```

2. Compile the Java backend:
   ```bash
   javac backend/src/ChessServe.java
   ```

3. Launch the server:
   ```bash
   java -cp backend/src ChessServe
   ```

4. You will see:
   ```text
   Chess Server started successfully on port 9090
   Server URL: http://localhost:9090
   Thread pool: 50 threads ready
   Memory management: Active games limit: 1000
   ```

5. Open your web browser and visit:
   👉 **[http://localhost:9090](http://localhost:9090)**

> **Tip - Custom Port:**
> By default, the server runs on port `9090`. You can change the port using an environment variable:
> - **PowerShell:** `$env:PORT=8080; java -cp backend/src ChessServe`
> - **CMD:** `set PORT=8080 && java -cp backend/src ChessServe`
> - **Linux/macOS:** `PORT=8080 java -cp backend/src ChessServe`

---

### Option 3: 🌐 Instant Browser-Only Mode (Zero Setup / No Java Required)
If you don't have Java installed or just want to play offline immediately:
1. Simply double-click **`frontend/index.html`** or right-click and select **"Open with Chrome / Edge / Firefox"**.
2. **All core features work immediately out-of-the-box in standalone client mode:**
   - 🤖 **VS Computer (AI)** with 4 difficulty levels.
   - 🧠 **AI Chess Coach** with real-time blunder detection, position evaluation, and post-game reports.
   - 👥 **Local 2-Player Pass & Play** with live clocks.
   - 🎯 **Tactical Practice Puzzles & Hints**.
   - ♟️ **Chess Master Academy & Strategy Packages**.
   - 🎨 **14 Board Themes, Custom Piece Sets & Visual Styles**.

*(Note: Live multiplayer room matching across different devices uses the Java backend).*

---

### Option 4: 🐳 Docker Container

If you prefer containerized deployment:

```bash
# 1. Build the Docker container
docker build -t chess-app .

# 2. Run the container on port 9090
docker run -d -p 9090:9090 --name chess-app chess-app

# 3. Open in browser
http://localhost:9090
```

---

### 🔍 Verification & Health Check Endpoints
Once the server is running, you can test the backend endpoints directly:
* **Web App**: `http://localhost:9090/`
* **Ping Test**: `http://localhost:9090/ping` *(returns `{"status":"pong"}`)*
* **Health Metrics**: `http://localhost:9090/health` *(returns uptime, active games, and memory stats)*

---

### ❓ Troubleshooting

* **"javac is not recognized as an internal or external command"**:
  Ensure you have JDK installed. Add your JDK `bin` directory (e.g. `C:\Program Files\Java\jdk-xx\bin`) to your system's `PATH` environment variable, or simply use **Option 3** to run `frontend/index.html` directly in your browser.
* **"Address already in use: bind" (Port 9090 occupied)**:
  Another service is using port 9090. Either free the port:
  ```powershell
  # Find PID using port 9090
  Get-Process -Id (Get-NetTCPConnection -LocalPort 9090).OwningProcess | Stop-Process -Force
  ```
  Or launch on a different port:
  ```powershell
  $env:PORT=9091; java -cp backend/src ChessServe
  ```

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
