package Draughts;
import java.util.Scanner;

public class Draughts  {
	// main variables.

	public static Scanner scanner = new Scanner(System.in);
	String control = null;
	Game game = new Game();
	public void mainScreen() {

		System.out.println("***** Draughts *****\n");

		

		while (true) {
			System.out.println("\n" + "1. Player vs Player\n" + "2. Player vs AI\n" + "3. AI vs AI \n"+ "4. Previous Games\n"
					+ "0. Exit\n");
			control = scanner.nextLine();

			if (control.equals("1")) {
				playerVsPlayer();
			}

			if (control.equals("2")) {
				playerVsAI();
			}
			if (control.equals("3")) {
				AI_vs_AI();
			}
			if (control.equals("4")) {
				previousGames();
			}
			if (control.equals("0")) {
				break;
			}
		}

		scanner.close();

	}

	private void playerVsPlayer() {
		game.playerVsPlayer();
	}

	private void playerVsAI() {
		game.playerVSAI();
	}

	private void AI_vs_AI() {
		game.aiVsAi();
	}

	private void previousGames() {
		GameSerializer load = new GameSerializer();
		game = null;
		game = load.loadGame();
		System.out.println("Game Loaded, Please choose how to play it");
		return;
	}

	public static void main(String[] args) {

		Draughts game = new Draughts();

			game.mainScreen();			
	
	}

}
