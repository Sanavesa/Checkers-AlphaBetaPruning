package checkers;

public enum AgentType
{
	Single, Game;

	public static AgentType parse(String str)
	{
		if (str.equals("SINGLE"))
			return Single;
		else if (str.equals("GAME"))
			return Game;
		else
			return null;
	}
}