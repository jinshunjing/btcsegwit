package org.jim.bitcoin.wallet;

import org.bitcoinj.crypto.MnemonicCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Dictionary
 *
 * @author JSJ
 */
public class Dictionary {

    /**
     * Check whether the word is valid
     *
     * @param word
     * @return
     */
    public static boolean checkWord(String word) {
        int idx = Collections.binarySearch(MnemonicCode.INSTANCE.getWordList(), word);
        return idx >= 0;
    }

    /**
     * Check the mnemonic words
     *
     * @param words
     * @return
     */
    public static boolean checkWords(List<String> words) {
        try {
            MnemonicCode.INSTANCE.toEntropy(words);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate a random mnemonic words
     *
     * @param count
     * @return
     */
    public static List<String> generateWords(int count) {
        // generate a random entropy
        int bits = (12 == count) ? 128 : 256;
        byte[] entropy = generateEntropy(bits);

        // convert to mnemonic words
        try {
            List<String> words = MnemonicCode.INSTANCE.toMnemonic(entropy);
            return words;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate a random entropy
     *
     * @param bits 128/256
     * @return
     */
    public static byte[] generateEntropy(int bits) {
        if (!(128 == bits || 256 == bits)) {
            return null;
        }
        byte[] bytes = new byte[64];
        new Random().nextBytes(bytes);
        return Arrays.copyOfRange(bytes, 64 - bits / 8, 64);
    }

}
