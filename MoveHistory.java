package Draughts;

import java.util.ArrayList;
import java.util.Scanner;

public class MoveHistory implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//reference to the game that will be manipulated
	private transient Game game;
	private ArrayList<MoveHistory_Move> Movements;
	private  int currentPosition;
	//private int tempTurn;
	MoveHistory(Game game) {
		this.game = game;
		//tempTurn = game.t;
		
		Movements  = new ArrayList<>();
	}
	
	public void MovementHistoryMainController() {
		
		//console controller to manipulate the game.
		//sets the position of currently viewed move. At the start of this method it will always 
		//be equal to the most recent move made.
		currentPosition = Movements.size()-1;
		Scanner scanner = new Scanner(System.in);
		String control = null;
		
		while(true) { 
			game.boardUpdate();
			System.out.println("GAME PAUSED");
			System.out.println("Current turn = " + (currentPosition+1) + "|" + (Movements.size()));
			System.out.println("1: Previous Move | 2: Next Move | 9: Show All moves " + '\n' + 
							   "0: Return to game from current position");
			control = scanner.nextLine();
		
				if(control.equals("1")) MoveBackward();
				else if(control.equals("2")) MoveForward();
				else if(control.equals("0")) {
					System.out.println("WARNING: You are resuming the game from position " + (currentPosition+1) +'|' +Movements.size()+
							 '\n' + "Press 0 to resume" + '\n'+ "Press any button to cancel");
					control = scanner.nextLine();
					if(!control.equals("0")) continue;
					resumeGame();
					return;
				}
				else if(control.equals("9")) getMoveHistory();
				else continue;
			
		}
	}
	
	
	public void AddMove(int[] beginPosition, int[] endPosition) {
		Movements.add(new MoveHistory_Move(beginPosition, endPosition));
	}
	
	public void AddJump(MoveHistory_Jump jump) {
		Movements.add(jump);
	}
	
	public void loadMoves(Game game) {
		//method plays the game from the beginning to the last move before saving
		this.game = game;
		currentPosition = -1;
		//iterate through moves
		for(int i = currentPosition;i<=Movements.size()-2;i++) {
			MoveForward();
		}
		
		
	}
	
	private void MoveForward() {
		//check if not the current move
		if(currentPosition==Movements.size()-1) {
			return;
		}
		//moving backwards moves the pointer back 1 position
		//return to the position that is to be reverted. 
		currentPosition++;
		 

		
		 //if move is jump iterate through jump sequence from begining to end
		 if(Movements.get(currentPosition).isJump) {
			 int[] begPos = {0,0};
			 int[] endPos = {0,0};
					 
			 MoveHistory_Jump temp = (MoveHistory_Jump) Movements.get(currentPosition);
			 for(int i = 0;i<temp.jumps.size();i++) {
				 begPos = temp.jumps.get(i).begPos;
				 endPos = temp.jumps.get(i).endPos;
				 game.jump(begPos, endPos, game.getEnemy(game.t));
			 }
		 }
		 else {
			 //simple pawn move
			 game.movePawn(Movements.get(currentPosition).begPos,
					 		Movements.get(currentPosition).endPos);		 	
		 }
		 //switch sides.
		 game.switchPlayer();
	}
	private void MoveBackward() {
		//method takes one move backwards
		//check if not first position in the game
		if(currentPosition==0) {
			return;
		}
		//check if move is jump and if so iterate through jump sequence from last to first 
		 if(Movements.get(currentPosition).isJump) {
			 int[] begPos = {0,0};
			 int[] middle = {0,0};
			 int[] endPos = {0,0};
					 
			 MoveHistory_Jump temp = (MoveHistory_Jump) Movements.get(currentPosition);
			 for(int i = temp.jumps.size()-1;i>=0;i--) {
				 begPos = temp.jumps.get(i).begPos;
				 endPos = temp.jumps.get(i).endPos;
				 middle = getMiddle(begPos,endPos); 
				 game.createPawn(middle, game.getTeam(game.t));
				 game.movePawn(endPos, begPos);
			
			 }
		 }
		 else {
			 game.movePawn(Movements.get(currentPosition).endPos,
					 		Movements.get(currentPosition).begPos);		 	
		 }
		
		 //revert the pointer 1 turn back;
		 if(currentPosition > 0)
		 currentPosition--;
		//change teams
		 game.switchPlayer();

	}
	
	private void resumeGame() {
		//delete moves in front of the current one
		while((Movements.size()-1) > currentPosition) {
			Movements.remove(Movements.size()-1);
		}
		return;
	}
	
	public void getMoveHistory() {
		for(int i = 0; i<Movements.size();i++) {
			if(Movements.get(i).isJump) {
				MoveHistory_Jump temp  = (MoveHistory_Jump) Movements.get(i);
				System.out.println("JUMP{");
				for(int j = 0 ; j < temp.jumps.size();j++) {
					
					String temp1 =  + temp.jumps.get(j).begPos[0] + "" + temp.jumps.get(j).begPos[1] + "->" +
							temp.jumps.get(j).endPos[0] + "" + temp.jumps.get(j).endPos[1];
					System.out.println(temp1);
				}
				System.out.println("}");
			}
			else System.out.println(Movements.get(i).getMove());
		}
	}
	
	
	
	private int[] getMiddle(int[] pos1,int[] pos2) {
		int[] middle = {0,0};
		
		if (pos1[0] < pos2[0])
			middle[0]=(pos1[0]+1);
		else
			middle[0]=(pos1[0]-1);
		if (pos1[1] < pos2[1])
			middle[1] = (pos1[1]+1);
		else
			middle[1] = (pos1[1]-1);
		
		return middle;
	}
}

class MoveHistory_Move implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	int[] begPos;
	int[] endPos;
	String enemy;
	boolean isJump;
	//empty constructor for jump subclass
	public MoveHistory_Move() {
		
	}
	
	//contructor if move
	public MoveHistory_Move(int[] beginPosition, int[] endPosition) {
		this.begPos = beginPosition;
		this.endPos = endPosition;
		isJump = false;
	}
	//constructor if its a jump;
	public MoveHistory_Move(int[] beginPosition,int[] endPosition, String enemy) {
		this.begPos = beginPosition;
		this.endPos = endPosition;
		
		this.enemy = enemy;
		isJump = true;
	}
	
	public String getMove() {
		String temp = begPos[0] + "" + begPos[1] + "->" +
						endPos[0] + "" + endPos[1];
		return temp;
	}
	
}

class MoveHistory_Jump extends MoveHistory_Move implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	ArrayList<MoveHistory_Move> jumps;
	
	public MoveHistory_Jump() {
		jumps = new ArrayList<>();
		isJump = true;
	}
	
	public void addJump(int[] beginingPosition, int[] endPosition,String enemy) {
		jumps.add(new MoveHistory_Move(beginingPosition, endPosition,enemy));
	}
	
	
}
