package improved;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The <code>StateBoard</code> class encapsulates the board state of a game of English Checkers, where Black starts at the top and White starts at the bottom. It utilizes a bitboard representation
 * rather than the naive array approach for performance reasons.
 * 
 * <p>
 * This class generates actions and maintains the state of the pieces on the board. Likewise, it also determines whether the game is over (won, lost, draw). Note: this implementation of English
 * Checkers will ignore the Draw condition where if the same exact board position has been reached 3 times, then it is a draw because it is memory-intensive.
 * </p>
 * 
 * @author Mohammad Alali
 * @see Action
 */
public final class StateBoard
{
	/**
	 * The bitmask of invalid pawn locations.
	 */
	public static final long MASK_INVALID = 0b1010101001010101101010100101010110101010010101011010101001010101L;

	/**
	 * The bitmask of valid pawn locations.
	 */
	public static final long MASK_VALID = ~0b1010101001010101101010100101010110101010010101011010101001010101L;

	/**
	 * The bitmask of black pawns start location (top of the board).
	 */
	public static final long MASK_BLACK_START = 0b0000000000000000000000000000000000000000101010100101010110101010L;

	/**
	 * The bitmask of white pawns start location (bottom of the board).
	 */
	public static final long MASK_WHITE_START = 0b0101010110101010010101010000000000000000000000000000000000000000L;

	/**
	 * The bitmask of the home row (first row) of the black team.
	 */
	public static final long MASK_BLACK_HOME_ROW = 0b0000000000000000000000000000000000000000000000000000000010101010L;

	/**
	 * The bitmask of the home row (first row) of the white team.
	 */
	public static final long MASK_WHITE_HOME_ROW = 0b0101010100000000000000000000000000000000000000000000000000000000L;

	/**
	 * The bitmask of the kings row of the black team. Equivalent to {@link #MASK_WHITE_HOME_ROW}.
	 */
	public static final long MASK_BLACK_KINGS_ROW = 0b0101010100000000000000000000000000000000000000000000000000000000L;

	/**
	 * The bitmask of the kings row of the white team. Equivalent to {@link #MASK_BLACK_HOME_ROW}.
	 */
	public static final long MASK_WHITE_KINGS_ROW = 0b0000000000000000000000000000000000000000000000000000000010101010L;

	/**
	 * The bitmask of the potential kings row (1 row off being a king) of the black team. This is equivalent to the second row of the white team.
	 */
	public static final long MASK_BLACK_POTENTIAL_KINGS_ROW = 0b0000000010101010000000000000000000000000000000000000000000000000L;

	/**
	 * The bitmask of the potential kings row (1 row off being a king) of the white team. This is equivalent to the second row of the black team.
	 */
	public static final long MASK_WHITE_POTENTIAL_KINGS_ROW = 0b0000000000000000000000000000000000000000000000000101010100000000L;

	/**
	 * The bitmask of the middle center of the board. Specifically, d4 and e5.
	 */
	public static final long MASK_CENTER_MID = 0b0000000000000000000000000000100000010000000000000000000000000000L;

	/**
	 * The position offset to obtain the adjacent North-West cell by <b>right shifting</b>. North is black, South is white.
	 */
	public static final long NW_RSHIFT = 7;

	/**
	 * The position offset to obtain the adjacent North-East cell by <b>right shifting</b>. North is black, South is white.
	 */
	public static final long NE_RSHIFT = 9;

	/**
	 * The position offset to obtain the adjacent South-West cell by <b>left shifting</b>. North is black, South is white.
	 */
	public static final long SW_LSHIFT = 9;

	/**
	 * The position offset to obtain the adjacent South-East cell by <b>left shifting</b>. North is black, South is white.
	 */
	public static final long SE_LSHIFT = 7;

	/**
	 * A <b>stable</b> comparator used to sort the jumpActions in descending order of their capture value.
	 */
	private static final Comparator<ChainJumpAction> DESCENDING_CHAIN_CAPTURE_VALUE_COMPARATOR;

