import javax.sound.sampled.*;

public class WakeWord {

    private static final float SAMPLE_RATE = 16000;
    private static final int SAMPLE_SIZE = 16;
    private static final int CHANNELS = 1;

    private static final double CLAP_THRESHOLD = 1800.0;

    private static final long MIN_CLAP_GAP = 100;
    private static final long MAX_CLAP_GAP = 1000;

    private static final long CLAP_COOLDOWN = 120;

    private static final AudioFormat FORMAT =
            new AudioFormat(
                    SAMPLE_RATE,
                    SAMPLE_SIZE,
                    CHANNELS,
                    true,
                    false
            );

    public static boolean waitForDoubleClap() throws Exception {

        DataLine.Info info =
                new DataLine.Info(
                        TargetDataLine.class,
                        FORMAT
                );

        if (!AudioSystem.isLineSupported(info)) {
            throw new Exception(
                    "Microphone format is not supported."
            );
        }

        TargetDataLine microphone =
                (TargetDataLine) AudioSystem.getLine(info);

        microphone.open(FORMAT);
        microphone.start();

        System.out.println(
                "NOURI: Waiting for double clap..."
        );

        byte[] buffer = new byte[1024];

        long firstClapTime = 0;
        long lastClapTime = 0;

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

            double volume =
                    calculateVolume(
                            buffer,
                            bytesRead
                    );

            long now =
                    System.currentTimeMillis();

            if (volume > CLAP_THRESHOLD) {

                // Prevent one loud sound from being
                // detected as multiple claps.
                if (now - lastClapTime < CLAP_COOLDOWN) {
                    continue;
                }

                lastClapTime = now;

                System.out.println(
                        "NOURI: Clap detected."
                );

                // First clap
                if (firstClapTime == 0) {

                    firstClapTime = now;

                    continue;
                }

                long gap =
                        now - firstClapTime;

                // Second clap at the right time
                if (gap >= MIN_CLAP_GAP
                        && gap <= MAX_CLAP_GAP) {

                    System.out.println(
                            "NOURI: Double clap detected!"
                    );

                    microphone.stop();
                    microphone.close();

                    return true;
                }

                // Too much time passed.
                // Treat this as a new first clap.
                if (gap > MAX_CLAP_GAP) {

                    firstClapTime = now;
                }
            }
        }
    }

    private static double calculateVolume(
            byte[] buffer,
            int bytesRead) {

        long sum = 0;

        int samples =
                bytesRead / 2;

        for (int i = 0;
             i < bytesRead - 1;
             i += 2) {

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
