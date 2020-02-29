package org.jim.bitcoin.segwit.transaction;

import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

/**
 * Sign the tx with SegWit vin
 * https://github.com/bitcoin/bips/blob/master/bip-0143.mediawiki#P2SHP2WPKH
 */
public class SignTransactionService {

    public static String signInputByWIF(String hash, String prvKey) {
        ECKey ecKey = DumpedPrivateKey.fromBase58(MainNetParams.get(), prvKey).getKey();
        ECKey.ECDSASignature signature = ecKey.sign(Sha256Hash.wrap(hash));
        String signatureHex = Utils.HEX.encode(signature.encodeToDER());

        return signatureHex;
    }

    public static String signInput(String hash, String prvKey) {
        ECKey ecKey = ECKey.fromPrivate(Utils.HEX.decode(prvKey));
        ECKey.ECDSASignature signature = ecKey.sign(Sha256Hash.wrap(hash));
        String signatureHex = Utils.HEX.encode(signature.encodeToDER());

        return signatureHex;
    }

    public static String signInput(int network, String rawTx, int vin, String prvKey, String scriptPubKey, String redeemScript) {
        // build tx
        NetworkParameters networkParam = (0 == network) ? MainNetParams.get() : TestNet3Params.get();
        Transaction transaction = new Transaction(networkParam, Utils.HEX.decode(rawTx));

        // hash for sign
        Sha256Hash hash = transaction.hashForSignature(vin, Utils.HEX.decode(redeemScript), Transaction.SigHash.ALL, false);
        System.out.println(hash.toString());
        // Why the hash is wrong?

        /// sign
        //ECKey ecKey = DumpedPrivateKey.fromBase58(networkParam, prvKey).getKey();
        ECKey ecKey = ECKey.fromPrivate(Utils.HEX.decode(prvKey));
        ECKey.ECDSASignature signature = ecKey.sign(hash);
        String signatureHex = Utils.HEX.encode(signature.encodeToDER());

        return signatureHex;
    }

}
