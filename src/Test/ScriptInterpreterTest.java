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

    @Test
    public void testIfTrueExecutes() {
        //*
        // Verifica que OP_IF ejecute el bloque cuando la condición es verdadera.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_IF));
        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_ENDIF));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testIfFalseSkips() {
        //*
        // Verifica que OP_IF ignore el bloque cuando la condición es falsa.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(OpCode.OP_0));
        script.add(new Item(OpCode.OP_IF));
        script.add(new Item(OpCode.OP_0));
        script.add(new Item(OpCode.OP_ENDIF));
        script.add(new Item(OpCode.OP_1));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testIfElseExecution() {
        //*
        // Verifica ejecución correcta de OP_ELSE.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(OpCode.OP_0));
        script.add(new Item(OpCode.OP_IF));
        script.add(new Item(OpCode.OP_0));
        script.add(new Item(OpCode.OP_ELSE));
        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_ENDIF));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testNestedIf() {
        //*
        // Verifica ejecución correcta de condicionales anidados.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_IF));

        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_IF));
        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_ENDIF));

        script.add(new Item(OpCode.OP_ENDIF));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testIfWithoutEndIfFails() {
        //*
        // Verifica que un IF sin ENDIF falle.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_IF));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertFalse(result);
    }

    @Test
    public void testSha256Operation() {
        //*
        // Verifica que OP_SHA256 funcione correctamente.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(new byte[]{1,2,3}));
        script.add(new Item(OpCode.OP_SHA256));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testSha128Operation() {
        //*
        // Verifica que OP_SHA128 funcione correctamente.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(new byte[]{1,2,3}));
        script.add(new Item(OpCode.OP_SHA128));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testHash256Operation() {
        //*
        // Verifica que OP_HASH256 funcione correctamente.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(new byte[]{1,2,3}));
        script.add(new Item(OpCode.OP_HASH256));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testHash128Operation() {
        //*
        // Verifica que OP_HASH128 funcione correctamente.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(new byte[]{1,2,3}));
        script.add(new Item(OpCode.OP_HASH128));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testHashInsideIf() {
        //*
        // Verifica uso de operaciones hash dentro de un bloque IF.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_IF));
        script.add(new Item(new byte[]{1,2,3}));
        script.add(new Item(OpCode.OP_SHA256));
        script.add(new Item(OpCode.OP_ENDIF));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }

    @Test
    public void testElseWithoutIfFails() {
        //*
        // Verifica que OP_ELSE sin OP_IF falle.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(OpCode.OP_ELSE));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertFalse(result);
    }

    @Test
    public void testEndIfWithoutIfFails() {
        //*
        // Verifica que OP_ENDIF sin OP_IF falle.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        script.add(new Item(OpCode.OP_ENDIF));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertFalse(result);
    }

    @Test
    public void testP2PKHWithIf() {
        //*
        // Simulación de P2PKH con estructura condicional.
        // @author Junior Lancerio
        //  */
        List<Item> script = new ArrayList<>();

        byte[] sig = {1,2,3,4};
        byte[] pubKey = {5,6,7,8};

        script.add(new Item(sig));
        script.add(new Item(pubKey));

        script.add(new Item(OpCode.OP_DUP));
        script.add(new Item(OpCode.OP_HASH160));

        CryptoSimulator crypto = new CryptoSimulator();
        byte[] hash = crypto.hash160(pubKey);

        script.add(new Item(hash));
        script.add(new Item(OpCode.OP_EQUALVERIFY));

        script.add(new Item(OpCode.OP_1));
        script.add(new Item(OpCode.OP_IF));
        script.add(new Item(OpCode.OP_CHECKSIG));
        script.add(new Item(OpCode.OP_ENDIF));

        ScriptInterpreter interpreter = new ScriptInterpreter(script);
        boolean result = interpreter.execute();

        assertTrue(result);
    }
}