package improved;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>ChainJumpAction</code> class is an implementation for variable-length jump in English Checkers. This uses the single jump implementation in the <code>JumpAction</code> class as part of
 * the chained jump sequence.
 * 
 * @author Mohammad Alali
 * @see Action
 * @see JumpAction
 */
public final class ChainJumpAction implements Action
{
	/**
	 * The sequence of jumps in order of execution.
	 * 
	 * @see JumpAction
	 */
	public final List<JumpAction> chain;

	/**
	 * The total value of the captured pawns.
	 */
	public double captureValue;

	/**
	 * Creates a new chain jump action with the given <code>jumpAction</code> as its first jump.
	 * 
	 * @param jumpAction the first jump in the sequence.
	 */
	public ChainJumpAction(JumpAction jumpAction)
	{
		this();
		chain.add(jumpAction);
	}

	/**
	 * Creates a new chain jump action with an empty sequence of jumps.
	 */
	public ChainJumpAction()
	{
		chain = new ArrayList<>();
	}

	/**
	 * Performs a shallow copy of the instance.
	 * 
	 * @return shallow copy
	 */
	public ChainJumpAction copy()
	{
		final ChainJumpAction chainJumpAction = new ChainJumpAction();
		chainJumpAction.chain.addAll(chain);
		return chainJumpAction;
	}

	/**
	 * Updates {@link #captureValue} to the sum of the value of the captured pawns.
	 */
	public void updateCaptureValue()
	{
		captureValue = 0;
		final int size = chain.size();
		for (int i = 0; i < size; i++)
			captureValue += chain.get(i).isVictimKing ? Constants.EVAL_CAPTURE_KING_WEIGHT : Constants.EVAL_CAPTURE_PAWN_WEIGHT;
	}

	/**
	 * Checks whether the specified jump is already part of the chain. <b>This checks for both directions of the jump, forward and reverse. For example, J a1 b3 is equivalent to J b3 a1 in this method.
	 * 
	 * @param criteriaJumpAction the jump to check
	 * @return true if <code>criteriaJumpAction</code> or its reverse is in the chain, false otherwise
	 */
	public boolean hasJump(JumpAction criteriaJumpAction)
	{
		final int size = chain.size();
		for (int i = 0; i < size; i++)
		{
			final JumpAction jumpAction = chain.get(i);
			final boolean matchForward = jumpAction.from == criteriaJumpAction.from && jumpAction.to == criteriaJumpAction.to;
			final boolean matchReverse = jumpAction.from == criteriaJumpAction.to && jumpAction.to == criteriaJumpAction.from;
			if (matchForward || matchReverse)
				return true;
		}

		return false;
	}

	/**
	 * @return the textual representation of the entire jump action sequence to be used for output, separated by a newline
	 * @see JumpAction#getText()
	 */
	@Override
	public String getText()
	{
		final StringBuilder builder = new StringBuilder();
		final int size = chain.size();
		for (int i = 0; i < size; i++)
		{
			builder.append(chain.get(i).getText());
			if (i != size - 1)
				builder.append("\n");
		}
		return builder.toString();
	}

	/**
	 * Executes the jump action sequence on the specified board. This will execute the jumps in {@link #chain} from start to end.
	 * 
	 * @param board the board to execute this jump action sequence on
	 * @see StateBoard
	 */
	@Override
	public void execute(StateBoard board)
	{
		final int size = chain.size();
		for (int i = 0; i < size; i++)
			chain.get(i).execute(board);
	}

	/**
	 * Undos the chain jump action's effect on the specified board. Also, {@link #undo(StateBoard)} will not fail if {@link #execute(StateBoard)} was not invoked beforehand. This will undo the jumps in
	 * {@link #chain} from end to start.
	 * 
	 * @param board the board to undo this chain jump action's effect on
	 * @see StateBoard
	 */
	@Override
	public void undo(StateBoard board)
	{
		final int size = chain.size();
		for (int i = size - 1; i >= 0; i--)
			chain.get(i).undo(board);
	}
}