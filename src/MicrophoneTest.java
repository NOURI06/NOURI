public class MicrophoneTest {

    public static void main(String[] args) {

        try {

            Microphone microphone = new Microphone();

            System.out.println("=================================");
            System.out.println("       NOURI MICROPHONE TEST");
            System.out.println("=================================");
            System.out.println("Speak something...");
            System.out.println("Listening for 5 seconds...");

            microphone.start();

            System.out.println("Microphone test complete.");

        } catch (Exception e) {

            System.out.println(
                    "NOURI: Microphone error: "
                    + e.getMessage()
            );
        }
    }
}
