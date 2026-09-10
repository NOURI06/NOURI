public class Commands {

private final AI ai = new AI();
private final Browser browser = new Browser();
private final AppLauncher appLauncher = new AppLauncher();
private final GeminiAI gemini = new GeminiAI();

public CommandResult prepare(String command) {

    command = command.trim();

    // =========================
    // BASIC AI
    // =========================

    String response = ai.handle(command);

    if (response != null) {

        return new CommandResult(
                response,
                null
        );
    }

    // =========================
    // BROWSER
    // =========================

    CommandResult browserResult =
            browser.prepare(command);

    if (browserResult != null) {
        return browserResult;
    }

    // =========================
    // APPLICATIONS
    // =========================

    CommandResult appResult =
            appLauncher.prepare(command);

    if (appResult != null) {
        return appResult;
    }

    // =========================
    // GEMINI
    // =========================

    response = gemini.ask(command);

    return new CommandResult(
            response,
            null
    );
}

/*
 * Compatibility method.
 *
 * This still works for other parts
 * of NOURI that may call execute().
 */
public String execute(String command) {

    CommandResult result =
            prepare(command);

    result.performAction();

    return result.getResponse();
}
