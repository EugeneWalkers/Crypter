import java.io.*;

public class Crypter {

    private static final int FIRST_UP_ELEMENT_NUMBER = 65;
    private static final int LAST_UP_ELEMENT_NUMBER = 90;
    private static final int NUMBER_OF_ELEMENTS = LAST_UP_ELEMENT_NUMBER - FIRST_UP_ELEMENT_NUMBER + 1;

    private String readTextFromFileWithoutSpacesToString(final File file) {
        BufferedReader reader = null;
        StringBuilder temporary = new StringBuilder();
        StringBuilder text = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            while (!(temporary.append(reader.readLine()).toString()).equals("null")) {
                text.append(temporary.toString());
                temporary.setLength(0);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return text.toString().toUpperCase().replaceAll("\\W", "");
    }


    private void writeStringToFile(final String text, final File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encrypt(final File _key, final File _text, final String outputName) {
        final String text = readTextFromFileWithoutSpacesToString(_text);
        final String key = readTextFromFileWithoutSpacesToString(_key);
        final StringBuilder outputText = new StringBuilder();

        int indexOfKey = 0;
        int keyLength = key.length();

        for (int i = 0; i < text.length(); i++) {
            final char originalElement = text.charAt(i);

            final char keyElement = key.charAt(indexOfKey);

            outputText.append(getNewElementToEncrypt(originalElement, keyElement));

            if (indexOfKey != keyLength - 1) {
                indexOfKey++;
            } else {
                indexOfKey = 0;
            }

        }


        File output = new File(outputName);

        writeStringToFile(outputText.toString(), output);

    }

    public void decrypt(final File _key, final File _text, final String outputName) {
        final String text = readTextFromFileWithoutSpacesToString(_text);
        final String key = readTextFromFileWithoutSpacesToString(_key);
        final StringBuilder outputText = new StringBuilder();

        int indexOfKey = 0;
        int keyLength = key.length();

        for (int i = 0; i < text.length(); i++) {
            final char originalElement = text.charAt(i);

            final char keyElement = key.charAt(indexOfKey);

            outputText.append(getNewElementToDecrypt(originalElement, keyElement));

            if (indexOfKey != keyLength - 1) {
                indexOfKey++;
            } else {
                indexOfKey = 0;
            }

        }

        File output = new File(outputName);

        writeStringToFile(outputText.toString(), output);

    }

    private int charToInt(char symbol) {
        return (int) symbol - FIRST_UP_ELEMENT_NUMBER + 1;
    }

    private char getNewElementToEncrypt(char oldElement, char key) {
        int oldPosition = charToInt(oldElement);
        int keyPosition = charToInt(key);

        int newPosition = (oldPosition + keyPosition - 1) % NUMBER_OF_ELEMENTS;
        newPosition += FIRST_UP_ELEMENT_NUMBER;

        return (char) newPosition;
    }

    private char getNewElementToDecrypt(char oldElement, char key) {
        int oldPosition = charToInt(oldElement);
        int keyPosition = charToInt(key);

        if (oldPosition <= keyPosition) {
            oldPosition += NUMBER_OF_ELEMENTS;
        }

        int newPosition = oldPosition - keyPosition;
        newPosition += FIRST_UP_ELEMENT_NUMBER - 1;

        return (char) newPosition;
    }
}

