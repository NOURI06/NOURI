import java.awt.Desktop;
import java.net.URI;

public class Browser {

    public String handle(String command) {

        command = command.toLowerCase().trim();

        try {

            // =========================
            // GOOGLE SEARCH
            // =========================

            if (command.startsWith("search ")) {

                String query = command.substring(7).trim();

                if (!query.isEmpty()) {
                    openUrl(
                        "https://www.google.com/search?q=" +
                        query.replace(" ", "+")
                    );

                    return "Searching Google for " + query + ".";
                }
            }

            // "google cats"
            if (command.startsWith("google ")) {

                String query = command.substring(7).trim();

                if (!query.isEmpty()) {
                    openUrl(
                        "https://www.google.com/search?q=" +
                        query.replace(" ", "+")
                    );

                    return "Searching Google for " + query + ".";
                }
            }

            // =========================
            // YOUTUBE
            // =========================

            if (containsAny(command,
                    "youtube",
                    "you tube")) {

                openUrl("https://www.youtube.com");

                return "Opening YouTube.";
            }

            // =========================
            // GOOGLE
            // =========================

            if (containsAny(command,
                    "open google",
                    "go to google",
                    "launch google")) {

                openUrl("https://www.google.com");

                return "Opening Google.";
            }

            // =========================
            // GITHUB
            // =========================

            if (containsAny(command,
                    "github",
                    "git hub")) {

                openUrl("https://github.com");

                return "Opening GitHub.";
            }

            // =========================
            // CHATGPT
            // =========================

            if (containsAny(command,
                    "chatgpt",
                    "chat gpt")) {

                openUrl("https://chatgpt.com");

                return "Opening ChatGPT.";
            }

        } catch (Exception e) {

            return "I couldn't open the browser.";
        }

        return null;
    }

    private void openUrl(String url) throws Exception {

        if (Desktop.isDesktopSupported()) {

            Desktop.getDesktop().browse(
                    new URI(url)
            );
        }
    }

    private boolean containsAny(
            String command,
            String... words) {

        for (String word : words) {

            if (command.contains(word)) {
                return true;
            }
        }

        return false;
    }
}
