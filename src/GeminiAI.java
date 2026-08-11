import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiAI {

    private static final String MODEL = "gemini-3.6-flash";

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

            String prompt =
                    "You are NOURI, a helpful voice assistant. "
                    + "Answer the user's question accurately and naturally. "
                    + "Keep your answer concise because it will be spoken aloud. "
                    + "For normal questions, use no more than 3 short sentences. "
                    + "If the user asks for a large amount of information, "
                    + "give a useful summary instead of a huge response. "
                    + "Do not use Markdown, bullet points, symbols, or equations. "
                    + "Speak naturally. "
                    + "User: " + escapeJson(question);

            String json =
                    "{"
                    + "\"contents\":["
                    + "{"
                    + "\"parts\":["
                    + "{"
                    + "\"text\":\"" + prompt + "\""
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

            System.out.println(
                    "Gemini HTTP status: "
                    + response.statusCode()
            );

            if (response.statusCode() != 200) {

                System.out.println(response.body());

                return "Gemini returned an error: "
                        + response.statusCode();
            }

            return extractText(response.body());

        } catch (Exception e) {

            System.out.println(
                    "Gemini error: "
                    + e.getMessage()
            );

            return "I couldn't connect to Gemini.";
        }
    }

    private String escapeJson(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private String extractText(String json) {

        String marker = "\"text\":";
        int position = json.indexOf(marker);

        if (position == -1) {
            return "Gemini returned no text.";
        }

        position += marker.length();

        while (position < json.length()
                && Character.isWhitespace(json.charAt(position))) {
            position++;
        }

        if (position >= json.length()
                || json.charAt(position) != '"') {
            return "Gemini returned an unexpected response.";
        }

        position++;

        StringBuilder result = new StringBuilder();
        boolean escaped = false;

        while (position < json.length()) {

            char c = json.charAt(position++);

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

                    case '/':
                        result.append('/');
                        break;

                    case 'b':
                        result.append('\b');
                        break;

                    case 'f':
                        result.append('\f');
                        break;

                    default:
                        result.append(c);
                        break;
                }

                escaped = false;

            } else if (c == '\\') {

                escaped = true;

            } else if (c == '"') {

                break;

            } else {

                result.append(c);
            }
        }

        return cleanResponse(result.toString());
    }

    private String cleanResponse(String text) {

        return text
                .replace("\\u003e", ">")
                .replace("\\u003c", "<")
                .replace("\\u0026", "&")
                .replace("###", "")
                .trim();
    }
}
