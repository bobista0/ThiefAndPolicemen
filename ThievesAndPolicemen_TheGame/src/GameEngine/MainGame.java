// 1. Odbicia furtek
// 2. Odbicia przeszkód
// 3. Zjadanie siê furtek

package GameEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.Future;

public class MainGame
{
	private Random random = new	Random(1410);
	public byte[][] _board = null;
	private static final byte _n = 20;
	public static final byte _ne = _n + 2;
	
	private static final byte _thief = 1;
	private static final byte _policeman = 2;
	private static final byte _obstacle = 3;
	private static final byte _wall = 4;
	private static final byte _gateway = 5;
	
	public static final byte _numberOfThieves = 1;
	public static final byte _numberOfPolicemen = 5;
	
	public Map<Person, List<Integer>> plans = new HashMap<Person, List<Integer>>();

	public HashMap<Integer, ElementPosition> GatewaysDict = new HashMap<Integer, ElementPosition>();
	public HashMap<Integer, ElementPosition> ObstaclesDict = new HashMap<Integer, ElementPosition>();
	public HashMap<Person, Position> PolicemenDict = new HashMap<Person, Position>();
	public HashMap<Person, Position> ThiefDict = new HashMap<Person, Position>();
	
	private static final byte _numberOfGateways = 2;
	private static final byte _lengthOfGateway = 2;
	
	private static final byte _numerOfWalls = 4;
	private static final byte _lengthOfWall = 4;
	
	//prawdopodobienstwo ruchu furtki
	private static final float _probabilityFR = 0.5f;//0.5f;
	//prawdopodobienstwo zmiany kierunku furtki
	private static final float _probabilityFZ = 0.01f;//0.01f;
	
	//prawdopodobienstwo ruchu sciany
	private static final float _probabilitySR = 0.75f;
	//prawdopodobienstwo zmiany kierunku sciany
	private static final float _probabilitySZ = 0.05f;
	
	HashMap<Person, Person> Persons;
	
