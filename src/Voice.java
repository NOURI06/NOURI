import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;

public class Voice {

    private static final String VOICE_ID =
            "WkQNWeRIRZHzOYi4vP18";

    private static final String API_URL =
            "https://api.elevenlabs.io/v1/text-to-speech/"
            + VOICE_ID;

    private static final java.net.http.HttpClient CLIENT =
            java.net.http.HttpClient.newHttpClient();

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

            java.net.http.HttpRequest request =
                    java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create(API_URL))
                            .header("xi-api-key", apiKey)
                            .header(
                                    "Content-Type",
                                    "application/json")
                            .header(
                                    "Accept",
                                    "audio/mpeg")
                            .POST(
                                    java.net.http.HttpRequest
                                            .BodyPublishers
                                            .ofString(json))
                            .build();

            java.net.http.HttpResponse<byte[]> response =
                    CLIENT.send(
                            request,
                            java.net.http.HttpResponse
                                    .BodyHandlers
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

    private static void playMP3(byte[] mp3Data)
            throws Exception {

        System.out.println(
                "NOURI: JLayer decoding MP3...");

        ByteArrayInputStream input =
                new ByteArrayInputStream(mp3Data);

        Bitstream bitstream =
                new Bitstream(input);

        Decoder decoder =
                new Decoder();

        SourceDataLine line = null;

        try {

            Header header;

            while ((header = bitstream.readFrame()) != null) {

                SampleBuffer output =
                        (SampleBuffer)
                                decoder.decodeFrame(
                                        header,
                                        bitstream);

                if (line == null) {

                    AudioFormat format =
                            new AudioFormat(
                                    output.getSampleFrequency(),
                                    16,
                                    output.getChannelCount(),
                                    true,
                                    false);

                    DataLine.Info info =
                            new DataLine.Info(
                                    SourceDataLine.class,
                                    format);

                    line =
                            (SourceDataLine)
                                    AudioSystem.getLine(info);

                    line.open(format);
                    line.start();

                    System.out.println(
                            "NOURI: Speaking...");
                }

                short[] samples =
                        output.getBuffer();

                byte[] pcm =
                        new byte[
                                output.getBufferLength() * 2];

                for (int i = 0;
                     i < output.getBufferLength();
                     i++) {

                    pcm[i * 2] =
                            (byte)
                            (samples[i] & 0xff);

                    pcm[i * 2 + 1] =
                            (byte)
                            ((samples[i] >> 8) & 0xff);
                }

                line.write(
                        pcm,
                        0,
                        pcm.length);

                bitstream.closeFrame();
            }

        } finally {

            if (line != null) {

                line.drain();
                line.stop();
                line.close();
            }

            bitstream.close();
        }

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
