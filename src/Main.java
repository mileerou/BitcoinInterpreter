import controller.ScriptController;
import model.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Item> script = new ArrayList<>();

        // Datos simulados
        byte[] sig = {1,2,3,4};
        byte[] pubKey = {5,6,7,8};

        CryptoSimulator crypto = new CryptoSimulator();
        byte[] pubKeyHash = crypto.hash160(pubKey);

        // scriptSig
        script.add(new Item(sig));
        script.add(new Item(pubKey));

        // scriptPubKey
        script.add(new Item(OpCode.OP_DUP));
        script.add(new Item(OpCode.OP_HASH160));
        script.add(new Item(pubKeyHash));
        script.add(new Item(OpCode.OP_EQUALVERIFY));

        // Condicional
        script.add(new Item(OpCode.OP_1)); // condición verdadera
        script.add(new Item(OpCode.OP_IF));

        script.add(new Item(OpCode.OP_CHECKSIG));

        script.add(new Item(OpCode.OP_ELSE));
        script.add(new Item(OpCode.OP_0));

        script.add(new Item(OpCode.OP_ENDIF));

        // Prueba de lógica
        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_0));
        script.add(new Item(OpCode.OP_BOOLOR));

        // Prueba aritmética
        script.add(new Item(new byte[]{2}));
        script.add(new Item(new byte[]{3}));
        script.add(new Item(OpCode.OP_ADD));

        // Prueba de pila
        script.add(new Item(new byte[]{9}));
        script.add(new Item(new byte[]{8}));
        script.add(new Item(OpCode.OP_SWAP));

        // Prueba de hash avanzado
        script.add(new Item(new byte[]{1,2,3}));
        script.add(new Item(OpCode.OP_HASH256));

        // Ejecutar
        ScriptController controller = new ScriptController(script);
        controller.ejecutarScript();
    }
}