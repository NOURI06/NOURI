public class Commands {

    private AI ai = new AI();
    private Browser browser = new Browser();
    private AppLauncher appLauncher = new AppLauncher();
    private GeminiAI gemini = new GeminiAI();

    public String execute(String command) {

        command = command.trim();

        String response = ai.handle(command);

        if (response != null) {
            return response;
        }

        response = browser.handle(command);

        if (response != null) {
            return response;
        }

        response = appLauncher.handle(command);

        if (response != null) {
            return response;
        }

        return gemini.ask(command);
    }
}
