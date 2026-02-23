package controller;

import model.*;
import view.ScriptView;

import java.util.List;

public class ScriptController {

    private ScriptInterpreter interpreter;
    private ScriptView view;

    public ScriptController(List<Item> script) {
        this.interpreter = new ScriptInterpreter(script);
        this.view = new ScriptView();
    }

    public void ejecutarScript() {
        interpreter.setTraceMode(true);
        boolean resultado = interpreter.execute();
        view.mostrarResultado(resultado);
    }
}