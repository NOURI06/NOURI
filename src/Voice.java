import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

public class Voice {

    private static final String VOICE_ID =
            "WkQNWeRIRZHzOYi4vP18";

    private static final String API_URL =
            "https://api.elevenlabs.io/v1/text-to-speech/" + VOICE_ID;

    public static void speak(String text) {

        try {

            System.out.println("NOURI: Sending text to ElevenLabs...");

            String apiKey =
                    System.getenv("ELEVENLABS_API_KEY");

            if (apiKey == null || apiKey.isEmpty()) {
                System.out.println(
                        "ERROR: ELEVENLABS_API_KEY is not set."
                );
                return;
            }

            String json =
                    "{"
                    + "\"text\":\""
                    + escapeJson(text)
                    + "\","
                    + "\"model_id\":\"eleven_multilingual_v2\""
                    + "}";

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(API_URL))
                            .header("xi-api-key", apiKey)
                            .header("Content-Type", "application/json")
                            .header("Accept", "audio/mpeg")
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(json)
                            )
                            .build();

            HttpClient client =
                    HttpClient.newHttpClient();

            HttpResponse<byte[]> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );

            System.out.println(
                    "ElevenLabs HTTP status: "
                    + response.statusCode()
            );

            System.out.println(
                    "Audio bytes received: "
                    + response.body().length
            );

            if (response.statusCode() != 200) {

                System.out.println(
                        new String(response.body())
                );

                return;
            }

            File audio =
                    File.createTempFile(
                            "nouri_voice_",
                            ".mp3"
                    );

            Files.write(
                    audio.toPath(),
                    response.body()
            );

            System.out.println(
                    "Audio file created:"
            );

            System.out.println(
                    audio.getAbsolutePath()
            );

            System.out.println(
                    "NOURI: Voice generation successful."
            );

        } catch (Exception e) {

            System.out.println(
                    "NOURI Voice error:"
            );

            e.printStackTrace();
        }
    }

    private static String escapeJson(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
