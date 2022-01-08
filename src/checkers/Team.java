package checkers;

public enum Team
{
	Black, White;

	public Team opponent()
	{
		if (this == Black)
			return White;
		else
			return Black;
	}

	public static Team parse(String str)
	{
		if (str.equals("BLACK"))
			return Black;
		else if (str.equals("WHITE"))
			return White;
		else
			return null;
	}
}