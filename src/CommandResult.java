public class CommandResult {

private final String response;
private final Runnable action;

public CommandResult(String response, Runnable action) {
    this.response = response;
    this.action = action;
}

public String getResponse() {
    return response;
}

public void performAction() {
    if (action != null) {
        action.run();
    }
}

}
