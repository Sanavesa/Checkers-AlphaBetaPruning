package improved;

/**
 * The <code>Action</code> interface should be implemented by any class whose instances are intended to be an action for the game of English Checkers.
 * <p>
 * This interface is designed to streamline the actions for the game in a simple manner:
 * <ul>
 * <li>Executing the action via <code>execute(StateBoard)</code>.</li>
 * <li>Undoing the action via <code>undo(StateBoard)</code>.</li>
 * <li>Checking legality of an action via <code>isLegal(StateBoard)</code>.</li>
 * <li>Retrieving the action's textual representation via <code>getText()</code>.</li>
 * </ul>
 * 
 * @author Mohammad Alali
 * @see StateBoard
 */
public interface Action
{
	/**
	 * @return the textual representation of the action to be used for output
	 */
	public abstract String getText();

	/**
	 * Executes the action on the specified board.
	 * 
	 * @param board the board to execute this action on
	 * @see StateBoard
	 */
	public abstract void execute(StateBoard board);

	/**
	 * Undos the action's effect on the specified board. Also, {@link #undo(StateBoard)} will not fail if {@link #execute(StateBoard)} was not invoked beforehand.
	 * 
	 * @param board the board to undo this action's effect on
	 * @see StateBoard
	 */
	public abstract void undo(StateBoard board);
}