package connect4;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Choose bot difficulty:");
        System.out.println("1 - Easy");
        System.out.println("2 - Medium");
        System.out.println("3 - Hard");
        int choice = sc.nextInt();

        Player bot;

        switch (choice) {
            case 1 -> bot = new EasyBot();
            case 2 -> bot = new MediumBot();
            case 3 -> bot = new HardBot();
            default -> {
                System.out.println("Invalid choice. Defaulting to Easy.");
                bot = new EasyBot();
            }
        }

        Game game = new Game(new HumanPlayer(), bot);
        game.start();

        sc.close();
    }
}
