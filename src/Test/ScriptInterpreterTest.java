import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import model.*;

public class ScriptInterpreterTest {

    @Test
    public void testOpDupAndEqual() {

        List<Item> script = new ArrayList<>();

        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_DUP));
        script.add(new Item(OpCode.OP_EQUAL));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testEqualVerifyFails() {

        List<Item> script = new ArrayList<>();

        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_0));
        script.add(new Item(OpCode.OP_EQUALVERIFY));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertFalse(result);
    }

    @Test
    public void testHash160Operation() {

        List<Item> script = new ArrayList<>();

        byte[] data = new byte[]{10, 20, 30, 40};

        script.add(new Item(data));
        script.add(new Item(OpCode.OP_HASH160));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testSimpleP2PKHSimulation() {

        List<Item> script = new ArrayList<>();

        byte[] signature = new byte[]{1,2,3,4};
        byte[] pubKey = new byte[]{5,6,7,8};

        // scriptSig
        script.add(new Item(signature));
        script.add(new Item(pubKey));

        // scriptPubKey
        script.add(new Item(OpCode.OP_DUP));
        script.add(new Item(OpCode.OP_HASH160));

        CryptoSimulator crypto = new CryptoSimulator();
        byte[] expectedHash = crypto.hash160(pubKey);

        script.add(new Item(expectedHash));
        script.add(new Item(OpCode.OP_EQUALVERIFY));
        script.add(new Item(OpCode.OP_CHECKSIG));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testStackUnderflow() {

        List<Item> script = new ArrayList<>();

        // OP_DUP sin nada en la pila debería fallar
        script.add(new Item(OpCode.OP_DUP));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertFalse(result);
    }
}