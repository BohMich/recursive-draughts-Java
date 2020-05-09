package Draughts;

public class Pawn{

	protected int x; 
	protected int y;	
	private int colour;	//1 = white -1 = black
	
	public Pawn(int y,int x,int colour) {
		this.x = x;
		this.y = y;
		this.colour = colour;
	}
	public int getX() { return x;}
	public int getY() { return y;}
	public int[] getVector(){
		int[] temp = {y,x};
		return temp;
	}
	public int getColour() { return colour;}
	
	
	public void setX(int x) {this.x = x;}
	public void setY(int y) {this.y = y;}
	
	public boolean isOnPosition(String xy) {
		//TODO
		return false;
	}
	
	
}
