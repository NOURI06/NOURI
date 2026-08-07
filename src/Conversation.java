import java.util.ArrayList;

public class Conversation {

    private ArrayList<String> history = new ArrayList<>();

    public void add(String message) {
        history.add(message);
    }

    public void show() {

        System.out.println();
        System.out.println("========== Conversation ==========");

        for (String message : history) {
            System.out.println(message);
        }

        System.out.println("==================================");
        System.out.println();
    }
}