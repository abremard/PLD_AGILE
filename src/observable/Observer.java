package observable;

public interface Observer {
    void update(Observable observed, Object arg);
}
