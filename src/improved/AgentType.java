package improved;

/**
 * The <code>AgentType</code> enum represents the type of problem the agent should solve in the homework, which influences the behavior of the agent.
 * 
 * @author Mohammad Alali
 * @see Agent
 */
public enum AgentType
{
	/**
	 * The agent only needs to compute a <b>valid</b> action for the given problem. The quality of the action is unrelated. Has the textual parsing representation <i>SINGLE</i>.
	 */
	Single("SINGLE"),

	/**
	 * The agent has to compute a <b>valid, high-quality</b> action for the given problem under strict time constraints. Has the textual parsing representation <i>GAME</i>.
	 */
	Game("GAME");

	/**
	 * The textual parsing representation of the <code>AgentType</code>.
	 */
	private final String parseText;

	/**
	 * Creates an <code>AgentType</code> enum with the specified parse text.
	 * 
	 * @param parseText the text that when parsed returns this <code>AgentType</code> value
	 */
	private AgentType(String parseText)
	{
		this.parseText = parseText;
	}

	/**
	 * Parses the given string into its given <code>AgentType</code> value. The only valid strings are <i>SINGLE</i> and <i>GAME</i>; it is cap-sensitive.
	 * 
	 * @param str string to parse
	 * @return the <code>AgentType</code> value, or null if invalid string
	 */
	public static AgentType parse(String str)
	{
		if (Game.parseText.equals(str))
			return Game;
		else if (Single.parseText.equals(str))
			return Single;
		else
			return null;
	}
}