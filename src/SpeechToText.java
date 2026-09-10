import java.io.*;
import java.nio.file.*;

public class SpeechToText {

    private static Process recognizerProcess;
    private static BufferedWriter recognizerInput;
    private static BufferedReader recognizerOutput;

    private static void startRecognizer() throws Exception {

        if (recognizerProcess != null && recognizerProcess.isAlive()) {
            return;
        }

        Path script = Paths.get("src", "SpeechRecognizer.ps1")
                .toAbsolutePath()
                .normalize();

        if (!Files.exists(script)) {
            throw new FileNotFoundException(
                    "SpeechRecognizer.ps1 not found: " + script
            );
        }

        ProcessBuilder pb = new ProcessBuilder(
                "powershell.exe",
                "-NoProfile",
                "-ExecutionPolicy", "Bypass",
                "-File", script.toString()
        );

        pb.redirectErrorStream(true);

        recognizerProcess = pb.start();

        recognizerInput = new BufferedWriter(
                new OutputStreamWriter(
                        recognizerProcess.getOutputStream()
                )
        );

        recognizerOutput = new BufferedReader(
                new InputStreamReader(
                        recognizerProcess.getInputStream()
                )
        );

        String line;

        while ((line = recognizerOutput.readLine()) != null) {

            if (line.equals("__READY__")) {
                System.out.println("NOURI: Speech recognition ready.");
                return;
            }

            if (line.startsWith("__ERROR__")) {
                throw new IOException(
                        "Speech recognizer error: " +
                        line.substring(9)
                );
            }
        }

        throw new IOException(
                "Windows speech recognizer stopped before becoming ready."
        );
    }

    public static synchronized String transcribe(Path audioFile)
            throws Exception {

        startRecognizer();

        recognizerInput.write(audioFile.toAbsolutePath().toString());
        recognizerInput.newLine();
        recognizerInput.flush();

        String line;

        while ((line = recognizerOutput.readLine()) != null) {

            if (line.startsWith("__RESULT__")) {

                String result = line.substring(10);

                int separator = result.lastIndexOf('|');

                if (separator >= 0) {
                    result = result.substring(0, separator);
                }

                return result.trim();
            }

            if (line.startsWith("__ERROR__")) {
                throw new IOException(
                        "Speech recognition error: " +
                        line.substring(9)
                );
            }
        }

        throw new IOException(
                "Speech recognizer stopped unexpectedly."
        );
    }

    public static synchronized void shutdown() {

        try {
            if (recognizerInput != null) {
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
}
