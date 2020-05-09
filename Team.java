package Draughts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Team {

	String name;
	String enemy;
	ArrayList<Pawn> pawns = new ArrayList<>();

	public Team(String name, String enemy) {
		this.name = name;
		this.enemy = enemy;
	}

	// apply binary search here and there when finding pawns.
	protected boolean movePawn(int orginY, int orginX, int destY, int destX) {
		Pawn temp = getPawn(orginY,orginX);
		temp.setY(destY);
		temp.setX(destX);
				return true;
		
	}

	public boolean deletePawn(int y, int x) {
		return pawns.remove(getPawn(y,x));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private void sortPawns() {
		// overridden sort where first Y axis is sorted and then x axis.
		Collections.sort(pawns, new Comparator<Pawn>() {
			@Override
			public int compare(Pawn p2, Pawn p1) // override compare function
			{
				// if c = -1 means less than 0 == equal and 1 = more than
				// first check if Y is equal
				int c = p2.getY() - p1.getY();
				if (c == 0)
					return (p2.getX() - p1.getX()); // if Y is equal sort by X;
				return p2.getY() - p1.getY(); // else sort by Y
			}
		});
	}

	public Pawn getPawn(int y, int x) {
		sortPawns();
		// binary search that go through the list of pawns and searches for the required
		// one.
		int low = 0;
		int high = pawns.size() - 1;
		int middle;
		while (low<=high) {
			// get middle
			// if the top of the range is not even , add 1 to get an even middle
			
			middle = ( low + (high-low) / 2) ;
			// pawns are sorted based on y axis then by x axis
			// choose y
			if (pawns.get(middle).getY() > y) {
				// right
				high = middle - 1;
			} else if (pawns.get(middle).getY() == y) {
				// y is the same check X
				if (pawns.get(middle).getX() > x) {
					// right
					high = middle- 1;
				} else if (pawns.get(middle).getX() == x) {
					// found
					
					return pawns.get(middle);
				} else if (pawns.get(middle).getX() < x) {
					// left
					low = middle + 1;
				}
			} else if (pawns.get(middle).getY() < y) {
				// left
				low = middle + 1;
			}

		}
		return null;
	}

}
