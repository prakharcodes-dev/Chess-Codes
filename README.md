♟️ Chess Game Project
This is my first chess game built with HTML, CSS, and JavaScript. I created everything myself including the board setup, piece movements, and game rules.

✨ What It Does
✅ Basic Game Features
Full chess board with all pieces in starting position

Piece movement rules for pawns, knights, bishops, rooks, queens, and kings

Turn system - players take turns moving pieces

Legal move checking - pieces can only move according to chess rules

Visual feedback - selected pieces and valid moves are highlighted

Move history - all moves are shown in a list

Undo button - go back one move (for practice games)

Check detection - warns when king is in danger

Checkmate detection - ends game when king is trapped

🎨 Visual Design
Dark theme with nice colors for the board

Chess piece symbols that look good on screen

Responsive layout that works on different screen sizes

Clear indicators for whose turn it is

Game status messages at the bottom

⏱️ Game Controls
New Game - resets the board to starting position

Undo - takes back the last move (local games only)

Resign - gives up the current game

Leave - exits multiplayer games

🛠️ How It Works
Game Setup
The board is an 8x8 grid. Each square can have:

No piece (empty)

A piece with color (white or black) and type (pawn, rook, etc.)

Piece Movement Rules
Pawns - move forward one square, two squares from starting position, capture diagonally

Rooks - move straight in any direction

Knights - move in L-shape (two squares one way, one square sideways)

Bishops - move diagonally

Queens - move straight or diagonally any distance

Kings - move one square in any direction

Game Logic
Turn system: White moves first, then black, alternating

Move validation: Checks if moves are legal for each piece type

Check detection: Looks if any piece threatens the opponent's king

Checkmate check: Tests if player has any legal moves when in check

Stalemate detection: Ends game if player has no legal moves but isn't in check

📁 File Structure
index.html - Main game file with HTML, CSS, and JavaScript

No external libraries - everything is in one file

Simple setup - just open the HTML file in a browser

🚀 How to Play
Open the HTML file in your web browser

Click "Local Game" to start a practice game

Click on any piece to select it

Click on highlighted squares to move the piece

Watch the move history update as you play

Use Undo to practice different moves

Try Resign or New Game to restart

🎯 What I Learned
How chess piece movements really work

JavaScript arrays for the game board

Event handling for user clicks

Game state management

CSS grid for board layout

Turn-based game logic

Move validation algorithms
