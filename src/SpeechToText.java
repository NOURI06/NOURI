import java.io.*;
import java.nio.file.Path;

public class SpeechToText {

    private static Process recognizerProcess;
    private static BufferedWriter recognizerInput;
    private static BufferedReader recognizerOutput;

    private static final Object LOCK = new Object();

    private static void startRecognizer() throws Exception {

        if (recognizerProcess != null
                && recognizerProcess.isAlive()) {
            return;
        }

        System.out.println(
                "NOURI: Loading Windows speech recognition..."
        );

        String psScript =
                "$ErrorActionPreference = 'Stop'; " +

                "Add-Type -AssemblyName System.Speech; " +

                "$info = [System.Speech.Recognition.SpeechRecognitionEngine]" +
                "::InstalledRecognizers() | " +
                "Where-Object { $_.Culture.Name -eq 'en-GB' } | " +
                "Select-Object -First 1; " +

                "if (-not $info) { " +
                "Write-Output '__ERROR__No en-GB recognizer'; exit 1; } " +

                "$r = New-Object " +
                "System.Speech.Recognition.SpeechRecognitionEngine($info); " +

                "$choices = New-Object " +
                "System.Speech.Recognition.Choices; " +

                "$phrases = @(" +
                "'open calculator'," +
                "'open calc'," +
                "'launch calculator'," +
                "'open notepad'," +
                "'launch notepad'," +
                "'open browser'," +
                "'open chrome'," +
                "'launch browser'," +
                "'open explorer'," +
                "'open file explorer'," +
                "'open nouri'," +
                "'go to sleep'," +
                "'sleep now'," +
                "'stop listening'," +
                "'goodbye'," +
                "'hello'," +
                "'hi'," +
                "'hey'," +
                "'who are you'," +
                "'what are you'," +
                "'what time is it'," +
                "'what is the time'," +
                "'time'," +
                "'what is today s date'," +
                "'what date is it'," +
                "'date'" +
                "); " +

                "foreach ($phrase in $phrases) { " +
                "$choices.Add($phrase); } " +

                "$builder = New-Object " +
                "System.Speech.Recognition.GrammarBuilder; " +

                "$builder.Append($choices); " +

                "$grammar = New-Object " +
                "System.Speech.Recognition.Grammar($builder); " +

                "$r.LoadGrammar($grammar); " +

                "$r.InitialSilenceTimeout = " +
                "[TimeSpan]::FromMilliseconds(1500); " +

                "$r.BabbleTimeout = " +
                "[TimeSpan]::FromMilliseconds(1000); " +

                "$r.EndSilenceTimeout = " +
                "[TimeSpan]::FromMilliseconds(400); " +

                "$r.EndSilenceTimeoutAmbiguous = " +
                "[TimeSpan]::FromMilliseconds(600); " +

                "Write-Output '__READY__'; " +

                "while ($true) { " +

                "$path = [Console]::ReadLine(); " +

                "if ($null -eq $path -or $path -eq '__EXIT__') { break; } " +

                "try { " +

                "$r.SetInputToWaveFile($path); " +

                "$result = $r.Recognize(); " +

                "if ($result) { " +
                "Write-Output ('__RESULT__' + $result.Text); " +
                "} else { " +
                "Write-Output '__RESULT__'; " +
                "} " +

                "} catch { " +
                "Write-Output ('__ERROR__' + $_.Exception.Message); " +
                "} " +

                "} " +

                "$r.Dispose();";

        recognizerProcess =
                new ProcessBuilder(
                        "powershell.exe",
                        "-NoProfile",
                        "-ExecutionPolicy",
                        "Bypass",
                        "-Command",
                        psScript
                )
                .redirectErrorStream(true)
                .start();

        recognizerInput =
                new BufferedWriter(
                        new OutputStreamWriter(
                                recognizerProcess.getOutputStream()
                        )
                );

        recognizerOutput =
                new BufferedReader(
                        new InputStreamReader(
                                recognizerProcess.getInputStream()
                        )
                );

        String line;

        while ((line = recognizerOutput.readLine()) != null) {

            if (line.equals("__READY__")) {

                System.out.println(
                        "NOURI: Windows command recognition ready."
                );

                return;
            }

            if (line.startsWith("__ERROR__")) {

                throw new Exception(
                        line.substring("__ERROR__".length())
                );
            }
        }

        throw new Exception(
                "Windows speech recognizer failed to start."
        );
    }

    public static String transcribe(Path audioFile) {

        if (audioFile == null) {
            return "";
        }

        synchronized (LOCK) {

            try {

                startRecognizer();

                String path =
                        audioFile
                                .toAbsolutePath()
                                .toString();

                System.out.println(
                        "NOURI: Recognizing command..."
                );

                recognizerInput.write(path);
                recognizerInput.newLine();
                recognizerInput.flush();

                String line;

                while ((line =
                        recognizerOutput.readLine()) != null) {

                    if (line.startsWith("__RESULT__")) {

                        String result =
                                line.substring(
                                        "__RESULT__".length()
                                ).trim();

                        if (!result.isBlank()) {

                            System.out.println(
                                    "NOURI heard: "
                                            + result
                            );

                            return result;
                        }

                        return "";
                    }

                    if (line.startsWith("__ERROR__")) {

                        System.out.println(
                                "NOURI recognition error: "
                                        + line.substring(
                                                "__ERROR__".length()
                                        )
                        );

                        return "";
                    }
                }

                restartRecognizer();
                return "";

            } catch (Exception e) {

                System.out.println(
                        "NOURI speech recognition error: "
                                + e.getMessage()
                );

                restartRecognizer();

                return "";
            }
        }
    }

    private static void restartRecognizer() {

        try {

            if (recognizerInput != null) {

                recognizerInput.write("__EXIT__");
                recognizerInput.newLine();
                recognizerInput.flush();
                recognizerInput.close();
            }

        } catch (Exception ignored) {
        }

        try {

            if (recognizerProcess != null) {
                recognizerProcess.destroy();
            }

        } catch (Exception ignored) {
        }

        recognizerProcess = null;
        recognizerInput = null;
        recognizerOutput = null;
    }

    public static void shutdown() {

        synchronized (LOCK) {

            restartRecognizer();

            System.out.println(
                    "NOURI: Speech recognition stopped."
            );
        }
    }
}
