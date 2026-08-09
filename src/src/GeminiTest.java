public class GeminiTest {

    public static void main(String[] args) {

        GeminiAI gemini = new GeminiAI();

        String response = gemini.ask(
                "Hello Gemini. Introduce yourself in one short sentence."
        );

        System.out.println();
        System.out.println("=================================");
        System.out.println("        GEMINI RESPONSE");
        System.out.println("=================================");
        System.out.println(response);
        System.out.println("=================================");
    }
}
