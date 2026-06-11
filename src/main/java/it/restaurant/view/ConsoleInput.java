package it.restaurant.view;

import java.util.*;

public class ConsoleInput {

    private static final Scanner SCANNER = createScanner();

    private static final String FORMAT_ERROR = "Attenzione: il dato inserito non e' nel formato corretto";
    private static final String MIN_ERROR = "Attenzione: e' richiesto un valore maggiore o uguale a ";
    private static final String EMPTY_STRING_ERROR = "Attenzione: non hai inserito alcun carattere";
    private static final String MAX_ERROR = "Attenzione: e' richiesto un valore minore o uguale a ";
    private static final String ALLOWED_CHARS = "Attenzione: i caratteri ammissibili sono: ";

    private static final char YES = 'S';
    private static final char NO = 'N';

    private static Scanner createScanner() {
        Scanner s = new Scanner(System.in);
        s.useDelimiter(System.getProperty("line.separator"));
        return s;
    }

    public static String readString(String message) {
        System.out.print(message);
        return SCANNER.next();
    }

    public static String readNonEmptyString(String message) {
        boolean done = false;
        String value = null;
        do {
            value = readString(message);
            value = value.trim();
            if (value.length() > 0) {
                done = true;
            } else {
                System.out.println(EMPTY_STRING_ERROR);
            }
        } while (!done);
        return value;
    }

    public static int readInt(String message) {
        boolean done = false;
        int value = 0;
        do {
            System.out.print(message);
            try {
                value = SCANNER.nextInt();
                done = true;
            } catch (InputMismatchException e) {
                System.out.println(FORMAT_ERROR);
                SCANNER.next();
            }
        } while (!done);
        return value;
    }

    public static int readInt(String message, int min, int max) {
        boolean done = false;
        int value = 0;
        do {
            value = readInt(message);
            if (value >= min && value <= max) {
                done = true;
            } else if (value < min) {
                System.out.println(MIN_ERROR + min);
            } else {
                System.out.println(MAX_ERROR + max);
            }
        } while (!done);
        return value;
    }

    public static int readIntAtLeast(String message, int min) {
        boolean done = false;
        int value = 0;
        do {
            value = readInt(message);
            if (value >= min) {
                done = true;
            } else {
                System.out.println(MIN_ERROR + min);
            }
        } while (!done);
        return value;
    }

    public static int readNonNegativeInt(String message) {
        return readIntAtLeast(message, 0);
    }

    public static double readDouble(String message, double min, double max) {
        boolean done = false;
        double value = 0;
        do {
            System.out.print(message);
            try {
                value = SCANNER.nextDouble();
                if (value > min && value < max) {
                    done = true;
                } else if (value <= min) {
                    System.out.println(MIN_ERROR + min);
                } else {
                    System.out.println(MAX_ERROR + max);
                }
            } catch (InputMismatchException e) {
                System.out.println(FORMAT_ERROR);
                SCANNER.next();
            }
        } while (!done);
        return value;
    }

    public static boolean readYesNo(String message) {
        String fullMessage = message + "(" + YES + "/" + NO + ")";
        char value = readUpperChar(fullMessage, String.valueOf(YES) + NO);
        return value == YES;
    }

    private static char readChar(String message) {
        boolean done = false;
        char value = '\0';
        do {
            System.out.print(message);
            String input = SCANNER.next();
            if (input.length() > 0) {
                value = input.charAt(0);
                done = true;
            } else {
                System.out.println(EMPTY_STRING_ERROR);
            }
        } while (!done);
        return value;
    }

    private static char readUpperChar(String message, String allowed) {
        boolean done = false;
        char value = '\0';
        do {
            value = readChar(message);
            value = Character.toUpperCase(value);
            if (allowed.indexOf(value) != -1) {
                done = true;
            } else {
                System.out.println(ALLOWED_CHARS + allowed);
            }
        } while (!done);
        return value;
    }

    public static <T> List<T> selectItems(List<T> items, java.util.function.Function<T, String> label,
                                          String selectPrompt, String addAnotherPrompt) {
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> picked = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            System.out.println(Messages.PROMPT_PREFIX + i + " - " + label.apply(items.get(i)));
        }
        do {
            int choice = readInt(selectPrompt, 0, items.size() - 1);
            if (!picked.contains(choice)) {
                picked.add(choice);
            }
        } while (readYesNo(addAnotherPrompt));
        return picked.stream().map(items::get).collect(java.util.stream.Collectors.toList());
    }

    public static <T> T selectOne(List<T> items, java.util.function.Function<T, String> label, String prompt) {
        for (int i = 0; i < items.size(); i++) {
            System.out.println(Messages.PROMPT_PREFIX + i + " - " + label.apply(items.get(i)));
        }
        int choice = readInt(prompt, 0, items.size() - 1);
        return items.get(choice);
    }
}
