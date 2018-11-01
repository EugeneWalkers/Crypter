import java.io.File;

public class Main {

    public static final File KEY = new File("key.txt");
    public static final File INPUT = new File("input.txt");
    public static final String OUTPUT_ENCRYPTED = "output_encrypted.txt";
    public static final String OUTPUT_DECRYPTED = "output_decrypted.txt";

    public static void main(final String[] args) {

        Crypter crypter = new Crypter();
        crypter.encrypt(KEY, INPUT, OUTPUT_ENCRYPTED);
        crypter.decrypt(KEY, new File(OUTPUT_ENCRYPTED), OUTPUT_DECRYPTED);

        crypter.analyzeKasiski(new File(OUTPUT_ENCRYPTED));
    }

}