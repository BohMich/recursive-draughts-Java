package Draughts;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

//Class which holds an individual game. Holds details about each move and all methods regarding movement;
public class Game{
	// constant variables
	// pawns move up only, directions relative to pawns position
	//these describe movement direction, relative to the player. For Example jumping left = y-2 , x + 2
	//after each turn these values are multiplied by -1 to switch their positions by 180 degrees(for next player)
	private static int[] _ATT_DOWN_LEFT = new int[] { -2, 2 };
	private static int[] _ATT_DOWN_RIGHT = new int[] { -2, -2 };
	private static int[] _ATT_UP_LEFT = new int[] { 2, 2 };
	private static int[] _ATT_UP_RIGHT = new int[] { 2, -2 };
	private static int[] _MV_LEFT = new int[] { 1, 1 };
	private static int[] _MV_RIGHT = new int[] { 1, -1 };
	//arrays of above fields
	public static int[][] _MOVES = { _MV_LEFT, _MV_RIGHT };
	public static int[][] _JUMPS = { _ATT_DOWN_LEFT, _ATT_DOWN_RIGHT, _ATT_UP_LEFT, _ATT_UP_RIGHT };
	
	//teams values;
	public static String _WHITE = "0"; // PLAYER 1 = WHITE
	public static String _BLACK = "#";
	public static String _EMPTY = " ";
	public String[] player = new String[] { _BLACK, _WHITE };
	//team class holds data about position of each pawn. also contains tools for searching them
	protected Team[] teams = { new Team(player[0], _WHITE), new Team(player[1], _BLACK) };

	//input variables
	private String control = null;
	Scanner scanner = new Scanner(System.in);
	
	// Board class. holds information about state of the graphical board. 
	public static Board board = new Board();

	// coordinating variables, t ={1,0} used to refer to teams where 1 = white and 0 = black
	// turn is the same but turn= {1,-1} used to transform the board 180 before moving pawns;
	protected int t = 1;
	protected int turn = 1; 
	
	//object responsible for storing each move as a sequence of moves. doesnt perform any checks so only validated 
	//moves are placed there
	protected MoveHistory _MOVEHISTORY;

	public Game() {
		createBoard();
		_MOVEHISTORY = new MoveHistory(this);
	}
	public void aiVsAi() {
		//method controls two computer players
		// game rule, a player must always make the biggest jump sequence.
		// stores the largest jump that can be made.
		
		//variable controls how many turns should be made without opening the console input
		int turnSkip = 0;
		boardUpdate();
		System.out.println("Game start");
		while (true) {
			if (turnSkip == 0) {
				// check turn at the beginning of the loop
				while (true) {
					System.out.println(teams[t].name + "'s TURN");
					System.out.println("1: Next Move | 5: Skip 5 Turns " + '\n'
							+ "2: Movement History | 3: Save Game & Exit");
					control = scanner.nextLine();
					if (control.equals("1"))
						break;
					else if (control.equals("5")) {
						turnSkip = 5;
						break;
					} else if (control.equals("3")) {
						System.out.println("Name of the game? (no : , - <> [] bamboozle)");
						control = scanner.nextLine();
						saveGame(control);
						return;
					} else if (control.equals("2")) {
						_MOVEHISTORY.MovementHistoryMainController();
						continue;
					} else
						continue;

				}
			} else if (turnSkip > 0)
				turnSkip--;
			computerMove(teams[t]);
			boardUpdate();
			continue;
		}

	}

	public void playerVSAI() {
		while (true) {
			boardUpdate();
			System.out.println(player[t] + "'s TURN");
			System.out.println("1: Make Move | 2: Movement History | 3: Save Game & Exit");
			control = scanner.nextLine();
			if (control.equals("1"))
				if (t == 1) {
					playerMove();	
				} else {
					System.out.println("AI turn: press anything to perform move");
					scanner.nextLine();
					if (computerMove(teams[0]) == false)
						endGame();
				}
			else if (control.equals("3")) {
				System.out.println("Name of the game? (no : , - <> [] bamboozle)");
				control = scanner.nextLine();
				saveGame(control);
				return;
			} else if (control.equals("2")) {
				_MOVEHISTORY.MovementHistoryMainController();
				continue;
			} else
				continue;

		}

	}

