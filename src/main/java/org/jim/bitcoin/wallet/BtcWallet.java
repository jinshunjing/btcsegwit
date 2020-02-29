package org.jim.bitcoin.wallet;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.jim.bitcoin.wallet.model.KeyBean;
import org.jim.bitcoin.wallet.model.PathBean;
import org.jim.bitcoin.wallet.model.WalletBean;

import java.util.Arrays;
import java.util.List;

/**
 * BTC wallet
 *
 * @author JSJ
 */
public class BtcWallet {

    /**
     * Create a new wallet
     */
    public void createWallet() {
        List<String> wordList = Dictionary.generateWords(12);
        System.out.println(StringUtils.join(wordList, " "));

        WalletBean wallet = new WalletBean();
        wallet.setWords(wordList);
        wallet.setPassphrase("");

        demoAddress(wallet);
    }

    /**
     * Import a wallet
     */
    public void importWallet(String words) {
        WalletBean wallet = new WalletBean();
        wallet.setWords(Arrays.asList(StringUtils.split(words)));
        wallet.setPassphrase("");

        demoAddress(wallet);
    }

    private void demoAddress(WalletBean wallet) {
        NetworkParameters networkParam = MainNetParams.get();
        int network = 0;
        int change = 0;
        int size = 5;

        HDWallet.toSeed(wallet);
        HDWallet.toMaster(wallet);

        // xPub: m/44'/0'/0'
        PathBean path = new PathBean();
        path.setNetwork(networkParam);
        path.setPath(Arrays.asList(
                new ChildNumber(44, true),
                ChildNumber.ONE_HARDENED,
                ChildNumber.ZERO_HARDENED));
        String xPub = HDWallet.toXPub(wallet, path);
        System.out.println(xPub);

        // address
        for (int i = 0; i < size; i++) {
            System.out.println(i + ":" + BtcAddress.toP2PKHAddress(network, xPub, change, i));
        }

        // private key
        path.setPath(Arrays.asList(
                new ChildNumber(44, true),
                ChildNumber.ONE_HARDENED,
                ChildNumber.ZERO_HARDENED,
                ChildNumber.ZERO));
        HDWallet.toParent(wallet, path);
        for (int i = 0; i < size; i++) {
            KeyBean key = HDWallet.toChild(wallet, path, new ChildNumber(i, false));
            System.out.println(i + ":" + key.getPubkey());
            System.out.println(i + ":" + key.getPrvkey());
        }
    }

}
