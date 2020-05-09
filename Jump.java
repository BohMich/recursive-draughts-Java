package Draughts;

import java.util.ArrayList;

public class Jump{
	//recursive tree class. finds the best move possible for a jump sequence
	int[] dest = new int[2];
	int[] position = new int[2];
	int count;

	ArrayList<Jump> nodes = new ArrayList<>();
	ArrayList<int[]> previousJumps = new ArrayList<>();
	ArrayList<int[]> takenPawns = new ArrayList<>();
	
	//recursive tree like structure, 4 possible nodes for each jump direction.
	public Jump(Board board, int[] original, int[] previous, String enemy, ArrayList<int[]> jHist,ArrayList<int[]> takenPawns) {	
		//check each direction if can be jumped
		for (int i = 0; i < Game._JUMPS.length; i++) {
			//check if landing position (+2) is empty
			if (board.getColour(original[0] + Game._JUMPS[i][0], original[1] + Game._JUMPS[i][1]).equals(Game._EMPTY)) {
				//check if middle (between original and landing) is enemy
				if (board.getColour(original[0] + (Game._JUMPS[i][0] / 2),( original[1] + (Game._JUMPS[i][1] / 2))).equals(enemy)) {
					//jump's syntax is valid. 
					int[] dest = { original[0] + Game._JUMPS[i][0], original[1] + Game._JUMPS[i][1] };	//set destination vector
					boolean canJump = true;													
					int[] pawnTaken = { original[0] + (Game._JUMPS[i][0] / 2), original[1] + (Game._JUMPS[i][1] / 2) };  	//set taken vector 
					//Check if the pawn was not already taken.
					for (int x = 0; x < takenPawns.size(); x++) {
						if (Game.positionsEqual(takenPawns.get(x), pawnTaken)) {
							canJump = false;
							break;
						}
					}
					
					if (canJump) {
						//update parameters for this position
						jHist.add(dest);
						takenPawns.add(pawnTaken);
						nodes.add(new Jump(board, dest, original, enemy, jHist, takenPawns));
					}
				}
			}
		}
		this.takenPawns = takenPawns;
		position = original;
		previousJumps = jHist;
	}

	public int getSize() {
		//returns size of the biggest jump possible
		if (nodes.isEmpty()) {
			return count += 0;
		} else {
			for (int i = 0; i < nodes.size(); i++) {
				count += nodes.get(i).getSize();
			}
			return count += 1;
		}

	}

	public ArrayList<int[]> getBestMove() {	
		//returns sequence of jumps that make a biggest combo
		//array[0] = starting position 
		//array[max] = ending position
		ArrayList<int[]> moves = new ArrayList<>();
		moves.add(position);
		if (nodes.isEmpty()) {
			return moves;
		}
		
		int bestBranch =  0;
		for(int i = 0 ; i < nodes.size();i++) {
			int size = nodes.get(i).getSize();
			if(size > nodes.get(bestBranch).getSize()) {
				bestBranch = i;
			}		
		}
		for(int[] x : nodes.get(bestBranch).getBestMove()) {
			moves.add(x);
		}
		return moves;
	}
}
