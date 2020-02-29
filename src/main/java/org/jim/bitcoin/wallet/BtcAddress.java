package org.jim.bitcoin.wallet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * BTC address
 *
 * @author JSJ
 */
public class BtcAddress {

    /**
     * P2PKH address
     *
     * @param network
     * @param xPub
     * @param change
     * @param sequence
     * @return
     */
    public static String toP2PKHAddress(int network, String xPub, int change, int sequence) {
        DeterministicKey dKey = HDWallet.deriveChildKey(xPub, change, sequence);
        ECKey key = DeterministicKey.fromPublicOnly(dKey.getPubKey());
        NetworkParameters parameters = network == 0 ? MainNetParams.get() : TestNet3Params.get();
        return LegacyAddress.fromKey(parameters, key).toBase58();
    }

    /**
     * MultiSig P2SH address
     *
     * @param network
     * @param m
     * @param pubKeys
     * @return
     */
    public static Pair<String, String> toMultiSigAddress(int network, int m, List<String> pubKeys) {
        List<ECKey> keyList = new ArrayList<>();
        for (String pubKey : pubKeys) {
            ECKey ecKey = ECKey.fromPublicOnly(Utils.HEX.decode(pubKey));
            if (!ecKey.isCompressed()) {
                byte[] compressed = ecKey.getPubKeyPoint().getEncoded(true);
                ecKey = ECKey.fromPublicOnly(compressed);
            }
            keyList.add(ecKey);
        }
        keyList.sort(Comparator.comparing(ECKey::getPublicKeyAsHex));

        Script script = ScriptBuilder.createMultiSigOutputScript(m, keyList);
        String redeemScript = Utils.HEX.encode(script.getProgram());

        byte[] hash160 = Utils.sha256hash160(script.getProgram());

        NetworkParameters parameters = network == 0 ? MainNetParams.get() : TestNet3Params.get();
        String address = LegacyAddress.fromScriptHash(parameters, hash160).toString();

        return ImmutablePair.of(address, redeemScript);
    }

}
