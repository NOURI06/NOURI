import java.io.IOException;

public class SystemCommands {

    public String handle(String command) {

        command = command.toLowerCase().trim();

        // =========================
        // WINDOWS SETTINGS
        // =========================

        if (command.contains("open settings")) {
            return run("start ms-settings:");
        }

        // =========================
        // TASK MANAGER
        // =========================

        if (command.contains("open task manager")) {
            return run("taskmgr");
        }

        // =========================
        // CONTROL PANEL
        // =========================

        if (command.contains("open control panel")) {
            return run("control");
        }

        // =========================
        // DESKTOP
        // =========================

        if (command.contains("open desktop")) {
            return run("explorer shell:Desktop");
        }

        // =========================
        // DOWNLOADS
        // =========================

        if (command.contains("open downloads")) {
            return run("explorer shell:Downloads");
        }

        // =========================
        // DOCUMENTS
        // =========================

        if (command.contains("open documents")) {
            return run("explorer shell:Documents");
        }

        // =========================
        // PICTURES
        // =========================

        if (command.contains("open pictures")) {
            return run("explorer shell:Pictures");
        }

        // =========================
        // MUSIC
        // =========================

        if (command.contains("open music")) {
            return run("explorer shell:Music");
        }

        // =========================
        // VIDEOS
        // =========================

        if (command.contains("open videos")) {
            return run("explorer shell:Videos");
        }

        return null;
    }

    private String run(String command) {

        try {

            Runtime.getRuntime().exec(
                    new String[]{"cmd", "/c", command}
            );

            return "Opening it for you.";

        } catch (IOException e) {

            return "I couldn't open that.";
        }
    }
}
