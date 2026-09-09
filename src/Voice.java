import java.io.*;
import java.nio.file.*;

public class Voice {

    private static final String PIPER =
            "C:\\Users\\dell\\Desktop\\NOURI\\piper\\piper\\piper.exe";

    private static final String MODEL =
            "C:\\Users\\dell\\Desktop\\NOURI\\piper\\en_GB-northern_english_male-medium.onnx";

    private static Process piperProcess;
    private static BufferedWriter piperInput;
    private static Thread outputReader;

    private static final Object LOCK = new Object();


    private static void startPiper() throws Exception {

        if (piperProcess != null
                && piperProcess.isAlive()) {
            return;
        }

        System.out.println(
                "NOURI: Loading Piper voice..."
        );


        ProcessBuilder builder =
                new ProcessBuilder(
                        PIPER,
                        "--model",
                        MODEL,
                        "--json-input",
                        "--length_scale",
                        "1.00",
                        "--noise_scale",
                        "0.75",
                        "--noise_w",
                        "0.85",
                        "--sentence_silence",
                        "0.15"
                );


        /*
         * Piper messages go to the console.
         */
        builder.redirectError(
                ProcessBuilder.Redirect.INHERIT
        );


        piperProcess = builder.start();


        piperInput =
                new BufferedWriter(
                        new OutputStreamWriter(
                                piperProcess.getOutputStream()
                        )
                );


        /*
         * IMPORTANT:
         *
         * Keep reading Piper's stdout.
         * Otherwise the process can eventually block
         * when its output buffer becomes full.
         */
        outputReader =
                new Thread(() -> {

                    try {

                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                piperProcess
                                                        .getInputStream()
                                        )
                                );


                        while (reader.readLine() != null) {
                            // Piper output intentionally ignored.
                        }

                    } catch (Exception ignored) {
                    }

                });


        outputReader.setDaemon(true);
        outputReader.start();


        System.out.println(
                "NOURI: Piper loaded and ready."
        );
    }


    public static void speak(String text) {

        if (text == null || text.isBlank()) {
            return;
        }


        synchronized (LOCK) {

            Path outputFile = null;

            try {

                startPiper();


                System.out.println(
                        "NOURI: Speaking..."
                );


                /*
                 * Create a unique WAV file.
                 */
                outputFile =
                        Files.createTempFile(
                                "nouri_voice_",
                                ".wav"
                        );


                String safeText =
                        text
                                .replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\r", " ")
                                .replace("\n", " ");


                String safePath =
                        outputFile
                                .toAbsolutePath()
                                .toString()
                                .replace("\\", "\\\\")
                                .replace("\"", "\\\"");


                /*
                 * Send ONE JSON request to Piper.
                 *
                 * Piper supports output_file for JSON input.
                 */
                String json =
                        "{\"text\":\""
                                + safeText
                                + "\","
                                + "\"output_file\":\""
                                + safePath
                                + "\"}";


                piperInput.write(json);
                piperInput.newLine();
                piperInput.flush();


                /*
                 * Wait until Piper has actually created
                 * the WAV file.
                 */
                long start =
                        System.currentTimeMillis();


                while (true) {

                    if (Files.exists(outputFile)
                            && Files.size(outputFile) > 44) {

                        break;
                    }


                    if (System.currentTimeMillis()
                            - start > 30000) {

                        throw new Exception(
                                "Piper timed out."
                        );
                    }


                    Thread.sleep(20);
                }


                /*
                 * Play the WAV ourselves.
                 *
                 * This is the important part:
                 * Java controls playback instead of Piper.
                 */
                Process player =
                        new ProcessBuilder(
                                "powershell.exe",
                                "-NoProfile",
                                "-Command",
                                "(New-Object Media.SoundPlayer '"
                                        + outputFile
                                                .toAbsolutePath()
                                                .toString()
                                                .replace(
                                                        "'",
                                                        "''"
                                                )
                                        + "').PlaySync()"
                        ).start();


                player.waitFor();


                System.out.println(
                        "NOURI: Voice finished."
                );


            } catch (Exception e) {

                System.out.println(
                        "NOURI Piper error: "
                                + e.getMessage()
                );


                restartPiper();


            } finally {

                if (outputFile != null) {

                    try {

                        Files.deleteIfExists(
                                outputFile
                        );

                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }


    private static void restartPiper() {

        try {

            if (piperInput != null) {
                piperInput.close();
            }

        } catch (Exception ignored) {
        }


        try {

            if (piperProcess != null) {
                piperProcess.destroy();
            }

        } catch (Exception ignored) {
        }


        piperProcess = null;
        piperInput = null;
    }


    public static void shutdown() {

        synchronized (LOCK) {

            restartPiper();

            System.out.println(
                    "NOURI: Piper stopped."
            );
        }
    }
}
