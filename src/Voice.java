import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

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
                            .header("xi-api-key", apiKey)
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Accept",
                                    "audio/pcm"
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

                System.out.println(
                        new String(response.body())
                );

                return;
            }

            Path audioFile =
                    Files.createTempFile(
                            "nouri_voice_",
                            ".pcm"
                    );

            Files.write(
                    audioFile,
                    response.body()
            );

            playPCM(audioFile.toFile());

            Files.deleteIfExists(audioFile);

        } catch (Exception e) {

            System.out.println(
                    "NOURI Voice error: "
                    + e.getMessage()
            );
        }
    }

    private static void playPCM(File file)
            throws Exception {

        AudioFormat format =
                new AudioFormat(
                        44100,
                        16,
                        1,
                        true,
                        false
                );

        byte[] audioData =
                Files.readAllBytes(
                        file.toPath()
                );

        AudioInputStream audioStream =
                new AudioInputStream(
                        new java.io.ByteArrayInputStream(
                                audioData
                        ),
                        format,
                        audioData.length / 2
                );

        Clip clip =
                AudioSystem.getClip();

        clip.open(audioStream);

        clip.start();

        while (clip.isRunning()) {
            Thread.sleep(50);
        }

        clip.stop();
        clip.close();
        audioStream.close();
    }

    private static String escapeJson(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