	static
	{
		DESCENDING_CHAIN_CAPTURE_VALUE_COMPARATOR = new Comparator<ChainJumpAction>()
		{
			@Override
			public int compare(ChainJumpAction o1, ChainJumpAction o2)
			{
				return -Double.compare(o1.captureValue, o2.captureValue);
			}
		};
	}

	/**
	 * The current player's turn.
	 */
	public boolean isBlackTurn;

	/**
	 * The bitboard representation of all black pieces on the board.
	 */
	public long blacks;

	/**
	 * The bitboard representation of all white pieces on the board.
	 */
	public long whites;

	/**
	 * The bitboard representation of all king pieces on the board.
	 */
	public long kings;

	/**
	 * A ply counter since the last piece has been crown or piece has been captured.
	 */
	public int plysSinceLastCrownOrCapture;

	/**
	 * A cache for the list of valid move actions. Will be invalidated if an action has been executed or undo'd, or even when the player turn is switched.
	 * 
	 * @see #areMovesDirty
	 */
	private List<MoveAction> cachedMoveActions;

	/**
	 * A cache for the list of valid jump actions. Will be invalidated if an action has been executed or undo'd, or even when the player turn is switched.
	 * 
	 * @see #areJumpsDirty
	 */
	private List<ChainJumpAction> cachedJumpActions;

	/**
	 * A flag that determines if the valid move actions are outdated: when an action has been executed or undo'd, or even when the player turn is switched.
	 * 
	 * @see #cachedMoveActions
	 */
	private boolean areMovesDirty;

	/**
	 * A flag that determines if the valid jump actions are outdated: when an action has been executed or undo'd, or even when the player turn is switched.
	 * 
	 * @see #cachedJumpActions
	 */
	private boolean areJumpsDirty;

	/**
	 * Creates an empty English Checkers board with no pieces, with the Black team playing first.
	 */
	public StateBoard()
	{
		cachedMoveActions = new ArrayList<>();
		cachedJumpActions = new ArrayList<>();
		isBlackTurn = true;
		blacks = 0L;
		whites = 0L;
		kings = 0L;
		areMovesDirty = true;
		areJumpsDirty = true;
	}

	/**
	 * Creates the regular initial position for an English Checkers board, with the Black team playing first.
	 * 
	 * @return the board with the initial game position
	 */
	public static StateBoard initial()
	{
		final StateBoard board = new StateBoard();
		board.blacks = StateBoard.MASK_BLACK_START;
		board.whites = StateBoard.MASK_WHITE_START;
		board.kings = 0;
		return board;
	}

	/**
	 * @return return a random state board. Used for testing.
	 */
	public static StateBoard random()
	{
		final double kingChance = 0.1; // 10%
		final int blackPieces = 1 + Constants.RANDOM.nextInt(12); // [1, 12]
		final int whitePieces = 1 + Constants.RANDOM.nextInt(12); // [1, 12]
		final boolean isBlackTurn = Constants.RANDOM.nextBoolean();

		return random(blackPieces, whitePieces, kingChance, isBlackTurn);
	}

