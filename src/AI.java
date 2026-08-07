import java.time.LocalDate;
import java.time.LocalTime;

public class AI {

    public boolean handle(String command) {

        command = command.toLowerCase();

        if (command.equals("hello")) {
            System.out.println("NOURI: Hello! Nice to see you.");
            return true;
        }

        if (command.equals("time")) {
            System.out.println("NOURI: The time is " + LocalTime.now().withNano(0));
            return true;
        }

        if (command.equals("date")) {
            System.out.println("NOURI: Today is " + LocalDate.now());
            return true;
        }

        if (command.equals("who are you")) {
            System.out.println("NOURI: I am NOURI, your personal desktop assistant.");
            return true;
        }

        return false;
    }
}
