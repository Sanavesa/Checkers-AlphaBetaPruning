package checkers;

public enum GameState
{
	Ongoing, Draw, WhiteWin, BlackWin;

	public boolean isOver()
	{
		return this != Ongoing;
	}
}