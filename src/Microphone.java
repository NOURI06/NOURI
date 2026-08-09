import javax.sound.sampled.*;

public class Microphone {

    private TargetDataLine microphone;

    public void start() throws Exception {

        AudioFormat format = new AudioFormat(
                16000,
                16,
                1,
                true,
                false
        );

        DataLine.Info info =
                new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            throw new Exception("Microphone format not supported.");
        }

        microphone =
                (TargetDataLine) AudioSystem.getLine(info);

        microphone.open(format);
        microphone.start();

        System.out.println("NOURI: 🎙️ Listening...");

        byte[] buffer = new byte[4096];

        while (true) {

            int bytesRead =
                    microphone.read(buffer, 0, buffer.length);

            if (bytesRead > 0) {

                System.out.println(
                        "NOURI: Received "
                        + bytesRead
                        + " bytes of audio."
                );

                break;
            }
        }

        microphone.stop();
        microphone.close();
    }
}
