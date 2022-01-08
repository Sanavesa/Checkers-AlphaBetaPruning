package checkers.gui;

import java.io.FileWriter;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import checkers.Agent;
import checkers.BaseAction;
import checkers.ChainJumpAction;
import checkers.JumpAction;
import checkers.MoveAction;
import checkers.Pawn;
import checkers.Problem;
import checkers.State;
import checkers.StateActions;
import checkers.Stopwatch;
import checkers.Team;

public class Checker
{
	public static final Team PLAYER_TEAM = Team.White;
	public static final Team AGENT_TEAM = Team.Black;
	public static final ExecutorService service = Executors.newSingleThreadExecutor();
	public static double PLAYER_TIME = 100;
	public static double AGENT_TIME = 3;
	public static double AGENT_PADDING_TIME = 0.1;
	public static State state = State.createInitial();

	private static final String INPUT_FILENAME = "src/checkers/gui/input.txt";
	private static final String OUTPUT_FILENAME = "src/checkers/gui/output.txt";

	public static BaseAction createAction(int row, int column, int targetRow, int targetColumn)
	{
		StateActions stateActions = state.getActions();

		// Choose the longest jump that starts at row,column
		Optional<ChainJumpAction> bestJumpAction = stateActions.jumpActions.stream().filter(p ->
		{
			if (p.chain.size() == 0)
				return false;
			JumpAction jumpAction = p.chain.get(0);
			return jumpAction.rowFrom == row && jumpAction.columnFrom == column;
		}).max((a, b) -> Integer.compare(a.chain.size(), b.chain.size()));

		if (bestJumpAction.isPresent())
			return bestJumpAction.get();

		// Choose the move that starts at row,column and ends at targetRow,targetColumn
		Optional<MoveAction> bestMoveAction = stateActions.moveActions.stream().filter(p ->
		{
			return p.rowFrom == row && p.columnFrom == column && p.rowTo == targetRow && p.columnTo == targetColumn;
		}).findAny();

		if (bestMoveAction.isPresent())
			return bestMoveAction.get();

		System.err.println("You played a retarded move.");
		return null;
	}

	private static double elapsed = 0.0;
	private static final Stopwatch stopwatch2 = new Stopwatch();

	public static void agentPlay()
	{
		elapsed = 0.0;
		// Given by master agent
		createInputFile();

		Stopwatch stopwatch = new Stopwatch();

		// My thing
		final double readingProblemTime = 0.01;
		final Problem problem = readProblem();
		AGENT_TIME = problem.playTime - readingProblemTime;
		System.out.println("Before Remaining: " + AGENT_TIME);

		// My program starts here:
		final Callable<String> myProgram = () ->
		{
			stopwatch2.start(Thread.currentThread().getId());
			String output = Agent.solve(problem);
			state = problem.state;
			return output;
		};

		final long timeOut = (long) ((AGENT_TIME - AGENT_PADDING_TIME) * 1000); // ms, also keep a padding of 0.1sec
		Future<String> future = null;
		try
		{
			future = service.submit(myProgram);
			final String result = future.get(timeOut, TimeUnit.MILLISECONDS);

			try
			{
				System.out.println("[AI - " + AGENT_TEAM + "]:\n\t" + result.replaceAll("\n", "\n\t"));
				try (FileWriter writer = new FileWriter(OUTPUT_FILENAME))
				{
					writer.write(result);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (final TimeoutException e)
		{
			System.err.println("Time ran out");
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		finally
		{
			elapsed += stopwatch2.elapsedTime();
			if (future != null)
				future.cancel(true);
		}

		elapsed += stopwatch.elapsedTime();
		AGENT_TIME -= elapsed;
		System.out.println("After Remaining: " + AGENT_TIME);
		System.out.println("Elapsed: " + elapsed);
	}

	private static Problem readProblem()
	{
		try
		{
			return new Problem(INPUT_FILENAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static void createInputFile()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("GAME\n");
		builder.append(AGENT_TEAM.toString().toUpperCase() + "\n");
		builder.append(AGENT_TIME).append("\n");
		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				Pawn pawn = state.board[r][c];
				builder.append(pawn == null ? "." : pawn.getBoardChar());
			}

			if (r != 7)
				builder.append("\n");
		}

		try (FileWriter writer = new FileWriter(INPUT_FILENAME))
		{
			writer.write(builder.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

//	public static void agentPlay()
//	{
//		final long startTime = System.nanoTime();
//
//		String result = "";
//		final Future<String> futureTask = service.submit(() ->
//		{
//			createInputFile();
//			Problem problem = new Problem(INPUT_FILENAME);
//			AGENT_TIME = problem.playTime;
//			String output = Agent.solve(problem);
//			state = problem.state;
//			return output;
//		});
//		final long timeOut = (long) ((AGENT_TIME - AGENT_PADDING_TIME) * 1000); // ms, also keep a padding of 0.1sec
//
//		System.out.println("Remaining: " + AGENT_TIME);
//		System.out.println("Given: " + (timeOut / 1000.0));
//
//		try
//		{
//			result = futureTask.get(timeOut, TimeUnit.MILLISECONDS);
//		}
//		catch (final TimeoutException e)
//		{
//			System.err.println("Time ran out");
//		}
//		catch (InterruptedException e)
//		{
//			e.printStackTrace();
//		}
//		catch (ExecutionException e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			futureTask.cancel(true);
//		}
//
//		try
//		{
//			System.out.println("[AI - " + AGENT_TEAM + "]:\n\t" + result.replaceAll("\n", "\n\t"));
//			try (FileWriter writer = new FileWriter(OUTPUT_FILENAME))
//			{
//				writer.write(result);
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		final long endTime = System.nanoTime();
//		final double elapsed = (endTime - startTime) / 1_000_000_000.0;
//		AGENT_TIME -= elapsed;
//		System.out.println("After Remaining: " + AGENT_TIME);
//		System.out.println("Elapsed: " + elapsed);
//	}
}
