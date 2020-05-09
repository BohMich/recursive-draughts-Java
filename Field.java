package Draughts;
//Field class is a single field on a board but also it stores what pawn is on it. 
//pawns are not the entities in this program because the only information necessary about them is their colour 
//which is stored directly in the field class.
public class Field implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	public static int id_count = 1;
	
	private static int x;
	private static int y;
	private String colour;		//pawn movement is done by exchanging this between fields.
	private int id;
	
	private boolean isKing = false;
	
	public Field(int x,int y, String colour) {
		this.x = x; 
		this.y = y; 
		this.colour = colour;
		
		id=id_count;
		id++;
	}
	
	
	public int getX() {return x;}
	public int getY() {return y;}
	public void setColour(String c) {this.colour = c;}
	public String getColour() {return colour;}
	
	
}
