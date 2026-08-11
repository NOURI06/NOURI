public class NouriVoiceAssistant {

    private static final int MAX_VOICE_CHARACTERS = 350;

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

                if (response == null
                        || response.isBlank()) {

                    Voice.speak(
                            "I don't have a response for that."
                    );

                    continue;
                }

                System.out.println(
                        "NOURI: " + response
                );

                String voiceResponse =
                        prepareVoiceResponse(response);

                System.out.println(
                        "NOURI VOICE: " + voiceResponse
                );

                Voice.speak(voiceResponse);

            } catch (Exception e) {

                System.out.println(
                        "NOURI error: "
                        + e.getMessage()
                );
            }
        }
    }

    private static String prepareVoiceResponse(String response) {

        response = response
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("*", "")
                .replace("#", "")
                .replace("`", "")
                .trim();

        if (response.length() <= MAX_VOICE_CHARACTERS) {
            return response;
        }

        int end =
                response.lastIndexOf(
                        ".",
                        MAX_VOICE_CHARACTERS
                );

        if (end < 120) {
            end =
                    response.lastIndexOf(
                            "!",
                            MAX_VOICE_CHARACTERS
                    );
        }

        if (end < 120) {
            end =
                    response.lastIndexOf(
                            "?",
                            MAX_VOICE_CHARACTERS
                    );
        }

        if (end < 120) {
            end = MAX_VOICE_CHARACTERS;
        }

        String shortResponse =
                response.substring(0, end).trim();

        return shortResponse
                + ". I have more details if you need them.";
    }
}
