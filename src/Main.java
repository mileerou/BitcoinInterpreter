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
        byte[] pubKeyHash = {8,7,6,5}; // debe coincidir con hash160(pubKey)

        script.add(new Item(sig));
        script.add(new Item(pubKey));
        script.add(new Item(OpCode.OP_DUP));
        script.add(new Item(OpCode.OP_HASH160));
        script.add(new Item(pubKeyHash));
        script.add(new Item(OpCode.OP_EQUALVERIFY));
        script.add(new Item(OpCode.OP_CHECKSIG));

        ScriptController controller = new ScriptController(script);
        controller.ejecutarScript();
    }
}