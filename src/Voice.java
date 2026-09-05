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

            // Make text safe for SSML/XML
            String safeText = text
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;")
                    .replace("\r", " ")
                    .replace("\n", " ");

            /*
             * NOURI voice settings:
             *
             * Voice  : Microsoft George
             * Accent : English (Great Britain)
             * Rate   : +8%
             * Pitch  : +1 semitone
             * Volume : 100
             *
             * No artificial breaks.
             */

            String ssml =
                    "<speak version=\"1.0\" "
                    + "xmlns=\"http://www.w3.org/2001/10/synthesis\" "
                    + "xml:lang=\"en-GB\">"
                    + "<prosody rate=\"+8%\" pitch=\"+1st\">"
                    + safeText
                    + "</prosody>"
                    + "</speak>";

            // Escape the SSML for PowerShell
            String safeSsml = ssml
                    .replace("`", "``")
                    .replace("\"", "`\"")
                    .replace("$", "`$");

            String command =
                    "Add-Type -AssemblyName System.Speech; "
                    + "$voice = New-Object "
                    + "System.Speech.Synthesis.SpeechSynthesizer; "
                    + "$voice.SelectVoice('Microsoft George'); "
                    + "$voice.Volume = 100; "
                    + "$voice.SpeakSsml(\""
                    + safeSsml
                    + "\"); "
                    + "$voice.Dispose()";

            ProcessBuilder process =
                    new ProcessBuilder(
                            "powershell",
                            "-NoProfile",
                            "-Command",
                            command
                    );

            process.redirectErrorStream(true);

            Process p = process.start();

            // Show PowerShell output if there is an error
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
