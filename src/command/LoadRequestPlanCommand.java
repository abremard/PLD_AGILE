package command;

import objects.PlanningRequest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class LoadRequestPlanCommand implements Command {
    public PlanningRequest getPlanningRequest() {
        return planningRequest;
    }

    public void setPlanningRequest(PlanningRequest planningRequest) {
        this.planningRequest = planningRequest;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private PlanningRequest planningRequest;
    private String path;

    public LoadRequestPlanCommand(String pa) {
        this.path = pa;
        planningRequest = new PlanningRequest();
    }

    @Override
    public void doCommand() {
        try {
            planningRequest.parseRequest(path);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void undoCommand() {
        // TODO
    }
}
