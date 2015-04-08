package GameEngine;

public class Position
{
	public Position(byte x, byte y)
	{
		this.x = x;
		this.y = y;
	}
	
	byte x;
	byte y;
	@Override
	public String toString() {
		
		return "x: " + x + ", y: " + y;
	}
	
	public Position(Position original)
	{
		this.x = original.x;
		this.y = original.y;
	}
	
}
