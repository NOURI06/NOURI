import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class Voice {

    private static final String VOICE_ID =
            "WkQNWeRIRZHzOYi4vP18";

    private static final String API_URL =
            "https://api.elevenlabs.io/v1/text-to-speech/" + VOICE_ID;

    private static final HttpClient CLIENT =
            HttpClient.newHttpClient();

    public static void speak(String text) {

        if (text == null || text.isBlank()) {
            return;
        }

        // Split long answers into smaller pieces.
        String[] chunks = splitText(text);

        for (String chunk : chunks) {

            if (chunk.isBlank()) {
                continue;
            }

            speakChunk(chunk);
        }
    }

    private static void speakChunk(String text) {

        try {

            String apiKey =
                    System.getenv("ELEVENLABS_API_KEY");

            if (apiKey == null || apiKey.isEmpty()) {

                System.out.println(
                        "NOURI: ElevenLabs API key not found.");

                return;
            }

            System.out.println(
                    "NOURI: Sending voice text...");

            String json =
                    "{"
                    + "\"text\":\""
                    + escapeJson(text)
                    + "\","
                    + "\"model_id\":\"eleven_multilingual_v2\","
                    + "\"voice_settings\":{"
                    + "\"stability\":0.55,"
                    + "\"similarity_boost\":0.80,"
                    + "\"style\":0.20,"
                    + "\"use_speaker_boost\":true"
                    + "}"
                    + "}";

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(API_URL))
                            .header(
                                    "xi-api-key",
                                    apiKey)
                            .header(
                                    "Content-Type",
                                    "application/json")
                            .header(
                                    "Accept",
                                    "audio/mpeg")
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(json))
                            .build();

            HttpResponse<byte[]> response =
                    CLIENT.send(
                            request,
                            HttpResponse.BodyHandlers
                                    .ofByteArray());

            System.out.println(
                    "ElevenLabs HTTP status: "
                    + response.statusCode());

            if (response.statusCode() != 200) {

                System.out.println(
                        new String(response.body()));

                return;
            }

            Path audioFile =
                    Files.createTempFile(
                            "nouri_voice_",
                            ".mp3");

            Files.write(
                    audioFile,
                    response.body());

            System.out.println(
                    "NOURI: Audio created.");

            playAudio(audioFile.toFile());

        } catch (Exception e) {

            System.out.println(
                    "NOURI Voice error: "
                    + e.getMessage());
        }
    }

    // ==========================================
    // SPLIT LONG TEXT
    // ==========================================

    private static String[] splitText(String text) {

        // Short answers don't need splitting.
        if (text.length() <= 500) {
            return new String[]{text};
        }

        java.util.ArrayList<String> chunks =
                new java.util.ArrayList<>();

        StringBuilder current =
                new StringBuilder();

        String[] sentences =
                text.split("(?<=[.!?])\\s+");

        for (String sentence : sentences) {

            if (current.length()
                    + sentence.length()
                    + 1 <= 500) {

                if (current.length() > 0) {
                    current.append(" ");
                }

                current.append(sentence);

            } else {

                if (current.length() > 0) {
                    chunks.add(current.toString());
                }

                current =
                        new StringBuilder(sentence);
            }
        }

        if (current.length() > 0) {
            chunks.add(current.toString());
        }

        return chunks.toArray(
                new String[0]);
    }

    // ==========================================
    // JSON ESCAPE
    // ==========================================

    private static String escapeJson(
            String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    // ==========================================
    // PLAY AUDIO
    // ==========================================

    private static void playAudio(File file) {

        try {

            Process player =
                    new ProcessBuilder(
                            "cmd",
                            "/c",
                            "start",
                            "",
                            file.getAbsolutePath())
                            .inheritIO()
                            .start();

            /*
             * Wait for the Windows player process
             * to launch before continuing.
             */
            Thread.sleep(300);

        } catch (Exception e) {

            System.out.println(
                    "NOURI: Could not play audio.");
        }
    }
}