	public MainGame()
	{	
		_board = new byte[_ne][_ne];
		
		for (int i = 0; i < _ne; i++)
		{
			_board[0][i] = _wall;
			_board[_ne - 1][i] = _wall;
		}
		for(int j = 1; j < _ne - 1; j++)
		{
			_board[j][0] = _wall;
			_board[j][_ne - 1] = _wall;
		}
		
		int VerticalOrHorizontalGateway;
		int PositionOfGateway;
		int SideOfGateway;
		
		for (int i = 0; i < _numberOfGateways; i++)
		{
			VerticalOrHorizontalGateway = random.nextInt(2); // pion(0) czy poziom(1)
			SideOfGateway = random.nextInt(2); // góra(0)-dó³(1) / lewo(0)-prawo(1) -- w zale¿noœci od poziom / pion
			PositionOfGateway = random.nextInt(_ne - _lengthOfGateway); // numer kolumny / wiersza
			
			ElementPosition GatewayObject = null;
			
			if(VerticalOrHorizontalGateway == 0)
			{
				if(SideOfGateway == 0)
				{
					GatewayObject = new ElementPosition((byte)PositionOfGateway, (byte)0, false);
					
					for(int j = 0; j < _lengthOfGateway; j++)
					{
						_board[PositionOfGateway + j][0] = _gateway;						
					}
				}
				else
				{
					GatewayObject = new ElementPosition((byte)PositionOfGateway, (byte)(_ne - 1), false);
					
					for(int j = 0; j < _lengthOfGateway; j++)
					{
						_board[PositionOfGateway + j][_ne - 1] = _gateway;						
					}
				}
			}
			else
			{
				if(SideOfGateway == 0)
				{
					GatewayObject = new ElementPosition((byte)0, (byte)PositionOfGateway, true);
					
					for(int j = 0; j < _lengthOfGateway; j++)
					{
						_board[0][PositionOfGateway + j] = _gateway;						
					}
				}
				else
				{
					GatewayObject = new ElementPosition((byte)(_ne - 1), (byte)PositionOfGateway, true);
					
					for(int j = 0; j < _lengthOfGateway; j++)
					{
						_board[_ne - 1][PositionOfGateway + j] = _gateway;						
					}
				}
			}
			
			GatewaysDict.put(i, GatewayObject);
		}
		
		
		for(int i = 0; i < _numerOfWalls; i++)
		{
			ElementPosition ObstacleObject = null;
			
			int tempValueOfOrientation = random.nextInt(2);
			//0 - poziom
			//1 - pion
			if (tempValueOfOrientation == 0)
			{
				int rowPositionOfWall = random.nextInt(_n - 1) + 1;
				int columnPositionOfWall = random.nextInt(_n -  _lengthOfWall + 1) + 1;
				
				ObstacleObject = new ElementPosition((byte)rowPositionOfWall, (byte)columnPositionOfWall, true);
				
				for(int j = 0; j < _lengthOfWall; j++)
				{
					_board[rowPositionOfWall][columnPositionOfWall + j] = _obstacle;
				}
			}
			else
			{
				int columnPositionOfWall = random.nextInt(_n - 1) + 1;
				int rowPositionOfWall = random.nextInt(_n -  _lengthOfWall + 1) + 1;
				
				ObstacleObject = new ElementPosition((byte)rowPositionOfWall, (byte)columnPositionOfWall, false);
				
				for(int j = 0; j < _lengthOfWall; j++)
				{
					_board[rowPositionOfWall + j][columnPositionOfWall] = _obstacle;
				}
			}	
			
			ObstaclesDict.put(i, ObstacleObject);
		}
		
		Persons = new HashMap<Person, Person>();
		
		
		
		
		for(int i = 0; i < _numberOfPolicemen; i++)
		{
			Position PolicemanObject = null;
			Policeman p = new Policeman();
			Persons.put(p, p);
			boolean flagPoliceman = false;
			while(flagPoliceman == false)
			{
				int rowPositionOfPoliceman = random.nextInt(_n - 1) + 1;
				int columnPositionOfPoliceman = random.nextInt(_n - 1) + 1;
				
				if(_board[rowPositionOfPoliceman][columnPositionOfPoliceman] == 0)
				{
					PolicemanObject = new Position((byte)rowPositionOfPoliceman, (byte)columnPositionOfPoliceman);
					
					_board[rowPositionOfPoliceman][columnPositionOfPoliceman] = _policeman;
					flagPoliceman = true;
				}
			}
			
			PolicemenDict.put(p, PolicemanObject);
		}
		
		Position ThiefObject = null;
		Thief t = new Thief();
		Persons.put(t, t);
		boolean flagThiev = false;
		while(flagThiev == false)
		{
			int rowPositionOfThief = random.nextInt(_n - 1) + 1;
			int columnPositionOfThief = random.nextInt(_n - 1) + 1;
			if(_board[rowPositionOfThief][columnPositionOfThief] == 0)
			{
				ThiefObject = new Position((byte)rowPositionOfThief, (byte)columnPositionOfThief);
				
				_board[rowPositionOfThief][columnPositionOfThief] = _thief;
				flagThiev = true;
			}
		}
		ThiefDict.put(t, ThiefObject);
		
	}
	
	@SuppressWarnings("unchecked")
	public MainGame(MainGame copy)
	{
		HashMap<Integer, ElementPosition> copyGatewayDict = new HashMap<Integer, ElementPosition>();
		for(Entry<Integer, ElementPosition> entry : copy.GatewaysDict.entrySet())
		{
			copyGatewayDict.put(entry.getKey(), new ElementPosition(entry.getValue()));
		}
		GatewaysDict = copyGatewayDict;
		
		HashMap<Integer, ElementPosition> copyObstaclesDict = new HashMap<Integer, ElementPosition>();
		for(Entry<Integer, ElementPosition> entry : copy.ObstaclesDict.entrySet())
		{
			copyObstaclesDict.put(entry.getKey(), new ElementPosition(entry.getValue()));
		}
		ObstaclesDict = copyObstaclesDict;
		
		HashMap<Person, Position> copyPolicemenDict = new HashMap<Person, Position>();
		for(Entry<Person, Position> entry : copy.PolicemenDict.entrySet())
		{
			copyPolicemenDict.put(entry.getKey(), new Position(entry.getValue()));
		}
		PolicemenDict = copyPolicemenDict;
		
		HashMap<Person, Position> copyThiefDict = new HashMap<Person, Position>();
		for(Entry<Person, Position> entry : copy.ThiefDict.entrySet())
		{
			copyThiefDict.put(entry.getKey(), new Position(entry.getValue()));
		}
		ThiefDict = copyThiefDict;
	}
	
