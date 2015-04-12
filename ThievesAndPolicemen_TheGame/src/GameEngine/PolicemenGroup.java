package GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PolicemenGroup extends Group
{
	List<Policeman> PolicemenList;
	
	public PolicemenGroup(MainGame mg)
	{
		super(mg);
		PolicemenList = new ArrayList<Policeman>();
		
		for(Person p : mg.Persons.keySet())
		{
			if(p instanceof Policeman)
			{
				PolicemenList.add((Policeman)p);
			}
		}
	}
	
	@Override
	public List<Integer> call() throws Exception
	{
		Random random = new Random();
		int r;
		List<Integer> ListOfMoves = new ArrayList<Integer>();
		
		for(int i = 0; i < mg._numberOfPolicemen; i++)
		{			
			for(int j = 0; j < 2; j++)
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
			
			mg.set(PolicemenList.get(i), ListOfMoves);
			ListOfMoves.clear();
		}
		
		// Zwrócenie listy z kolejnymi k-ruchami
		return ListOfMoves;
	}
}
