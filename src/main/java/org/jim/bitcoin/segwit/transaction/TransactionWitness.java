package org.jim.bitcoin.segwit.transaction;

import org.bitcoinj.core.VarInt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TransactionWitness {

    private final List<byte[]> pushes;

    public TransactionWitness() {
        pushes = new ArrayList<>();
    }

    public void addPush(byte[] value) {
        pushes.add(value);
    }

    public void bitcoinSerializeToStream(OutputStream stream) throws IOException {
        stream.write(new VarInt(pushes.size()).encode());
        for (int i = 0; i < pushes.size(); i++) {
            byte[] push = pushes.get(i);
            stream.write(new VarInt(push.length).encode());
            stream.write(push);
        }
    }

}
