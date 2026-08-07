public class Commands {

    private AI ai = new AI();
    private Browser browser = new Browser();
    private AppLauncher appLauncher = new AppLauncher();

    public void execute(String command) {

        command = command.toLowerCase().trim();

        if (ai.handle(command)) {
            return;
        }

        if (browser.handle(command)) {
            return;
        }

        if (appLauncher.handle(command)) {
            return;
        }

        System.out.println("NOURI: I don't understand that command.");
    }
}
