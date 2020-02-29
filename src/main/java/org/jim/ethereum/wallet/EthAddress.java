package org.jim.ethereum.wallet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bitcoinj.crypto.DeterministicKey;
import org.bouncycastle.math.ec.ECPoint;
import org.jim.bitcoin.wallet.HDWallet;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.util.Arrays;

/**
 * ETH address
 *
 * @author JSJ
 */
public class EthAddress {

    public static Pair<String, String> deriveAddress(String xPub, int change, int sequence) {
        DeterministicKey dKey = HDWallet.deriveChildKey(xPub, change, sequence);

        byte[] uncompressedPub  = dKey.getPubKeyPoint().getEncoded(false);
        byte[] result = Keys.getAddress(Arrays.copyOfRange(uncompressedPub, 1, uncompressedPub.length));
        String addr = Numeric.toHexString(result);
        String pubkey = dKey.getPublicKeyAsHex();
        return ImmutablePair.of(addr, pubkey);
    }

    public static String deriveFromPubKey(String pub) {
        return "0x" + Keys.getAddress(pub.substring(2));
    }

    public static Pair<String, String> deriveFromPrvKey(String prv) {
        ECPoint point = DeterministicKey.publicPointFromPrivate(Numeric.toBigInt(prv));
        byte[] uncompressedPub  = point.getEncoded(false);
        byte[] result = Keys.getAddress(Arrays.copyOfRange(uncompressedPub, 1, uncompressedPub.length));
        String addr = Numeric.toHexString(result);
        String pubkey = Numeric.toHexString(point.getEncoded(true));
        return ImmutablePair.of(addr, pubkey);
    }

}
