package command;

import controller.MVCController;
import objects.PlanningRequest;
import sample.Controller;

import java.util.ArrayList;
import java.util.Collections;

/**
 * <h1>Swap Order Command Class</h1>
 * <p>The Swap Order Command defines the command to be called when user wants to swap cards of pickup/delivery on the IHM</p>
 *
 * @author H4302
 * @see Command
 * @see MVCController
 */
public class SwapOrderCommand implements Command {

    /**
     * The index of the first card to swap
     */
    private int a;

    /**
     * The index of the second card to swap
     */
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

    /**
     * A list of cards of pickup/delivery
     */
    private ArrayList<Controller.LocationTagContent> ltcList;

    /**
     * Constructor
     * @param a The index of the first card to swap
     * @param b The index of the second card to swap
     * @param ltcList A list of cards of pickup/delivery in which 2 cards will be swaped
     */
    public SwapOrderCommand(int a, int b, ArrayList<Controller.LocationTagContent> ltcList) {
        this.a = a;
        this.b = b;
        this.ltcList = ltcList;
    }

    /**
     * Execute Command : Swap the 2 cards we want to swap
     * @param c the MVCController pointer used to update its map
     */
    @Override
    public void doCommand(MVCController c) {
        Collections.swap(this.ltcList, a, b);
        c.setLtcList(ltcList);
    }

    /**
     * Undo command : revert this command's previous execution, it swap the cards situated at the same exact index
     * @param c the MVCController pointer used to update its planning request and ltcList
     */
    @Override
    public void undoCommand(MVCController c) {
        Collections.swap(this.ltcList, a, b);
    }

    public ArrayList<Controller.LocationTagContent> getLtcList() {
        return ltcList;
    }

    public void setLtcList(ArrayList<Controller.LocationTagContent> ltcList) {
        this.ltcList = ltcList;
    }
}
