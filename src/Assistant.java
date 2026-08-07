import java.util.Scanner;

public class Assistant {

    private Scanner scanner = new Scanner(System.in);
    private Commands commands = new Commands();
    private Memory memory = new Memory();
    private Conversation conversation = new Conversation();

    public void start() {

        System.out.println("=================================");
        System.out.println("        NOURI Assistant");
        System.out.println("=================================");

        System.out.print("What's your name? ");
        String name = scanner.nextLine();

        memory.setUserName(name);

        System.out.println("NOURI: Nice to meet you, " + memory.getUserName() + "!");

        while (true) {

            System.out.print("You: ");
            String input = scanner.nextLine();

            conversation.add("You: " + input);

            if (input.equalsIgnoreCase("exit")) {

                System.out.println("NOURI: Goodbye!");
                break;

            } else if (input.equalsIgnoreCase("history")) {

                conversation.show();
                continue;
            }

            String response = commands.execute(input);

            conversation.add("NOURI: " + response);

            System.out.println("NOURI: " + response);
        }

        scanner.close();
    }
}