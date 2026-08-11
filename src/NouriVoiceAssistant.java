public class NouriVoiceAssistant {

    public static void main(String[] args) {

        Microphone microphone = new Microphone();
        Commands commands = new Commands();

        System.out.println("=================================");
        System.out.println("          NOURI ONLINE");
        System.out.println("=================================");
        System.out.println("Say: Wake up, Buddy");

        boolean conversationMode = false;

        while (true) {

            try {

                java.io.File audioFile =
                        microphone.recordUntilSilence();

                String text =
                        SpeechToText.transcribe(
                                audioFile.toPath()
                        );

                audioFile.delete();

                if (text == null || text.isBlank()) {
                    continue;
                }

                System.out.println("You: " + text);

                if (!conversationMode) {

                    if (WakeWord.isWakeWord(text)) {

                        System.out.println(
                                "NOURI: Wake word detected."
                        );

                        conversationMode = true;

                        Voice.speak(
                                "Greetings. How can I help you, sir?"
                        );
                    }

                    continue;
                }

                String lower =
                        text.toLowerCase().trim();

                if (lower.contains("go to sleep")
                        || lower.contains("sleep now")
                        || lower.contains("stop listening")
                        || lower.equals("goodbye")) {

                    Voice.speak(
                            "Of course, sir. Going to sleep."
                    );

                    conversationMode = false;

                    System.out.println(
                            "NOURI: Conversation mode OFF."
                    );

                    continue;
                }

                String response =
                        commands.execute(text);

                System.out.println(
                        "NOURI: " + response
                );

                if (response != null
                        && !response.isBlank()) {

                    Voice.speak(response);
                }

            } catch (Exception e) {

                System.out.println(
                        "NOURI error: "
                                + e.getMessage()
                );
            }
        }
    }
}
