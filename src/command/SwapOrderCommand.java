package command;

// TODO : on change l'objet manipulé : planningRequest -> tournee et on fait initCardContent pour raffraichir les cartes sur Controller.

import objects.PlanningRequest;
import sample.Controller;

import java.util.ArrayList;
import java.util.Collections;

public class SwapOrderCommand implements Command {

    private int a;
    private int b;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    private ArrayList<Controller.LocationTagContent> ltcList;

    public SwapOrderCommand(int a, int b, ArrayList<Controller.LocationTagContent> ltcList) {
        this.a = a;
        this.b = b;
        this.ltcList = ltcList;
    }

    @Override
    public void doCommand() {
        Collections.swap(this.ltcList, a, b);
    }

    @Override
    public void undoCommand() {
        Collections.swap(this.ltcList, a, b);
    }

    public ArrayList<Controller.LocationTagContent> getLtcList() {
        return ltcList;
    }

    public void setLtcList(ArrayList<Controller.LocationTagContent> ltcList) {
        this.ltcList = ltcList;
    }
}
