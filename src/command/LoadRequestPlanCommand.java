package command;

import controller.MVCController;
import objects.PlanningRequest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * <h1>Load Request Plan Command Class</h1>
 * <p>The Load Request Plan Command defines the command to be called when user wants to load a list of requests onto the application</p>
 *
 * @author H4302
 * @see Command
 * @see MVCController
 */
public class LoadRequestPlanCommand implements Command {

    /**
     * A planning of all the requests to be completed during the Tour
     */
    private PlanningRequest planningRequest;

    /**
     * The path to the file in the File Explorer (it's relative to the project's root)
     */
    private String path;

    /** getters & setters **/
    public PlanningRequest getPlanningRequest() {
        return planningRequest;
    }

    /**
     * Constructor
     * @param pa The path to the file
     */
    public LoadRequestPlanCommand(String pa) {
        this.path = pa;
        planningRequest = new PlanningRequest();
    }

    /**
     * Execute Command : Set the MvcController's requests' planning as the one corresponding to the path
     * @param c the MVCController pointer used to update its map
     */
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
