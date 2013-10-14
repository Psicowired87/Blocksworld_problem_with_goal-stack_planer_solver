package blocksworld;

import java.util.Scanner;

public class BlocksWorld
{
// It is the main of the solver
/////////////////
// Inputs:
//	** filename: string of characters, the name of the file to parse 
/////////////////
// Outputs:
//	** The plan to solve the problem
/////////////////
// Author: 'Toño G. Quintela' tgq.spm@gmail.com with the unpayable help of C.Levinas


	public static void main(String[] args)
	{
		System.out.println("Nombre del archivo:");
		Scanner scanner = new Scanner(System.in);
		String filename = scanner.nextLine();
		scanner.close();
		boolean success = Parser.Parse(filename);
		if (success)
		{
			PredicatesList initialState = Parser.getInitialState();
			PredicatesList goalState = Parser.getGoalState();
			Planner planner = new Planner(initialState, goalState);
			planner.solve();
		}
	}
}
