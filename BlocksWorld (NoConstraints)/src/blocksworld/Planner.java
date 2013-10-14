package blocksworld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Planner
{
// This is the solver. It is implemented the goal stack planner into this class.
/////////////////
// Inputs:
//	** initialState: a PredicatesList which defines the initial state
//	** goalState: a PredicatesList which defines the state we want to arrive
/////////////////
// Outputs:
//	** It outputs the plan to solve the problem
/////////////////
// Author: 'Toño G. Quintela' tgq.spm@gmail.com with the unpayable help of C.Levinas


	public static Block X = new Block("X", 1);
	public static Block Y = new Block("Y", 1);
	private Stack<Object> stack;
	private List<Operator> plan;
	private PredicatesList initialState;
	private PredicatesList goalState;
	private PredicatesList currentState;
	private HashMap<Predicate, Integer> currentChoices = new HashMap<Predicate, Integer>();

	public Planner(PredicatesList initialState, PredicatesList goalState)
	{
		this.stack = new Stack<Object>();
		this.plan = new ArrayList<Operator>();
		this.initialState = initialState;
		this.currentState = this.initialState;
		this.goalState = goalState;
	}
	
	public void solve()
	{
		if ((this.initialState == null) || this.initialState.isEmpty())
		{
			System.out.println("No initial state!");
			return;
		}
		if ((this.goalState == null) || this.goalState.isEmpty())
		{
			System.out.println("No goal state!");
			return;
		}
		
		// Push goal state into the stack
		this.stack.push(this.goalState);
		
		// Loop until there are no more items in the stack
		while (!this.stack.empty())
		{
			// Get the topmost item
			Object item = this.stack.peek();
			
			// If item is a predicate list (preconditions)
			if (item instanceof PredicatesList)
			{
				PredicatesList state = (PredicatesList)item;
				
				// If the predicate list is completely fulfilled by the current state, pop it
				if (this.currentState.contains(state))
				{
					this.stack.pop();
				}			
				// Else, push predicates one by one into the stack
				else
				{
					List<Predicate> predicates = state.getAll();
					boolean stateChanged = false;
					for (Predicate predicate : predicates)
					{
						if (!this.currentState.contains(predicate))
						{
							this.stack.add(predicate);
							stateChanged = true;
						}
					}
					if (!stateChanged)
					{
						this.stack.pop();
					}
				}
			}
			// If item is an operator
			else if (item instanceof Operator)
			{
				Operator operator = (Operator)item;
				
				// Check if preconditions are fulfilled
				boolean fulfilled = true;
				PredicatesList preconditions = operator.getPreconditions();
				for (Predicate predicate : preconditions.getAll())
				{
					fulfilled &= this.currentState.contains(predicate);
				}
				
				// If preconditions are fulfilled, apply the operator, pop it from the stack, and add it to the plan
				if (fulfilled)
				{
					PredicatesList addPredicates = operator.getAddPredicates();
					for (Predicate add : addPredicates.getAll())
					{
						this.currentState.add(add);
					}
					PredicatesList removePredicates = operator.getRemovePredicates();
					for (Predicate remove : removePredicates.getAll())
					{
						this.currentState.remove(remove);
					}
					// Check that this operator is not pushing an identity operator into the plan
					Operator lastPlanOperator = getLastPlanOperator();
					if ((lastPlanOperator != null) && 
						(operator.getInverseOperatorName() == lastPlanOperator.getOperatorName()) &&
						operator.getBlock1().equals(lastPlanOperator.getBlock1()) &&
						((operator.getBlock2() == null) || (operator.getBlock2().equals(lastPlanOperator.getBlock2()))))
					{
						this.plan.remove(getLastPlanOperator());
						System.out.println("****************************************************************");
						PrintOutput.Print(this.plan);
					}
					else
					{
						this.plan.add(operator);
						System.out.println("****************************************************************");
						PrintOutput.Print(this.plan);
					}
					this.stack.pop();
				}			
				// Else, push the operator's preconditions into the stack
				else
				{
					this.stack.add(preconditions);
				}
			}
			// If item is a predicate
			else if (item instanceof Predicate)
			{
				Predicate predicate = (Predicate)item;

				// If predicate is not instantiated, must instantiate it
				if (!predicate.isInstantiated())
				{
					PredicatesList predicatesList = getFirstPredicatesList();
					if (predicatesList != null)
					{
						Block keepFree = null;
						Operator stackQueuedOperator = getMainStackQueuedOperator();
						if (stackQueuedOperator != null)
						{
							keepFree = stackQueuedOperator.getBlock2();
						}
						Block dontStack = null;
						Operator lastPlanOperator = getLastPlanOperator();
						if ((lastPlanOperator != null) && (lastPlanOperator.getOperatorName() == OperatorName.unstack))
						{
							dontStack = lastPlanOperator.getBlock2();
						}
						Instances instances = predicatesList.instantiate(this.currentState, keepFree, dontStack);
						if (instances == null)
						{
							// We could not instantiate our predicate. Our only solution is to undo what we did.
							instances = new Instances();
							instances.instance1 = getLastPlanOperator().getBlock1();
							instances.instance2 = getLastPlanOperator().getBlock2();
						}
						instantiate(instances);
					}
				}
				// Else, if prediate is fulfilled in current state, then pop it
				else if (this.currentState.contains(predicate))
				{
					this.stack.pop();
				}
				// Else, find the operator(s) that can fulfill the predicate and add it to the stack
				else 
				{
					List<Operator> operators = predicate.getOperatorsThatSolve(this.currentState);
					if (!operators.isEmpty())
					{
						Integer currentChoice = 0;
						if (operators.size() > 1)
						{
							Integer lastChoice = this.currentChoices.get(predicate);
							if (lastChoice == null)
							{
								lastChoice = -1;
							}
							currentChoice = ++lastChoice % operators.size();
							this.currentChoices.put(predicate, currentChoice);
						}
						this.stack.push(operators.get(currentChoice));
					}
					// We are constrained and can't find an operator that solves the precondition.
					else
					{
						System.out.println("Can't solve!");
						PrintOutput.Print(this.plan);
						return;
					}
				}
			}
			System.out.println("================================================================");
			List<Object> state = new ArrayList<Object>();
			state.add(this.currentState);
			PrintOutput.Print(state);
			System.out.println("----------------------------------------------------------------");
			PrintOutput.Print(this.stack);
		}
		PrintOutput.Print(this.plan);
	}
	
	private void instantiate(Instances instances)
	{
		ArrayList<Object> items = new ArrayList<Object>(this.stack);
		Collections.reverse(items);
		for (Object item : items)
		{
			if (item instanceof Operator)
			{
				Operator operator = (Operator)item;
				if (!operator.isInstantiated())
				{
					if (instances.instance1 != null)
					{
						if (operator.getBlock1().equals(Planner.X))
						{
							operator.setBlock1(instances.instance1);
						}
					}
					if (instances.instance2 != null)
					{
						if (operator.getBlock2().equals(Planner.Y))
						{
							operator.setBlock2(instances.instance2);
						}
					}
				}
			}
			else if (item instanceof Predicate)
			{
				Predicate predicate = (Predicate)item;
				if (!predicate.isInstantiated())
				{
					if (instances.instance1 != null)
					{
						if (predicate.getBlock1().equals(Planner.X))
						{
							predicate.setBlock1(instances.instance1);
						}
					}
					if (instances.instance2 != null)
					{
						if (predicate.getBlock1().equals(Planner.Y))
						{
							predicate.setBlock1(instances.instance2);
						}
						else if ((predicate.getBlock2() != null) && predicate.getBlock2().equals(Planner.Y))
						{
							predicate.setBlock2(instances.instance2);
						}
					}
				}
			}
		}
	}
	
	private PredicatesList getFirstPredicatesList()
	{
		ArrayList<Object> items = new ArrayList<Object>(this.stack);
		Collections.reverse(items);
		for (Object item : items)
		{
			if (item instanceof PredicatesList)
			{
				PredicatesList predicatesList = (PredicatesList)item;
				return predicatesList;
			}
		}
		return null;
	}
	
	private Operator getLastPlanOperator()
	{
		if (this.plan.size() > 0)
		{
			return this.plan.get(this.plan.size()-1);
		}
		return null;
	}
	
	private Operator getMainStackQueuedOperator()
	{
		for (Object item : this.stack)
		{
			if (item instanceof Operator)
			{
				Operator operator = (Operator)item;
				if (operator.getOperatorName() == OperatorName.stack)
				{
					return operator;
				}
				return null;
			}
		}
		return null;
	}
}