	/**
	 * @return return a random state board. Used for testing.
	 */
	public static StateBoard random(int blackPieces, int whitePieces, double kingChance, boolean isBlackTurn)
	{
		while (true)
		{
			final StateBoard board = new StateBoard();
			board.isBlackTurn = isBlackTurn;
			int remainingBlackPieces = blackPieces;
			int remainingWhitePieces = whitePieces;

			while (remainingBlackPieces > 0)
			{
				final int index = Constants.RANDOM.nextInt(64); // [0, 63]

				// Redo if invalid index or occupied
				if (((StateBoard.MASK_INVALID >> index) & 1L) != 0)
					continue;
				if ((((board.blacks | board.whites) >> index) & 1L) != 0)
					continue;

				final long position = 1L << index;
				board.blacks |= position;
				remainingBlackPieces--;

				final boolean isKing = ((StateBoard.MASK_BLACK_KINGS_ROW >> index) & 1L) != 0L || Constants.RANDOM.nextDouble() <= kingChance;
				if (isKing)
					board.kings |= position;
			}

			while (remainingWhitePieces > 0)
			{
				final int index = Constants.RANDOM.nextInt(64); // [0, 63]

				// Redo if invalid index or occupied
				if (((StateBoard.MASK_INVALID >> index) & 1L) != 0)
					continue;
				if ((((board.blacks | board.whites) >> index) & 1L) != 0)
					continue;

				final long position = 1L << index;
				board.whites |= position;
				remainingWhitePieces--;

				final boolean isKing = ((StateBoard.MASK_WHITE_KINGS_ROW >> index) & 1L) != 0L || Constants.RANDOM.nextDouble() <= kingChance;
				if (isKing)
					board.kings |= position;
			}

			// Redo if there are no valid actions
			if ((board.getMoves(board.isBlackTurn) | board.getJumps(board.isBlackTurn)) == 0L)
				continue;

			return board;
		}
	}

	/**
	 * Executes the specified <code>action</code> and switches the turn to the other team.
	 * 
	 * @param action the action to execute
	 * @see Action
	 */
	public void executeAction(Action action)
	{
		final int plys = plysSinceLastCrownOrCapture;
		action.execute(this);
		if (plys == plysSinceLastCrownOrCapture)
			plysSinceLastCrownOrCapture++;

		// Switch the turn and invalidate the cached actions
		isBlackTurn = !isBlackTurn;
		areMovesDirty = true;
		areJumpsDirty = true;
	}

	/**
	 * Invalidates the action caches. Used for testing only.
	 */
	public void markAsDirty()
	{
		areMovesDirty = true;
		areJumpsDirty = true;
	}

	/**
	 * Undos the specified <code>action</code> and switches the turn to the other team.
	 * 
	 * @param action the action to undo
	 * @see Action
	 */
	public void undoAction(Action action)
	{
		action.undo(this);

		// Switch the turn and invalidate the cached actions
		isBlackTurn = !isBlackTurn;
		areMovesDirty = true;
		areJumpsDirty = true;
	}

	/**
	 * Determines the state of the game. This will <b>not</b> consider player timeouts, but rather just to the values in {@link GameState}. Furthermore, this does not consider the draw condition where it
	 * is a draw when the board position has been reached 3 times exactly.
	 * 
	 * @return the state of the game
	 * @see GameState
	 */
	public GameState getGameState()
	{
		if (plysSinceLastCrownOrCapture >= 100)
			return GameState.Draw;

		if (Long.bitCount(blacks) == 0)
			return GameState.WhiteWin;

		if (Long.bitCount(whites) == 0)
			return GameState.BlackWin;

		final long validActions = getMoves(isBlackTurn) | getJumps(isBlackTurn);
		if (validActions == 0L)
			return (isBlackTurn) ? GameState.WhiteWin : GameState.BlackWin;

		return GameState.Ongoing;
	}

