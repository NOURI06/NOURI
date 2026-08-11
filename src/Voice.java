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
            "https://api.elevenlabs.io/v1/text-to-speech/"
            + VOICE_ID;

    private static final HttpClient CLIENT =
            HttpClient.newHttpClient();

    public static void speak(String text) {

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
                    + "\"output_format\":\"mp3_44100_128\","
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

            Path file =
                    Path.of(
                            "nouri_voice_test.mp3");

            Files.write(
                    file,
                    response.body());

            System.out.println(
                    "NOURI: Voice saved to:");

            System.out.println(
                    file.toAbsolutePath());

            System.out.println(
                    "NOURI: Audio size: "
                    + response.body().length
                    + " bytes.");

        } catch (Exception e) {

            System.out.println(
                    "NOURI Voice error: "
                    + e.getMessage());

            e.printStackTrace();
        }
    }

    private static String escapeJson(
            String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
