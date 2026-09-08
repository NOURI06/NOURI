import javax.sound.sampled.*;
import java.io.*;

public class Voice {

    private static final String PIPER =
            "C:\\Users\\dell\\Desktop\\NOURI\\piper\\piper\\piper.exe";

    private static final String MODEL =
            "C:\\Users\\dell\\Desktop\\NOURI\\piper\\en_GB-northern_english_male-medium.onnx";

    /*
     * IMPORTANT:
     * This must match the sample rate in the
     * Piper model's .onnx.json file.
     *
     * Your Northern English voice is normally
     * 22050 Hz, 16-bit, mono.
     */
    private static final float SAMPLE_RATE = 22050;

    private static final int SAMPLE_SIZE = 16;
    private static final int CHANNELS = 1;

    private static Process piperProcess;
    private static BufferedWriter piperInput;
    private static Thread audioThread;

    private static final Object LOCK =
            new Object();

    private static void startPiper()
            throws Exception {

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
                        "--length_scale",
                        "1.00",
                        "--noise_scale",
                        "0.75",
                        "--noise_w",
                        "0.85",
                        "--sentence_silence",
                        "0.15",
                        "--output-raw"
                );

        /*
         * Piper audio goes to stdout.
         * Piper diagnostic messages go to stderr.
         */
        builder.redirectError(
                ProcessBuilder.Redirect.INHERIT
        );

        piperProcess =
                builder.start();

        piperInput =
                new BufferedWriter(
                        new OutputStreamWriter(
                                piperProcess.getOutputStream()
                        )
                );

        System.out.println(
                "NOURI: Piper loaded and ready."
        );
    }

    public static void speak(
            String text) {

        if (text == null
                || text.isBlank()) {
            return;
        }

        synchronized (LOCK) {

            try {

                startPiper();

                System.out.println(
                        "NOURI: Speaking..."
                );

                /*
                 * Send the sentence to the
                 * already-loaded Piper process.
                 */
                piperInput.write(text);
                piperInput.newLine();
                piperInput.flush();

                /*
                 * Read Piper's raw PCM audio.
                 */
                playPiperAudio();

                System.out.println(
                        "NOURI: Voice finished."
                );

            } catch (Exception e) {

                System.out.println(
                        "NOURI Piper error: "
                                + e.getMessage()
                );

                restartPiper();
            }
        }
    }

    private static void playPiperAudio()
            throws Exception {

        AudioFormat format =
                new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        SAMPLE_RATE,
                        SAMPLE_SIZE,
                        CHANNELS,
                        2,
                        SAMPLE_RATE,
                        false
                );

        DataLine.Info info =
                new DataLine.Info(
                        SourceDataLine.class,
                        format
                );

        SourceDataLine speakers =
                (SourceDataLine)
                        AudioSystem.getLine(info);

        speakers.open(format);
        speakers.start();

        InputStream audio =
                piperProcess.getInputStream();

        byte[] buffer =
                new byte[4096];

        int bytesRead;

        while ((bytesRead =
                audio.read(buffer)) != -1) {

            if (bytesRead > 0) {

                speakers.write(
                        buffer,
                        0,
                        bytesRead
                );
            }
        }

        speakers.drain();
        speakers.stop();
        speakers.close();
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
