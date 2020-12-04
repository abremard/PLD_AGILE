package command;

// TODO : if same as compute tour command then delete this class...

import objects.Map;
import objects.PlanningRequest;
import objects.Tournee;
import processing.ComputeTour;
import processing.Heuristique;

public class ApplyModificationCommand implements Command {

    private Map map;
    private PlanningRequest planningRequest;
    private Tournee tournee;

    public ApplyModificationCommand(Map m, PlanningRequest p) {
        this.map = m;
        this.planningRequest = p;
    }

    @Override
    public void doCommand() {
        tournee = ComputeTour.planTour(map, planningRequest, Heuristique.GREEDY);
        // TODO : intégrer avec Back-end + algo
    }

    @Override
    public void undoCommand() {}
}
