import java.io.*;

public class Voice {

    private static final String PIPER =
            "C:\\Users\\dell\\Desktop\\NOURI\\piper\\piper\\piper.exe";

    private static final String MODEL =
            "C:\\Users\\dell\\Desktop\\NOURI\\piper\\en_GB-northern_english_male-medium.onnx";

    private static final String OUTPUT =
            "C:\\Users\\dell\\Desktop\\NOURI\\piper\\nouri_voice.wav";

    private static Process piperProcess;
    private static BufferedWriter piperInput;

    private static void startPiper() throws Exception {

        if (piperProcess != null
                && piperProcess.isAlive()) {
            return;
        }

        System.out.println(
                "NOURI: Starting Piper..."
        );

        ProcessBuilder builder =
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
                        "--output_dir",
                        new File(
                                OUTPUT
                        ).getParent()
                );

        builder.redirectErrorStream(true);

        piperProcess =
                builder.start();

        piperInput =
                new BufferedWriter(
                        new OutputStreamWriter(
                                piperProcess.getOutputStream()
                        )
                );

        System.out.println(
                "NOURI: Piper is ready."
        );
    }

    public static synchronized void speak(
            String text) {

        if (text == null
                || text.isBlank()) {
            return;
        }

        try {

            startPiper();

            System.out.println(
                    "NOURI: Speaking..."
            );

            /*
             * Send text directly to the
             * already-running Piper process.
             */

            piperInput.write(text);
            piperInput.newLine();
            piperInput.flush();

            /*
             * Piper's --output_dir mode creates
             * a WAV for each line of input.
             *
             * We wait for the generated file,
             * then play it.
             */

            File folder =
                    new File(
                            new File(OUTPUT)
                                    .getParent()
                    );

            File newestFile = null;

            long start =
                    System.currentTimeMillis();

            while (System.currentTimeMillis()
                    - start < 30000) {

                File[] files =
                        folder.listFiles(
                                (dir, name) ->
                                        name.toLowerCase()
                                                .endsWith(".wav")
                );

                if (files != null
                        && files.length > 0) {

                    File candidate =
                            files[files.length - 1];

                    if (newestFile == null
                            || candidate.lastModified()
                            > newestFile.lastModified()) {

                        newestFile = candidate;
                    }

                    if (newestFile != null
                            && newestFile.length() > 44) {

                        break;
                    }
                }

                Thread.sleep(50);
            }

            if (newestFile == null) {

                System.out.println(
                        "NOURI: Piper did not create audio."
                );

                return;
            }

            Process player =
                    new ProcessBuilder(
                            "powershell.exe",
                            "-NoProfile",
                            "-Command",
                            "(New-Object Media.SoundPlayer '" +
                                    newestFile
                                            .getAbsolutePath()
                                            .replace(
                                                    "'",
                                                    "''"
                                            ) +
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

    public static synchronized void shutdown() {

        try {

            if (piperInput != null) {
                piperInput.close();
            }

            if (piperProcess != null) {
                piperProcess.destroy();
            }

        } catch (Exception ignored) {
        }
    }
}
