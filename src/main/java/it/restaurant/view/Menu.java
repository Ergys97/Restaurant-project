package it.restaurant.view;

public class Menu {

    private static final String FRAME = "--------------------------------";
    private static final String EXIT_OPTION = "0\tEsci";
    private static final String PROMPT = "Digita il numero dell'opzione desiderata > ";

    private final String title;
    private final String[] options;

    public Menu(String title, String[] options) {
        this.title = title;
        this.options = options;
    }

    public int choose() {
        print();
        return ConsoleInput.readInt(PROMPT, 0, options.length);
    }

    private void print() {
        System.out.println(FRAME);
        System.out.println(title);
        System.out.println(FRAME);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + "\t" + options[i]);
        }
        System.out.println();
        System.out.println(EXIT_OPTION);
        System.out.println();
    }
}
