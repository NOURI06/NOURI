import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

public class SpeechToText {

    private static final String API_URL =
            "https://api.elevenlabs.io/v1/speech-to-text";

    public static String transcribe(Path audioFile) {

        try {

            String apiKey =
                    System.getenv("ELEVENLABS_API_KEY");

            if (apiKey == null || apiKey.isEmpty()) {

                System.out.println(
                        "NOURI: ElevenLabs API key not found."
                );

                return null;
            }

            String boundary =
                    "----NOURIBoundary" +
                    System.currentTimeMillis();

            byte[] audio =
                    Files.readAllBytes(audioFile);

            ByteArrayOutputStream body =
                    new ByteArrayOutputStream();

            PrintWriter writer =
                    new PrintWriter(
                            new OutputStreamWriter(
                                    body,
                                    "UTF-8"
                            ),
                            true
                    );

            writer.append("--")
                    .append(boundary)
                    .append("\r\n");

            writer.append(
                    "Content-Disposition: form-data; " +
                    "name=\"model_id\"\r\n\r\n"
            );

            writer.append("scribe_v1")
                    .append("\r\n");

            writer.append("--")
                    .append(boundary)
                    .append("\r\n");

            writer.append(
                    "Content-Disposition: form-data; " +
                    "name=\"file\"; filename=\"speech.wav\"\r\n"
            );

            writer.append(
                    "Content-Type: audio/wav\r\n\r\n"
            );

            writer.flush();

            body.write(audio);

            writer = new PrintWriter(
                    new OutputStreamWriter(
                            body,
                            "UTF-8"
                    ),
                    true
            );

            writer.append("\r\n")
                    .append("--")
                    .append(boundary)
                    .append("--")
                    .append("\r\n");

            writer.flush();

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(API_URL))
                            .header(
                                    "xi-api-key",
                                    apiKey
                            )
                            .header(
                                    "Content-Type",
                                    "multipart/form-data; boundary="
                                    + boundary
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofByteArray(
                                                    body.toByteArray()
                                            )
                            )
                            .build();

            HttpClient client =
                    HttpClient.newHttpClient();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers
                                    .ofString()
                    );

            System.out.println(
                    "ElevenLabs STT HTTP status: "
                    + response.statusCode()
            );

            if (response.statusCode() != 200) {

                System.out.println(
                        response.body()
                );

                return null;
            }

            String json =
                    response.body();

            String marker =
                    "\"text\":\"";

            int start =
                    json.indexOf(marker);

            if (start == -1) {

                System.out.println(
                        "NOURI: No transcription found."
                );

                return null;
            }

            start += marker.length();

            int end =
                    json.indexOf(
                            "\"",
                            start
                    );

            if (end == -1) {
                return null;
            }

            String text =
                    json.substring(
                            start,
                            end
                    );

            text = text
                    .replace("\\n", " ")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

            return text;

        } catch (Exception e) {

            System.out.println(
                    "NOURI STT error: "
                    + e.getMessage()
            );

            return null;
        }
    }
}
