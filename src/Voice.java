import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Voice {

    private static final String VOICE_ID =
            "WkQNWeRIRZHzOYi4vP18";

    private static final String API_URL =
            "https://api.elevenlabs.io/v1/text-to-speech/"
            + VOICE_ID;

    private static final HttpClient CLIENT =
            HttpClient.newHttpClient();

    public static void speak(String text) {

        if (text == null || text.isBlank()) {
            return;
        }

        String[] chunks = splitText(text);

        for (String chunk : chunks) {

            if (!chunk.isBlank()) {
                speakChunk(chunk);
            }
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
                    "NOURI: Generating voice...");

            String json =
                    "{"
                    + "\"text\":\""
                    + escapeJson(text)
                    + "\","
                    + "\"model_id\":\"eleven_multilingual_v2\","
                    + "\"output_format\":\"pcm_44100\","
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
                                    "audio/pcm")
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

            System.out.println(
                    "NOURI: Playing voice directly...");

            playPCM(response.body());

        } catch (Exception e) {

            System.out.println(
                    "NOURI Voice error: "
                    + e.getMessage());
        }
    }

    // ==========================================
    // DIRECT PCM PLAYBACK
    // ==========================================

    private static void playPCM(byte[] audioData)
            throws Exception {

        /*
         * ElevenLabs pcm_44100:
         *
         * 44,100 Hz
         * 16-bit
         * mono
         * signed
         * little-endian
         */

        javax.sound.sampled.AudioFormat format =
                new javax.sound.sampled.AudioFormat(
                        44100,
                        16,
                        1,
                        true,
                        false);

        AudioInputStream audioStream =
                new AudioInputStream(
                        new ByteArrayInputStream(
                                audioData),
                        format,
                        audioData.length / 2);

        Clip clip =
                AudioSystem.getClip();

        clip.open(audioStream);

        clip.start();

        // Wait until NOURI finishes speaking.
        while (clip.isRunning()) {
            Thread.sleep(20);
        }

        clip.stop();
        clip.close();
        audioStream.close();

        System.out.println(
                "NOURI: Finished speaking.");
    }

    // ==========================================
    // SPLIT LONG ANSWERS
    // ==========================================

    private static String[] splitText(String text) {

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
                    chunks.add(
                            current.toString());
                }

                current =
                        new StringBuilder(sentence);
            }
        }

        if (current.length() > 0) {
            chunks.add(
                    current.toString());
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
}
