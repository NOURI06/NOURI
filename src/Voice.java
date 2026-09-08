import java.io.*;

public class Voice {

    private static final String PIPER =
            "C:\\Users\\dell\\Desktop\\NOURI\\piper\\piper\\piper.exe";

    private static final String MODEL =
            "C:\\Users\\dell\\Desktop\\NOURI\\piper\\en_GB-northern_english_male-medium.onnx";

    private static final String OUTPUT =
            "C:\\Users\\dell\\Desktop\\NOURI\\piper\\nouri_voice.wav";

    public static void speak(String text) {

        if (text == null || text.isBlank()) {
            return;
        }

        System.out.println("NOURI: Speaking...");

        try {

            ProcessBuilder process =
                    new ProcessBuilder(
                            PIPER,
                            "--model",
                            MODEL,
                            "--length_scale",
                            "1.00",
                            "--noise_scale",
                            "0.75",
                            "--noise_w",
                            "0.85",
                            "--sentence_silence",
                            "0.15",
                            "--output_file",
                            OUTPUT
                    );

            process.redirectErrorStream(true);

            Process p = process.start();

            try (BufferedWriter writer =
                         new BufferedWriter(
                                 new OutputStreamWriter(
                                         p.getOutputStream()
                                 )
                         )) {

                writer.write(text);
                writer.newLine();
            }

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

            if (exitCode != 0) {

                System.out.println(
                        "NOURI: Piper failed. Exit code: "
                                + exitCode
                );

                return;
            }

            // Play the generated WAV
            Process player =
                    new ProcessBuilder(
                            "powershell.exe",
                            "-NoProfile",
                            "-Command",
                            "(New-Object Media.SoundPlayer '" +
                                    OUTPUT.replace("'", "''") +
                                    "').PlaySync()"
                    ).start();

            player.waitFor();

            System.out.println(
                    "NOURI: Voice finished."
            );

        } catch (Exception e) {

            System.out.println(
                    "NOURI Piper voice error: "
                            + e.getMessage()
            );
        }
    }
}