	public void playerVsPlayer() {
		while (true) {
			boardUpdate();
			System.out.println(teams[t].name + "'s TURN");
			System.out.println("1: Move | 2: Movement History | 3: Save Game & Exit");
			control = scanner.nextLine();
			if (control.equals("1")) {
				playerMove();
			} else if (control.equals("3")) {
				System.out.println("Name of the game? (no : , - <> [] bamboozle)");
				control = scanner.nextLine();
				saveGame(control);
				return;
			} else if (control.equals("2")) {
				_MOVEHISTORY.MovementHistoryMainController();
				continue;
			} else
				continue;
		}
	}

	private boolean computerMove(Team team) {
		//computer move algorithm
		//prioritise jumping then moves, if both fail it means no more moves are possible and game ends.
		boolean moveMade = false;
		if (jumpAround(team)) {
			moveMade = true;
		} else if (moveAround(team)) {
			moveMade = true;
		}

		if (moveMade) {
			switchPlayer();
			return true;
		} else
			return false;

	}

	private void playerMove() {
		//player movement
		//input variables
		String origin;
		String dest;
		int choice = 0;
		while (true) {
			// players turn
			System.out.println(" Choose pawn: ");
			origin = scanner.nextLine();
			if (!moveSyntaxValid(origin)) {
				System.out.println("Invalid Syntax");
				continue;
			}
			System.out.println("Choose destination: ");
			dest = scanner.nextLine();
			if (!moveSyntaxValid(dest)) {
				continue;
			}

			int[] pawnYX = { Integer.parseInt(origin.substring(0, 1)), Integer.parseInt(origin.substring(1, 2)) };
			int[] destYX = { Integer.parseInt(dest.substring(0, 1)), Integer.parseInt(dest.substring(1, 2)) };

			// determines the move, 0= invalid 1= move 2= jump
			choice = checkMove(pawnYX, destYX);
			if (choice == 0)
				continue;
			else if (choice == 1) {
				//moves pawn on board and in players team object, adds the move to the history and switches player
				movePawn(pawnYX, destYX);
				_MOVEHISTORY.AddMove(pawnYX, destYX);
				switchPlayer();
				return;
			} else if (choice == 2) {
				// jump algorithm, first jump because can jump
				boolean jumpComplete = jumpLoop(pawnYX, destYX);
				if (jumpComplete == false)
					continue;
				else {
					return;
				}
			}
		}
	}

	public boolean endGame() {
		//end game, runs when no more moves possible from either of the teams.
		Scanner scanner = new Scanner(System.in);
		String control = null;
		System.out.println("Game Finished");
		while (true) {
			System.out.println("1: Show Moves | 2: Save & Exit");
			control = scanner.nextLine();
			if (control.equals("1")) {
				_MOVEHISTORY.MovementHistoryMainController();
				return false;
			} else if (control.equals("2")) {
				System.out.println("Name of the game? (no : , - <> [] bamboozle)");
				control = scanner.nextLine();
				saveGame(control);
				return true;
			} else
				continue;
		}
	}

	public void boardUpdate() {
		//draws the board in console
		System.out.println("        " + player[1]);
		System.out.println();

		// print horizontal positioning legend
		System.out.print(" ");
		for (int i = 0; i < 10; i++) {
			System.out.print(" ");
			System.out.print(i);
		}
		
		System.out.println();
		for (int y = 0; y < 10; y++) {
			System.out.print(y);
			System.out.print("|");

			for (int x = 0; x < 10; x++) {

				System.out.print(board.getColour(y, x));
				System.out.print("|");

			}
			System.out.println();

		}
		System.out.println();
		System.out.println("        " + player[0]);
		System.out.println();
	}

	public boolean movePawn(int[] originYX, int[] destYX) {
		// method doesn't perform any checks.
		// this method changes the state of team arrays and the 2d board array
		// simultaneously.
		if (board.getColour(originYX[0], originYX[1]).equals(_WHITE)) {
			// change position of pawn in both arrays;
			teams[1].movePawn(originYX[0], originYX[1], destYX[0], destYX[1]);
			board.setColour(destYX[0], destYX[1], _WHITE);
			board.setColour(originYX[0], originYX[1], _EMPTY);
			return true;
		}

		else if (board.getColour(originYX[0], originYX[1]).equals(_BLACK)) {
			// change position of pawn in both arrays;
			teams[0].movePawn(originYX[0], originYX[1], destYX[0], destYX[1]);
			board.setColour(destYX[0], destYX[1], _BLACK);
			board.setColour(originYX[0], originYX[1], _EMPTY);
			return true;
		}

		return false;
	}

