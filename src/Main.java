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

        // hash esperado (debe coincidir con hash160(pubKey))
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

        // Condicional (demo)
        script.add(new Item(OpCode.OP_1)); // condición verdadera
        script.add(new Item(OpCode.OP_IF));

        // Si entra al IF = valida firma
        script.add(new Item(OpCode.OP_CHECKSIG));

        script.add(new Item(OpCode.OP_ELSE));

        // Si fuera falso = empuja 0 (fallo)
        script.add(new Item(OpCode.OP_0));

        script.add(new Item(OpCode.OP_ENDIF));

        // Ejemplo extra de hash 
        script.add(new Item(new byte[]{9,9,9}));
        script.add(new Item(OpCode.OP_SHA256));

        ScriptController controller = new ScriptController(script);
        controller.ejecutarScript();
    }
}