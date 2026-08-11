import java.nio.file.Path;

public class SpeechToText {

    public static String transcribe(Path audioFile) {

        try {

            System.out.println(
                    "NOURI: Using Windows English-UK speech recognition..."
            );

            String audioPath =
                    audioFile.toAbsolutePath()
                            .toString()
                            .replace("'", "''");

            String command =
                    "Add-Type -AssemblyName System.Speech; " +
                    "$r = New-Object System.Speech.Recognition.SpeechRecognitionEngine('en-GB'); " +
                    "$r.LoadGrammar(New-Object System.Speech.Recognition.DictationGrammar); " +
                    "$r.SetInputToWaveFile('" + audioPath + "'); " +
                    "$result = $r.Recognize(); " +
                    "if ($result) { Write-Output $result.Text }";

            ProcessBuilder pb =
                    new ProcessBuilder(
                            "powershell.exe",
                            "-NoProfile",
                            "-ExecutionPolicy",
                            "Bypass",
                            "-Command",
                            command
                    );

            pb.redirectErrorStream(true);

            Process process = pb.start();

            String output =
                    new String(
                            process.getInputStream().readAllBytes()
                    ).trim();

            int exitCode = process.waitFor();

            if (exitCode != 0) {

                System.out.println(
                        "NOURI: Windows speech recognition failed."
                );

                System.out.println(output);

                return null;
            }

            if (output.isBlank()) {

                System.out.println(
                        "NOURI: I couldn't understand the audio."
                );

                return null;
            }

            System.out.println(
                    "NOURI heard: " + output
            );

            return output;

        } catch (Exception e) {

            System.out.println(
                    "NOURI STT error: " + e.getMessage()
            );

            return null;
        }
    }
}
