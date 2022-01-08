package checkers;

import java.util.ArrayList;
import java.util.List;

public class ChainJumpAction extends BaseAction
{
	public final List<JumpAction> chain;

	private boolean actionExecuted;

	public ChainJumpAction()
	{
		super();
		chain = new ArrayList<>();
		actionExecuted = false;
	}

	public ChainJumpAction copy()
	{
		ChainJumpAction chainJumpAction = new ChainJumpAction();
		chainJumpAction.chain.addAll(chain);
		return chainJumpAction;
	}

	public boolean endsWith(JumpAction jumpAction)
	{
		if (chain.size() == 0 || jumpAction == null)
			return false;

		JumpAction lastJump = chain.get(chain.size() - 1);
		return lastJump.equals(jumpAction);
	}

	public boolean isValidAddition(JumpAction jumpAction)
	{
		if (chain.size() == 0)
			return true;

		if (jumpAction == null)
			return false;

		for (JumpAction jump : chain)
		{
			boolean matchForward = jump.rowFrom == jumpAction.rowFrom && jump.columnFrom == jumpAction.columnFrom && jump.rowTo == jumpAction.rowTo && jump.columnTo == jumpAction.columnTo;
			boolean matchReverse = jump.rowFrom == jumpAction.rowTo && jump.columnFrom == jumpAction.columnTo && jump.rowTo == jumpAction.rowFrom && jump.columnTo == jumpAction.columnFrom;
			if (matchForward || matchReverse)
				return false;
		}

		return true;
	}

	@Override
	public String getText()
	{
		StringBuilder builder = new StringBuilder();
		final int size = chain.size();
		for (int i = 0; i < size; i++)
		{
			JumpAction jumpAction = chain.get(i);
			builder.append(jumpAction.getText());
			if (i != size - 1)
				builder.append("\n");
		}
		return builder.toString();
	}

	@Override
	public String getDebugText()
	{
		StringBuilder builder = new StringBuilder("Chain (size = ").append(chain.size()).append(" jumps)");
		for (JumpAction jumpAction : chain)
			builder.append("\n\t").append(jumpAction.getDebugText());
		return builder.toString();
	}

	@Override
	public void execute(State state)
	{
//		if (!isValidAction(state))
//			return;

		if (actionExecuted)
		{
			error(state, "Cannot execute action as it has already been executed previously.");
			return;
		}

		for (JumpAction jumpAction : chain)
			jumpAction.execute(state);

		actionExecuted = true;
	}

	@Override
	public void undo(State state)
	{
		if (!actionExecuted)
		{
			error(state, "Cannot undo as the action was never executed.");
			return;
		}

		for (int i = chain.size() - 1; i >= 0; i--)
		{
			JumpAction jumpAction = chain.get(i);
			jumpAction.undo(state);
		}

		actionExecuted = false;
	}

	@Override
	public boolean isValidAction(State state)
	{
		if (chain.size() == 0)
		{
			error(state, "Chain jump has size of 0.");
			return false;
		}

		for (JumpAction jumpAction : chain)
			if (!jumpAction.isValidAction(state))
				return false;

		return true;
	}
}