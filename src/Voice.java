import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import javazoom.jl.player.Player;

public class Voice {

    private static final String VOICE_ID =
            "WkQNWeRIRZHzOYi4vP18";

    private static final String API_URL =
            "https://api.elevenlabs.io/v1/text-to-speech/" + VOICE_ID;

    public static void speak(String text) {

        try {

            String apiKey =
                    System.getenv("ELEVENLABS_API_KEY");

            if (apiKey == null || apiKey.isEmpty()) {

                System.out.println(
                        "NOURI: ElevenLabs API key not found."
                );

                return;
            }

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
                                    apiKey
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Accept",
                                    "audio/mpeg"
                            )
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
                            HttpResponse.BodyHandlers
                                    .ofByteArray()
                    );

            if (response.statusCode() != 200) {

                System.out.println(
                        "NOURI Voice HTTP error: "
                        + response.statusCode()
                );

                return;
            }

            Path audioFile =
                    Files.createTempFile(
                            "nouri_voice_",
                            ".mp3"
                    );

            Files.write(
                    audioFile,
                    response.body()
            );

            playAudio(audioFile.toFile());

        } catch (Exception e) {

            System.out.println(
                    "NOURI Voice error: "
                    + e.getMessage()
            );
        }
    }

    private static String escapeJson(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static void playAudio(File file) {

        try {

            FileInputStream stream =
                    new FileInputStream(file);

            Player player =
                    new Player(stream);

            player.play();

            stream.close();

            file.delete();

        } catch (Exception e) {

            System.out.println(
                    "NOURI: Could not play audio."
            );

            System.out.println(
                    e.getMessage()
            );
        }
    }
}
