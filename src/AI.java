import java.time.LocalDate;
import java.time.LocalTime;

public class AI {

    public String handle(String command) {

        command = command.toLowerCase().trim();

        if (command.equals("hello")) {
            return "Hello! Nice to see you.";
        }

        if (command.equals("hi")) {
            return "Hello! How can I help you?";
        }

        if (command.equals("hey")) {
            return "Hey! What can I do for you?";
        }

        if (command.equals("time")) {
            return "The time is " + LocalTime.now().withNano(0);
        }

        if (command.equals("date")) {
            return "Today is " + LocalDate.now();
        }

        if (command.equals("who are you")) {
            return "I am NOURI, your personal desktop assistant.";
        }

        if (command.equals("how are you")) {
            return "I'm doing great! Ready to help.";
        }

        if (command.equals("what can you do")) {
            return "I can open applications, control your browser, tell you the time and date, and respond to your commands.";
        }

        return null;
    }
}
