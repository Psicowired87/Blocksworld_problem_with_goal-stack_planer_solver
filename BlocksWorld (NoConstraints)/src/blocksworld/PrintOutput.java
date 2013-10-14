package blocksworld;

import java.util.List;

public class PrintOutput
{
// The print class in order to ouput the results
/////////////////
// Inputs:
//	** name: 
//	** block1:
//	** block2:
//	** value: 
/////////////////
// Outputs:
//	** Print in screen
/////////////////
// Author: 'Toño G. Quintela' tgq.spm@gmail.com with the unaffordable help of C.Levinas



	public static void Print(String name, Block block1, Block block2)
	{
		if (block1 == null)
		{
			System.out.printf(name);
			System.out.print("()");
		}
		else if (block2 == null)
		{
			System.out.printf(name);
			System.out.print("(");
			System.out.printf(block1.getName());
			System.out.print(")");
		}
		else
		{
			System.out.printf(name);
			System.out.print("(");
			System.out.printf(block1.getName());
			System.out.print(",");
			System.out.printf(block2.getName());
			System.out.print(")");
		}
	}
	
	public static void Print(List<?> items)
	{
		for (Object item : items)
		{
			if (item instanceof PredicatesList)
			{
				PredicatesList predicatesList = (PredicatesList)item;
				for (Predicate predicate : predicatesList.getAll())
				{
					Print(predicate.getPredicateName().toString(), predicate.getBlock1(), predicate.getBlock2());
					System.out.print(" ");
				}
			}
			else if (item instanceof Operator)
			{
				Operator operator = (Operator)item;
				Print(operator.getOperatorName().toString(), operator.getBlock1(), operator.getBlock2());
			}
			else if (item instanceof Predicate)
			{
				Predicate predicate = (Predicate)item;
				Print(predicate.getPredicateName().toString(), predicate.getBlock1(), predicate.getBlock2());
			}
			System.out.println();
		}
	}
}
