```java
public class Commands {

    private AI ai = new AI();
    private Browser browser = new Browser();
    private AppLauncher appLauncher = new AppLauncher();

    public String execute(String command) {

        command = command.toLowerCase().trim();

        if (ai.handle(command)) {
            return "Done.";
        }

        if (browser.handle(command)) {
            return "Opening it for you.";
        }

        if (appLauncher.handle(command)) {
            return "Opening the application.";
        }

        return "I don't understand that command.";
    }
}
```
