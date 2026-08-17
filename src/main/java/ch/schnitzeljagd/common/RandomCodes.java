package ch.schnitzeljagd.common;

import java.security.SecureRandom;

/**
 * Erzeugt kurze Zufallscodes für Teilnehmende und für die QR-Posten.
 * <p>
 * Das Alphabet lässt bewusst alles weg, was sich auf einem Handy verwechseln
 * lässt: 0/O, 1/I/l. Klein- und Grossbuchstaben werden nicht gemischt, damit
 * niemand rätselt, ob der Code gross oder klein einzugeben ist.
 */
public final class RandomCodes {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private RandomCodes() {
    }

    public static String generate(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }
}
