import java.util.HashMap;
import java.util.Map;

public class AppLauncher {

    private Map<String, String> apps = new HashMap<>();

    public AppLauncher() {

        apps.put("calculator", "calc");
        apps.put("calc", "calc");

        apps.put("paint", "mspaint");

        apps.put("notepad", "notepad");

        apps.put("command prompt", "cmd");
        apps.put("cmd", "cmd");

        apps.put("explorer", "explorer");

    }

    public boolean handle(String command) {

        command = command.toLowerCase().trim();

        if (!command.startsWith("open ")) {
            return false;
        }

        String app = command.substring(5);

        if (!apps.containsKey(app)) {
            return false;
        }

        try {

            Runtime.getRuntime().exec(apps.get(app));

            System.out.println("NOURI: Opening " + app + "...");

        } catch (Exception e) {

            System.out.println("NOURI: I couldn't open " + app + ".");

        }

        return true;
    }
}
