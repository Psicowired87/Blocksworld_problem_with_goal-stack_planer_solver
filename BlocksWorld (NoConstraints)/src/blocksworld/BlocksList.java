package blocksworld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BlocksList
{
// Definition of the class blockList which is a class of list of classes block
/////////////////
// Inputs:
//	** 
/////////////////
// Outputs:
//	** The definition of the new blockList
/////////////////
// Author: 'Toño G. Quintela' tgq.spm@gmail.com with the unaffordable help of C.Levinas


	private List<Block> blockList = new ArrayList<Block>();

	public void addAll(List<Block> blocks)
	{
		this.blockList.addAll(blocks);
		Collections.sort(this.blockList, new BlockComparator());
	}

	public void add(Block block)
	{
		this.blockList.add(block);
		Collections.sort(this.blockList, new BlockComparator());
	}
	
	public Block getLighter()
	{
		if (!this.blockList.isEmpty())
		{
			return this.blockList.get(0);
		}
		return null;
	}
	
	public Block getHeavier()
	{
		if (!this.blockList.isEmpty())
		{
			return this.blockList.get(this.blockList.size()-1);
		}
		return null;
	}
	
	public Block get(String blockName)
	{
		for (Block block : this.blockList)
		{
			if (block.getName().equalsIgnoreCase(blockName))
			{
				return block;
			}
		}
		return null;
	}
	
	public List<Block> getAll()
	{
		return this.blockList;
	}
	
	public void clear()
	{
		this.blockList.clear();
	}
	
	public int size()
	{
		return this.blockList.size();
	}
	
	public boolean isEmpty()
	{
		return this.blockList.isEmpty();
	}

	class BlockComparator implements Comparator<Block>
	{
		@Override
		public int compare(Block o1, Block o2)
		{
			return o1.getWeight() > o2.getWeight() ? 1 : -1;
		}
	}
}
