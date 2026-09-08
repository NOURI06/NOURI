import java.io.File;

public class PCController {

    public static String handle(String text) {

        if (text == null || text.isBlank()) {
            return null;
        }

        String lower = text.toLowerCase().trim();

        // ================================
        // OPEN NOTEPAD
        // ================================

        if (lower.equals("open notepad")
                || lower.equals("launch notepad")) {

            return launch(
                    "notepad.exe",
                    "Notepad"
            );
        }

        // ================================
        // OPEN CALCULATOR
        // ================================

        if (lower.equals("open calculator")
                || lower.equals("launch calculator")) {

            return launch(
                    "calc.exe",
                    "Calculator"
            );
        }

        // ================================
        // OPEN FILE EXPLORER
        // ================================

        if (lower.equals("open file explorer")
                || lower.equals("open explorer")) {

            return launch(
                    "explorer.exe",
                    "File Explorer"
            );
        }

        // ================================
        // OPEN NOURI FOLDER
        // ================================

        if (lower.equals("open nouri folder")
                || lower.equals("open my nouri folder")) {

            return openPath(
                    "C:\\Users\\dell\\Desktop\\NOURI",
                    "NOURI folder"
            );
        }

        // ================================
        // LOCK PC
        // ================================

        if (lower.equals("lock my pc")
                || lower.equals("lock the computer")) {

            try {

                new ProcessBuilder(
                        "rundll32.exe",
                        "user32.dll,LockWorkStation"
                ).start();

                return "The computer is locked, sir.";

            } catch (Exception e) {

                return "I was unable to lock the computer, sir.";
            }
        }

        return null;
    }

    private static String launch(
            String program,
            String name) {

        try {

            new ProcessBuilder(program).start();

            return name +
                    " is now open, sir.";

        } catch (Exception e) {

            System.out.println(
                    "NOURI PC error: "
                            + e.getMessage()
            );

            return "I was unable to open " +
                    name +
                    ", sir.";
        }
    }

    private static String openPath(
            String path,
            String name) {

        try {

            File file =
                    new File(path);

            if (!file.exists()) {

                return name +
                        " could not be found, sir.";
            }

            new ProcessBuilder(
                    "explorer.exe",
                    path
            ).start();

            return name +
                    " is now open, sir.";

        } catch (Exception e) {

            return "I was unable to open " +
                    name +
                    ", sir.";
        }
    }
}
