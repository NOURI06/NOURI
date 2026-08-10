public class WakeWord {

    private static final String WAKE_PHRASE = "wake up nou";

    public static boolean isWakeWord(String text) {

        if (text == null) {
            return false;
        }

        text = text
                .toLowerCase()
                .trim();

        // Remove common punctuation
        text = text
                .replace(",", "")
                .replace(".", "")
                .replace("!", "")
                .replace("?", "");

        return text.contains(WAKE_PHRASE);
    }
}
