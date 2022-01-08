package checkers;

public class Pawn
{
	public final Team team;
	public boolean isKing;

	public Pawn(boolean isKing, Team team)
	{
		this.isKing = isKing;
		this.team = team;
	}

	public char getBoardChar()
	{
		if (team == Team.Black)
		{
			if (isKing)
				return 'B';
			else
				return 'b';
		}
		else
		{
			if (isKing)
				return 'W';
			else
				return 'w';
		}
	}
}