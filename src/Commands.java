public class Commands {

    private AI ai = new AI();
    private Browser browser = new Browser();
    private AppLauncher appLauncher = new AppLauncher();
    private GeminiAI gemini = new GeminiAI();

    public String execute(String command) {

        command = command.trim();

        // Try built-in AI commands
        String response = ai.handle(command);

        if (response != null) {
            return response;
        }

        // Try browser commands
        response = browser.handle(command);

        if (response != null) {
            return response;
        }

        // Try application commands
        response = appLauncher.handle(command);

        if (response != null) {
            return response;
        }

        // Unknown command → Gemini
        return gemini.ask(command);
    }
}
