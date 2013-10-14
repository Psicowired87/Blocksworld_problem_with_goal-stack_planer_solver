package blocksworld;

public class Operator
{
// Define the operator and aply to the 
/////////////////
// Inputs:
//	** operatorName: string of characters, the name of the type of the operator
//	** block1: class block
//	** block2: class block
/////////////////
// Outputs:
//	** It is applied a given operator to the present state of the system
/////////////////
// Author: 'Toño G. Quintela' tgq.spm@gmail.com with the unpayable help of C.Levinas


	private OperatorName operatorName;
	private Block block1;
	private Block block2;

	public Operator(OperatorName operatorName, Block block1, Block block2)
	{
		this.operatorName = operatorName;
		this.block1 = block1;
		this.block2 = block2;
	}

	public OperatorName getOperatorName()
	{
		return this.operatorName;
	}
	
	public OperatorName getInverseOperatorName()
	{
		switch (this.operatorName)
		{
			case leave:
				return OperatorName.pick_up;
			case pick_up:
				return OperatorName.leave;
			case stack:
				return OperatorName.unstack;
			case unstack:
				return OperatorName.stack;
		}
		return null;
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

	public boolean isInstantiated()
	{
		return this.block1.isInstantiated() && ((this.block2 == null) || this.block2.isInstantiated());
	}

	public PredicatesList getPreconditions()
	{
		// Heuristics. Predicates sorted to ensure best solving order.
		
		PredicatesList preconditions = new PredicatesList();
		Predicate predicate;
		switch (this.operatorName)
		{
			case pick_up:
				predicate = new Predicate(PredicateName.free_arm, null, null, 0);
				preconditions.add(predicate);
				predicate = new Predicate(PredicateName.on_table, this.block1, null, 0);
				preconditions.add(predicate);
				predicate = new Predicate(PredicateName.free, this.block1, null, 0);
				preconditions.add(predicate);
				break;
			case leave:
				predicate = new Predicate(PredicateName.picked_up, this.block1, null, 0);
				preconditions.add(predicate);
				break;
			case unstack:
				predicate = new Predicate(PredicateName.free_arm, null, null, 0);
				preconditions.add(predicate);
				predicate = new Predicate(PredicateName.on, this.block1, this.block2, 0);
				preconditions.add(predicate);
				predicate = new Predicate(PredicateName.free, this.block1, null, 0);
				preconditions.add(predicate);
				break;
			case stack:
				predicate = new Predicate(PredicateName.picked_up, this.block1, null, 0);
				preconditions.add(predicate);
				predicate = new Predicate(PredicateName.free, this.block2, null, 0);
				preconditions.add(predicate);
				break;
		}
		return preconditions;
	}

	public PredicatesList getAddPredicates()
	{
		PredicatesList addPredicates = new PredicatesList();
		Predicate predicate = null;
		switch (this.operatorName)
		{
			case pick_up:
				predicate = new Predicate(PredicateName.picked_up, this.block1, null, 0);
				addPredicates.add(predicate);
				break;
			case leave:
				predicate = new Predicate(PredicateName.free_arm, null, null, 0);
				addPredicates.add(predicate);
				predicate = new Predicate(PredicateName.on_table, this.block1, null, 0);
				addPredicates.add(predicate);
				break;
			case unstack:
				predicate = new Predicate(PredicateName.free, this.block2, null, 0);
				addPredicates.add(predicate);
				predicate = new Predicate(PredicateName.picked_up, this.block1, null, 0);
				addPredicates.add(predicate);
				break;
			case stack:
				predicate = new Predicate(PredicateName.free_arm, null, null, 0);
				addPredicates.add(predicate);
				predicate = new Predicate(PredicateName.on, block1, this.block2, 0);
				addPredicates.add(predicate);
				break;
		}
		return addPredicates;
	}

	public PredicatesList getRemovePredicates()
	{
		PredicatesList removePredicates = new PredicatesList();
		Predicate predicate = null;
		switch (this.operatorName)
		{
			case pick_up:
				predicate = new Predicate(PredicateName.free_arm, null, null, 0);
				removePredicates.add(predicate);
				predicate = new Predicate(PredicateName.on_table, this.block1, null, 0);
				removePredicates.add(predicate);
				break;
			case leave:
				predicate = new Predicate(PredicateName.picked_up, this.block1, null, 0);
				removePredicates.add(predicate);
				break;
			case unstack:
				predicate = new Predicate(PredicateName.free_arm, null, null, 0);
				removePredicates.add(predicate);
				predicate = new Predicate(PredicateName.on, this.block1, this.block2, 0);
				removePredicates.add(predicate);
				break;
			case stack:
				predicate = new Predicate(PredicateName.free, this.block2, null, 0);
				removePredicates.add(predicate);
				predicate = new Predicate(PredicateName.picked_up, this.block1, null, 0);
				removePredicates.add(predicate);
				break;
		}
		return removePredicates;
	}
}
