import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiAI {

    private static final String MODEL = "gemini-2.5-flash";

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
            + MODEL
            + ":generateContent";

    private final HttpClient client = HttpClient.newHttpClient();

    public String ask(String question) {

        String apiKey = System.getenv("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            return "Gemini API key is not configured.";
        }

        try {

            String escapedQuestion = question
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\r", "\\r")
                    .replace("\n", "\\n");

            String json =
                    "{"
                    + "\"contents\":["
                    + "{"
                    + "\"parts\":["
                    + "{"
                    + "\"text\":\"" + escapedQuestion + "\""
                    + "}"
                    + "]"
                    + "}"
                    + "]"
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {

                System.out.println("Gemini HTTP status: "
                        + response.statusCode());

                System.out.println("Gemini response:");
                System.out.println(response.body());

                return "Gemini returned an error: "
                        + response.statusCode();
            }

            return extractText(response.body());

        } catch (Exception e) {

            e.printStackTrace();

            return "I couldn't connect to Gemini.";
        }
    }

    private String extractText(String json) {

        String marker = "\"text\":\"";

        int start = json.indexOf(marker);

        if (start == -1) {
            return "Gemini returned no text.";
        }

        start += marker.length();

        StringBuilder result = new StringBuilder();

        boolean escaped = false;

        for (int i = start; i < json.length(); i++) {

            char c = json.charAt(i);

            if (escaped) {

                switch (c) {

                    case 'n':
                        result.append('\n');
                        break;

                    case 'r':
                        result.append('\r');
                        break;

                    case 't':
                        result.append('\t');
                        break;

                    case '"':
                        result.append('"');
                        break;

                    case '\\':
                        result.append('\\');
                        break;

                    default:
                        result.append(c);
                        break;
                }

                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"') {
                break;
            }

            result.append(c);
        }

        return result.toString();
    }
}
