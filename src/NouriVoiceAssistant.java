public class NouriVoiceAssistant {

    public static void main(String[] args) {

        Microphone microphone = new Microphone();

        System.out.println("=================================");
        System.out.println("          NOURI ONLINE");
        System.out.println("=================================");
        System.out.println("Say: Wake up, NOURI");

        while (true) {

            try {

                // Wait for speech
                java.io.File audioFile =
                        microphone.recordUntilSilence();

                // Speech → text
                String text =
                        SpeechToText.transcribe(
                                audioFile.toPath()
                        );

                audioFile.delete();

                if (text == null || text.isBlank()) {
                    continue;
                }

                System.out.println(
                        "You: " + text
                );

                // Wake word
                if (WakeWord.isWakeWord(text)) {

                    System.out.println(
                            "NOURI: Wake word detected."
                    );

                    Voice.speak(
                            "Greetings. How can I help you, sir?"
                    );

                    // Listen for command
                    java.io.File commandAudio =
                            microphone.recordUntilSilence();

                    String command =
                            SpeechToText.transcribe(
                                    commandAudio.toPath()
                            );

                    commandAudio.delete();

                    if (command == null ||
                            command.isBlank()) {

                        Voice.speak(
                                "I didn't catch that, sir."
                        );

                        continue;
                    }

                    System.out.println(
                            "You: " + command
                    );

                    handleCommand(command);
                }

            } catch (Exception e) {

                System.out.println(
                        "NOURI error: "
                        + e.getMessage()
                );
            }
        }
    }

    private static void handleCommand(String command) {

        Commands commands = new Commands();

        String response =
                commands.execute(command);

        System.out.println(
                "NOURI: " + response
        );

        Voice.speak(response);
    }
}
