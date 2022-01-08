package improved;

/**
 * The <code>JumpAction</code> class is an implementation for single-length jump in English Checkers. This is used by the <code>ChainJumpAction</code> class as part of a chained jump sequence.
 * 
 * @author Mohammad Alali
 * @see Action
 * @see ChainJumpAction
 */
public final class JumpAction implements Action
{
	/**
	 * The team of the jumping piece.
	 */
	public final boolean isBlack;

	/**
	 * The kingship status of the jumping piece.
	 */
	public final boolean isKing;

	/**
	 * The team of the captured piece.
	 */
	public final boolean isVictimBlack;

	/**
	 * The kingship status of the captured piece.
	 */
	public final boolean isVictimKing;

	/**
	 * The start location of the jumping piece.
	 */
	public final int from;

	/**
	 * The location of the captured piece. It is the midpoint of {@link #from} and {@link #to}.
	 */
	public final int victim;

	/**
	 * The destination location of the jumping piece.
	 */
	public final int to;

	/**
	 * A snapshot of whether the jumping piece was a king before this action. This is needed for rollback using {@link #undo(StateBoard)}.
	 */
	private boolean snapshotPawnWasKing;

	/**
	 * A snapshot of whether the captured piece was a king before this action. This is needed for rollback using {@link #undo(StateBoard)}.
	 */
	private boolean snapshotVictimWasKing;

	/**
	 * A snapshot of the number of plys for the draw condition. This is needed for rollback using {@link #undo(StateBoard)}.
	 */
	private int snapshotStatePlys;

	/**
	 * The bitboard position of the start position.
	 */
	private final long fromPosition;

	/**
	 * The bitboard position of the victim's position.
	 */
	private final long victimPosition;

	/**
	 * The bitboard position of the destination position.
	 */
	private final long toPosition;

	/**
	 * Creates a jump action with the specified arguments.
	 * 
	 * @param board   the current state of the board
	 * @param from    the start location of the jumping piece
	 * @param to      the destination location of the jumping piece
	 * @param isBlack the team of the jumping piece
	 * @param isKing  the kingship status of the jumping piece
	 */
	public JumpAction(StateBoard board, int from, int to, boolean isBlack, boolean isKing)
	{
		super();
		this.from = from;
		this.to = to;
		this.isBlack = isBlack;
		this.isKing = isKing;
		victim = (from + to) / 2;

		isVictimBlack = ((board.blacks >> victim) & 1L) != 0L;
		isVictimKing = ((board.kings >> victim) & 1L) != 0L;

		snapshotPawnWasKing = false;
		snapshotVictimWasKing = false;
		snapshotStatePlys = 0;

		fromPosition = 1L << from;
		victimPosition = 1L << victim;
		toPosition = 1L << to;
	}

	/**
	 * @return the textual representation of the jump action to be used for output
	 * @see Converter#jumpOutput(int, int, int, int)
	 */
	@Override
	public String getText()
	{
		final int rowFrom = from / 8;
		final int columnFrom = from % 8;
		final int rowTo = to / 8;
		final int columnTo = to % 8;
		return Converter.jumpOutput(rowFrom, columnFrom, rowTo, columnTo);
	}

	/**
	 * Executes the jump action on the specified board.
	 * 
	 * @param board the board to execute this jump action on
	 * @see StateBoard
	 */
	@Override
	public void execute(StateBoard board)
	{
		// Capture snapshot
		snapshotPawnWasKing = ((board.kings >> from) & 1L) != 0L;
		snapshotVictimWasKing = ((board.kings >> victim) & 1L) != 0L;
		snapshotStatePlys = board.plysSinceLastCrownOrCapture;

		// Clear king on all positions
		board.kings &= ~fromPosition;
		board.kings &= ~victimPosition;
		board.kings &= ~toPosition;

		// Move piece from->to, clearing victim
		// And crown if reached kings row
		boolean shouldCrown;
		if (isBlack)
		{
			board.blacks &= ~fromPosition;
			board.whites &= ~victimPosition;
			board.blacks |= toPosition;
			shouldCrown = (StateBoard.MASK_BLACK_KINGS_ROW & toPosition) != 0L;
		}
		else
		{
			board.whites &= ~fromPosition;
			board.blacks &= ~victimPosition;
			board.whites |= toPosition;
			shouldCrown = (StateBoard.MASK_WHITE_KINGS_ROW & toPosition) != 0L;
		}

		// Set king at destination if the jump was created for a king piece
		if (isKing || shouldCrown)
			board.kings |= toPosition;

		board.plysSinceLastCrownOrCapture = 0;
	}

	/**
	 * Undos the jump action's effect on the specified board. Also, {@link #undo(StateBoard)} will not fail if {@link #execute(StateBoard)} was not invoked beforehand.
	 * 
	 * @param board the board to undo this jump action's effect on
	 * @see StateBoard
	 */
	@Override
	public void undo(StateBoard board)
	{
		// Move piece to->from, setting victim
		if (isBlack)
		{
			board.blacks &= ~toPosition;
			board.whites |= victimPosition;
			board.blacks |= fromPosition;
		}
		else
		{
			board.whites &= ~toPosition;
			board.blacks |= victimPosition;
			board.whites |= fromPosition;
		}

		// Retrieve snapshot
		board.plysSinceLastCrownOrCapture = snapshotStatePlys;

		// Clear king on all positions
		board.kings &= ~fromPosition;
		board.kings &= ~victimPosition;
		board.kings &= ~toPosition;

		// Restore kingship
		if (snapshotPawnWasKing)
			board.kings |= fromPosition;

		// Restore victim kingship
		if (snapshotVictimWasKing)
			board.kings |= victimPosition;
	}
}