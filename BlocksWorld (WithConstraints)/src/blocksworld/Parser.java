package blocksworld;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class Parser
{
// Format a text file to java blocksworld problem
/////////////////
// Inputs:
//	** It will call the text file which describes the initial configuration
/////////////////
// Outputs:
//	** predicates: class of predicates which is a list of classes predicate
/////////////////
// Author: 'Toño G. Quintela' tgq.spm@gmail.com


	private static Properties props = new Properties();
	private static List<String> blockNames = new ArrayList<String>();
	private static BlocksList blockList;
	private static PredicatesList initialState;
	private static PredicatesList goalState;
	private static String[] predicateNames = new String[]
	{
		"ON-TABLE(", "ON(", "FREE(", "FREE-ARM", "PICKED-UP(", "HEAVIER("
	};

	public static boolean Parse(String filename)
	{
		File file = new File(filename);
		if (file.exists())
		{
			FileInputStream fis = null;
			try
			{
				fis = new FileInputStream(file);
				Parser.props.load(fis);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if (fis != null)
					{
						fis.close();
					}
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}

			// BLOCKS
			String blocks = Parser.props.getProperty("Blocks");
			Parser.blockNames = Arrays.asList(blocks.substring(0, blocks.length() - 1).split(","));

			// INITIAL STATE
			List<String> initialPredicateStrings = new ArrayList<String>();
			String initialStates = Parser.props.getProperty("Initial_state");
			initialPredicateStrings = Arrays.asList(initialStates.substring(0, initialStates.length() - 1).split(";"));
			solveWeights(initialPredicateStrings);
			List<Predicate> initialPredicateList = Parser.parse(initialPredicateStrings);
			Parser.initialState = new PredicatesList(initialPredicateList);

			// GOAL STATE
			List<String> goalPredicateStrings = new ArrayList<String>();
			String goalStates = Parser.props.getProperty("Goal_state");
			goalPredicateStrings = Arrays.asList(goalStates.substring(0, goalStates.length() - 1).split(";"));
			List<Predicate> goalPredicateList = Parser.parse(goalPredicateStrings);
			Parser.goalState = new PredicatesList(goalPredicateList);
			return true;
		}
		else
		{
			System.out.println("File not found!");
			return false;
		}
	}
	
	private static void solveWeights(List<String> predicateStrings)
	{
		HashMap<String, ArrayList<String>> heavyMap = new HashMap<String, ArrayList<String>>();
		int encounteredNumberOfHeavierPredicates = 0;
		
		// Fetch all heavier predicates from the file
		for (String predicateString : predicateStrings)
		{
			if (predicateString.contains(Parser.predicateNames[5]))
			{
				String block1 = predicateString.substring(predicateString.indexOf("(") + 1, predicateString.indexOf(","));
				String block2 = predicateString.substring(predicateString.indexOf(",") + 1, predicateString.indexOf(")"));
				ArrayList<String> lighter = heavyMap.get(block1);
				if (lighter == null)
				{
					lighter = new ArrayList<String>();
					heavyMap.put(block1, lighter);
				}
				if (!lighter.contains(block2))
				{
					lighter.add(block2);
				}
				encounteredNumberOfHeavierPredicates++;
			}
		}

		// Solve the weight relationships of the blocks
		int expectedNumberOfHeavierPredicates = (Parser.blockNames.size() * (Parser.blockNames.size()-1)) / 2;
		if (expectedNumberOfHeavierPredicates != encounteredNumberOfHeavierPredicates)
		{
			System.out.println(">>>>>>> Some weight predicates are missing!!");
		}
		Parser.blockList = new BlocksList();
		for (String blockName : Parser.blockNames)
		{
			int weight = 1;
			ArrayList<String> lighter = heavyMap.get(blockName);
			if (lighter != null)
			{
				weight = lighter.size()+1;
			}
			Block block = new Block(blockName, weight);
			Parser.blockList.add(block);
		}
	}
	
	private static List<Predicate> parse(List<String> predicateStrings)
	{
		List<Predicate> predicates = new ArrayList<Predicate>();

		// Build the usual planner predicate commands
		for (String predicateString : predicateStrings)
		{
			if (predicateString.contains(Parser.predicateNames[0]))
			{
				String block = predicateString.substring(predicateString.indexOf("(") + 1, predicateString.indexOf(")"));
				Predicate p = new Predicate(PredicateName.on_table, Parser.blockList.get(block), null, 0);
				predicates.add(p);
			}
			else if (predicateString.contains(Parser.predicateNames[1]))
			{
				String[] blockPair = new String[2];
				blockPair[0] = predicateString.substring(predicateString.indexOf("(") + 1, predicateString.indexOf(","));
				blockPair[1] = predicateString.substring(predicateString.indexOf(",") + 1, predicateString.indexOf(")"));
				Predicate p = new Predicate(PredicateName.on, Parser.blockList.get(blockPair[0]), Parser.blockList.get(blockPair[1]), 0);
				predicates.add(p);
			}
			else if (predicateString.contains(Parser.predicateNames[2]))
			{
				String block = predicateString.substring(predicateString.indexOf("(") + 1, predicateString.indexOf(")"));
				Predicate p = new Predicate(PredicateName.free, Parser.blockList.get(block), null, 0);
				predicates.add(p);
			}
			else if (predicateString.contains(Parser.predicateNames[3]))
			{
				Predicate p = new Predicate(PredicateName.free_arm, null, null, 0);
				predicates.add(p);
			}
			else if (predicateString.contains(Parser.predicateNames[4]))
			{
				String block = predicateString.substring(predicateString.indexOf("(") + 1, predicateString.indexOf(")"));
				Predicate p = new Predicate(PredicateName.picked_up, Parser.blockList.get(block), null, 0);
				predicates.add(p);
			}
		}
		return predicates;
	}
	
	public static BlocksList getBlocksList()
	{
		return Parser.blockList;
	}
	
	public static PredicatesList getInitialState()
	{
		return Parser.initialState;
	}

	public static PredicatesList getGoalState()
	{
		return Parser.goalState;
	}
}
