import java.util.HashMap;
import java.util.Map;

public class AppLauncher {

    private Map<String, String> apps = new HashMap<>();

    public AppLauncher() {

        // Calculator
        apps.put("calculator", "calc");
        apps.put("calc", "calc");

        // Paint
        apps.put("paint", "mspaint");

        // Notepad
        apps.put("notepad", "notepad");

        // Command Prompt
        apps.put("command prompt", "cmd");
        apps.put("cmd", "cmd");

        // File Explorer
        apps.put("explorer", "explorer");
        apps.put("file explorer", "explorer");
    }

    public String handle(String command) {

        command = command.toLowerCase().trim();

        // We only look for app-opening requests
        if (!containsAny(
                command,
                "open",
                "launch",
                "start",
                "run")) {

            return null;
        }

        // =========================
        // FIND APPLICATION
        // =========================

        for (Map.Entry<String, String> entry : apps.entrySet()) {

            String appName = entry.getKey();

            if (command.contains(appName)) {

                try {

                    Runtime.getRuntime().exec(
                            entry.getValue()
                    );

                    return "Opening " + appName + ".";

                } catch (Exception e) {

                    return "I couldn't open " + appName + ".";
                }
            }
        }

        return null;
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
