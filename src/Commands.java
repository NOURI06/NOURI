public class Commands {

    private AI ai = new AI();
    private Browser browser = new Browser();
    private AppLauncher appLauncher = new AppLauncher();
    private GeminiAI gemini = new GeminiAI();

    public String execute(String command) {

        command = command.trim();

        // Built-in NOURI commands
        if (ai.handle(command)) {
            return "Done.";
        }

        // Browser commands
        if (browser.handle(command)) {
            return "Done.";
        }

        // Application commands
        if (appLauncher.handle(command)) {
            return "Done.";
        }

        // If NOURI doesn't recognize the command,
        // ask Gemini.
        return gemini.ask(command);
    }
}
