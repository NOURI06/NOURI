public class Commands {

    private AI ai = new AI();
    private Browser browser = new Browser();
    private AppLauncher appLauncher = new AppLauncher();
    private SystemCommands systemCommands = new SystemCommands();

    public String execute(String command) {

        if (command == null || command.trim().isEmpty()) {
            return "I didn't hear a command.";
        }

        command = command.toLowerCase().trim();

        // =========================
        // AI
        // =========================

        String aiResponse = ai.handle(command);

        if (aiResponse != null) {
            return aiResponse;
        }

        // =========================
        // BROWSER
        // =========================

        String browserResponse = browser.handle(command);

        if (browserResponse != null) {
            return browserResponse;
        }

        // =========================
        // APPLICATIONS
        // =========================

        String appResponse = appLauncher.handle(command);

        if (appResponse != null) {
            return appResponse;
        }

        // =========================
        // WINDOWS SYSTEM
        // =========================

        String systemResponse = systemCommands.handle(command);

        if (systemResponse != null) {
            return systemResponse;
        }

        // =========================
        // UNKNOWN COMMAND
        // =========================

        return "I'm not sure how to do that yet.";
    }
}
