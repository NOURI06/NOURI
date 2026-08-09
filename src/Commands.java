public class Commands {

    private AI ai = new AI();
    private Browser browser = new Browser();
    private AppLauncher appLauncher = new AppLauncher();

    public String execute(String command) {

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
        // UNKNOWN
        // =========================

        return "I'm not sure how to do that yet.";
    }
}
