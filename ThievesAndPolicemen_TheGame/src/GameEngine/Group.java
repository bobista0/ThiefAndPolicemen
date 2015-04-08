package GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class Group implements Callable<List<Integer>>
{
	MainGame mg;
	protected List<MainGame> currentGameList;

	public Group(MainGame mg)
	{
		this.mg = new MainGame();
		this.mg = mg;
		currentGameList = new ArrayList<MainGame>();
	}
	
	public void getHistory(List<MainGame> currentGameList)
	{
		this.currentGameList = currentGameList;
	}
	
	@Override
	public List<Integer> call() throws Exception
	{
		return null;
	}
}
