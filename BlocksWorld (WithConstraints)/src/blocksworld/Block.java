package blocksworld;

import java.util.Objects;

public class Block
{
// Definition of the class block
/////////////////
// Inputs:
//	** name: String which defines the name of the block
//	** weigth: number which defines the weigth property of the block
/////////////////
// Outputs:
//	** The definition of the new block
/////////////////
// Author: 'Toño G. Quintela' tgq.spm@gmail.com with the unpayable help of C.Levinas




	private String name;
	private int weigth;

	public Block(String name, int weigth)
	{
		this.name = name;
		this.weigth = weigth;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getWeight()
	{
		return weigth;
	}

	public void setWeigth(int weigth)
	{
		this.weigth = weigth;
	}

	public boolean isInstantiated()
	{
		return (this != Planner.X) && (this != Planner.Y);
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
		final Block other = (Block) obj;
		if (!Objects.equals(this.name, other.name))
		{
			return false;
		}
		if (this.weigth != other.weigth)
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 37 * hash + Objects.hashCode(this.name);
		hash = 37 * hash + this.weigth;
		return hash;
	}
}