	/**
	 * Returns the number of blocked pawns differential. If 0, then equal blockings on both teams. If positive, then the current team is less blocked than the opponent.
	 * 
	 * @return number of blocked pawns differential
	 */
	public int getNumBlockedPawnsDifferential()
	{
		final long empty = ~(blacks | whites) & MASK_VALID;
		int blackBlockedCount = 0;
		int whiteBlockedCount = 0;

		long remainingBlacks = blacks;
		while (remainingBlacks != 0)
		{
			// Find index of next black piece
			final int from = Long.numberOfTrailingZeros(remainingBlacks);
			final long fromPosition = 1L << from;

			// All black pawns can move SW, SE
			long moves = (fromPosition << SW_LSHIFT) | (fromPosition << SE_LSHIFT);
			// Only black kings can move NW, NE
			moves |= ((fromPosition & kings) >> NW_RSHIFT) | ((fromPosition & kings) >> NE_RSHIFT);

			// All black pawns can jump SW, SE
			long jumps = (((fromPosition << SW_LSHIFT) & whites) << SW_LSHIFT) | (((fromPosition << SE_LSHIFT) & whites) << SE_LSHIFT);
			// Only black kings can jump NW, NE
			jumps |= ((((fromPosition & kings) >> NW_RSHIFT) & whites) >> NW_RSHIFT) | ((((fromPosition & kings) >> NE_RSHIFT) & whites) >> NE_RSHIFT);

			// Filter invalid actions
			final long actions = (moves | jumps) & empty;
			if (actions == 0L)
				blackBlockedCount++;

			// Unset bit
			remainingBlacks &= ~fromPosition;
		}

		long remainingWhites = whites;
		while (remainingWhites != 0)
		{
			// Find index of next white piece
			final int from = Long.numberOfTrailingZeros(remainingWhites);
			final long fromPosition = 1L << from;

			// All white pawns can move NW, NE
			long moves = (fromPosition >> NW_RSHIFT) | (fromPosition >> NE_RSHIFT);
			// Only white kings can move SW, SE
			moves |= ((fromPosition & kings) << SW_LSHIFT) | ((fromPosition & kings) << SE_LSHIFT);

			// All white pawns can jump NW, NE
			long jumps = (((fromPosition >> NW_RSHIFT) & blacks) >> NW_RSHIFT) | (((fromPosition >> NE_RSHIFT) & blacks) >> NE_RSHIFT);
			// Only white kings can jump SW, SE
			jumps |= ((((fromPosition & kings) << SW_LSHIFT) & blacks) << SW_LSHIFT) | ((((fromPosition & kings) << SE_LSHIFT) & blacks) << SE_LSHIFT);

			// Filter invalid actions
			final long actions = (moves | jumps) & empty;
			if (actions == 0L)
				whiteBlockedCount++;

			// Unset bit
			remainingWhites &= ~fromPosition;
		}

		if (isBlackTurn)
			return whiteBlockedCount - blackBlockedCount;
		else
			return blackBlockedCount - whiteBlockedCount;
	}

	/**
	 * This method will compute the move actions for the current turn's player and cache the result. However, executing or undoing actions will invalidate this cache.
	 * 
	 * @return the valid move actions for all the current turn's player pieces
	 * @see MoveAction
	 */
	public List<MoveAction> getMoveActions()
	{
		if (areMovesDirty)
		{
			cachedMoveActions = generateMoves();
			areMovesDirty = false;
		}

		return cachedMoveActions;
	}

	/**
	 * This method will compute the jump actions for the current turn's player and cache the result. However, executing or undoing actions will invalidate this cache.
	 * 
	 * @return the valid jump actions for all the current turn's player pieces
	 * @see ChainJumpAction
	 */
	public List<ChainJumpAction> getJumpActions()
	{
		if (areJumpsDirty)
		{
			cachedJumpActions = generateJumps();
			areJumpsDirty = false;
		}

		return cachedJumpActions;
	}

	/**
	 * Computes the bitboard representation of all positions that the specified team can move to.
	 * 
	 * @param isBlack the team to get moves for
	 * @return bitboard of movable positions
	 */
	public long getMoves(boolean isBlack)
	{
		final long empty = ~(blacks | whites) & MASK_VALID;

		if (isBlack)
		{
			// All black pawns can move SW, SE
			long moves = (blacks << SW_LSHIFT) | (blacks << SE_LSHIFT);

			// Only black kings can move NW, NE
			final long blackKings = blacks & kings;
			moves |= (blackKings >> NW_RSHIFT) | (blackKings >> NE_RSHIFT);

			return empty & moves;
		}
		else
		{
			// All white pawns can move NW, NE
			long moves = (whites >> NW_RSHIFT) | (whites >> NE_RSHIFT);

			// Only white kings can move SW, SE
			final long whiteKings = whites & kings;
			moves |= (whiteKings << SW_LSHIFT) | (whiteKings << SE_LSHIFT);

			return empty & moves;
		}
	}

