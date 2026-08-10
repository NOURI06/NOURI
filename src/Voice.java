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
                    + "\"model_id\":\"eleven_multilingual_v2\""
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

            System.out.println(
                    "ElevenLabs HTTP status: "
                    + response.statusCode()
            );

            if (response.statusCode() != 200) {

                System.out.println(
                        new String(response.body())
                );

                return;
            }

            File audioFile =
                    File.createTempFile(
                            "nouri_voice_",
                            ".mp3"
                    );

            Files.write(
                    audioFile.toPath(),
                    response.body()
            );

            playAudio(audioFile);

        } catch (Exception e) {

            System.out.println(
                    "NOURI Voice error: "
                    + e.getMessage()
            );
        }
    }

    private static void playAudio(File file)
            throws Exception {

        String path =
                file.getAbsolutePath()
                        .replace("'", "''");

        String command =
                "Add-Type -AssemblyName PresentationCore; "
                + "$p=New-Object "
                + "System.Windows.Media.MediaPlayer; "
                + "$p.Open([Uri]'"
                + path
                + "'); "
                + "Start-Sleep -Milliseconds 500; "
                + "$p.Play(); "
                + "Start-Sleep -Milliseconds 500; "
                + "while($p.NaturalDuration.HasTimeSpan "
                + "-eq $false) { "
                + "Start-Sleep -Milliseconds 100 }; "
                + "Start-Sleep -Milliseconds "
                + "([int]$p.NaturalDuration.TimeSpan.TotalMilliseconds); "
                + "$p.Stop(); "
                + "$p.Close();";

        Process process =
                new ProcessBuilder(
                        "powershell",
                        "-NoProfile",
                        "-Command",
                        command
                ).start();

        process.waitFor();

        file.delete();
    }

    private static String escapeJson(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
