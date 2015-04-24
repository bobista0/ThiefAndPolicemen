package GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ThiefGroup  extends Group
{
	List<Thief> ThiefList;
	
	public ThiefGroup(MainGame mg)
	{
		super(mg);
		ThiefList = new ArrayList<Thief>();
		
		for(Person t : mg.Persons.keySet())
		{
			if(t instanceof Thief)
			{
				ThiefList.add((Thief)t);
			}
		}
	}
	
	@Override
	public List<Integer> call() throws Exception
	{
		List<Integer> ListOfMoves = new ArrayList<Integer>();
		
		Random random = new Random();
		int r;
		for(int i = 0; i < 5; i++)
		{
			r = random.nextInt(5);
			
			switch(r)
			{
			case 0:
				ListOfMoves.add(MoveDirection.NONE.getValue()); //!!!!!!!!!!!!
				break;
				
			case 1:
				ListOfMoves.add(MoveDirection.LEFT.getValue());
				break;
				
			case 2:
				ListOfMoves.add(MoveDirection.UP.getValue());
				break;
				
			case 3:
				ListOfMoves.add(MoveDirection.RIGHT.getValue());
				break;
				
			case 4:
				ListOfMoves.add(MoveDirection.DOWN.getValue());
				break;
				
				default:
					break;
			}
		}
		
		mg.set(ThiefList.get(0), ListOfMoves);
		
		// Zwrócenie listy z kolejnymi k-ruchami
		return ListOfMoves;
	}
}