	/**
	 * Computes the bitboard representation of all positions that the specified team can capture. Will only show <b>single-jumps</b>.
	 * 
	 * @param isBlack the team to get jumps for
	 * @return bitboard of capturable positions
	 */
	public long getJumps(boolean isBlack)
	{
		final long empty = ~(blacks | whites) & MASK_VALID;

		if (isBlack)
		{
			// All black pawns can jump SW, SE
			long jumps = (((blacks << SW_LSHIFT) & whites) << SW_LSHIFT) | (((blacks << SE_LSHIFT) & whites) << SE_LSHIFT);

			// Only black kings can jump NW, NE
			final long blackKings = blacks & kings;
			jumps |= (((blackKings >> NW_RSHIFT) & whites) >> NW_RSHIFT) | (((blackKings >> NE_RSHIFT) & whites) >> NE_RSHIFT);

			return empty & jumps;
		}
		else
		{

			// All white pawns can jump NW, NE
			long jumps = (((whites >> NW_RSHIFT) & blacks) >> NW_RSHIFT) | (((whites >> NE_RSHIFT) & blacks) >> NE_RSHIFT);

			// Only white kings can jump SW, SE
			final long whiteKings = whites & kings;
			jumps |= (((whiteKings << SW_LSHIFT) & blacks) << SW_LSHIFT) | (((whiteKings << SE_LSHIFT) & blacks) << SE_LSHIFT);

			return empty & jumps;
		}
	}

	/**
	 * @return a board representation (8x8) grid with b/B as black pawns and w/W as white pawns and capitalized letter indicates kingship.
	 */
	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();

		for (int i = 0; i < 64; i++)
		{
			final boolean isBlack = ((blacks >> i) & 1L) != 0L;
			final boolean isWhite = ((whites >> i) & 1L) != 0L;
			if (isBlack || isWhite)
			{
				final boolean isKing = ((kings >> i) & 1L) != 0L;
				if (isBlack)
				{
					if (isKing)
						builder.append("B");
					else
						builder.append("b");
				}
				else
				{
					if (isKing)
						builder.append("W");
					else
						builder.append("w");
				}
			}
			else
			{
				builder.append(".");
			}

			if ((i + 1) % 8 == 0 && i != 63)
				builder.append("\n");
		}

