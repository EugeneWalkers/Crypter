import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Crypter {

    private static final int FIRST_UP_ELEMENT_NUMBER = 65;
    private static final int LAST_UP_ELEMENT_NUMBER = 90;
    private static final int NUMBER_OF_ELEMENTS = LAST_UP_ELEMENT_NUMBER - FIRST_UP_ELEMENT_NUMBER + 1;

    private final double[] realFrequencies = {0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.0228, 0.02015,
            0.06094, 0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987,
            0.06327, 0.09056, 0.02758, 0.00978, 0.0236, 0.0015, 0.01974, 0.00074};

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

        for (int l = 4; l < 7; l++) {
            pairs.add(getLGramInfo(l, text));
        }

        //System.out.println(pairs.toString());

        final List<Integer> nodes = new ArrayList<>();

        for (int i=0; i<pairs.size(); i++){
            analyzeDistances(pairs.get(i).getDistances());
        }

        for (int i = 0; i < pairs.size(); i++) {
            final LGramData pair = pairs.get(i);
            final int nod = findNOD(pair.getDistances());

            if (!nodes.contains(nod)) {
                nodes.add(nod);
            }
        }

        delete1(nodes);
        final int nod = findNOD(nodes);

        System.out.println("Длина ключа = " + nod);

        final String key = findKey(nod, file);

        System.out.println(key);
    }

    private void analyzeDistances(final List<Integer> distances){
        for (int i=0; i<distances.size(); i++){
            if (distances.get(i) < 3){
                distances.remove(i--);
            }
        }

        Collections.sort(distances);

        final List<Integer> subDistances = new ArrayList<>();
        for (int i=distances.size()/2; i<distances.size(); i++){
            subDistances.add(distances.get(i));
        }
        distances.clear();
        distances.addAll(subDistances);
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

        //System.out.println(frequency);
        return data;
    }

    private Pair<Integer, Integer> findDistance(final int i, final int l, final String text) {
        final StringBuilder builder = new StringBuilder(text);
        int distance = Integer.MAX_VALUE;
        int frequency = 0;

        for (int j = i + l; j < text.length() - l; j++) {
            if (builder.substring(i, i + l).equals(builder.substring(j, j + l))) {
                distance = Math.min(distance, Math.abs(j - i));
                //System.out.println(distance + ":" + " i = " + i + "; j = " + j + "; text = " + builder.substring(i, i+l));
                frequency++;

                break;
            }
        }

        return new Pair<>(distance, frequency);
    }

    private void delete1(final List<Integer> integers){
        for (int i=0; i<integers.size(); i++){
            if (integers.get(i) == 1){
                integers.remove(i);
            }
        }
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

    String findKey(int keyLength, File file) {
        final String text = readTextFromFileWithoutSpacesToString(file);
        StringBuilder key = new StringBuilder();
        ArrayList<StringBuilder> subtexts = new ArrayList<>(keyLength);
        for (int i = 0; i < keyLength; i++) {
            subtexts.add(new StringBuilder());
            for (int j = i; j < text.length() - keyLength; j += keyLength) {
                subtexts.get(i).append(text.charAt(j));
            }
//System.out.println(subtexts.get(i));
        }

        int lettersNumber = 26;
        List<List<Double>> frequencies = new ArrayList<>(keyLength);
        StringBuilder currentText;
        for (int i = 0; i < keyLength; i++) {
            frequencies.add(new ArrayList<>(lettersNumber));
            currentText = subtexts.get(i);
            for (int j = 0; j < lettersNumber; j++) {
                char letterToCompare = (char) (j + FIRST_UP_ELEMENT_NUMBER);
                frequencies.get(i).add(0d);
                for (int k = 0; k < currentText.length(); k++) {
                    if (currentText.charAt(k) == letterToCompare) {
                        frequencies.get(i).set(j, frequencies.get(i).get(j) + 1);
                    }
                }
                frequencies.get(i).set(j, frequencies.get(i).get(j) / currentText.length());
            }
        }

        for (int i = 0; i < keyLength; i++) {
            int shiftOrLetter = 24 - findShift(frequencies.get(i));
//System.out.println(Character.valueOf((char) (shiftOrLetter + FIRST_UP_ELEMENT_NUMBER)));
            key.append(Character.valueOf((char) (shiftOrLetter + FIRST_UP_ELEMENT_NUMBER)));
        }

        return key.toString();
    }

    private List<Double> cyclicRightShift(List<Double> frequencies, int k) {
        List<Double> newFrequencies = new ArrayList<>();

        for (int i = frequencies.size() - 1 - k; i < frequencies.size(); i++) {
            newFrequencies.add(frequencies.get(i));
        }

        for (int i = 0; i < frequencies.size() - k; i++) {
            newFrequencies.add(frequencies.get(i));
        }

        return newFrequencies;
    }

    private double pearson(List<Double> countedFrequencies) {
        double result = 0;
        for (int i = 0; i < realFrequencies.length; i++) {
            result += (realFrequencies[i] - countedFrequencies.get(i)) * (realFrequencies[i] - countedFrequencies.get(i));
        }
        return result / (realFrequencies.length * realFrequencies.length);
    }

    private int findShift(List<Double> countedFrequencies) {
        double minPearson = Integer.MAX_VALUE, curPearson;
        int shift = 0;
//System.out.println("minPearson = " + minPearson + "; shift = " + shift);
        for (int i = 0; i < NUMBER_OF_ELEMENTS; i++) {
            curPearson = pearson(cyclicRightShift(countedFrequencies, i));
            if (minPearson > curPearson) {
                minPearson = curPearson;
                shift = i;
//System.out.println("minPearson = " + minPearson + "; shift = " + shift);
            }
        }
        return shift;
    }

}

