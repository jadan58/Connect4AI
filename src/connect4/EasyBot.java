package connect4;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class EasyBot implements Player{
	int playerNumber = 2;
    private Random rand = new Random();
	public EasyBot() {
		
	}
	@Override
	public Move getMove(Board board) {
		List <Integer> valid = board.getValidCols();
		int choice = valid.get(rand.nextInt(valid.size()));
		System.out.println("EasyBot chooses column: " + choice);
	    return new Move(playerNumber, choice);
	}
}
