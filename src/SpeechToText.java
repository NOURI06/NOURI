import java.nio.file.Path;

public class SpeechToText {

    public static String transcribe(Path audioFile) {

        if (audioFile == null) {
            return null;
        }

        try {

            System.out.println(
                    "NOURI: Using Windows English-UK speech recognition..."
            );

            String audioPath =
                    audioFile.toAbsolutePath()
                            .toString()
                            .replace("'", "''");

            /*
             * We use a command grammar instead of
             * DictationGrammar.
             *
             * This gives Windows a much smaller
             * vocabulary to recognize accurately.
             */

            String command =
                    "Add-Type -AssemblyName System.Speech; " +

                    "$recognizers = " +
                    "[System.Speech.Recognition.SpeechRecognitionEngine]" +
                    "::InstalledRecognizers(); " +

                    "$info = $recognizers | " +
                    "Where-Object { $_.Culture.Name -eq 'en-GB' } | " +
                    "Select-Object -First 1; " +

                    "if (-not $info) { " +
                    "Write-Error 'English UK recognizer not found.'; " +
                    "exit 1; " +
                    "} " +

                    "$r = New-Object " +
                    "System.Speech.Recognition.SpeechRecognitionEngine($info); " +

                    "$choices = New-Object " +
                    "System.Collections.Generic.List[string]; " +

                    /*
                     * Basic NOURI commands
                     */

                    "$choices.Add('open calculator'); " +
                    "$choices.Add('launch calculator'); " +

                    "$choices.Add('open notepad'); " +
                    "$choices.Add('launch notepad'); " +

                    "$choices.Add('open file explorer'); " +
                    "$choices.Add('open explorer'); " +

                    "$choices.Add('open nouri folder'); " +

                    "$choices.Add('lock my pc'); " +
                    "$choices.Add('lock the computer'); " +

                    "$choices.Add('what time is it'); " +
                    "$choices.Add('what is the time'); " +

                    "$choices.Add('what is today''s date'); " +
                    "$choices.Add('what date is it'); " +

                    "$choices.Add('hello nouri'); " +
                    "$choices.Add('hello'); " +

                    "$choices.Add('go to sleep'); " +
                    "$choices.Add('sleep now'); " +
                    "$choices.Add('stop listening'); " +
                    "$choices.Add('goodbye'); " +

                    /*
                     * Create a Choices grammar.
                     */

                    "$choicesObject = " +
                    "New-Object System.Speech.Recognition.Choices; " +

                    "foreach ($choice in $choices) { " +
                    "$choicesObject.Add($choice); " +
                    "} " +

                    "$builder = " +
                    "New-Object System.Speech.Recognition.GrammarBuilder; " +

                    "$builder.Append($choicesObject); " +

                    "$grammar = " +
                    "New-Object System.Speech.Recognition.Grammar($builder); " +

                    "$r.LoadGrammar($grammar); " +

                    "$r.SetInputToWaveFile('" +
                    audioPath +
                    "'); " +

                    "$result = $r.Recognize(); " +

                    "if ($result) { " +
                    "Write-Output $result.Text " +
                    "} " +

                    "$r.Dispose();";

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

            Process process =
                    pb.start();

            String output =
                    new String(
                            process.getInputStream()
                                    .readAllBytes()
                    ).trim();

            int exitCode =
                    process.waitFor();

            if (exitCode != 0) {

                System.out.println(
                        "NOURI: Speech recognition failed."
                );

                System.out.println(output);

                return null;
            }

            if (output.isBlank()) {

                System.out.println(
                        "NOURI: I couldn't understand the command."
                );

                return null;
            }

            System.out.println(
                    "NOURI heard: " + output
            );

            return output;

        } catch (Exception e) {

            System.out.println(
                    "NOURI STT error: "
                            + e.getMessage()
            );

            return null;
        }
    }
}
