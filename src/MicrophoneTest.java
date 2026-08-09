public class MicrophoneTest {

    public static void main(String[] args) {

        try {

            Microphone microphone = new Microphone();

            System.out.println("=================================");
            System.out.println("       NOURI MICROPHONE TEST");
            System.out.println("=================================");
            System.out.println("Speak for 5 seconds...");

            java.io.File audioFile =
                    microphone.record(5);

            System.out.println(
                    "Audio saved to: "
                    + audioFile.getAbsolutePath()
            );

        } catch (Exception e) {

            System.out.println(
                    "NOURI: Microphone error: "
                    + e.getMessage()
            );
        }
    }
}
