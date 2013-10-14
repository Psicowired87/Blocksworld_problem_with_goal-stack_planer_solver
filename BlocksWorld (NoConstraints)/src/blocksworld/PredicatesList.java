package blocksworld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PredicatesList
{
// Definition of the class PredicatesList which is a class of list of classes predicate
/////////////////
// Inputs:
//	** 
/////////////////
// Outputs:
//	** The definition of the class
/////////////////
// Author: 'Toño G. Quintela' tgq.spm@gmail.com with the unpayable help of C.Levinas



	private List<Predicate> predicateList;

	public PredicatesList()
	{
		this.predicateList = new ArrayList<Predicate>();
	}

	public PredicatesList(List<Predicate> predicateList)
	{
		this.predicateList = predicateList;
		Collections.sort(predicateList, new PredicateComparator());
	}
	
	public List<Predicate> getAll()
	{
		return this.predicateList;
	}
	
	public void add(Predicate predicate)
	{
		this.predicateList.add(predicate);
		Collections.sort(predicateList, new PredicateComparator());
	}

	public void remove(Predicate predicate)
	{
		this.predicateList.remove(predicate);
	}

	public boolean contains(PredicatesList target)
	{
		for (Predicate predicate : target.getAll())
		{
			if (!this.predicateList.contains(predicate))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean contains(Predicate target)
	{
		for (Predicate predicate : this.predicateList)
		{
			if (predicate.equals(target))
			{
				return true;
			}
		}
		return false;
	}

	public int size()
	{
		return this.predicateList.size();
	}

	public boolean isEmpty()
	{
		return this.predicateList.isEmpty();
	}
	
	public Instances instantiate(PredicatesList currentState, Block keepFree, Block dontStack)
	{
		boolean freeYcase = false;
		Instances instances = new Instances();
		for (Predicate predicate : this.predicateList)
		{
			switch (predicate.getPredicateName())
			{
				case free:
					// stack(X,Y) yields picked_up(X) free(Y), which is problematic because we can't solve Y.
					if ((predicate.getBlock1() != null) && predicate.getBlock1().equals(Planner.Y))
					{
						freeYcase = true;
					}
					break;
				case on_table:
					// No easy way to know which one to choose...
					break;
				case picked_up:
					if (!predicate.getBlock1().isInstantiated())
					{
						List<Predicate> pickedUpPredicates = currentState.getMatches(PredicateName.picked_up);
						if (pickedUpPredicates.size() == 1)
						{
							instances.instance1 = pickedUpPredicates.get(0).getBlock1();
						}
					}
					break;
				case on:
					if (!predicate.getBlock1().isInstantiated() || !predicate.getBlock2().isInstantiated())
					{
						List<Predicate> onPredicates = currentState.getMatches(PredicateName.on);
						for (Predicate onPredicate : onPredicates)
						{
							if ((predicate.getBlock1().equals(onPredicate.getBlock1())) && !predicate.getBlock2().isInstantiated())
							{
								instances.instance2 = onPredicate.getBlock2();
							}
							if ((predicate.getBlock2().equals(onPredicate.getBlock2())) && !predicate.getBlock1().isInstantiated())
							{
								instances.instance1 = onPredicate.getBlock1();
							}
						}
					}
					break;
				case free_arm:
					break;
			}
		}
		if (freeYcase && (instances.instance2 == null))
		{
			if (instances.instance1 == null)
			{
				return null; // This is utter failure!
			}
			List<Predicate> predicates = currentState.getMatches(PredicateName.free);
			List<Predicate> pickedup = currentState.getMatches(PredicateName.picked_up);
			if (!pickedup.isEmpty())
			{
				Predicate toRemove = null;
				for (Predicate predicate : predicates)
				{
					if (predicate.getBlock1().equals(pickedup.get(0).getBlock1()))
					{
						toRemove = predicate;
					}
				}
				if (toRemove != null)
				{
					predicates.remove(toRemove);
				}
			}
			BlocksList alternatives = new BlocksList();
			for (Predicate predicate : predicates)
			{
				if (predicate.getBlock1().equals(instances.instance1))
				{
					continue;
				}
				if (predicate.getBlock1().equals(keepFree))
				{
					continue;
				}
				if (predicate.getBlock1().equals(dontStack))
				{
					continue;
				}
				alternatives.add(predicate.getBlock1());
			}
			if (alternatives.isEmpty())
			{
				return null; // We could not resolve instance2
			}
			else
			{
				instances.instance2 = alternatives.getLighter();
				return instances;
			}
		}
		return instances;
	}
	
	public int getNumColumns()
	{
		int numColumns = 0;
		for (Predicate predicate : this.predicateList)
		{
			if (predicate.getPredicateName() == PredicateName.on_table)
			{
				numColumns++;
			}
		}
		return numColumns;
	}

	public List<Predicate> getMatches(PredicateName predicateName)
	{
		List<Predicate> onPredicates = new ArrayList<Predicate>();
		for (Predicate predicate : this.predicateList)
		{
			if (predicate.getPredicateName() == predicateName)
			{
				onPredicates.add(predicate);
			}
		}
		return onPredicates;
	}

	class PredicateComparator implements Comparator<Predicate>
	{
		@Override
		public int compare(Predicate o1, Predicate o2)
		{
			if ((o1.getPredicateName() == o2.getPredicateName()) && (o1.getPredicateName() == PredicateName.on))
			{
				// Heuristics. 'On' predicates sorted by weight so we solve heavier ones first.
				return o1.getBlock1().getWeight() > o2.getBlock1().getWeight() ? 1 : -1;
			}
			else
			{
				// Heuristics. Order predicates according to enum, which ensures best solving order.
				return o1.getPredicateName().compareTo(o2.getPredicateName());
			}
		}
	}
}
