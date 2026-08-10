import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class Microphone {

    private static final float SAMPLE_RATE = 16000;
    private static final int SAMPLE_SIZE = 16;
    private static final int CHANNELS = 1;

    private final AudioFormat format = new AudioFormat(
            SAMPLE_RATE,
            SAMPLE_SIZE,
            CHANNELS,
            true,
            false
    );

    public File recordUntilSilence() throws Exception {

        DataLine.Info info =
                new DataLine.Info(
                        TargetDataLine.class,
                        format
                );

        if (!AudioSystem.isLineSupported(info)) {
            throw new Exception(
                    "Microphone format is not supported."
            );
        }

        TargetDataLine microphone =
                (TargetDataLine) AudioSystem.getLine(info);

        microphone.open(format);
        microphone.start();

        System.out.println("NOURI: Listening...");

        ByteArrayOutputStream audio =
                new ByteArrayOutputStream();

        byte[] buffer = new byte[2048];

        // Wait for speech
        final double SPEECH_THRESHOLD = 900.0;

        // Stop after this much silence
        final long SILENCE_TIME = 500;

        // Maximum recording time
        final long MAX_TIME = 5000;

        boolean speechStarted = false;

        long speechStartTime = 0;
        long lastSpeechTime = 0;

        long startTime =
                System.currentTimeMillis();

        while (true) {

            int bytesRead =
                    microphone.read(
                            buffer,
                            0,
                            buffer.length
                    );

            if (bytesRead <= 0) {
                continue;
            }

            audio.write(
                    buffer,
                    0,
                    bytesRead
            );

            double volume =
                    calculateVolume(
                            buffer,
                            bytesRead
                    );

            long now =
                    System.currentTimeMillis();

            // =========================
            // SPEECH DETECTED
            // =========================

            if (volume > SPEECH_THRESHOLD) {

                if (!speechStarted) {

                    speechStarted = true;

                    speechStartTime = now;
                }

                lastSpeechTime = now;
            }

            // =========================
            // STOP AFTER SILENCE
            // =========================

            if (speechStarted) {

                if (now - lastSpeechTime
                        >= SILENCE_TIME) {

                    break;
                }
            }

            // =========================
            // MAXIMUM TIME
            // =========================

            if (now - startTime >= MAX_TIME) {
                break;
            }
        }

        microphone.stop();
        microphone.close();

        System.out.println(
                "NOURI: Done listening."
        );

        File wavFile =
                File.createTempFile(
                        "nouri_speech_",
                        ".wav"
                );

        byte[] audioData =
                audio.toByteArray();

        AudioInputStream audioStream =
                new AudioInputStream(
                        new java.io.ByteArrayInputStream(
                                audioData
                        ),
                        format,
                        audioData.length
                                / format.getFrameSize()
                );

        AudioSystem.write(
                audioStream,
                AudioFileFormat.Type.WAVE,
                wavFile
        );

        audioStream.close();

        return wavFile;
    }

    private double calculateVolume(
            byte[] buffer,
            int bytesRead) {

        long sum = 0;

        int samples = bytesRead / 2;

        for (int i = 0; i < bytesRead - 1; i += 2) {

            int low =
                    buffer[i] & 0xff;

            int high =
                    buffer[i + 1];

            int sample =
                    (high << 8) | low;

            sum +=
                    (long) sample * sample;
        }

        if (samples == 0) {
            return 0;
        }

        return Math.sqrt(
                (double) sum / samples
        );
    }
}
