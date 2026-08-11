import java.io.File;
import java.nio.file.Path;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.io.InputStream;

public class SpeechToText {

    public static String transcribe(Path audioFile) {

        try {

            System.out.println("NOURI: Using Windows English-UK speech recognition...");

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-Command",
                    "$ErrorActionPreference='Stop'; " +
                    "Add-Type -AssemblyName System.Speech; " +
                    "$recognizer = New-Object System.Speech.Recognition.SpeechRecognitionEngine('en-GB'); " +
                    "$recognizer.SetInputToWaveFile('" +
                    audioFile.toAbsolutePath().toString().replace("'", "''") +
                    "'); " +
                    "$result = $recognizer.Recognize(); " +
                    "if ($result -ne $null) { [Console]::WriteLine($result.Text) }"
            );

            Process process = processBuilder.start();

            InputStream inputStream = process.getInputStream();

            String output =
                    new String(
                            inputStream.readAllBytes()
                    ).trim();

            process.waitFor();

            if (output.isEmpty()) {

                System.out.println(
                        "NOURI: I did not understand that."
                );

                return null;
            }

            System.out.println(
                    "NOURI heard: " + output
            );

            return output;

        } catch (Exception e) {

            System.out.println(
                    "NOURI STT error: " +
                    e.getMessage()
            );

            return null;
        }
    }
}
