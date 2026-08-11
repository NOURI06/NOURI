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

    private final HttpClient client =
            HttpClient.newHttpClient();

    public String ask(String question) {

        String apiKey =
                System.getenv("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {

            return "Gemini API key is not configured.";
        }

        try {

            String systemInstruction =
                    "You are NOURI, a personal voice assistant. "
                    + "Answer naturally and clearly. "
                    + "Your responses will be spoken aloud using text to speech. "
                    + "Keep normal answers concise, usually one to four sentences. "
                    + "Do not use Markdown, bullet points, tables, headings, "
                    + "or long lists unless the user specifically asks for them. "
                    + "If the user asks for a large amount of information, "
                    + "give a useful spoken summary first and say that you can "
                    + "continue with more details if they want. "
                    + "Do not repeat the user's question.";

            String escapedInstruction =
                    escapeJson(systemInstruction);

            String escapedQuestion =
                    escapeJson(question);

            String json =
                    "{"
                    + "\"system_instruction\":{"
                    + "\"parts\":["
                    + "{"
                    + "\"text\":\""
                    + escapedInstruction
                    + "\""
                    + "}"
                    + "]"
                    + "},"
                    + "\"contents\":["
                    + "{"
                    + "\"role\":\"user\","
                    + "\"parts\":["
                    + "{"
                    + "\"text\":\""
                    + escapedQuestion
                    + "\""
                    + "}"
                    + "]"
                    + "}"
                    + "],"
                    + "\"generationConfig\":{"
                    + "\"maxOutputTokens\":300,"
                    + "\"temperature\":0.7"
                    + "}"
                    + "}";

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(API_URL))
                            .header(
                                    "Content-Type",
                                    "application/json")
                            .header(
                                    "x-goog-api-key",
                                    apiKey)
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(json))
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString());

            System.out.println(
                    "Gemini HTTP status: "
                    + response.statusCode());

            if (response.statusCode() != 200) {

                System.out.println(
                        response.body());

                return "Gemini returned an error: "
                        + response.statusCode();
            }

            String answer =
                    extractText(response.body());

            return cleanForVoice(answer);

        } catch (Exception e) {

            e.printStackTrace();

            return "I couldn't connect to Gemini.";
        }
    }

    private String extractText(String json) {

        String marker = "\"text\":";

        int position =
                json.indexOf(marker);

        if (position == -1) {

            return "Gemini returned no text.";
        }

        position += marker.length();

        while (position < json.length()
                && Character.isWhitespace(
                        json.charAt(position))) {

            position++;
        }

        if (position >= json.length()
                || json.charAt(position) != '"') {

            return "Gemini returned an unexpected response.";
        }

        position++;

        StringBuilder result =
                new StringBuilder();

        boolean escaped = false;

        while (position < json.length()) {

            char c =
                    json.charAt(position++);

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

        return result.toString();
    }

    private String cleanForVoice(String text) {

        if (text == null) {

            return "I didn't get a response.";
        }

        String cleaned = text;

        // Remove Markdown headings
        cleaned = cleaned.replaceAll(
                "(?m)^#{1,6}\\s*",
                "");

        // Remove bullet points
        cleaned = cleaned.replaceAll(
                "(?m)^\\s*[-*+]\\s+",
                "");

        // Remove numbered list prefixes
        cleaned = cleaned.replaceAll(
                "(?m)^\\s*\\d+[.)]\\s+",
                "");

        // Remove Markdown emphasis markers
        cleaned = cleaned.replace(
                "**",
                "");

        cleaned = cleaned.replace(
                "__",
                "");

        cleaned = cleaned.replace(
                "###",
                "");

        cleaned = cleaned.replace(
                "##",
                "");

        cleaned = cleaned.replace(
                "#",
                "");

        // Remove excessive whitespace
        cleaned = cleaned.replaceAll(
                "\\s+",
                " ");

        return cleaned.trim();
    }

    private String escapeJson(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
```