	public boolean CheckIfCaught()
	{
		boolean IsCaught = false;
		
		for(Person p : PolicemenDict.keySet())
		{
			for(Person t : ThiefDict.keySet())
			if((PolicemenDict.get(p).x == ThiefDict.get(t).x && PolicemenDict.get(p).y - 1 == ThiefDict.get(t).y)
			|| (PolicemenDict.get(p).x - 1 == ThiefDict.get(t).x && PolicemenDict.get(p).y == ThiefDict.get(t).y)
			|| (PolicemenDict.get(p).x == ThiefDict.get(t).x && PolicemenDict.get(p).y + 1 == ThiefDict.get(t).y)
			|| (PolicemenDict.get(p).x + 1 == ThiefDict.get(t).x && PolicemenDict.get(p).y == ThiefDict.get(t).y))
			{
				IsCaught = true;
				break;
			}
		}
		
		return IsCaught;
	}
	
	public boolean CheckIfEscaped()
	{
		boolean IsEscaped = false;
		
		for(int i = 0; i < _numberOfGateways; i++)
		{
			if(GatewaysDict.get(i).IsHorizontalOrIsVertical == true)
			{
				for(int j = 0; j < _lengthOfGateway; j++) 
				{
					for(Person t : ThiefDict.keySet())
					if(_board[GatewaysDict.get(i).x][GatewaysDict.get(i).y + j] == _board[ThiefDict.get(t).x][ThiefDict.get(t).y])
					{
						IsEscaped = true;
						return IsEscaped;
					}
				}
			}
			else
			{
				for(int j = 0; j < _lengthOfGateway; j++) 
				{
					for(Person t : ThiefDict.keySet())
					if(_board[GatewaysDict.get(i).x + j][GatewaysDict.get(i).y] == _board[ThiefDict.get(t).x][ThiefDict.get(t).y])
					{
						IsEscaped = true;
						return IsEscaped;
					}
				}
			}
		}
		
		return IsEscaped;
	}
	
