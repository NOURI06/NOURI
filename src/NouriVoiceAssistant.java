public class NouriVoiceAssistant {

    private static final int MAX_VOICE_CHARACTERS = 350;

    public static void main(String[] args) {

        Microphone microphone = new Microphone();
        Commands commands = new Commands();

        System.out.println("=================================");
        System.out.println("          NOURI ONLINE");
        System.out.println("=================================");
        System.out.println("Clap twice to wake NOURI.");

        boolean conversationMode = false;

        while (true) {

            try {

                /*
                 * WAIT FOR WAKE SIGNAL
                 */
                if (!conversationMode) {

                    WakeWord.waitForDoubleClap();

                    System.out.println("NOURI: Wake signal detected.");

                    conversationMode = true;

                    /*
                     * GREETING
                     */
                    Voice.speak(
                            "Greetings. How can I help you, sir?"
                    );

                    /*
                     * IMPORTANT:
                     * Do NOT continue here.
                     *
                     * The program will immediately continue
                     * into the listening section below.
                     */
                }


                /*
                 * LISTEN FOR USER COMMAND
                 */
                System.out.println("NOURI: Ready for your command.");

                java.io.File audioFile =
                        microphone.recordUntilSilence();


                /*
                 * SPEECH TO TEXT
                 */
                String text =
                        SpeechToText.transcribe(
                                audioFile.toPath()
                        );

                audioFile.delete();


                /*
                 * NOTHING HEARD
                 */
                if (text == null || text.isBlank()) {

                    System.out.println(
                            "NOURI: I didn't catch that, sir."
                    );

                    continue;
                }


                System.out.println(
                        "You: " + text
                );


                /*
                 * NORMALIZE COMMAND
                 */
                String lower =
                        text.toLowerCase().trim();


                /*
                 * SLEEP COMMANDS
                 */
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

                    System.out.println();
                    System.out.println(
                            "Clap twice to wake NOURI."
                    );

                    continue;
                }


                /*
                 * PROCESS COMMAND
                 */
                String response =
                        commands.execute(text);


                /*
                 * NO RESPONSE
                 */
                if (response == null
                        || response.isBlank()) {

                    System.out.println(
                            "NOURI: No response."
                    );

                    continue;
                }


                /*
                 * SHOW RESPONSE
                 */
                System.out.println(
                        "NOURI: " + response
                );


                /*
                 * PREPARE RESPONSE FOR VOICE
                 */
                String voiceResponse =
                        prepareVoiceResponse(response);


                System.out.println(
                        "NOURI VOICE: "
                                + voiceResponse
                );


                /*
                 * SPEAK RESPONSE
                 */
                Voice.speak(voiceResponse);


                /*
                 * LOOP BACK TO LISTENING
                 *
                 * NOURI remains awake.
                 */
                System.out.println(
                        "NOURI: Ready for your next command."
                );


            } catch (Exception e) {

                System.out.println(
                        "NOURI error: "
                                + e.getMessage()
                );
            }
        }
    }


    /*
     * Keep very long AI responses from being
     * spoken for too long.
     */
    private static String prepareVoiceResponse(
            String response) {

        response =
                response
                        .replace("\n", " ")
                        .replace("\r", " ")
                        .replace("*", "")
                        .replace("#", "")
                        .replace("  ", " ")
                        .trim();


        if (response.length()
                <= MAX_VOICE_CHARACTERS) {

            return response;
        }


        int limit =
                MAX_VOICE_CHARACTERS;


        int end =
                response.lastIndexOf(
                        ". ",
                        limit
                );


        if (end < 100) {

            end =
                    response.lastIndexOf(
                            ", ",
                            limit
                    );
        }


        if (end < 100) {

            end = limit;

        } else {

            end += 1;
        }


        return response
                .substring(0, end)
                .trim()
                + " I have more details if you want them.";
    }
}
```

### Then sync it from GitHub

After you put this file on GitHub:

```bat
cd C:\Users\dell\Desktop\NOURI
git fetch origin
git checkout origin/main -- src\NouriVoiceAssistant.java
javac -d out src\*.java
java -cp out NouriVoiceAssistant
```

The important fix is that after:

**👏👏 → greeting**

NOURI now goes directly to:

**🎤 Listening → speech recognition → command → response → listening again**

It will **stay awake** until you say **“go to sleep”**.
