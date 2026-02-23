package model;

import java.util.*;

public class ScriptInterpreter {

    private Stack<byte[]> stack;
    private List<Item> script;
    private CryptoSimulator crypto;
    private boolean traceMode;

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
        }
    }

    private void requireStackSize(int size) {
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