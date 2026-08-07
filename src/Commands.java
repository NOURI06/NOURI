import java.time.LocalTime;

public class Commands {

    public String execute(String command) {

        command = command.toLowerCase();

        try {

            if (command.startsWith("open ")) {

                String app = command.substring(5);

                if (app.equals("youtube")) {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler https://www.youtube.com");
                    return "Opening YouTube...";
                }

                if (app.equals("google")) {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler https://www.google.com");
                    return "Opening Google...";
                }

                if (app.equals("github")) {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler https://github.com");
                    return "Opening GitHub...";
                }

                if (app.equals("chatgpt")) {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler https://chatgpt.com");
                    return "Opening ChatGPT...";
                }

                if (app.equals("calculator"))
                    app = "calc";
                else if (app.equals("paint"))
                    app = "mspaint";
                else if (app.equals("command prompt"))
                    app = "cmd";

                Runtime.getRuntime().exec(app);

                return "Opening " + app + "...";

            }

            if (command.equals("hello"))
                return "Hello!";

            if (command.equals("time"))
                return "The current time is " + LocalTime.now().withNano(0);

            if (command.startsWith("search ")) {

                String query = command.substring(7).replace(" ", "+");

                Runtime.getRuntime().exec(
                        "rundll32 url.dll,FileProtocolHandler https://www.google.com/search?q=" + query);

                return "Searching Google for " + query.replace("+", " ");
            }

            return "I don't understand that command.";

        } catch (Exception e) {

            return "I couldn't complete that command.";
        }
    }
}