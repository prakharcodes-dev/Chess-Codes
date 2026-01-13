♟️ Chess Game Project
This is a basic chess game I built from scratch using HTML, CSS, and JavaScript. It's my first attempt at creating a complete chess game with working features.

✨ What Works
Basic Chess Features
✅ Chess board with all pieces in starting position

✅ Piece movement for pawns, knights, bishops, rooks, queens, and kings

✅ Turn-based system (white moves first, then black)

✅ Check detection (highlights king in yellow when threatened)

✅ Move validation (prevents illegal moves)

✅ Checkmate detection (ends game when king is trapped)

Game Modes
Local Game: Play against a friend on the same computer

VS Computer: Play against a basic computer opponent

Multiplayer Setup: Interface for online play (mock server only)

Basic UI Features
✅ Visual board with alternating light/dark squares

✅ Selected pieces highlighted with red border

✅ Possible moves shown with green dots

✅ Move history display

✅ Timer for each player (10 minutes each)

✅ Game status messages

🔧 What's Implemented
Chess Rules Working
Pawns: Move forward 1 square, 2 squares from starting row, capture diagonally

Knights: L-shaped movement (2 squares one way, 1 square sideways)

Bishops: Diagonal movement any distance

Rooks: Straight movement any distance

Queens: Combination of rook and bishop movements

Kings: One square in any direction

Game Controls
New Game: Resets the board

Undo: Takes back last move (local games only)

Resign: Forfeit current game

Leave Game: Exit to main menu

⚠️ Limitations & Basic Features
VS Computer Mode (Very Basic AI)
The computer makes random legal moves

No advanced chess strategy or thinking

Different difficulty levels only change thinking time, not skill

Computer doesn't understand tactics or strategy

Mostly makes random moves from available options

Missing Advanced Chess Rules
❌ No Castling (king moving with rook)

❌ No En Passant (special pawn capture)

❌ No Pawn Promotion (pawns don't become queens)

❌ No Checkmate patterns recognition

❌ No Stalemate detection in all cases

Chat System (Basic)
Simple text chat that works locally

Computer sends random pre-written responses

No real multiplayer chat functionality

Basic quick message buttons

Multiplayer (Mock Only)
Uses a simulated/mock server

Not real online multiplayer

Limited to basic game state sharing

No real opponent connection

📱 Simple Design
Basic dark theme interface

Works in modern browsers

Responsive layout (adjusts to screen size)

Clear turn indicators

Game status notifications

🎮 How to Use
Basic Play
Open the HTML file in any browser

Click "Local Game" for practice

Click a piece to select it

Click a highlighted square to move

Take turns playing against yourself or a friend

VS Computer
Go to "VS Computer" tab

Click "Start vs Computer"

Computer will make simple moves when it's its turn

🔍 Technical Details
What I Built
All chess logic from scratch

Board representation as 2D array

Move validation for each piece type

Basic game state management

Simple computer opponent logic

Simple Code Structure
Single HTML file with everything included

Basic JavaScript for game logic

CSS for board styling

No external libraries or frameworks

🎓 What I Learned
How chess pieces move in detail

JavaScript arrays for game boards

Basic event handling for clicks

Simple game state management

CSS grid for board layout

Turn-based game logic basics

🚫 What's Not Working Well
Computer plays very poorly (mostly random)

Missing important chess rules

No advanced features

Basic user interface

No real multiplayer

Limited move validation in some edge cases

📂 File Structure

chess-game.html  # The complete game in one file

🎯 For Beginners
This project is good for:

Learning basic chess programming

Understanding game state management

Seeing how piece movement works

Simple browser-based game development

⚠️ Note
This is a basic learning project, not a full-featured chess game. The computer opponent is very simple, many advanced chess rules are missing, and it's meant for educational purposes rather than serious chess play.
