import java.util.Scanner;

public class Assistant {

    private Scanner scanner = new Scanner(System.in);

    private Commands commands = new Commands();
    private Memory memory = new Memory();
    private Conversation conversation = new Conversation();

    private boolean awake = false;

    public static void main(String[] args) {
        Assistant assistant = new Assistant();
        assistant.start();
    }

    public void start() {

        System.out.println("=================================");
        System.out.println("        NOURI Assistant");
        System.out.println("=================================");
        System.out.println();
        System.out.println("NOURI is sleeping.");
        System.out.println("Wake word: Buddy");
        System.out.println();

        Voice.speak("NOURI is online and waiting.");

        while (true) {

            System.out.print("You: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            conversation.add("You: " + input);

            // =========================
            // WAKE WORD
            // =========================

            if (!awake) {

                if (input.equalsIgnoreCase("buddy")) {

                    awake = true;

                    String response =
                            "Yes, sir. How may I assist you?";

                    System.out.println("NOURI: " + response);
                    Voice.speak(response);

                    conversation.add("NOURI: " + response);

                } else if (input.equalsIgnoreCase("exit")) {

                    Voice.speak("Goodbye, sir.");
                    break;

                } else {

                    System.out.println("NOURI is sleeping...");

                }

                continue;
            }

            // =========================
            // ACTIVE MODE
            // =========================

            if (input.equalsIgnoreCase("sleep")) {

                awake = false;

                String response =
                        "Of course, sir. I shall be here when you need me.";

                System.out.println("NOURI: " + response);
                Voice.speak(response);

                conversation.add("NOURI: " + response);

                System.out.println();
                System.out.println("NOURI is sleeping.");

                continue;
            }

            // =========================
            // EXIT
            // =========================

            if (input.equalsIgnoreCase("exit")) {

                String response =
                        "Goodbye, sir. It was a pleasure assisting you.";

                System.out.println("NOURI: " + response);
                Voice.speak(response);

                break;
            }

            // =========================
            // HISTORY
            // =========================

            if (input.equalsIgnoreCase("history")) {

                conversation.show();
                continue;
            }

            // =========================
            // COMMANDS
            // =========================

            String response = commands.execute(input);

            conversation.add("NOURI: " + response);

            System.out.println("NOURI: " + response);

            Voice.speak(response);
        }

        scanner.close();
    }
}