		return builder.toString();
	}

	/**
	 * Computes the move actions for the current turn's player.
	 * 
	 * @return the valid move actions for all the current turn's player pieces
	 * @see MoveAction
	 */
	private List<MoveAction> generateMoves()
	{
		final List<MoveAction> moveActions = new ArrayList<>();
		final long empty = ~(blacks | whites) & MASK_VALID;
		if (isBlackTurn)
		{
			long remainingBlacks = blacks;
			while (remainingBlacks != 0)
			{
				// Find index of next black piece
				final int from = Long.numberOfTrailingZeros(remainingBlacks);
				final boolean isKing = ((kings >> from) & 1L) != 0L;
				final long fromPosition = 1L << from;

				// All black pawns can move SW, SE
				long moves = (fromPosition << SW_LSHIFT) | (fromPosition << SE_LSHIFT);

				// Only black kings can move NW, NE
				moves |= ((fromPosition & kings) >> NW_RSHIFT) | ((fromPosition & kings) >> NE_RSHIFT);

				// Filter invalid moves
				moves &= empty;

				while (moves != 0)
				{
					final int to = Long.numberOfTrailingZeros(moves);
					final long toPosition = 1L << to;
					moveActions.add(new MoveAction(from, to, true, isKing));
					moves &= ~toPosition;
				}

				// Unset bit
				remainingBlacks &= ~fromPosition;
			}
		}
		else
		{
			long remainingWhites = whites;
			while (remainingWhites != 0)
			{
				// Find index of next white piece
				final int from = Long.numberOfTrailingZeros(remainingWhites);
				final boolean isKing = ((kings >> from) & 1L) != 0L;
				final long fromPosition = 1L << from;

				// All white pawns can move NW, NE
				long moves = (fromPosition >> NW_RSHIFT) | (fromPosition >> NE_RSHIFT);

				// Only white kings can move SW, SE
				moves |= ((fromPosition & kings) << SW_LSHIFT) | ((fromPosition & kings) << SE_LSHIFT);

				// Filter invalid moves
				moves &= empty;

				while (moves != 0)
				{
					final int to = Long.numberOfTrailingZeros(moves);
					final long toPosition = 1L << to;
					moveActions.add(new MoveAction(from, to, false, isKing));
					moves &= ~toPosition;
				}

				// Unset bit
				remainingWhites &= ~fromPosition;
			}
		}

		return moveActions;
	}

	/**
	 * Computes the jump actions for the current turn's player.
	 * 
	 * @return the valid jump actions for all the current turn's player pieces
	 * @see ChainJumpAction
	 */
	private List<ChainJumpAction> generateJumps()
	{
		final List<ChainJumpAction> jumpActions = new ArrayList<>();
		final long empty = ~(blacks | whites) & MASK_VALID;
		if (isBlackTurn)
		{
			long remainingBlacks = blacks;
			while (remainingBlacks != 0)
			{
				// Find index of next black piece
				final int from = Long.numberOfTrailingZeros(remainingBlacks);
				final boolean isKing = ((kings >> from) & 1L) != 0L;
				final long fromPosition = 1L << from;

				// All black pawns can jump SW, SE
				long jumps = (((fromPosition << SW_LSHIFT) & whites) << SW_LSHIFT) | (((fromPosition << SE_LSHIFT) & whites) << SE_LSHIFT);

				// Only black kings can jump NW, NE
				jumps |= ((((fromPosition & kings) >> NW_RSHIFT) & whites) >> NW_RSHIFT) | ((((fromPosition & kings) >> NE_RSHIFT) & whites) >> NE_RSHIFT);

				// Filter invalid moves
				jumps &= empty;

				while (jumps != 0)
				{
					final int to = Long.numberOfTrailingZeros(jumps);
					final long toPosition = 1L << to;

					final ChainJumpAction chainJumpAction = new ChainJumpAction(new JumpAction(this, from, to, true, isKing));

					chainJumpAction.execute(this);
					findAllChains(jumpActions, chainJumpAction, to, isKing);
					chainJumpAction.undo(this);

					jumps &= ~toPosition;
				}

				// Unset bit
				remainingBlacks &= ~fromPosition;
			}
		}
		else
		{
			long remainingWhites = whites;
			while (remainingWhites != 0)
			{
				// Find index of next white piece
				final int from = Long.numberOfTrailingZeros(remainingWhites);
				final boolean isKing = ((kings >> from) & 1L) != 0L;
				final long fromPosition = 1L << from;

				// All white pawns can jump NW, NE
				long jumps = (((fromPosition >> NW_RSHIFT) & blacks) >> NW_RSHIFT) | (((fromPosition >> NE_RSHIFT) & blacks) >> NE_RSHIFT);

				// Only white kings can jump SW, SE
				jumps |= ((((fromPosition & kings) << SW_LSHIFT) & blacks) << SW_LSHIFT) | ((((fromPosition & kings) << SE_LSHIFT) & blacks) << SE_LSHIFT);

				// Filter invalid moves
				jumps &= empty;

				while (jumps != 0)
				{
					final int to = Long.numberOfTrailingZeros(jumps);
					final long toPosition = 1L << to;

					final ChainJumpAction chainJumpAction = new ChainJumpAction(new JumpAction(this, from, to, false, isKing));

					chainJumpAction.execute(this);
					findAllChains(jumpActions, chainJumpAction, to, isKing);
					chainJumpAction.undo(this);

					jumps &= ~toPosition;
				}

				// Unset bit
				remainingWhites &= ~fromPosition;
			}
		}

		if (jumpActions.size() > 0)
			jumpActions.sort(DESCENDING_CHAIN_CAPTURE_VALUE_COMPARATOR);

		return jumpActions;
	}

	/**
	 * A recursive way to determine all chain jumps from a given chain. It uses a Depth-First Search to find all chains, and adds them to the given list.
	 * 
	 * @param jumpActions      the list containing all jump actions generated by the DFS so far
	 * @param chain            the current jump chain
	 * @param from             the current position
	 * @param isKingOriginally whether the initial pawn was a king or not
	 * @see ChainJumpAction
	 */
	private void findAllChains(List<ChainJumpAction> jumpActions, ChainJumpAction chain, int from, boolean isKingOriginally)
	{
		final List<JumpAction> currentJumpActions = getJumpActionsAt(from, isKingOriginally);

		if (currentJumpActions == null || currentJumpActions.isEmpty())
		{
			jumpActions.add(chain);
			chain.updateCaptureValue();
			return;
		}

		int processedCount = 0;
		if (currentJumpActions != null)
		{
			final int size = currentJumpActions.size();
			for (int i = 0; i < size; i++)
			{
				final JumpAction jumpAction = currentJumpActions.get(i);
				// Ignore duplicates (forward/reversed jumps)
				if (chain.hasJump(jumpAction))
					continue;

				final ChainJumpAction chainCopy = chain.copy();
				chainCopy.chain.add(jumpAction);
				jumpAction.execute(this);
				findAllChains(jumpActions, chainCopy, jumpAction.to, isKingOriginally);
				jumpAction.undo(this);
				processedCount++;
			}
		}

		if (processedCount == 0)
		{
			jumpActions.add(chain);
			chain.updateCaptureValue();
		}
	}

	/**
	 * Computes the list of jump actions for the current turn's player at the specified board position.
	 * 
	 * @param from             the position to get jump actions at
	 * @param isKingOriginally whether the initial pawn was a king or not
	 * @return the list of jumps possible from a given board position, or empty if none, or null if pawn was crowned which terminates search
	 * @see JumpAction
	 */
	private List<JumpAction> getJumpActionsAt(int from, boolean isKingOriginally)
	{
		final boolean isKing = ((kings >> from) & 1L) != 0L;

		// Terminate the search when the pawn is crowned
		if (isKing && !isKingOriginally)
			return null;

		final long empty = ~(blacks | whites) & MASK_VALID;
		final long fromPosition = 1L << from;
		long jumps = 0;

		if (isBlackTurn)
		{
			// All black pawns can jump SW, SE
			jumps = (((fromPosition << SW_LSHIFT) & whites) << SW_LSHIFT) | (((fromPosition << SE_LSHIFT) & whites) << SE_LSHIFT);

			// Only black kings can jump NW, NE
			jumps |= ((((fromPosition & kings) >> NW_RSHIFT) & whites) >> NW_RSHIFT) | ((((fromPosition & kings) >> NE_RSHIFT) & whites) >> NE_RSHIFT);
		}
		else
		{
			// All white pawns can jump NW, NE
			jumps = (((fromPosition >> NW_RSHIFT) & blacks) >> NW_RSHIFT) | (((fromPosition >> NE_RSHIFT) & blacks) >> NE_RSHIFT);

			// Only white kings can jump SW, SE
			jumps |= ((((fromPosition & kings) << SW_LSHIFT) & blacks) << SW_LSHIFT) | ((((fromPosition & kings) << SE_LSHIFT) & blacks) << SE_LSHIFT);
		}

		// Filter invalid moves
		jumps &= empty;

		final List<JumpAction> currentJumpActions = new ArrayList<>();
		while (jumps != 0)
		{
			final int to = Long.numberOfTrailingZeros(jumps);
			final long toPosition = 1L << to;
			currentJumpActions.add(new JumpAction(this, from, to, isBlackTurn, isKing));
			jumps &= ~toPosition;
		}

		return currentJumpActions;
	}
}