	protected void switchPlayer() {
		//changes the coordinate values. 
		if (t == 1) {
			t = 0;
			turn = -1;
		} else if (t == 0) {
			t = 1;
			turn = 1;
		}
		//go through the movement vectors and multiply by -1 to switch 
		for (int i = 0; i < _MOVES.length; i++) {
			_MOVES[i][0] *= -1;
			_MOVES[i][1] *= -1;
		}
		for (int i = 0; i < _JUMPS.length; i++) {
			_JUMPS[i][0] *= -1;
			_JUMPS[i][1] *= -1;
		}

	}

	public ArrayList<int[]> getAvailableMoves(Pawn pawn) {
		//checks possible moves for a pawn
		ArrayList<int[]> availableMoves = new ArrayList<int[]>();
		for (int i = 0; i < _MOVES.length; i++) {
			int[] dest = { pawn.getY() + _MOVES[i][0], pawn.getX() + _MOVES[i][1] };
			if (board.getColour(dest[0], dest[1]).equals(_EMPTY)) {
				availableMoves.add(dest);
			}
		}
		return availableMoves;
	}

	public boolean deletePawn(int[] orginYX, int y, int x) {
		//removes a pawn form a team and board object
		if (board.getColour(orginYX[0], orginYX[1]).equals(_WHITE)) {
			// change position of pawn in both arrays;
			if (!teams[0].deletePawn(y, x))
				return false;
			board.setColour(y, x, _EMPTY);
			return true;
		}

		else if (board.getColour(orginYX[0], orginYX[1]).equals(_BLACK)) {
			if (!teams[1].deletePawn(y, x))
				return false;
			board.setColour(y, x, _EMPTY);
			return true;
		}

		return false;
	}

	protected boolean validMove(int[] pawnYX, int[] destYX) {
		for (int i = 0; i < _MOVES.length; i++) {
			if (pawnYX[0] + _MOVES[i][0] == destYX[0] && pawnYX[1] + _MOVES[i][1] == destYX[1]) {
				return true;
			}
		}
		return false;
	}

	protected boolean validJump(int[] pawnYX, int[] destYX) {
		for (int i = 0; i < _JUMPS.length; i++) {
			if (pawnYX[0] + _JUMPS[i][0] == destYX[0] && pawnYX[1] + _JUMPS[i][1] == destYX[1]) {
				return true;
			}
		}
		return false;
	}

