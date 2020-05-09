package Draughts;

public class Board{
	//board holdsinformation regarding the 10x10 fields and what they hold
	private Field[][] board = new Field[10][10];
	public Board() {
		
	}
	public void addField(int y,int x, String colour) {
		board[y][x] = new Field(y, x, colour);
	}
	public String getColour(int y, int x) {
		if(fieldValid(y,x)) {
			return board[y][x].getColour();
		}
		else return "null";
		
	}
	public void setColour(int y,int x,String colour) {
		if(fieldValid(y,x)){
			board[y][x].setColour(colour);
		}
	}
	
	public boolean fieldValid(int y,int x) {
		//checks if field is valid ie. is within the range of the array 0-9
		if(y < 10 && y>=0) {
			if(x <10 && x>=0) return true;
			else return false;
		}
		else return false;
		
	}
}
