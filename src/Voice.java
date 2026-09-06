import java.io.*;

public class Voice {

    public static void speak(String text) {

        if (text == null || text.isBlank()) {
            return;
        }

        System.out.println("NOURI: Speaking...");

        windowsSpeak(text);
    }

    private static void windowsSpeak(String text) {

        try {

            String safeText = text
                    .replace("'", "''")
                    .replace("\r", " ")
                    .replace("\n", " ");

            String command =
                    "Add-Type -AssemblyName System.Speech; " +
                    "$voice = New-Object " +
                    "System.Speech.Synthesis.SpeechSynthesizer; " +
                    "$voice.SelectVoice('Microsoft George'); " +
                    "$voice.Volume = 100; " +
                    "$voice.Rate = 1; " +
                    "$voice.Speak('" +
                    safeText +
                    "'); " +
                    "$voice.Dispose()";

            ProcessBuilder process =
                    new ProcessBuilder(
                            "powershell.exe",
                            "-NoProfile",
                            "-ExecutionPolicy",
                            "Bypass",
                            "-Command",
                            command
                    );

            process.redirectErrorStream(true);

            Process p = process.start();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    p.getInputStream()
                            )
                    );

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = p.waitFor();

            if (exitCode == 0) {

                System.out.println(
                        "NOURI: Windows voice finished."
                );

            } else {

                System.out.println(
                        "NOURI: Windows voice exited with code "
                                + exitCode
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "NOURI Windows voice error: "
                            + e.getMessage()
            );
        }
    }
}
