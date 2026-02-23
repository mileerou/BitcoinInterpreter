package model;

public class CryptoSimulator {
    public byte[] hash160(byte[] data){
        byte[] result = new byte[4];

        for (int i = 0; i < 4 && i < data.length; i++){
            result[i] = data[data.length - 1 - i];
        }

        return result;
    }

    public boolean checkSig(byte[] sig, byte[] pubkey){
        return sig.length == pubkey.length;
    }
}
