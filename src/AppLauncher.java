import java.util.HashMap;
import java.util.Map;

public class AppLauncher {

private final Map<String, String> apps =
        new HashMap<>();

public AppLauncher() {

    apps.put("calculator", "calc");
    apps.put("calc", "calc");

    apps.put("paint", "mspaint");

    apps.put("notepad", "notepad");

    apps.put("command prompt", "cmd");
    apps.put("cmd", "cmd");

    apps.put("explorer", "explorer");
    apps.put("file explorer", "explorer");
}

public CommandResult prepare(String command) {

    command = command.toLowerCase().trim();

    if (!containsAny(
            command,
            "open",
            "launch",
            "start",
            "run")) {

        return null;
    }

    for (Map.Entry<String, String> entry :
            apps.entrySet()) {

        String appName = entry.getKey();
        String executable = entry.getValue();

        if (command.contains(appName)) {

            return new CommandResult(
                    "Opening " + appName + ".",
                    () -> launch(
                            appName,
                            executable
                    )
            );
        }
    }

    return null;
}

private void launch(
        String appName,
        String executable) {

    try {

        Runtime.getRuntime().exec(
                executable
        );

        System.out.println(
                "NOURI: " + appName + " launched."
        );

    } catch (Exception e) {

        System.out.println(
                "NOURI: Couldn't open "
                        + appName
                        + ": "
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

}
