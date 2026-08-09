import java.io.File;

public class SpeechTest {

    public static void main(String[] args) {

        try {

            Microphone microphone = new Microphone();

            System.out.println("=================================");
            System.out.println("        NOURI SPEECH TEST");
            System.out.println("=================================");

            File audioFile =
                    microphone.recordUntilSilence();

            System.out.println(
                    "NOURI: Sending audio to ElevenLabs..."
            );

            String text =
                    SpeechToText.transcribe(
                            audioFile.toPath()
                    );

            if (text != null && !text.isBlank()) {

                System.out.println(
                        "NOURI heard: " + text
                );

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