	public void ElementMovement()
	{
		
		double RandomValue;
		boolean RandomBool;
		for(int i = 0; i < _numberOfGateways; i++)
		{
			RandomValue = random.nextDouble();
			
			
			// wtedy sie rusza
			if(RandomValue > _probabilityFR)
			{
				RandomValue = random.nextDouble();
				
				if(GatewaysDict.get(i).ClockwiseOrAntiClockwise == true) // 
				{
					if(RandomValue > _probabilityFZ) // nie zmienia - idzie zgodnie ze wskazówkami
					{
						if (GatewaysDict.get(i).IsHorizontalOrIsVertical == true) // furtka pozioma
						{
							if(GatewaysDict.get(i).x == 0)
							{
								if((GatewaysDict.get(i).y) == (_ne - _lengthOfGateway))
								{
									GatewaysDict.get(i).y = _ne - 1;
									GatewaysDict.get(i).IsHorizontalOrIsVertical = false;
								}
								else
									GatewaysDict.get(i).y++;
							}
							else
							{
								if((GatewaysDict.get(i).y) == 0)
								{
									GatewaysDict.get(i).x = _ne - _lengthOfGateway;
									GatewaysDict.get(i).IsHorizontalOrIsVertical = false;
								}
								else
									GatewaysDict.get(i).y--;
							}
						}
						else // furtka pionowa
						{
							if(GatewaysDict.get(i).y == 0)
							{
								if((GatewaysDict.get(i).x) == 0)
									GatewaysDict.get(i).IsHorizontalOrIsVertical = true;
								else
									GatewaysDict.get(i).x--;
							}
							else
							{
								if((GatewaysDict.get(i).x) == (_ne - _lengthOfGateway))
								{
									GatewaysDict.get(i).x = _ne - 1;
									GatewaysDict.get(i).y = _ne - _lengthOfGateway;
									GatewaysDict.get(i).IsHorizontalOrIsVertical = true;
								}
								else
									GatewaysDict.get(i).x++;
							}
						}
								
					}
					else // zmieni - idzie przeciwnie do wskazówek
						GatewaysDict.get(i).ClockwiseOrAntiClockwise = false;
				}
				else
				{
					if(RandomValue > _probabilityFZ) // nie zmienia - idzie przeciwnie do wskazówek
					{
						if (GatewaysDict.get(i).IsHorizontalOrIsVertical == true) // furtka pozioma
						{
							if(GatewaysDict.get(i).x == 0)
							{
								if((GatewaysDict.get(i).y) == 0)
									GatewaysDict.get(i).IsHorizontalOrIsVertical = false;
								else
									GatewaysDict.get(i).y--;
							}
							else
							{
								if((GatewaysDict.get(i).y) == (_ne - _lengthOfGateway))
								{
									GatewaysDict.get(i).x = _ne - _lengthOfGateway;
									GatewaysDict.get(i).y = _ne - 1;
									GatewaysDict.get(i).IsHorizontalOrIsVertical = false;
								}
								else
									GatewaysDict.get(i).y++;
							}
						}
						else // furtka pionowa
						{
							if(GatewaysDict.get(i).y == 0)
							{
								if((GatewaysDict.get(i).x) == (_ne - _lengthOfGateway))
								{
									GatewaysDict.get(i).x = _ne - 1;
									GatewaysDict.get(i).IsHorizontalOrIsVertical = true;
								}
								else
									GatewaysDict.get(i).x++;
							}
							else
							{
								if((GatewaysDict.get(i).x) == 0)
								{
									GatewaysDict.get(i).y = _ne - _lengthOfGateway;
									GatewaysDict.get(i).IsHorizontalOrIsVertical = true;
								}
								else
									GatewaysDict.get(i).x--;
							}
						}
								
					}
					else // zmieni - idzie zgodnie ze wskazówkami
						GatewaysDict.get(i).ClockwiseOrAntiClockwise = true;
				}
			}
		}

		for(int i = 0; i < _numerOfWalls; i++)
		{
			RandomValue = random.nextDouble();
			//System.out.println(RandomValue);
			
			// wtedy sie rusza
			if(RandomValue > _probabilitySR)
			{
				RandomBool = random.nextBoolean();
				
				if(ObstaclesDict.get(i).ClockwiseOrAntiClockwise == true) // jesli klocek porusza siê w prawo albo w górê
				{
					if(RandomValue > _probabilitySZ) // nie zmienia
					{
						if(ObstaclesDict.get(i).IsHorizontalOrIsVertical == true) // poziomy klocek
						{
							if(RandomBool == true) // poziomy klocek - ruch w prawo
							{
								if((ObstaclesDict.get(i).y) == (_n - _lengthOfWall + 1)) // Idzie w lewo bo odbija
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x && PolicemenDict.get(p).y == ObstaclesDict.get(i).y - 1)
											canMove = false;
									}
									
									for(Person t : ThiefDict.keySet())
									if(ThiefDict.get(t).x == ObstaclesDict.get(i).x && ThiefDict.get(t).y == ObstaclesDict.get(i).y - 1)
										canMove = false;
									
									if(canMove)
										ObstaclesDict.get(i).y--;
									
									ObstaclesDict.get(i).ClockwiseOrAntiClockwise = false;
								}
								else // idzie normalnie w prawo
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x && PolicemenDict.get(p).y == ObstaclesDict.get(i).y + _lengthOfWall)
											canMove = false;
									}
									
									for(Person t : ThiefDict.keySet())
									if(ThiefDict.get(t).x == ObstaclesDict.get(i).x && ThiefDict.get(t).y == ObstaclesDict.get(i).y + _lengthOfWall)
										canMove = false;
									
									if(canMove)
										ObstaclesDict.get(i).y++;
								}
							}
							else // poziomy klocek - ruch w górê
							{
								if((ObstaclesDict.get(i).x) == 1) // Idzie w dó³ bo odbija
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										for(int k = 0; k < _lengthOfWall; k++)
										{
											if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x + 1 && PolicemenDict.get(p).y == ObstaclesDict.get(i).y + k)
											{
												canMove = false;
												break;
											}
										}
									}

									for(Person t : ThiefDict.keySet())
									for(int k = 0; k < _lengthOfWall; k++)
									{
										if(ThiefDict.get(t).x == ObstaclesDict.get(i).x + 1 && ThiefDict.get(t).y == ObstaclesDict.get(i).y + k)
										{
											canMove = false;
											break;
										}
									}
									
									if(canMove)
										ObstaclesDict.get(i).x++;
									
									ObstaclesDict.get(i).ClockwiseOrAntiClockwise = false;
								}
								else // idzie normalnie w górê
								{
									boolean canMove = true;
									//DAC FORA po PERSON 
									for(Person p : PolicemenDict.keySet())
									{
										for(int k = 0; k < _lengthOfWall; k++)
										{
											if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x - 1 && PolicemenDict.get(p).y == ObstaclesDict.get(i).y + k)
											{
												canMove = false;
												break;
											}
										}
									}
									
									for(Person t : ThiefDict.keySet())
									{
										for(int k = 0; k < _lengthOfWall; k++)
										{
											if(ThiefDict.get(t).x == ObstaclesDict.get(i).x - 1 && ThiefDict.get(t).y == ObstaclesDict.get(i).y + k)
											{
												canMove = false;
												break;
											}
										}
									}
									
									if(canMove)
										ObstaclesDict.get(i).x--;
								}
							}
						}
						else // pionowy klocek ///////////////////////////////////////////////////////////////////////////////////////////
						{
							if(RandomBool == true) // pionowy klocek - ruch w prawo
							{
								if((ObstaclesDict.get(i).y) == (_n - 1)) // Idzie w lewo bo odbija
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										for(int k = 0; k < _lengthOfWall; k++)
										{
											if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x + k && PolicemenDict.get(p).y == ObstaclesDict.get(i).y - 1)
											{
												canMove = false;
												break;
											}
										}
									}

									for(Person t : ThiefDict.keySet())
										for(int k = 0; k < _lengthOfWall; k++)
										{
											if(ThiefDict.get(t).x == ObstaclesDict.get(i).x + k && ThiefDict.get(t).y == ObstaclesDict.get(i).y - 1)
											{
												canMove = false;
												break;
											}
										}
									
									if(canMove)
										ObstaclesDict.get(i).y--;
									
									ObstaclesDict.get(i).ClockwiseOrAntiClockwise = false;
								}
								else // idzie normalnie w prawo
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										for(int k = 0; k < _lengthOfWall; k++)
										{
											if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x + k && PolicemenDict.get(p).y == ObstaclesDict.get(i).y + 1)
											{
												canMove = false;
												break;
											}
										}
									}

									for(Person t : ThiefDict.keySet())
										for(int k = 0; k < _lengthOfWall; k++)
										{
											if(ThiefDict.get(t).x == ObstaclesDict.get(i).x + k && ThiefDict.get(t).y == ObstaclesDict.get(i).y + 1)
											{
												canMove = false;
												break;
											}
										}
									
									if(canMove)
										ObstaclesDict.get(i).y++;
								}
							}
							else // pionowy klocek - ruch w górê
							{
								if((ObstaclesDict.get(i).x) == 1) // Idzie w dó³ bo odbija
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x + _lengthOfWall && PolicemenDict.get(p).y == ObstaclesDict.get(i).y)
											canMove = false;
									}
									
									for(Person t : ThiefDict.keySet())
									if(ThiefDict.get(t).x == ObstaclesDict.get(i).x + _lengthOfWall && ThiefDict.get(t).y == ObstaclesDict.get(i).y)
										canMove = false;
									
									if(canMove)
										ObstaclesDict.get(i).x++;
									
									ObstaclesDict.get(i).ClockwiseOrAntiClockwise = false;
								}
								else // Idzie w normalnie w górê
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x - 1 && PolicemenDict.get(p).y == ObstaclesDict.get(i).y)
											canMove = false;
									}
									
									for(Person t : ThiefDict.keySet())
									if(ThiefDict.get(t).x == ObstaclesDict.get(i).x - 1 && ThiefDict.get(t).y == ObstaclesDict.get(i).y)
										canMove = false;
									
									if(canMove)
										ObstaclesDict.get(i).x--;
								}
							}
						}
					}
					else // zmienia
					{
						if(GatewaysDict.get(i).ClockwiseOrAntiClockwise == true)
							GatewaysDict.get(i).ClockwiseOrAntiClockwise = false;
						else
							GatewaysDict.get(i).ClockwiseOrAntiClockwise = true;
					}
				}
				else // jesli klocek porusza siê w lewo albo w dó³  ///////////////////////////////////////////////////////////////////////////////////////////
				{
					if(RandomValue > _probabilitySZ) // nie zmienia
					{
						if(ObstaclesDict.get(i).IsHorizontalOrIsVertical == true) // poziomy klocek
						{
							if(RandomBool == true) // poziomy klocek - ruch w lewo
							{
								if((ObstaclesDict.get(i).y) == 1) // Idzie w prawo bo odbija
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x && PolicemenDict.get(p).y == ObstaclesDict.get(i).y + _lengthOfWall)
											canMove = false;
									}
									
									for(Person t : ThiefDict.keySet())
									if(ThiefDict.get(t).x == ObstaclesDict.get(i).x && ThiefDict.get(t).y == ObstaclesDict.get(i).y + _lengthOfWall)
										canMove = false;
									
									if(canMove)
										ObstaclesDict.get(i).y++;
									
									ObstaclesDict.get(i).ClockwiseOrAntiClockwise = true;
								}
								else // idzie normalnie w lewo
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x && PolicemenDict.get(p).y == ObstaclesDict.get(i).y - 1)
											canMove = false;
									}
									
									for(Person t : ThiefDict.keySet())
									if(ThiefDict.get(t).x == ObstaclesDict.get(i).x && ThiefDict.get(t).y == ObstaclesDict.get(i).y - 1)
										canMove = false;
									
									if(canMove)
										ObstaclesDict.get(i).y--;
								}
							}
							else // poziomy klocek - ruch w dó³
							{
								if((ObstaclesDict.get(i).x) == (_n - 1)) // Idzie w górê bo odbija
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										for(int k = 0; k < _lengthOfWall; k++)
										{
											if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x - 1 && PolicemenDict.get(p).y == ObstaclesDict.get(i).y + k)
											{
												canMove = false;
												break;
											}
										}
									}

									for(Person t : ThiefDict.keySet())
									for(int k = 0; k < _lengthOfWall; k++)
									{
										if(ThiefDict.get(t).x == ObstaclesDict.get(i).x - 1 && ThiefDict.get(t).y == ObstaclesDict.get(i).y + k)
										{
											canMove = false;
											break;
										}
									}
									
									if(canMove)
										ObstaclesDict.get(i).x--;
									
									ObstaclesDict.get(i).ClockwiseOrAntiClockwise = true;
								}
								else // idzie normalnie w dó³
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										for(int k = 0; k < _lengthOfWall; k++)
										{
											if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x + 1 && PolicemenDict.get(p).y == ObstaclesDict.get(i).y + k)
											{
												canMove = false;
												break;
											}
										}
									}

								for(Person t : ThiefDict.keySet())
									for(int k = 0; k < _lengthOfWall; k++)
									{
										if(ThiefDict.get(t).x == ObstaclesDict.get(i).x + 1 && ThiefDict.get(t).y == ObstaclesDict.get(i).y + k)
										{
											canMove = false;
											break;
										}
									}
									
									if(canMove)
										ObstaclesDict.get(i).x++;
							
							}
						}
						}
						else // pionowy klocek
						{
							if(RandomBool == true) // pionowy klocek - ruch w lewo
							{
								if((ObstaclesDict.get(i).y) == 1) // Idzie w prawo bo odbija
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										for(int k = 0; k < _lengthOfWall; k++)
										{
											if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x + k && PolicemenDict.get(p).y == ObstaclesDict.get(i).y + 1)
											{
												canMove = false;
												break;
											}
										}
									}

									for(Person t : ThiefDict.keySet())
									for(int k = 0; k < _lengthOfWall; k++)
									{
										if(ThiefDict.get(t).x == ObstaclesDict.get(i).x + k && ThiefDict.get(t).y == ObstaclesDict.get(i).y + 1)
										{
											canMove = false;
											break;
										}
									}
									
									if(canMove)
										ObstaclesDict.get(i).y++;
									
									ObstaclesDict.get(i).ClockwiseOrAntiClockwise = true;
								}
								else // idzie normalnie w lewo
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										for(int k = 0; k < _lengthOfWall; k++)
										{
											if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x + k && PolicemenDict.get(p).y == ObstaclesDict.get(i).y - 1)
											{
												canMove = false;
												break;
											}
										}
									}

									for(Person t : ThiefDict.keySet())
									for(int k = 0; k < _lengthOfWall; k++)
									{
										if(ThiefDict.get(t).x == ObstaclesDict.get(i).x + k && ThiefDict.get(t).y == ObstaclesDict.get(i).y - 1)
										{
											canMove = false;
											break;
										}
									}
									
									if(canMove)
										ObstaclesDict.get(i).y--;
								}
							}
							else // pionowy klocek - ruch w dó³
							{
								if((ObstaclesDict.get(i).x) == (_n - _lengthOfWall + 1)) // Idzie w górê bo odbija
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x - 1 && PolicemenDict.get(p).y == ObstaclesDict.get(i).y)
											canMove = false;
									}
									
									for(Person t : ThiefDict.keySet())
									if(ThiefDict.get(t).x == ObstaclesDict.get(i).x - 1 && ThiefDict.get(t).y == ObstaclesDict.get(i).y)
										canMove = false;
									
									if(canMove)
										ObstaclesDict.get(i).x--;
									
									ObstaclesDict.get(i).ClockwiseOrAntiClockwise = true;
								}
								else // Idzie w normalnie w dó³
								{
									boolean canMove = true;
									for(Person p : PolicemenDict.keySet())
									{
										if(PolicemenDict.get(p).x == ObstaclesDict.get(i).x + 1 && PolicemenDict.get(p).y == ObstaclesDict.get(i).y)
											canMove = false;
									}
									
									for(Person t : ThiefDict.keySet())
									if(ThiefDict.get(t).x == ObstaclesDict.get(i).x + 1 && ThiefDict.get(t).y == ObstaclesDict.get(i).y)
										canMove = false;
									
									if(canMove)
										ObstaclesDict.get(i).x++;
								}
							}
						}
					}
					else // zmienia
					{
						if(GatewaysDict.get(i).ClockwiseOrAntiClockwise == true)
							GatewaysDict.get(i).ClockwiseOrAntiClockwise = false;
						else
							GatewaysDict.get(i).ClockwiseOrAntiClockwise = true;
					}
				}
			}
		}
	}
	
	public void RedrawBoard()
	{
		for(int i = 0; i < _ne; i++)
		{
			for(int j = 0; j < _ne; j++)
			{
				_board[i][j] = 0;
			}
		}
		
		for (int i = 0; i < _ne; i++)
		{
			_board[0][i] = _wall;
			_board[_ne - 1][i] = _wall;
		}
		for(int j = 1; j < _ne - 1; j++)
		{
			_board[j][0] = _wall;
			_board[j][_ne - 1] = _wall;
		}
		
		for(int i = 0; i < _numberOfGateways; i++)
		{
			for(int j = 0; j < _lengthOfGateway; j++)
			{
				if(GatewaysDict.get(i).IsHorizontalOrIsVertical == true)
					_board[GatewaysDict.get(i).x][GatewaysDict.get(i).y + j] = _gateway;
				else
					_board[GatewaysDict.get(i).x + j][GatewaysDict.get(i).y] = _gateway;
			}
		}
		
		for(int i = 0; i < _numerOfWalls; i++)
		{
			for(int j = 0; j < _lengthOfWall; j++)
			{
				if(ObstaclesDict.get(i).IsHorizontalOrIsVertical == true)
					_board[ObstaclesDict.get(i).x][ObstaclesDict.get(i).y + j] = _obstacle;
				else
					_board[ObstaclesDict.get(i).x + j][ObstaclesDict.get(i).y] = _obstacle;
			}
		}
		
		for(Person p : PolicemenDict.keySet())
			_board[PolicemenDict.get(p).x][PolicemenDict.get(p).y] = _policeman;
		
		for(Person t : ThiefDict.keySet())
		_board[ThiefDict.get(t).x][ThiefDict.get(t).y] = _thief;
	}

	public HashMap<Integer, ElementPosition> GetGateways()
	{
		HashMap<Integer, ElementPosition> copyGatewayDict = new HashMap<Integer, ElementPosition>();
		for(Entry<Integer, ElementPosition> entry : GatewaysDict.entrySet())
		{
			copyGatewayDict.put(entry.getKey(), new ElementPosition(entry.getValue()));
		}
		return copyGatewayDict;
	}
	
	public HashMap<Integer, ElementPosition> GetObstacles()
	{
		HashMap<Integer, ElementPosition> copyObstaclesDict = new HashMap<Integer, ElementPosition>();
		for(Entry<Integer, ElementPosition> entry : ObstaclesDict.entrySet())
		{
			copyObstaclesDict.put(entry.getKey(), new ElementPosition(entry.getValue()));
		}
		return copyObstaclesDict;
	}
	
	public HashMap<Person, Position> GetPolicemen()
	{
		HashMap<Person, Position> copyPolicemenDict = new HashMap<Person, Position>();
		for(Entry<Person, Position> entry : PolicemenDict.entrySet())
		{
			copyPolicemenDict.put(entry.getKey(), new Position(entry.getValue()));
		}
		return copyPolicemenDict;
	}
	
	public HashMap<Person, Position> GetThief()
	{
		HashMap<Person, Position> copyThiefDict = new HashMap<Person, Position>();
		for(Entry<Person, Position> entry : ThiefDict.entrySet())
		{
			copyThiefDict.put(entry.getKey(), new Position(entry.getValue()));
		}
		return copyThiefDict;
	}

	private boolean CanMove(char personType, Person person, int direction)
	{
		boolean move = true;
		int x = 0;
		int y = 0;
		int xx, yy;
		xx = yy = 0;
		
		switch(direction)
		{
		case 4:
			xx = 0;
			yy = -1;
			break;
			
		case 8:
			xx = -1;
			yy = 0;
			break;
			
		case 6:
			xx = 0;
			yy = 1;
			break;
			
		case 2:
			xx = 1;
			yy = 0;
			break;
			
			default:
				break;
		}
		if(personType == 'p')
		{
			x = PolicemenDict.get(person).x;
			y = PolicemenDict.get(person).y;
		}
		else if(personType == 't')
		{
			x = ThiefDict.get(person).x;
			y = ThiefDict.get(person).y;
		}
		
		x = x + xx;
		y = y + yy;

		if(x == 0 || x == _ne - 1 || y == 0 || y == _ne - 1)
		{
			boolean checkGateway = false;
			if(person instanceof Thief)
			{
				for(int i = 0; i < _numberOfGateways; i++)
				{
					if(GatewaysDict.get(i).IsHorizontalOrIsVertical == true)
					{
						for(int j = 0; j < _lengthOfGateway; j++)
						{
							if(GatewaysDict.get(i).x == x && GatewaysDict.get(i).y + j == y)
							{
								checkGateway = true;
							}
						}
					}
					else
					{
						for(int j = 0; j < _lengthOfGateway; j++)
						{
							if(GatewaysDict.get(i).y == y && GatewaysDict.get(i).x + j == x)
							{
								checkGateway = true;
							}
						}
					}
				}
			}
			if(checkGateway == false)
			{
				move = false;
				return move;
			}
		}
		
		for(int j = 0; j < _numerOfWalls; j++)
		{
			for(int k = 0; k < _lengthOfWall; k++)
			{
				if(ObstaclesDict.get(j).IsHorizontalOrIsVertical == true)
				{
					if(x == ObstaclesDict.get(j).x && y == ObstaclesDict.get(j).y + k)
					{
						move = false;
						return move;
					}
				}
				else
				{
					if(x == ObstaclesDict.get(j).x + k && y == ObstaclesDict.get(j).y)
					{
						move = false;
						return move;
					}
				}
			}
		}
		
		for(Person p : PolicemenDict.keySet())
		{
			if(x == PolicemenDict.get(p).x && y == PolicemenDict.get(p).y)
			{
				move = false;
				return move;
			}
		}
			
		
		return move;
	}
	
	public void ReloadBoard(int numberOfMove)
	{
		int move;
		boolean isMove;
		List<Integer> moves = null;
		for(Person p : Persons.keySet())
		{
			char personType = 'p';
			Map<Person, Position> dict = PolicemenDict;
			if(p instanceof Thief)
			{
				personType = 't';
				dict = ThiefDict;
			}
			
			moves = get(p);
			

			if(moves != null && moves.size() > numberOfMove)
			{
				move = moves.get(numberOfMove);
				
				switch(move)
				{
				case 4:
					isMove = CanMove(personType, p, move);
					if(isMove)
						dict.get(p).y--;
					break;
					
				case 8:
					isMove = CanMove(personType, p, move);
					if(isMove)
						dict.get(p).x--;
					break;
					
				case 6:
					isMove = CanMove(personType, p, move);
					if(isMove)
						dict.get(p).y++;
					break;
					
				case 2:
					isMove = CanMove(personType, p, move);
					if(isMove)
						dict.get(p).x++;
					break;
					
				default:
					break;
				}
			}
		}
	}
	
	public void set(Person person, List<Integer> moves)
	{
		synchronized(person)
		{
			List<Integer> movesCopy = new ArrayList<Integer>(moves.size());
			
			for(int i : moves)
			{
				movesCopy.add(i);
			}
			
			plans.put(person, movesCopy);
		}
	}
	
	public List<Integer> get(Person person)
	{
		List<Integer> results = null;
		synchronized (person) {
			results = plans.get(person);
		}
		
		return results;
	}
	
	@Override public String toString()
	{
		StringBuilder txt = new StringBuilder("");
		txt.append("\n");
		txt.append("\n");
		txt.append("   ");
		for (int j = 0; j < _ne; j++)
			txt.append("--  ");
		txt.append("\n");
		for (int i = 0; i < _ne; i++)
		{
			txt.append(" | ");
			for (int j = 0; j < _ne; j++)
			{
				switch (_board[i][j])
				{
				case 0:
					txt.append(" ");
					break;
				case 1:
					txt.append("Z");
					break;
				case 2:
					txt.append("P");
					break;
				case 3:
					txt.append("O");
					break;
				case 4:
					txt.append("X");
					break;
				case 5:
					txt.append(" ");
					break;
				}
				if (j < _ne)
					txt.append(" | ");
			}
			if (i < _ne)
				txt.append("\n");
			txt.append("   ");
			for (int j = 0; j < _ne; j++)
				txt.append("--  ");
			txt.append("\n");
		}
		
		for(Person p : PolicemenDict.keySet())
			System.out.println("p: " + p + " " + PolicemenDict.get(p));
		
		for(Person t : ThiefDict.keySet())
			System.out.println("\nt: " + t + " " + ThiefDict.get(t));
		
		return txt.toString();
	}
}
