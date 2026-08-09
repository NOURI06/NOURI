import javax.sound.sampled.*;
import java.io.File;

public class Microphone {

    private TargetDataLine microphone;

    private final AudioFormat format = new AudioFormat(
            16000,
            16,
            1,
            true,
            false
    );

    public File record(int seconds) throws Exception {

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

        microphone =
                (TargetDataLine) AudioSystem.getLine(info);

        microphone.open(format);
        microphone.start();

        File audioFile =
                File.createTempFile(
                        "nouri_speech_",
                        ".wav"
                );

        System.out.println(
                "NOURI: 🎙️ Listening..."
        );

        AudioInputStream audioStream =
                new AudioInputStream(
                        microphone
                );

        Thread recorder =
                new Thread(() -> {

                    try {

                        AudioSystem.write(
                                audioStream,
                                AudioFileFormat.Type.WAVE,
                                audioFile
                        );

                    } catch (Exception e) {

                        System.out.println(
                                "NOURI recorder error: "
                                + e.getMessage()
                        );
                    }
                });

        recorder.start();

        Thread.sleep(seconds * 1000L);

        microphone.stop();
        microphone.close();

        audioStream.close();

        System.out.println(
                "NOURI: 🎙️ Recording complete."
        );

        return audioFile;
    }
}
