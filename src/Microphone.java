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

        System.out.println("NOURI: 🎙️ Listening...");

        ByteArrayOutputStream audio =
                new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];

        long startTime = System.currentTimeMillis();

        // Maximum recording time: 10 seconds
        long maxTime = 10000;

        // We'll keep recording for now.
        // The next upgrade will use real voice activity detection.
        while (System.currentTimeMillis() - startTime < maxTime) {

            int bytesRead =
                    microphone.read(
                            buffer,
                            0,
                            buffer.length
                    );

            if (bytesRead > 0) {
                audio.write(buffer, 0, bytesRead);
            }
        }

        microphone.stop();
        microphone.close();

        System.out.println("NOURI: 🎙️ Done listening.");

        File wavFile =
                File.createTempFile(
                        "nouri_speech_",
                        ".wav"
                );

        byte[] audioData = audio.toByteArray();

        AudioInputStream audioStream =
                new AudioInputStream(
                        new java.io.ByteArrayInputStream(audioData),
                        format,
                        audioData.length / format.getFrameSize()
                );

        AudioSystem.write(
                audioStream,
                AudioFileFormat.Type.WAVE,
                wavFile
        );

        audioStream.close();

        return wavFile;
    }
}
