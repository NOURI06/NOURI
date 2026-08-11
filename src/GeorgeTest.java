import java.nio.file.*;

public class GeorgeTest {

    public static void main(String[] args) {

        System.out.println("NOURI: Testing Microsoft George...");

        String voicePath =
            System.getenv("WINDIR") +
            "\\Speech_OneCore\\Engines\\TTS\\en-GB\\M2057George.APM";

        System.out.println("George voice file:");
        System.out.println(voicePath);

        if (Files.exists(Paths.get(voicePath))) {
            System.out.println("George voice files found!");
            System.out.println("Microsoft George is installed.");
        } else {
            System.out.println("ERROR: George voice files were not found.");
        }
    }
}
