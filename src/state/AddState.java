package state;

import command.ListOfCommands;
import command.AddRequestCommand;
import controller.MVCController;

//TODO mettre en place un cancel pour revenir sur ModifySTate

public class AddState implements State {
    public void done(ListOfCommands l, MVCController c) {
        //l.Add(new AddRequestCommand());
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.print("Confirming addition of a new request to the request list ");
            System.out.println(l.getI());
        }
    }
}
