package improved;

/**
 * The <code>MoveAction</code> class is an implementation for moving a piece in English Checkers.
 * 
 * @author Mohammad Alali
 * @see Action
 */
public final class MoveAction implements Action
{
	/**
	 * The team of the piece being moved.
	 */
	public final boolean isBlack;

	/**
	 * The kingship status of the piece being moved.
	 */
	public final boolean isKing;

	/**
	 * The start location of the piece being moved.
	 */
	public final int from;

	/**
	 * The destination location of the piece being moved.
	 */
	public final int to;

	/**
	 * A snapshot of whether the piece was a king before this move. This is needed for rollback using {@link #undo(StateBoard)}.
	 */
	private boolean snapshotPawnWasKing;

	/**
	 * A snapshot of the number of plys for the draw condition. This is needed for rollback using {@link #undo(StateBoard)}.
	 */
	private int snapshotStatePlys;

	/**
	 * Creates a move action with the specified arguments.
	 * 
	 * @param from    the start location of the piece being moved
	 * @param to      the destination location of the piece being moved
	 * @param isBlack the team of the piece being moved
	 * @param isKing  the kingship status of the piece being moved
	 */
	public MoveAction(int from, int to, boolean isBlack, boolean isKing)
	{
		this.from = from;
		this.to = to;
		this.isBlack = isBlack;
		this.isKing = isKing;

		snapshotPawnWasKing = false;
		snapshotStatePlys = 0;
	}

	/**
	 * @return the textual representation of the move action to be used for output
	 * @see Converter#moveOutput(int, int, int, int)
	 */
	@Override
	public String getText()
	{
		final int rowFrom = from / 8;
		final int columnFrom = from % 8;
		final int rowTo = to / 8;
		final int columnTo = to % 8;
		return Converter.moveOutput(rowFrom, columnFrom, rowTo, columnTo);
	}

	/**
	 * Executes the move action on the specified board.
	 * 
	 * @param board the board to execute this move action on
	 * @see StateBoard
	 */
	@Override
	public void execute(StateBoard board)
	{
		final long fromPosition = 1L << from;
		final long toPosition = 1L << to;

		// Capture snapshot
		snapshotPawnWasKing = ((board.kings >> from) & 1L) != 0L;
		snapshotStatePlys = board.plysSinceLastCrownOrCapture;

		// Clear king on both positions
		board.kings &= ~fromPosition;
		board.kings &= ~toPosition;

		// Move piece from->to
		if (isBlack)
		{
			board.blacks &= ~fromPosition;
			board.blacks |= toPosition;
		}
		else
		{
			board.whites &= ~fromPosition;
			board.whites |= toPosition;
		}

		// Set king at destination if the move was created for a king piece
		if (isKing)
			board.kings |= toPosition;
		else
		{
			// Crown if reached kings row
			boolean shouldCrown;
			if (isBlack)
				shouldCrown = (StateBoard.MASK_BLACK_KINGS_ROW & toPosition) != 0;
			else
				shouldCrown = (StateBoard.MASK_WHITE_KINGS_ROW & toPosition) != 0;

			if (shouldCrown)
			{
				board.kings |= toPosition;
				board.plysSinceLastCrownOrCapture = 0;
			}
		}
	}

	/**
	 * Undos the move action's effect on the specified board. Also, {@link #undo(StateBoard)} will not fail if {@link #execute(StateBoard)} was not invoked beforehand.
	 * 
	 * @param board the board to undo this move action's effect on
	 * @see StateBoard
	 */
	@Override
	public void undo(StateBoard board)
	{
		final long fromPosition = 1L << from;
		final long toPosition = 1L << to;

		// Move piece to->from
		if (isBlack)
		{
			board.blacks &= ~toPosition;
			board.blacks |= fromPosition;
		}
		else
		{
			board.whites &= ~toPosition;
			board.whites |= fromPosition;
		}

		// Retrieve snapshot
		board.plysSinceLastCrownOrCapture = snapshotStatePlys;

		// Clear king on both positions
		board.kings &= ~fromPosition;
		board.kings &= ~toPosition;

		// Restore kingship
		if (snapshotPawnWasKing)
			board.kings |= fromPosition;
	}
}