import java.net.URI;
import java.awt.Desktop;

public class Browser {

    public boolean handle(String command) {

        command = command.toLowerCase().trim();

        try {

            // Search Google
            if (command.startsWith("search ")) {

                String query = command.substring(7).replace(" ", "+");

                Desktop.getDesktop().browse(
                        new URI("https://www.google.com/search?q=" + query));

                System.out.println("NOURI: Searching Google for " +
                        query.replace("+", " "));

                return true;
            }

            // Open websites
            if (command.equals("open youtube")) {

                Desktop.getDesktop().browse(
                        new URI("https://www.youtube.com"));

                System.out.println("NOURI: Opening YouTube...");
                return true;
            }

            if (command.equals("open google")) {

                Desktop.getDesktop().browse(
                        new URI("https://www.google.com"));

                System.out.println("NOURI: Opening Google...");
                return true;
            }

            if (command.equals("open github")) {

                Desktop.getDesktop().browse(
                        new URI("https://github.com"));

                System.out.println("NOURI: Opening GitHub...");
                return true;
            }

            if (command.equals("open chatgpt")) {

                Desktop.getDesktop().browse(
                        new URI("https://chatgpt.com"));

                System.out.println("NOURI: Opening ChatGPT...");
                return true;
            }

        } catch (Exception e) {

            System.out.println("NOURI: I couldn't open the browser.");
            return true;
        }

        return false;
    }
}
