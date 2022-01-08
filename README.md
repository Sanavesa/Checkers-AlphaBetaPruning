# Alpha-Beta Pruning Checkers
A minimax implementation with alpha-beta pruning for Checkers as part of my CSCI 561 (AI) course at USC Spring 2021. Developed using Java/JavaFX for an interactive gameplay, where you can face the minimax agent.

To speed up the implementation, the board state was encoded using 64-bit integers instead of a 8x8 2D array. As such, this sped up the search by two orders of magnitude. Most operations became bit-wise; for example, to retrieve all movable black pieces, it is simply <code>(blackPieces << 9) | (blackPieces << 7)</code>, where 9 means the cell diagonally below on the right (7 would be to the left).

Moreover, transposition tables were utilized to cache vital states that have been visited to speed up the minimax tree search. This improvement, however, came at a cost; more memory for faster execution.

![](preview.png)
