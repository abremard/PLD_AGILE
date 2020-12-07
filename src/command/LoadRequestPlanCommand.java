package command;

import controller.MVCController;
import objects.PlanningRequest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class LoadRequestPlanCommand implements Command {

    /** classe attributes **/
    private PlanningRequest planningRequest;
    private String path;

    /** getters & setters **/
    public PlanningRequest getPlanningRequest() {
        return planningRequest;
    }

    /** constructor **/
    public LoadRequestPlanCommand(String pa) {
        this.path = pa;
        planningRequest = new PlanningRequest();
    }

    @Override
    public void doCommand(MVCController c) {
        try {
            planningRequest.parseRequest(path);
            c.setPlanningRequest(planningRequest);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void undoCommand(MVCController c) {}
}
