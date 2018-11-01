import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    public void analyzeKasiski(final File file) {
        final String text = readTextFromFileWithoutSpacesToString(file);
        final List<LGramData> pairs = new ArrayList<>();

        for (int l = 3; l < 5; l++) {
            final LGramData lGramData = getLGramInfo(l, text);

            pairs.add(lGramData);
        }

        //System.out.println(pairs.toString());

        final List<Integer> nodes = new ArrayList<>();

        for (int i = 0; i < pairs.size(); i++) {
            final LGramData pair = pairs.get(i);

            System.out.println(pair.getDistances().toString());

            final int nod = findNOD(pair.getDistances());

            if (!nodes.contains(nod)) {
                nodes.add(nod);
            }
        }

        System.out.println(nodes.toString());


    }

    private LGramData getLGramInfo(final int l, final String text) {
        final LGramData data = new LGramData(l);

        final List<Integer> distances = new ArrayList<>();
        int frequency = 0;

        for (int i = 0; i < text.length() - l; i++) {
            final Pair<Integer, Integer> pair = findDistance(i, l, text);

            if (pair.getKey() != Integer.MAX_VALUE) {
                final int distance = pair.getKey();
                frequency += pair.getValue();

                distances.add(distance);
            }
        }

        data.add(distances);
        data.setFrequency(frequency);

        System.out.println(frequency);
        return data;
    }

    private Pair<Integer, Integer> findDistance(final int i, final int l, final String text) {
        final StringBuilder builder = new StringBuilder(text);
        int distance = Integer.MAX_VALUE;
        int frequency = 0;

        for (int j = i + l; j < text.length() - l; j++) {
            if (builder.substring(i, i + l).equals(builder.substring(j, j + l))) {
                distance = Math.min(distance, Math.abs(j - i));
                frequency++;

                break;
            }
        }

        return new Pair<>(distance, frequency);
    }

    private int findNOD(final List<Integer> integers) {
        final int min = findMin(integers);
        int nod = -1;

        for (int k = 1; k <= min; k++) {
            if (isDivided(k, integers)) {
                nod = k;
            }
        }

        return nod;
    }

    private int findMin(final List<Integer> integers) {
        int min = Integer.MAX_VALUE;

        for (final Integer integer : integers) {
            min = Math.min(min, integer);
        }

        return min;
    }

    private boolean isDivided(final int number, final List<Integer> integers) {
        for (final int integer : integers) {
            if (integer % number != 0) {
                return false;
            }
        }

        return true;
    }
}

