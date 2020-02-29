package org.jim.bitcoin.wallet;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bouncycastle.util.encoders.Hex;
import org.jim.bitcoin.wallet.model.KeyBean;
import org.jim.bitcoin.wallet.model.PathBean;
import org.jim.bitcoin.wallet.model.WalletBean;

import java.util.Objects;

/**
 * HD Wallet
 *
 * @author JSJ
 */
public class HDWallet {
    public static final String XPUB = "xpub";

    /**
     * From mnemonic words to seed
     *
     * @param walletBean
     */
    public static void toSeed(WalletBean walletBean) {
        byte[] seed = MnemonicCode.toSeed(walletBean.getWords(), walletBean.getPassphrase());
        walletBean.setSeed(seed);

        byte[] hash = Sha256Hash.hash(Sha256Hash.hash(seed));
        walletBean.setSeedChecksum(Hex.toHexString(hash).substring(0, 8));
    }

    /**
     * From seed to master key
     *
     * @param walletBean
     */
    public static void toMaster(WalletBean walletBean) {
        DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(walletBean.getSeed());
        walletBean.setM(master);
    }

    /**
     * Derive parent key
     *
     * @param walletBean
     * @param pathBean
     */
    public static void toParent(WalletBean walletBean, PathBean pathBean) {
        DeterministicKey p = walletBean.getM();
        for (ChildNumber c : pathBean.getPath()) {
            p = HDKeyDerivation.deriveChildKey(p, c);
        }
        pathBean.setKey(p);
    }

    /**
     * Derive child key
     *
     * @param walletBean
     * @param pathBean
     * @param c
     * @return
     */
    public static KeyBean toChild(WalletBean walletBean, PathBean pathBean, ChildNumber c) {
        DeterministicKey key = HDKeyDerivation.deriveChildKey(pathBean.getKey(), c);
        String pubKey = key.getPublicKeyAsHex();
        String prvKey = key.getPrivateKeyAsHex();

        // encrypt private key
        if (Objects.nonNull(walletBean.getEncodedPassword())) {
            byte[] encryptedPrivKey = Encryption.encrypt(walletBean.getEncodedPassword(), key.getPrivKeyBytes());
            prvKey = Hex.toHexString(encryptedPrivKey);
        }

        KeyBean record = new KeyBean();
        record.setIndex(c.toString());
        record.setPubkey(pubKey);
        record.setPrvkey(prvKey);
        return record;
    }

    /**
     * Derive the Extended Public Key
     *
     * @param walletBean
     * @param pathBean
     * @return
     */
    public static String toXPub(WalletBean walletBean, PathBean pathBean) {
        toParent(walletBean, pathBean);
        return pathBean.getKey().serializePubB58(pathBean.getNetwork());
    }

    /**
     * From encoded Extended Public Key to key
     *
     * @param xPub
     * @return
     */
    public static DeterministicKey toXPubKey(String xPub) {
        NetworkParameters params = (xPub.startsWith(XPUB)) ? MainNetParams.get() : TestNet3Params.get();
        return DeterministicKey.deserializeB58(xPub, params);
    }

    public static DeterministicKey deriveChildKey(String xPub, int changeType, int sequence) {
        DeterministicKey xPubMaster = toXPubKey(xPub);

        DeterministicKey key0 = HDKeyDerivation.deriveChildKey(xPubMaster, new ChildNumber(changeType, false));
        DeterministicKey dKey = HDKeyDerivation.deriveChildKey(key0, new ChildNumber(sequence, false));

        return dKey;
    }

}
