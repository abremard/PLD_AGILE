package command;

import objects.Request;

public class EditRequestCommand implements Command {

    private Request oldRequest;
    private Request newRequest;
    private Request returnedRequest;

    public EditRequestCommand(Request oldRequest, Request newRequest) {
        this.oldRequest = oldRequest;
        this.newRequest = newRequest;
        this.returnedRequest = newRequest;
    }

    @Override
    public void doCommand() { returnedRequest = newRequest; }

    @Override
    public void undoCommand() { returnedRequest = oldRequest; }

    public Request getOldRequest() {
        return oldRequest;
    }

    public void setOldRequest(Request oldRequest) {
        this.oldRequest = oldRequest;
    }

    public Request getNewRequest() {
        return newRequest;
    }

    public void setNewRequest(Request newRequest) {
        this.newRequest = newRequest;
    }

    public Request getReturnedRequest() {
        return returnedRequest;
    }

    public void setReturnedRequest(Request returnedRequest) {
        this.returnedRequest = returnedRequest;
    }
}
