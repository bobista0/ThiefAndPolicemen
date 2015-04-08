package GameEngine;

public class ElementPosition extends Position
{
	boolean IsHorizontalOrIsVertical; // Horizontal = true / Vertical = false
	boolean ClockwiseOrAntiClockwise = true; // Clockwise = true / AntiClockwise = false
	
	public ElementPosition(byte x, byte y, boolean IsHorizontalOrIsVertical)
	{
		super(x, y);
		this.IsHorizontalOrIsVertical = IsHorizontalOrIsVertical;
	}
	
	public ElementPosition(ElementPosition original)
	{
		super(original);

		this.IsHorizontalOrIsVertical = original.IsHorizontalOrIsVertical;
		this.ClockwiseOrAntiClockwise = original.ClockwiseOrAntiClockwise;		
	}
}
