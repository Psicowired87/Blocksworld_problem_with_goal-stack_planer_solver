package blocksworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Predicate
{
// Definition of the class predicate
/////////////////
// Inputs:
//	** type: type of the predicate: {on_table,on,picked_up,free,free_arm}
//	** block1:
//	** block2:
//	** value: in order to implement the heuristics
/////////////////
// Outputs:
//	** It outputs the predicate as a class
/////////////////
// Author: 'Toño G. Quintela' tgq.spm@gmail.com with the unpayable help of C.Levinas


	private PredicateName predicateName;
	private Block block1;
	private Block block2;
	private int value;

	public Predicate(PredicateName type, Block block1, Block block2, int value)
	{
		this.predicateName = type;
		this.block1 = block1;
		this.block2 = block2;
		this.value = value;
	}

	public PredicateName getType()
	{
		return this.predicateName;
	}
	
	public PredicateName getPredicateName()
	{
		return this.predicateName;
	}

	public void setPredicateName(PredicateName type)
	{
		this.predicateName = type;
	}

	public Block getBlock1()
	{
		return this.block1;
	}

	public void setBlock1(Block block1)
	{
		this.block1 = block1;
	}

	public Block getBlock2()
	{
		return this.block2;
	}

	public void setBlock2(Block block2)
	{
		this.block2 = block2;
	}

	public int getValue()
	{
		return this.value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	public boolean isInstantiated()
	{
		return (((this.block1 == null) || this.block1.isInstantiated()) && 
				((this.block2 == null) || this.block2.isInstantiated()));
	}
	
	public List<Operator> getOperatorsThatSolve(PredicatesList currentState, boolean mustMakeSpace)
	{
		ArrayList<Operator> operators = new ArrayList<Operator>();
		Operator operator;
		switch (this.predicateName)
		{
			case on_table:
				if (currentState.getNumColumns() < 3)
				{
					operator = new Operator(OperatorName.leave, this.block1, null);
					operators.add(operator);
				}
				break;
			case on:
				if (this.block1.getWeight() < this.block2.getWeight())
				{
					operator = new Operator(OperatorName.stack, this.block1, this.block2);
					operators.add(operator);
				}
				break;
			case picked_up:
				// Choose to pick_up or unstack, depending on the situation of block
				List<Predicate> onTablePredicates = currentState.getMatches(PredicateName.on_table);
				boolean blockOnTable = false;
				for (Predicate onTablePredicate : onTablePredicates)
				{
					if (onTablePredicate.block1.equals(this.block1))
					{
						blockOnTable = true;
						break;
					}
				}
				if (blockOnTable)
				{
					operator = new Operator(OperatorName.pick_up, this.block1, null);
					operators.add(operator);
				}
				else
				{
					operator = new Operator(OperatorName.unstack, this.block1, Planner.Y);
					operators.add(operator);
				}
				break;
			case free:
				operator = new Operator(OperatorName.unstack, Planner.X, this.block1);
				operators.add(operator);
				break;
			case free_arm:
				if ((currentState.getNumColumns() < 3) && !mustMakeSpace)
				{
					operator = new Operator(OperatorName.leave, Planner.X, null);
					operators.add(operator);
				}
				else
				{
					operator = new Operator(OperatorName.stack, Planner.X, Planner.Y);
					operators.add(operator);
				}
				break;
		}
		return operators;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Predicate other = (Predicate) obj;
		if (this.predicateName != other.predicateName)
		{
			return false;
		}
		if (!Objects.equals(this.block1, other.block1))
		{
			return false;
		}
		if (!Objects.equals(this.block2, other.block2))
		{
			return false;
		}
		if (this.value != other.value)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 83 * hash + (this.predicateName != null ? this.predicateName.hashCode() : 0);
		hash = 83 * hash + Objects.hashCode(this.block1);
		hash = 83 * hash + Objects.hashCode(this.block2);
		hash = 83 * hash + this.value;
		return hash;
	}
}
