public class WakeWord {

    private static final String WAKE_WORD = "buddy";

    public static boolean isWakeWord(String text) {

        if (text == null) {
            return false;
        }

        String normalized =
                text.toLowerCase()
                        .trim()
                        .replace(",", "")
                        .replace(".", "")
                        .replace("!", "")
                        .replace("?", "");

        return normalized.equals(WAKE_WORD)
                || normalized.startsWith(WAKE_WORD + " ")
                || normalized.endsWith(" " + WAKE_WORD)
                || normalized.contains(" " + WAKE_WORD + " ");
    }
}
