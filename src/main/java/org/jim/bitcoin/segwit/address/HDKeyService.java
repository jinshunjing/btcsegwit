package org.jim.bitcoin.segwit.address;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Derive SegWit Address
 */
public class HDKeyService {

    /**
     * Derive the Witness Address: P2SH(P2WPKH)
     *
     * @param network
     * @param xPubKey
     * @param changeType
     * @param index
     * @return
     */
    public static String deriveWitnessAddress(int network, String xPubKey, int changeType, int index) {
        NetworkParameters params = (0 == network) ? MainNetParams.get() : TestNet3Params.get();

        // derive the address index key
        DeterministicKey dKey = deriveChildKey(xPubKey, changeType, index);

        // public key hash
        byte[] pubKeyHash = dKey.getPubKeyHash();

        // redeem/witness script
        Script witnessScript = (new ScriptBuilder()).data(new byte[0]).data(pubKeyHash).build();

        // P2SH address
        byte[] hash160 = Utils.sha256hash160(witnessScript.getProgram());
        String address = LegacyAddress.fromP2SHHash(params, hash160).toString();;

        return address;
    }

    /**
     * Derive the Witness Script: P2SH(P2WPKH)
     *
     * @param xPubKey
     * @param changeType
     * @param index
     * @return
     */
    public static String[] deriveWitnessScript(String xPubKey, int changeType, int index) {
        // derive the address index key
        DeterministicKey dKey = deriveChildKey(xPubKey, changeType, index);

        // public key hash
        String pubKeyHex = dKey.getPublicKeyAsHex();
        byte[] pubKeyHash = dKey.getPubKeyHash();

        // redeem script
        Script redeemScript = (new ScriptBuilder()).data(new byte[0]).data(pubKeyHash).build();
        String redeemScriptHex = Utils.HEX.encode(redeemScript.getProgram());

        // public key hash
        byte[] hash160 = Utils.sha256hash160(redeemScript.getProgram());
        Script p2shScript = (new ScriptBuilder()).op(169).data(hash160).op(135).build();
        String scriptPubKeyHex = Utils.HEX.encode(p2shScript.getProgram());

        return new String[]{pubKeyHex, redeemScriptHex};
    }

    /**
     * Derive the Witness Address: P2SH(P2WSH)
     *
     * @param network
     * @param m
     * @param xPubKeys
     * @param changeType
     * @param index
     * @return
     */
    public static String deriveWitnessAddress(int network, int m, List<String> xPubKeys, int changeType, int index) {
        NetworkParameters params = (0 == network) ? MainNetParams.get() : TestNet3Params.get();

        // prepare the pub keys
        List<ECKey> keyList = new ArrayList<>();
        for (String xPubKey : xPubKeys) {
            DeterministicKey key = deriveChildKey(xPubKey, changeType, index);
            ECKey ecKey = ECKey.fromPublicOnly(key.getPubKey());
            keyList.add(ecKey);
        }
        keyList.sort(Comparator.comparing(ECKey::getPublicKeyAsHex));

        // witness script
        Script witnessScript = ScriptBuilder.createMultiSigOutputScript(m, keyList);

        // redeem script
        byte[] hash256 = Sha256Hash.hash(witnessScript.getProgram());
        Script redeemScript = (new ScriptBuilder()).data(new byte[0]).data(hash256).build();

        // P2SH address
        byte[] hash160 = Utils.sha256hash160(redeemScript.getProgram());
        String address = LegacyAddress.fromP2SHHash(params, hash160).toString();

        return address;
    }

    /**
     * Derive the Witness Script: P2SH(P2WSH)
     *
     * @param m
     * @param xPubKeys
     * @param changeType
     * @param index
     * @return
     */
    public static String[] deriveWitnessScript(int m, List<String> xPubKeys, int changeType, int index) {
        // prepare the pub keys
        List<ECKey> keyList = new ArrayList<>();
        for (String xPubKey : xPubKeys) {
            DeterministicKey key = deriveChildKey(xPubKey, changeType, index);
            ECKey ecKey = ECKey.fromPublicOnly(key.getPubKey());
            keyList.add(ecKey);
        }
        keyList.sort(Comparator.comparing(ECKey::getPublicKeyAsHex));

        // witness script
        Script witnessScript = ScriptBuilder.createMultiSigOutputScript(m, keyList);
        String witnessScriptHex = Utils.HEX.encode(witnessScript.getProgram());

        // redeem script
        byte[] hash256 = Sha256Hash.hash(witnessScript.getProgram());
        Script redeemScript = (new ScriptBuilder()).data(new byte[0]).data(hash256).build();
        String redeemScriptHex = Utils.HEX.encode(redeemScript.getProgram());

        // public key hash
        byte[] hash160 = Utils.sha256hash160(redeemScript.getProgram());
        Script p2shScript = (new ScriptBuilder()).op(169).data(hash160).op(135).build();
        String scriptPubKeyHex = Utils.HEX.encode(p2shScript.getProgram());

        return new String[]{scriptPubKeyHex, redeemScriptHex, witnessScriptHex};
    }

    /**
     * Derive the child key
     *
     * @param xPubKey
     * @param changeType
     * @param index
     * @return
     */
    private static DeterministicKey deriveChildKey(String xPubKey, int changeType, int index) {
        // build extend public key, which is m/44'/0'/0' or m/44'/1'/0'
        NetworkParameters params = (xPubKey.startsWith("xpub")) ? MainNetParams.get() : TestNet3Params.get();
        DeterministicKey xPubMaster = DeterministicKey.deserializeB58(xPubKey, params);

        // derive the change/receive key
        DeterministicKey changeTypeKey = HDKeyDerivation.deriveChildKey(xPubMaster, new ChildNumber(changeType, false));

        // derive the address index key
        DeterministicKey indexKey = HDKeyDerivation.deriveChildKey(changeTypeKey, new ChildNumber(index, false));

        return indexKey;
    }

}
