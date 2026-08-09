```java
public class Commands {

    private AI ai = new AI();
    private Browser browser = new Browser();
    private AppLauncher appLauncher = new AppLauncher();

    public String execute(String command) {

        command = command.toLowerCase().trim();

        // AI commands
        if (ai.handle(command)) {
            return "Done.";
        }

        // Browser commands
        if (browser.handle(command)) {
            return "Opening it for you.";
        }

        // Application commands
        if (appLauncher.handle(command)) {
            return "Opening the application.";
        }

        // Unknown command
        return "I don't understand that command.";
    }
}
```
