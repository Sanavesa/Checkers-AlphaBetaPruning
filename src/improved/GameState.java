package improved;
/**
 * The <code>GameState</code> enum represents the possible values for an English Checkers game states such as Draw, Win, or Lose.
 * 
 * @author Mohammad Alali
 * @see StateBoard
 */
public enum GameState
{
	/**
	 * The game is still ongoing and there is no clear outcome.
	 */
	Ongoing,

	/**
	 * The game has ended and is a draw. There is no winner.
	 */
	Draw,

	/**
	 * The game has ended and the Black team is the winner.
	 */
	BlackWin,

	/**
	 * The game has ended and the White team is the winner.
	 */
	WhiteWin;
}