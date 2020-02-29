package org.jim.bitcoin.wallet;

import java.util.HashMap;
import java.util.Map;

/**
 * Key bag
 *
 * @author JSJ
 */
public class KeyBag {

    private Map<String, String> keys;
    private Map<String, String> pubKeys;
    private Map<String, String> redeemScripts;

    private static KeyBag INSTANCE = new KeyBag();
    public static KeyBag getInstance() {
        return INSTANCE;
    }

    private KeyBag() {
        keys = new HashMap<>(16);
        pubKeys = new HashMap<>(16);
        redeemScripts = new HashMap<>(16);
    }

    public void addKey(String addr, String prvKey) {
        keys.put(addr, prvKey);
    }

    public void addPubKey(String addr, String pubKey) {
        pubKeys.put(addr, pubKey);
    }

    public void addRedeemScript(String addr, String script) {
        redeemScripts.put(addr, script);
    }

    public String getPrvKey(String addr) {
        return keys.get(addr);
    }

    public String getPubKey(String addr) {
        return pubKeys.get(addr);
    }

    public String getRedeemScript(String addr) {
        return redeemScripts.get(addr);
    }
}
