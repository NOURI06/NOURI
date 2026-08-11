import java.io.*;
import java.nio.file.*;

public class GeorgeTest {

    public static void main(String[] args) throws Exception {

        System.out.println("NOURI: Testing Microsoft George...");

        String voicePath =
            System.getenv("WINDIR") +
            "\\Speech_OneCore\\Engines\\TTS\\en-GB\\M2057George";

        System.out.println("George voice path:");
        System.out.println(voicePath);

        if (!Files.exists(Paths.get(voicePath))) {
            System.out.println("ERROR: George voice files were not found.");
            return;
        }

        System.out.println("George voice files found!");
        System.out.println("Next we will connect the OneCore speech engine.");

    }
}