	public boolean canJump(int[] pawn) {
		// check if as jump is possible
		for (int i = 0; i < _JUMPS.length; i++) {
			if (board.getColour(pawn[0] + _JUMPS[i][0], pawn[1] + _JUMPS[i][1]).equals(_EMPTY)) {
				if (board.getColour(pawn[0] + (_JUMPS[i][0] / 2), pawn[1] + (_JUMPS[i][1] / 2))
						.equals(teams[t].enemy)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean jump(int pawn[], int dest[], String enemy) {
		// method deals with only with jumping algorithm
		boolean canJump = false;
		//direction of jump 
		int dirX = 0;
		int dirY = 0;
		//set direction based on current player
		if (pawn[0] < dest[0])
			dirY = 1 * turn;
		else
			dirY = -1 * turn;
		if (pawn[1] < dest[1])
			dirX = 1 * turn;
		else
			dirX = -1 * turn;

		int[] jumpDirection = new int[2];
		// is x a y a jump forward or backwards
		if (dirY == 1 && dirX == 1) {
			if (pawn[0] + _ATT_UP_LEFT[0] == dest[0] && pawn[1] + _ATT_UP_LEFT[1] == dest[1]) {
				if (board.getColour(pawn[0] + (_ATT_UP_LEFT[0] / 2), pawn[1] + (_ATT_UP_LEFT[1] / 2)).equals(enemy)) {
					jumpDirection[0] = _ATT_UP_LEFT[0];
					jumpDirection[1] = _ATT_UP_LEFT[1];
					canJump = true;
				}
			}
		} else if (dirY == 1 && dirX == -1) {
			if (pawn[0] + _ATT_UP_RIGHT[0] == dest[0] && pawn[1] + _ATT_UP_RIGHT[1] == dest[1]) {
				if (board.getColour(pawn[0] + (_ATT_UP_RIGHT[0] / 2), pawn[1] + (_ATT_UP_RIGHT[1] / 2)).equals(enemy)) {
					jumpDirection[0] = _ATT_UP_RIGHT[0];
					jumpDirection[1] = _ATT_UP_RIGHT[1];
					canJump = true;
				}
			}
		} else if (dirY == -1 && dirX == 1) {
			if (pawn[0] + _ATT_DOWN_LEFT[0] == dest[0] && pawn[1] + _ATT_DOWN_LEFT[1] == dest[1]) {
				if (board.getColour(pawn[0] + (_ATT_DOWN_LEFT[0] / 2), pawn[1] + (_ATT_DOWN_LEFT[1] / 2))
						.equals(enemy)) {
					jumpDirection[0] = _ATT_DOWN_LEFT[0];
					jumpDirection[1] = _ATT_DOWN_LEFT[1];
					canJump = true;
				}
			}
		} else if (dirY == -1 && dirX == -1) {
			if (pawn[0] + _ATT_DOWN_RIGHT[0] == dest[0] && pawn[1] + _ATT_DOWN_RIGHT[1] == dest[1]) {
				if (board.getColour(pawn[0] + (_ATT_DOWN_RIGHT[0] / 2), pawn[1] + (_ATT_DOWN_RIGHT[1] / 2))
						.equals(enemy)) {
					jumpDirection[0] = _ATT_DOWN_RIGHT[0];
					jumpDirection[1] = _ATT_DOWN_RIGHT[1];
					canJump = true;
				}
			}
		} else
			return false;

		// direction is known and jump is made
		if (!canJump)
			return false;
		//deletes the pawn in the middle and moves the pawn 
		if (!deletePawn(pawn, pawn[0] + (jumpDirection[0] / 2), pawn[1] + (jumpDirection[1] / 2)))
			return false;
		if (!movePawn(pawn, dest))
			return false;

		return true;
	}

	protected void createBoard() {
		boolean black = false; // counter of black fields not pawns
		int pawnCount = 0; // number to place the new pawn into array

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 10; j++) {
				if (black && j == 9) { // make black fields diagonal
					board.addField(i, j, _WHITE);
					teams[1].pawns.add(new Pawn(i, j, 1));
					pawnCount++;
					continue;
				}
				if (!black && j == 9) { // make black fields diagonal
					board.addField(i, j, _EMPTY);
					black = false;
					continue;
				}

				if (black) {
					board.addField(i, j, _WHITE);
					teams[1].pawns.add(new Pawn(i, j, 1));
					pawnCount++;
					black = false;
				} else {
					board.addField(i, j, _EMPTY);
					black = true;
				}

			}
		}
		// middle empty 2
		for (int i = 4; i < 6; i++) {
			for (int j = 0; j < 10; j++) {
				board.addField(i, j, _EMPTY);
			}
		}
		pawnCount = 0;
		// bottom 4 - black
		for (int i = 6; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (black && j == 9) { // make black fields diagonal
					board.addField(i, j, _BLACK);
					teams[0].pawns.add(new Pawn(i, j, -1));
					pawnCount++;
					continue;
				}
				if (!black && j == 9) { // make black fields diagonal
					board.addField(i, j, _EMPTY);
					black = false;
					continue;
				}

				if (black) {
					board.addField(i, j, _BLACK);
					teams[0].pawns.add(new Pawn(i, j, -1));
					pawnCount++;
					black = false;
				} else {
					board.addField(i, j, _EMPTY);
					black = true;
				}
			}
		}
	}

	public static boolean positionsEqual(int[] posA, int[] posB) {
		if (posA.length != posB.length)
			return false;
		if (posA.length > 2 || posA.length < 2)
			return false;
		if (posA[0] == posB[0]) {
			if (posA[1] == posB[1])
				return true;
		}
		return false;

	}

	public void createPawn(int[] yx, String team) {
		if (team == _WHITE) {
			teams[1].pawns.add(new Pawn(yx[0], yx[1], 1));
			board.setColour(yx[0], yx[1], team);
		} else {
			teams[0].pawns.add(new Pawn(yx[0], yx[1], -1));
			board.setColour(yx[0], yx[1], team);
		}
	}

	protected boolean jumpAround(Team team) {
		//algorithms used to choose the longest jump 
		// check which pawn makes a biggest jump.
		//array stores the longest squence of moves.
		ArrayList<int[]> bestJump = new ArrayList<>();
		
		//iterate through the team's pawns
		for (int i = 0; i < team.pawns.size(); i++) {
			
			ArrayList<int[]> takenPawns = new ArrayList<>();
			//jumps stored as individual positions. So start pos = jumps[n] destination = jumps[n+1]
			ArrayList<int[]> jumps = new ArrayList<int[]>();	
			int[] origin = team.pawns.get(i).getVector();
			jumps.add(origin);	//jumps[0] is the original position before jumping
			Jump test = new Jump(board, origin, origin, team.enemy, jumps, takenPawns);	//recursive method
			ArrayList<int[]> jump = test.getBestMove();	// gives an array of largest sequence of moves
			if (jump.size() > bestJump.size() && jump.size() > 1) {
				//check the new sequence against previous pawn checked.
				bestJump = jump;
			}
		}
		// first position in the array is the original position. so jump size will
		// always be at least 1
		if (bestJump.size() > 1) {
			//Jump is a subclass of movement class in MoveHistory class, it holds a list of jump sequences
			MoveHistory_Jump j = new MoveHistory_Jump();
			for (int i = 0; i < bestJump.size(); i++) {
				if (i != bestJump.size() - 1) {
					//iterate through the sequence of jumps and add each one to the list
					j.addJump(bestJump.get(i), bestJump.get(i + 1), team.enemy);
					//invoke the actual jump method
					jump(bestJump.get(i), bestJump.get(i + 1), team.enemy);
				}

			}
			//add the sequence to the history of moves.
			_MOVEHISTORY.AddJump(j);
			return true;
		} else
			return false;

	}

	protected boolean moveAround(Team team) {
		// move algorithm
		// computer will make a move that could attack in next turn
		Random random = new Random();
		ArrayList<int[]> moves = new ArrayList<>(); // move and jump arraylists are rows of directions, n+1 where
		ArrayList<int[]> jumps = new ArrayList<>(); // n is the original position and +1 is a landing position
		for (int i = 0; i < team.pawns.size(); i++) { // so to get next move = n+2
			// get list of all possile moves(list contains the landing positions only, so
			// relative to the iterator)
			ArrayList<int[]> tempMoves = getAvailableMoves(team.pawns.get(i));
			if (!tempMoves.isEmpty()) {
				for (int[] mv : tempMoves) { // based on these moves see if a jump could be made in the next turn;
					if (canJump(mv)) {
						jumps.add(team.pawns.get(i).getVector());
						jumps.add(mv);
					} else {
						moves.add(team.pawns.get(i).getVector());
						moves.add(mv);
					}
				}
			}
		}
		
		//after iteration check if there are any moves that could lead to attack in the next round
		if (!jumps.isEmpty()) {
			if (jumps.size() > 2) {
				// if there is more than one move that would lead to jumps just choose one at random 
				int x = random.nextInt((jumps.size() - 1));
				if (x % 2 != 0) {
					_MOVEHISTORY.AddMove(jumps.get(x - 1), jumps.get(x));
					return movePawn(jumps.get(x - 1), jumps.get(x));
				} else {
					_MOVEHISTORY.AddMove(jumps.get(x), jumps.get(x + 1));
					return movePawn(jumps.get(x), jumps.get(x + 1));
				}
			} else {
				// only one move
				_MOVEHISTORY.AddMove(jumps.get(0), jumps.get(1));
				return movePawn(jumps.get(0), jumps.get(1));
			}
			//else simply choose from the other set
		} else if (!moves.isEmpty()) {
			if (moves.size() > 2) {
				int x = random.nextInt((moves.size() - 1));
				if (x % 2 != 0) {
					_MOVEHISTORY.AddMove(moves.get(x - 1), moves.get(x));
					return movePawn(moves.get(x - 1), moves.get(x));
				} else {
					_MOVEHISTORY.AddMove(moves.get(x), moves.get(x + 1));
					return movePawn(moves.get(x), moves.get(x + 1));
				}
			} else {
				_MOVEHISTORY.AddMove(moves.get(0), moves.get(1));
				return movePawn(moves.get(0), moves.get(1));
			}
		}

		else
			return false;

	}

	public String getTeam(int turn) {
		if (turn == 1)
			return Game._WHITE;
		else
			return Game._BLACK;
	}

	public String getEnemy(int turn) {
		if (turn == 1)
			return Game._BLACK;
		else
			return Game._WHITE;
	}

	public boolean saveGame(String name) {
		GameSerializer save = new GameSerializer();
		return save.saveGame(_MOVEHISTORY, name);
	}

	protected int checkMove(int[] pawnYX, int[] destYX) {
		//check the logic of the move. if it makes sense.
		// is pawn selected a valid place on the board
		if (((pawnYX[0] % 2) == 0 || pawnYX[0] == 0) & ((pawnYX[1] % 2) > 0)) {
		} else if (((pawnYX[1] % 2) == 0 || pawnYX[1] == 0) & ((pawnYX[0] % 2) > 0)) {
		} else {
			System.out.println("Invalid position on board, diagonal moves only");
			return 0;
		}
		// is pawn selected of the valid team
		if (!moveIsFriendlyPawn(pawnYX[0], pawnYX[1])) {
			System.out.println("Non-friendly pawn selected");
			return 0;
		}

		if (board.getColour(destYX[0], destYX[1]).equals(_EMPTY)) {
		} else {
			System.out.println("Selected destination occupied");
			return 0;
		}

		if (validMove(pawnYX, destYX)) {
			return 1;
		} else if (validJump(pawnYX, destYX)) {
			return 2;
		} else
			return 0;

	}

	protected boolean moveSyntaxValid(String x) {
		// checks validity of a single command
		// valid command = YX two integer values
		if (x.isEmpty()) {
			System.out.println("Use correct format ROW COLUMN example: 65");
			return false; // is not empty
		}
		if (x.length() != 2) {
			System.out.println("Use correct format ROW COLUMN example: 65");
			return false; // has exactly 2 char
		}
		// check if is a number 
		int numb;
		try {
			numb = Integer.parseInt(x);
		} catch (NumberFormatException e) {
			System.out.println("Use correct format ROW COLUMN example: 65");
			return false;
		}

		return true;

	}

	private boolean moveIsFriendlyPawn(int y, int x) {
		if (turn == 1) {
			if (teams[1].getPawn(y, x) == null)
				return false;
		} else if (turn == -1) {
			if (teams[0].getPawn(y, x) == null)
				return false;
		}
		return true;
	}

	protected boolean jumpLoop(int pawnYX[], int destYX[]) {
		//loop for a human player 
		MoveHistory_Jump j = new MoveHistory_Jump();
		Scanner scanner = new Scanner(System.in);
		String enemy = teams[t].enemy;
		String move;

		int[] pawn = pawnYX;
		int[] dest = destYX;

		// make first jump
		if (jump(pawn, dest, enemy) == false) {
			System.out.println("Couldn't make jump");
			return false;
		} else
			//jump is successful add the first position to the sequence of jumps. 
			//use .clone to only get the value since the reference is changed in the next line 
			j.addJump(pawn.clone(), dest.clone(), enemy);

		pawn[0] = dest[0]; // adjust new position
		pawn[1] = dest[1];
		while (canJump(pawn)) {
			boardUpdate();
			while (true) {
				System.out.println("Jump possibe: choose next position");
				move = scanner.nextLine();
				if (!moveSyntaxValid(move))
					continue;
				// set new destination
				dest[0] = Integer.parseInt(move.substring(0, 1));
				dest[1] = Integer.parseInt(move.substring(1, 2));

				if (jump(pawn, dest, enemy) == false)
					continue;
				// jump successful adjust position
				else {
					j.addJump(pawn.clone(), dest.clone(), enemy);
					pawn[0] = dest[0]; // adjust new position
					pawn[1] = dest[1];
					break;
				}
			}

		}

		_MOVEHISTORY.AddJump(j);
		switchPlayer();
		return true;
	}
}
