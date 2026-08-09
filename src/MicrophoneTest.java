public class MicrophoneTest {

    public static void main(String[] args) {

        try {

            Microphone microphone = new Microphone();

            System.out.println("=================================");
            System.out.println("       NOURI MICROPHONE TEST");
            System.out.println("=================================");

            java.io.File audioFile =
                    microphone.recordUntilSilence();

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
