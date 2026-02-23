package model;

public class Item {
    private OpCode opcode;
    private byte[] data;

    public Item(OpCode opcode){
        this.opcode = opcode;
    }

    public Item(byte[] data){
        this.data = data;
    }

    public boolean isOperation(){
        return opcode != null;
    }

    public OpCode getOpCode(){
        return opcode;
    }

    public byte[] getData(){
        return data;
    }
}
