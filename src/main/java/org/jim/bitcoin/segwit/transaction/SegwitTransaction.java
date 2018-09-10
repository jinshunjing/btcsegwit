package org.jim.bitcoin.segwit.transaction;

import org.bitcoinj.core.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Segwit transaction
 */
public class SegwitTransaction extends Transaction {

    private List<TransactionWitness> witnessList;

    public SegwitTransaction(NetworkParameters params, byte[] payloadBytes) throws ProtocolException {
        super(params, payloadBytes);
        witnessList = new ArrayList<>();
    }

    public void setWitness(int vin, TransactionWitness witness) {
        while (vin > witnessList.size()) {
            witnessList.add(null);
        }
        if (vin < witnessList.size()) {
            witnessList.set(vin, witness);
        } else {
            witnessList.add(witness);
        }
    }

    @Override
    public void bitcoinSerializeToStream(OutputStream stream) throws IOException {
        // version
        Utils.uint32ToByteStreamLE(super.getVersion(), stream);

        // marker, flag
        stream.write(0);
        stream.write(1);

        // txin_count, txins
        List<TransactionInput> inputs = super.getInputs();
        stream.write((new VarInt((long)inputs.size())).encode());
        Iterator var2 = inputs.iterator();
        while(var2.hasNext()) {
            TransactionInput in = (TransactionInput)var2.next();
            in.bitcoinSerialize(stream);
        }

        // txout_count, txouts
        List<TransactionOutput> outputs = super.getOutputs();
        stream.write((new VarInt((long)outputs.size())).encode());
        var2 = outputs.iterator();
        while(var2.hasNext()) {
            TransactionOutput out = (TransactionOutput)var2.next();
            out.bitcoinSerialize(stream);
        }

        // script_witnisses
        for (TransactionWitness witness : witnessList) {
            if (null == witness) {
                continue;
            }
            witness.bitcoinSerializeToStream(stream);
        }

        // lock_time
        Utils.uint32ToByteStreamLE(super.getLockTime(), stream);
    }
}
