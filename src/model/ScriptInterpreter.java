package model;

import java.util.*;

public class ScriptInterpreter {

    private Stack<byte[]> stack;
    private List<Item> script;
    private CryptoSimulator crypto;
    private boolean traceMode;

    // @author Junior Lancerio
    private boolean ejecutando = true;
    private Stack<Boolean> controlStack = new Stack<>();

    public ScriptInterpreter(List<Item> script) {
        this.script = script;
        this.stack = new Stack<>();
        this.crypto = new CryptoSimulator();
        this.traceMode = false;
    }

    public void setTraceMode(boolean enabled) {
        this.traceMode = enabled;
    }

    public boolean execute() {
        try {
            for (Item item : script) {
                executeInstruction(item);

                if (traceMode) {
                    printStack();
                }
            }
            return validate();
        } catch (Exception e) {
            return false;
        }
    }

    private void executeInstruction(Item item) {

        // @author Junior Lancerio
        if (!ejecutando && item.isOperation() &&
                item.getOpCode() != OpCode.OP_IF &&
                item.getOpCode() != OpCode.OP_ELSE &&
                item.getOpCode() != OpCode.OP_ENDIF) {
            return;
        }

        if (!item.isOperation()) {
            stack.push(item.getData());
            return;
        }

        switch (item.getOpCode()) {

            case OP_0:
                stack.push(new byte[]{0});
                break;

            case OP_1:
                stack.push(new byte[]{1});
                break;

            case OP_DUP:
                requireStackSize(1);
                stack.push(stack.peek());
                break;

            case OP_DROP:
                requireStackSize(1);
                stack.pop();
                break;

            case OP_EQUAL:
                requireStackSize(2);
                byte[] a = stack.pop();
                byte[] b = stack.pop();
                stack.push(Arrays.equals(a, b) ? new byte[]{1} : new byte[]{0});
                break;

            case OP_EQUALVERIFY:
                requireStackSize(2);
                byte[] x = stack.pop();
                byte[] y = stack.pop();
                if (!Arrays.equals(x, y)) {
                    throw new RuntimeException("OP_EQUALVERIFY failed");
                }
                break;

            case OP_HASH160:
                requireStackSize(1);
                byte[] data = stack.pop();
                stack.push(crypto.hash160(data));
                break;

            case OP_CHECKSIG:
                requireStackSize(2);
                byte[] pubKey = stack.pop();
                byte[] sig = stack.pop();
                boolean valid = crypto.checkSig(sig, pubKey);
                stack.push(valid ? new byte[]{1} : new byte[]{0});
                break;

            case OP_SWAP:
                //*
                // Requiere al menos dos elementos en la pila. Intercambia el elemento superior con el segundo elemento de la pila.
                // @author Milena
                //  */
                requireStackSize(2);
                byte[] top = stack.pop();
                byte[] second = stack.pop();
                stack.push(top);
                stack.push(second);
                break;

            case OP_OVER:
                //*
                // Requiere al menos dos elementos en la pila. Empuja una copia del segundo elemento de la pila.
                // @author Milena
                //  */
                requireStackSize(2);
                byte[] secondToTop = stack.get(stack.size() - 2);
                stack.push(secondToTop);
                break;

            case OP_NOT:
                //*
                // Requiere al menos un elemento en la pila. Empuja el negado del elemento superior. Si el elemento es 0, empuja 1; de lo contrario, empuja 0.
                // @author Milena
                //  */
                requireStackSize(1);
                byte[] value = stack.pop();
                stack.push(value[0] == 0 ? new byte[]{1} : new byte[]{0});
                break;

            case OP_BOOLAND:
                //*
                // Requiere al menos dos elementos en la pila. Empuja 1 si ambos elementos son distintos de 0; de lo contrario, empuja 0.
                // @author Milena
                //  */
                requireStackSize(2);
                byte[] val1 = stack.pop();
                byte[] val2 = stack.pop();
                stack.push((val1[0] != 0 && val2[0] != 0) ? new byte[]{1} : new byte[]{0});
                break;

            case OP_BOOLOR:
                //*
                // Requiere al menos dos elementos en la pila. Empuja 1 si al menos uno de los elementos es distinto de 0; de lo contrario, empuja 0.
                // @author Milena
                //  */
                requireStackSize(2);
                byte[] v1 = stack.pop();
                byte[] v2 = stack.pop();
                stack.push((v1[0] != 0 || v2[0] != 0) ? new byte[]{1} : new byte[]{0});
                break;

            case OP_ADD:
                //*
                // Requiere al menos dos elementos en la pila. Empuja la suma de los dos elementos superiores.
                // @author Milena
                //  */
                requireStackSize(2);
                byte[] num1 = stack.pop();
                byte[] num2 = stack.pop();
                int sum = (num1[0] & 0xFF) + (num2[0] & 0xFF);
                stack.push(new byte[]{(byte) sum});
                break;

            case OP_SUB:
                //*
                // Requiere al menos dos elementos en la pila. Empuja la diferencia de los dos elementos superiores (segundo elemento menos el elemento superior).
                // @author Milena
                //  */
                requireStackSize(2);
                byte[] n1 = stack.pop();
                byte[] n2 = stack.pop();
                int diff = (n1[0] & 0xFF) - (n2[0] & 0xFF);
                stack.push(new byte[]{(byte) diff});
                break;

            case OP_NUMEQUALVERIFY:
                //*
                // Requiere al menos dos elementos en la pila. Verifica que los dos elementos superiores
                // sean numéricamente iguales; si no, el script falla.
                // @author Camila Da Silva
                //  */
                requireStackSize(2);
                byte[] neqA = stack.pop();
                byte[] neqB = stack.pop();
                if ((neqA[0] & 0xFF) != (neqB[0] & 0xFF)) {
                    throw new RuntimeException("OP_NUMEQUALVERIFY failed");
                }
                break;
            
            case OP_LESSTHAN:
                //*
                // Requiere al menos dos elementos en la pila. Empuja 1 si el segundo elemento
                // es menor que el elemento superior; de lo contrario, empuja 0.
                // @author Camila Da Silva
                //  */
                requireStackSize(2);
                byte[] ltTop = stack.pop();
                byte[] ltSecond = stack.pop();
                stack.push((ltSecond[0] & 0xFF) < (ltTop[0] & 0xFF) ? new byte[]{1} : new byte[]{0});
                break;

            case OP_GREATERTHAN:
                //*
                // Requiere al menos dos elementos en la pila. Empuja 1 si el segundo elemento
                // es mayor que el elemento superior, sino, empuja 0.
                // @author Camila Da Silva
                //  */
                requireStackSize(2);
                byte[] gtTop = stack.pop();
                byte[] gtSecond = stack.pop();
                stack.push((gtSecond[0] & 0xFF) > (gtTop[0] & 0xFF) ? new byte[]{1} : new byte[]{0});
                break;

            case OP_LESSTHANOREQUAL:
                //*
                // Requiere al menos dos elementos en la pila. Empuja 1 si el segundo elemento
                // es menor o igual que el elemento superior, de lo contrario, empuja 0.
                // @author Camila Da Silva
                //  */
                requireStackSize(2);
                byte[] lteTop = stack.pop();
                byte[] lteSecond = stack.pop();
                stack.push((lteSecond[0] & 0xFF) <= (lteTop[0] & 0xFF) ? new byte[]{1} : new byte[]{0});
                break;

            case OP_GREATERTHANOREQUAL:
                //*
                // Requiere al menos dos elementos en la pila. Empuja 1 si el segundo elemento
                // es mayor o igual que el elemento superior, de lo contrario, empuja 0.
                // @author Camila Da Silva
                //  */
                requireStackSize(2);
                byte[] gteTop = stack.pop();
                byte[] gteSecond = stack.pop();
                stack.push((gteSecond[0] & 0xFF) >= (gteTop[0] & 0xFF) ? new byte[]{1} : new byte[]{0});
                break;

            
            case OP_IF:
                //*
                // Requiere al menos un elemento en la pila. Evalúa si es distinto de 0 para ejecutar el bloque IF.
                // @author Junior Lancerio
                //  */
                requireStackSize(1);
                byte[] condicion = stack.pop();
                boolean resultadoIf = condicion[0] != 0;
                controlStack.push(ejecutando);
                ejecutando = ejecutando && resultadoIf;
                break;

            case OP_NOTIF:
                //*
                // Requiere al menos un elemento en la pila. Evalúa si es igual a 0 para ejecutar
                // el bloque; es el inverso lógico de OP_IF.
                // @author Camila Da Silva
                //  */
                requireStackSize(1);
                byte[] condicionNotif = stack.pop();
                boolean resultadoNotif = condicionNotif[0] == 0;
                controlStack.push(ejecutando);
                ejecutando = ejecutando && resultadoNotif;
                break;


            case OP_ELSE:
                //*
                // Invierte la ejecución del bloque IF actual.
                // @author Junior Lancerio
                //  */
                if (controlStack.isEmpty()) {
                    throw new RuntimeException("OP_ELSE sin OP_IF");
                }
                boolean previo = controlStack.peek();
                ejecutando = previo && !ejecutando;
                break;

            case OP_ENDIF:
                //*
                // Finaliza el bloque condicional.
                // @author Junior Lancerio
                //  */
                if (controlStack.isEmpty()) {
                    throw new RuntimeException("OP_ENDIF sin OP_IF");
                }
                ejecutando = controlStack.pop();
                break;

            case OP_SHA128:
                //*
                // Requiere al menos un elemento en la pila. Aplica un hash simulado de 128 bits.
                // @author Junior Lancerio
                //  */
                requireStackSize(1);
                byte[] sha128Data = stack.pop();
                stack.push(crypto.sha128(sha128Data));
                break;

            case OP_SHA256:
                //*
                // Requiere al menos un elemento en la pila. Aplica un hash simulado de 256 bits.
                // @author Junior Lancerio
                //  */
                requireStackSize(1);
                byte[] sha256Data = stack.pop();
                stack.push(crypto.sha256(sha256Data));
                break;

            case OP_HASH128:
                //*
                // Requiere al menos un elemento en la pila. Aplica doble hash de 128 bits.
                // @author Junior Lancerio
                //  */
                requireStackSize(1);
                byte[] hash128Data = stack.pop();
                stack.push(crypto.hash128(hash128Data));
                break;

            case OP_HASH256:
                //*
                // Requiere al menos un elemento en la pila. Aplica doble hash de 256 bits.
                // @author Junior Lancerio
                //  */
                requireStackSize(1);
                byte[] hash256Data = stack.pop();
                stack.push(crypto.hash256(hash256Data));
                break;
        }
    }

    private void requireStackSize(int size) {
        //*Maneja el error de pila, donde se requiere un mínimo de elementos en la pila */
        if (stack.size() < size) {
            throw new RuntimeException("Stack underflow");
        }
    }

    private boolean validate() {
        if (stack.isEmpty()) return false;
        return stack.peek()[0] != 0;
    }

    private void printStack() {
        System.out.println("STACK:");
        for (byte[] item : stack) {
            System.out.println(Arrays.toString(item));
        }
        System.out.println("------");
    }
}