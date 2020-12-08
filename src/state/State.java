package state;

import controller.MVCController;
import command.ListOfCommands;
import objects.Intersection;
import objects.Map;
import objects.PlanningRequest;
import objects.Request;
import sample.Controller;

import java.util.ArrayList;

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


    /**
     * On va vers l'état ModifyState lorsqu'on veut modifier les paramètres d'une tournéé.
     *
     * @param c Controlleur dont on met à jour l'état.
     */
    default void modifyRequestList(MVCController c) {}

    /**
     * On va vers l'état AddState qui nous permettra d'ajouter une requête à la tournéé.
     *
     * @param c Controlleur dont on met à jour l'état.
     */
    default void addRequest(MVCController c) {}

    /**
     * On va vers l'état RemoveState qui nous permettra de supprimer une requête de la tournée.
     *
     * @param c Controlleur dont on met à jour l'état.
     */
    default void removeRequest(MVCController c) {}

    /**
     * On va vers l'état ModifyRequestState qui nous permettra de modifier les détails d'une requête de la tournée.
     *
     * @param c Controlleur dont on met à jour l'état.
     */
    default void modifyRequest(MVCController c) {}

    /**
     * Undo permet de défaire une commande. Plutôt que de la retirer de la liste, on décrémente le curseur indiquant la position actuelle et on exécute les corrections nécessaires.
     *
     * @param l Liste des commandes, à laquelle on déplace le curseur
     */
    default void undo(ListOfCommands l, MVCController c) { l.Undo(c); }

    /**
     * Redo permet de re-exécuter une commande qui a été retiré par un undo. On incrémente le curseur et re-exécute la commande.
     *
     * @param l Liste des commandes, à laquelle on déplace le curseur
     */
    default void redo(ListOfCommands l, MVCController c) { l.Redo(c); }


    /**
     * On intervertit deux pickup/delivery entre eux afin de modifier la priorité de l'un sur l'autre.
     *
     * @param l Liste des commandes effectuées jusqu'à maintenant.
     * @param c Controlleur dont on met à jour l'état.
     * @param a l'index du premier request/delivery à intervertir.
     * @param b l'index du premier request/delivery à intervertir.
     * @param ltcList La liste des request/delivery ordonnée de façon temporelle.
     */
    default void swapRequest(ListOfCommands l, MVCController c, int a, int b, ArrayList<Controller.LocationTagContent> ltcList){}

    /**
     * On valide l'ajout de la requête dans notre tournée.
     *
     * @param l Liste des commandes effectuées jusqu'à maintenant.
     * @param c Controlleur dont on met à jour l'état.
     * @param p Le planning des différentes requêtes à effectuer lors de la tournée.
     * @param m Objet symbolisant les données nécessaires à l'affichage de la carte sur l'IHM.
     * @param ltcList La liste des request/delivery ordonnée de façon temporelle.
     * @param r Requête que l'on veut ajouter au planning de requêtes.
     */
    default void addDone(ListOfCommands l, MVCController c, PlanningRequest p, Map m, ArrayList<Controller.LocationTagContent> ltcList, Request r) {}

    /**
     * On valide la suppression d'une requête de la tournée.
     *
     * @param l Liste des commandes effectuées jusqu'à maintenant.
     * @param c Controlleur dont on met à jour l'état.
     * @param oldPlanningRequest Le planning des différentes requêtes à effectuer lors de la tournée.
     * @param ltcList La liste des request/delivery ordonnée de façon temporelle.
     * @param removedRequestIndex Index de la requête supprimée dans le planning des requêtes.
     * @param removedCardIndex1 Index du premier request/delivery dans la liste associée
     * @param removedCardIndex2 Index du deuxième request/delivery dans la liste associée
     */
    default void removeDone(ListOfCommands l, MVCController c, PlanningRequest oldPlanningRequest, ArrayList<Controller.LocationTagContent> ltcList, int removedRequestIndex, int removedCardIndex1, int removedCardIndex2) {}

    /**
     * On valide la modification d'une requête de la tournée.
     *
     * @param l Liste des commandes effectuées jusqu'à maintenant.
     * @param c Controlleur dont on met à jour l'état.
     * @param oldRequest Requête que l'on cherche à remplacer.
     * @param newRequest Requête avec laquelle on remplace l'ancienne.
     */
    default void modifyRequestDone(ListOfCommands l, MVCController c, Request oldRequest, Request newRequest) {}

    /**
     * On valide toutes les modifications apportées à la tournée.
     *
     * @param l Liste des commandes effectuées jusqu'à maintenant.
     * @param c Controlleur dont on met à jour l'état.
     * @param p Le planning des différentes requêtes à effectuer lors de la tournée.
     * @param m Objet symbolisant les données nécessaires à l'affichage de la carte sur l'IHM.
     * @param order A CHANGER
     */
    default void applyModificationDone(ListOfCommands l, MVCController c, Map m, PlanningRequest p, ArrayList<Intersection> order) {}

    /**
     * On annule les modifications apportées.
     *
     * @param c Controlleur dont on met à jour l'état.
     */
    default void cancel(MVCController c) {}

}
