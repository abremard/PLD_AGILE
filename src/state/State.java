package state;

import controller.MVCController;
import command.ListOfCommands;
import objects.Map;
import objects.PlanningRequest;
import objects.Request;

public interface State {
    boolean debug = false;

    /**
     * Appel au parseur de la carte .xml
     *
     * @param l Liste des commandes, à laquelle on ajoute la commande de chargement de carte
     * @param c Controlleur dont on met à jour l'état
     * @param p Le chemin de donnée vers la carte .xml
     */
    default void loadMap(ListOfCommands l, MVCController c, String p) {}

    /**
     * Appel au parseur du fichier de requêtes .xml
     *
     * @param l Liste des commandes, à laquelle on ajoute la commande de chargement de requetes
     * @param c Controlleur dont on met à jour l'état
     * @param p Le chemin de donnée vers la liste des requetes .xml
     */
    default void loadRequestPlan(ListOfCommands l, MVCController c, String p) {}

    /**
     * Appel à l'algorithme de calcul du chemin
     *
     * @param l Liste des commandes, à laquelle on ajoute la commande de calcul de tour
     * @param c Controlleur dont on met à jour l'état
     * @param p Object planning request qui permet de calculer la tournée
     * @param m Object map qui permet de calculer la tournée
     */
    default void calculateTour(ListOfCommands l, MVCController c, PlanningRequest p, Map m) {}

    /**
     * Retour vers l'état précédent, le chargement d'un fichier est à nouveau possible
     *
     * @param l Liste des commandes, à laquelle on ajoute la commande de reinitialiser un tour
     * @param c Controlleur dont on met à jour l'état
     */
    default void newTour(ListOfCommands l, MVCController c) {}

    default void modifyRequestList(MVCController c) {}

    default void edit(ListOfCommands l, MVCController c) {}
    default void addRequest(MVCController c) {}
    default void removeRequest(MVCController c) {}
    default void modifyRequest(MVCController c) {}

    /**
     * Undo permet de défaire une commande. Plutôt que de la retirer de la liste, on décrémente le curseur indiquant la position actuelle et on exécute les corrections nécessaires.
     *
     * @param l Liste des commandes, à laquelle on déplace le curseur
     */
    default void undo(ListOfCommands l) { l.Undo(); }

    /**
     * Redo permet de re-exécuter une commande qui a été retiré par un undo. On incrémente le curseur et re-exécute la commande.
     *
     * @param l Liste des commandes, à laquelle on déplace le curseur
     */
    default void redo(ListOfCommands l) { l.Redo(); }

    default void done(ListOfCommands l, MVCController c) {}
    default void done(ListOfCommands l, MVCController c, PlanningRequest p, Request r) {}
    default void done(ListOfCommands l, MVCController c, PlanningRequest p, int i) {}
    default void done(ListOfCommands l, MVCController c, Request oldRequest, Request newRequest) {}
    default void done(ListOfCommands l, MVCController c, Map m, PlanningRequest p) {}

    default void cancel(MVCController c) {}

    default void confirmDelete(ListOfCommands l, MVCController c) {}
    default void confirmAdd(ListOfCommands l, MVCController c) {}
    // default void leftClick(ListOfCommands l, Controller c, Point p) {}
    default void rightClick(ListOfCommands l, MVCController c) {
        // c.setCurrentState(c.getTourState());
        // w.displayMessage("Command cancelled");
    }
}
