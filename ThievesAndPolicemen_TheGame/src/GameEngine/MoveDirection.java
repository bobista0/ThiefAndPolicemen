package GameEngine;

public enum MoveDirection {

	UP(8), DOWN(2), LEFT(4), RIGHT(6), NONE(5);
	private int move;
	
	private MoveDirection(int move)
	{
		this.move = move;
	}
	
	public int getValue()
	{
		return move;
	}
	
}
