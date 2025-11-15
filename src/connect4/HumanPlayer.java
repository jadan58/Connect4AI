package connect4;
import java.util.*;
public class HumanPlayer implements Player{
	int playerNumber = 1;
	public HumanPlayer() {
		
	}
	@Override
	public Move getMove(Board board) {
		Scanner sc = new Scanner(System.in);
		List <Integer> valid = board.getValidCols();
		System.out.println("Select a valid move: ");
		System.out.println(valid);
		int choice = sc.nextInt();
		if(!board.isValidMove(choice)) {
			System.out.println("Not a valid choice try again: ");
			choice = sc.nextInt();
		}
		return new Move(playerNumber, choice);
	}
}
