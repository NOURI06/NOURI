import java.io.File;

public class SpeechTest {

    public static void main(String[] args) {

        try {

            Microphone microphone = new Microphone();

            System.out.println("=================================");
            System.out.println("        NOURI SPEECH TEST");
            System.out.println("=================================");
            System.out.println("Speak for 5 seconds...");

            File audioFile = microphone.record(5);

            System.out.println("NOURI: Sending audio to ElevenLabs...");

            String text =
                    SpeechToText.transcribe(
                            audioFile.toPath()
                    );

            if (text != null) {
                System.out.println("NOURI heard: " + text);
            } else {
                System.out.println(
                        "NOURI: I couldn't understand the audio."
                );
            }

            audioFile.delete();

        } catch (Exception e) {

            System.out.println(
                    "NOURI Speech Test error: "
                    + e.getMessage()
            );
        }
    }
}
