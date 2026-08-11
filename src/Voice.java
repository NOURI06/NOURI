import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

public class Voice {

    private static final HttpClient CLIENT =
            HttpClient.newHttpClient();

    private static final String ELEVENLABS_URL =
            "https://api.elevenlabs.io/v1/text-to-speech/";

    public static void speak(String text) {

        if (text == null || text.isBlank()) {
            return;
        }

        System.out.println("NOURI: Generating voice...");

        try {

            String apiKey =
                    System.getenv("ELEVENLABS_API_KEY");

            String voiceId =
                    System.getenv("ELEVENLABS_VOICE_ID");

            if (apiKey == null
                    || apiKey.isBlank()
                    || voiceId == null
                    || voiceId.isBlank()) {

                System.out.println(
                        "NOURI: ElevenLabs configuration missing."
                );

                windowsSpeak(text);
                return;
            }

            String escapedText =
                    text.replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\r", " ")
                        .replace("\n", " ");

            String json =
                    "{"
                    + "\"text\":\"" + escapedText + "\","
                    + "\"model_id\":\"eleven_multilingual_v2\""
                    + "}";

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(
                                    ELEVENLABS_URL + voiceId
                            ))
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

            HttpResponse<byte[]> response =
                    CLIENT.send(
                            request,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );

            System.out.println(
                    "ElevenLabs HTTP status: "
                            + response.statusCode()
            );

            if (response.statusCode() == 200) {

                System.out.println(
                        "NOURI: ElevenLabs voice received."
                );

                playMP3(response.body());
                return;
            }

            if (response.statusCode() == 401) {

                String error =
                        new String(response.body());

                System.out.println(
                        "ElevenLabs rejected the request."
                );

                if (error.contains("quota_exceeded")) {

                    System.out.println(
                            "NOURI: ElevenLabs quota exceeded."
                    );

                    System.out.println(
                            "NOURI: Switching to Windows voice."
                    );

                    windowsSpeak(text);
                    return;
                }
            }

            System.out.println(
                    new String(response.body())
            );

            windowsSpeak(text);

        } catch (Exception e) {

            System.out.println(
                    "NOURI voice error: "
                            + e.getMessage()
            );

            windowsSpeak(text);
        }
    }

    private static void windowsSpeak(String text) {

        try {

            String safeText =
                    text.replace("\"", "'")
                        .replace("\r", " ")
                        .replace("\n", " ");

            String command =
                    "Add-Type -AssemblyName System.Speech; "
                    + "$voice = New-Object "
                    + "System.Speech.Synthesis.SpeechSynthesizer; "
                    + "$voice.Speak(\""
                    + safeText
                    + "\")";

            ProcessBuilder process =
                    new ProcessBuilder(
                            "powershell",
                            "-NoProfile",
                            "-Command",
                            command
                    );

            process.redirectErrorStream(true);

            Process p = process.start();

            p.waitFor();

            System.out.println(
                    "NOURI: Windows voice finished."
            );

        } catch (Exception e) {

            System.out.println(
                    "NOURI Windows voice error: "
                            + e.getMessage()
            );
        }
    }

    private static void playMP3(byte[] audio)
            throws Exception {

        File file =
                File.createTempFile(
                        "nouri_voice_",
                        ".mp3"
                );

        try {

            Files.write(
                    file.toPath(),
                    audio
            );

            ProcessBuilder player =
                    new ProcessBuilder(
                            "powershell",
                            "-NoProfile",
                            "-Command",
                            "Add-Type -AssemblyName PresentationCore; "
                            + "$player = New-Object "
                            + "System.Windows.Media.MediaPlayer; "
                            + "$player.Open([Uri]'"
                            + file.getAbsolutePath()
                            + "'); "
                            + "$player.Play(); "
                            + "Start-Sleep -Seconds 30; "
                            + "$player.Close()"
                    );

            Process process =
                    player.start();

            process.waitFor();

        } finally {

            file.delete();
        }
    }
}
