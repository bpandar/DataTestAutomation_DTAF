package bmo.Cipher;


public class Cipher {
    private static final String USAGE = "Usage: com.bmo.saccr.crypto.Cipher <Mode> <Value> [Key]";
    private static final String DECRYPT = "Decrypt";
    private static final String ENCRYPT = "Encrypt";

    public static void main(String[] args) {
        //disable log4j2 logging
        //Configurator.initialize(new NullConfiguration());
        boolean isEncrypted = true;
        String text;
        String key = null;

        if (args.length == 0) {
            System.out
                    .print("About to run Cipher, but missing arguments!\nPlease use following command to run.\n"
                            + USAGE);
            System.exit(ErrorCode.EXIT_FAILURE.getCode());
        }

        if (args[0].equalsIgnoreCase(ENCRYPT) || args[0].equalsIgnoreCase(DECRYPT)) {
            isEncrypted = args[0].equalsIgnoreCase(DECRYPT);
            text = args[1];
            key = args.length > 2 ? args[2] : null;
        } else {
            isEncrypted = args[0].endsWith("==");
            text = args[0];
            key = args.length > 1 ? args[1] : null;
        }

        if (isEncrypted) {
            try {
                String plain = (key == null) ? CryptoUtils.decrypt(text) : CryptoUtils.decrypt(text, key);
                System.out.print(plain);
            } catch (Exception e) {
                System.out.print("Error on decryption of --> " + text);
                //e.printStackTrace();
                System.exit(ErrorCode.EXIT_FAILURE.getCode());
            }
        } else {
            try {
                String encrypted = (key == null) ? CryptoUtils.encrypt(text) : CryptoUtils.encrypt(text, key);
                System.out.print(encrypted);
            } catch (Exception e) {
                System.out.print("Error on encryption of --> " + text);
                //e.printStackTrace();
                System.exit(ErrorCode.EXIT_FAILURE.getCode());
            }
        }
    }
}

