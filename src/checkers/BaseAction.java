package checkers;

public abstract class BaseAction
{
	public abstract String getText();

	public abstract String getDebugText();

	public abstract void execute(State state);

	public abstract void undo(State state);

	public abstract boolean isValidAction(State state);

	// TODO: Remove me later
	@Override
	public String toString()
	{
		return getDebugText();
	}

	protected final int manhattan(int rowFrom, int columnFrom, int rowTo, int columnTo)
	{
		int deltaRow = Math.abs(rowFrom - rowTo);
		int deltaColumn = Math.abs(columnFrom - columnTo);
		return deltaRow + deltaColumn;
	}

	protected final void error(State state, String str)
	{
		System.err.println("[System] Error applying action: " + str + " [" + getDebugText() + "]");
		System.err.println(state);
		System.err.println();
		System.exit(-1);
	}
}