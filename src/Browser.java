import java.awt.Desktop;
import java.net.URI;

public class Browser {

public CommandResult prepare(String command) {

    command = command.toLowerCase().trim();

    // =========================
    // GOOGLE SEARCH
    // =========================

    if (command.startsWith("search ")) {

        String query = command.substring(7).trim();

        if (!query.isEmpty()) {

            String url =
                    "https://www.google.com/search?q=" +
                    query.replace(" ", "+");

            return new CommandResult(
                    "Searching Google for " + query + ".",
                    () -> openUrl(url)
            );
        }
    }

    // "google cats"
    if (command.startsWith("google ")) {

        String query = command.substring(7).trim();

        if (!query.isEmpty()) {

            String url =
                    "https://www.google.com/search?q=" +
                    query.replace(" ", "+");

            return new CommandResult(
                    "Searching Google for " + query + ".",
                    () -> openUrl(url)
            );
        }
    }

    // =========================
    // YOUTUBE
    // =========================

    if (containsAny(
            command,
            "youtube",
            "you tube")) {

        return new CommandResult(
                "Opening YouTube.",
                () -> openUrl("https://www.youtube.com")
        );
    }

    // =========================
    // GOOGLE
    // =========================

    if (containsAny(
            command,
            "open google",
            "go to google",
            "launch google")) {

        return new CommandResult(
                "Opening Google.",
                () -> openUrl("https://www.google.com")
        );
    }

    // =========================
    // GITHUB
    // =========================

    if (containsAny(
            command,
            "github",
            "git hub")) {

        return new CommandResult(
                "Opening GitHub.",
                () -> openUrl("https://github.com")
        );
    }

    // =========================
    // CHATGPT
    // =========================

    if (containsAny(
            command,
            "chatgpt",
            "chat gpt")) {

        return new CommandResult(
                "Opening ChatGPT.",
                () -> openUrl("https://chatgpt.com")
        );
    }

    return null;
}

private void openUrl(String url) {

    try {

        if (Desktop.isDesktopSupported()) {

            Desktop.getDesktop().browse(
                    new URI(url)
            );

        } else {

            System.out.println(
                    "NOURI: Desktop browsing is not supported."
            );
        }

    } catch (Exception e) {

        System.out.println(
                "NOURI: Browser action failed: "
                        + e.getMessage()
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
