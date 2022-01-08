package checkers;

import java.io.FileReader;
import java.util.Scanner;

public class Problem
{
	public final AgentType agentType;
	public final Team team; // black top, white bottom
	public final double playTime; // seconds
	public final State state;

	// Board:
	// w for a grid cell occupied by a white regular piece
	// W for a grid cell occupied by a white king piece
	// b for a grid cell occupied by a black regular piece
	// B for a grid cell occupied by a black king piece
	// . (a dot) for an empty grid cell

	public Problem(String filename) throws Exception
	{
		try (Scanner scanner = new Scanner(new FileReader(filename)))
		{
			agentType = AgentType.parse(scanner.nextLine());
			team = Team.parse(scanner.nextLine());
			playTime = scanner.nextFloat();
			scanner.nextLine(); // Move to next line

			state = State.createEmpty();
			state.currentTurn = team;
			for (int row = 0; row < 8; row++)
			{
				String line = scanner.nextLine();
				for (int column = 0; column < 8; column++)
				{
					char c = line.charAt(column);
					Pawn pawn = null;

					switch (c)
					{
						case 'w':
							pawn = new Pawn(false, Team.White);
							break;
						case 'W':
							pawn = new Pawn(true, Team.White);
							break;
						case 'b':
							pawn = new Pawn(false, Team.Black);
							break;
						case 'B':
							pawn = new Pawn(true, Team.Black);
							break;
						default:
							break;
					}

					state.board[row][column] = pawn;
				}
			}
		}
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Problem");
		builder.append("\n\tAgent Type = ").append(agentType);
		builder.append("\n\tTeam = ").append(team);
		builder.append("\n\tPlay Time = ").append(playTime);
		builder.append("\n").append(state);
		return builder.toString();
	}
}