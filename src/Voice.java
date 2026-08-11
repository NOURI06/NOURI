import javax.sound.sampled.*;
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

            System.out.println(
                    "NOURI: MP3 received: "
                    + response.body().length
                    + " bytes.");

            playMP3(response.body());

        } catch (Exception e) {

            System.out.println(
                    "NOURI Voice error: "
                    + e.getMessage());

            e.printStackTrace();
        }
    }

    private static void playMP3(byte[] audioData)
            throws Exception {

        System.out.println(
                "NOURI: Decoding MP3...");

        ByteArrayInputStream input =
                new ByteArrayInputStream(audioData);

        AudioInputStream originalStream =
                AudioSystem.getAudioInputStream(input);

        AudioFormat baseFormat =
                originalStream.getFormat();

        AudioFormat decodedFormat =
                new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false);

        AudioInputStream decodedStream =
                AudioSystem.getAudioInputStream(
                        decodedFormat,
                        originalStream);

        DataLine.Info info =
                new DataLine.Info(
                        SourceDataLine.class,
                        decodedFormat);

        SourceDataLine line =
                (SourceDataLine)
                        AudioSystem.getLine(info);

        line.open(decodedFormat);
        line.start();

        System.out.println(
                "NOURI: Speaking...");

        byte[] buffer =
                new byte[4096];

        int bytesRead;

        while ((bytesRead =
                decodedStream.read(buffer))
                != -1) {

            line.write(
                    buffer,
                    0,
                    bytesRead);
        }

        line.drain();
        line.stop();
        line.close();

        decodedStream.close();
        originalStream.close();

        System.out.println(
                "NOURI: Finished speaking.");
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
