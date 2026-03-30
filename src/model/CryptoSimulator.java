package model;

/**
 * @author Junior Lancerio
 */
public class CryptoSimulator {
    
    public byte[] hash160(byte[] data) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4 && i < data.length; i++) {
            result[i] = data[data.length - 1 - i];
        }
        return result;
    }

    /**
     * @author Junior Lancerio
     * Simulación de algoritmos SHA y HASH mediante desplazamiento y transformación de bytes.
     */
    public byte[] simulateHash(byte[] data, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            if (i < data.length) {
                result[i] = (byte) (data[i] ^ (length + i)); // Transformación simple
            } else {
                result[i] = (byte) i;
            }
        }
        return result;
    }

    //*
    // Aplica un hash simulado de 128 bits.
    // @author Junior Lancerio
    //  */
    public byte[] sha128(byte[] data) {
        return simulateHash(data, 16);
    }

    //*
    // Aplica un hash simulado de 256 bits.
    // @author Junior Lancerio
    //  */
    public byte[] sha256(byte[] data) {
        return simulateHash(data, 32);
    }

    //*
    // Aplica doble hash de 128 bits.
    // @author Junior Lancerio
    //  */
    public byte[] hash128(byte[] data) {
        return sha128(sha128(data));
    }

    //*
    // Aplica doble hash de 256 bits.
    // @author Junior Lancerio
    //  */
    public byte[] hash256(byte[] data) {
        return sha256(sha256(data));
    }

    public boolean checkSig(byte[] sig, byte[] pubkey) {
        return sig.length == pubkey.length;
    }
}