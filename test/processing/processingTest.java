
package processing;

import objects.*;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors

public class processingTest {

    static FileWriter myWriter = null;

    public static void main(String[] args) {
        initFile();

        String mapPath = "data/largeMap.xml";
        String reqPath = "data/requestsLarge9.xml";

        testBatch(mapPath, reqPath, Heuristique.BRANCHANDBOUND, 10);

        closeFile();
    }

    static void randomTests() {

//        String mapPath = "data/map_test.xml";
//        String reqPath = "data/requests_test.xml";

//        String mapPath = "data/smallMap.xml";
//        String reqPath = "data/requestsSmall1.xml";

//        String mapPath = "data/mediumMap.xml";
//        String reqPath = "data/requestsMedium5.xml";

        String mapPath = "data/largeMap.xml";
        String reqPath = "data/requestsLarge9.xml";

        // ------------ chargement & parsing des données de test
        Map map = new Map(mapPath);
        System.out.println("Map chargee, nombre d'intersections : " + map.getNoOfIntersections() + ",   nombre de segments : " + map.getNoOfSegments());

        PlanningRequest planning = new PlanningRequest();
        try {
            planning.parseRequest(reqPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Liste de requetes chargee, nombre de requetes : " + planning.getRequestList().size() + "\n");

        // ------------ calcul du chemin selon l'heuristique choisie
        Tournee tournee = ComputeTour.planTour(map, planning, Heuristique.BRANCHANDBOUND);
//        Tournee tournee = ComputeTour.planTour(map, planning, Heuristique.DOUBLEINSERTION);

        // ------------ outputs
        /*
        System.out.println("\nTournee calculee :"); // pour map_test.xml, requests_test.xml, tourneeTriviale : S01, S12, S25, S51, S23, S30
//        for (Segment seg : tournee.getSegmentList()) {
//            System.out.println(seg);
//        }

//        SuperArete[][] adjMatrix = ComputeTour.testFullGraph(map, planning);
//        for (SuperArete[] line : adjMatrix) {
//            for (SuperArete arete : line) {
//                System.out.println(arete);
//            }
//            System.out.println();
//        }
        for (TupleRequete req : tournee.getPtsPassage()) {
            if(req.isDepart) {
                System.out.println(req.requete.getPickup().getId() + " " + req.time + " " + req.chemin);
            } else {
                System.out.println(req.requete.getDelivery().getId() + " " + req.time + " " + req.chemin);
            }
        }

         */

        System.out.println("Durée de la tournée : " + tournee.getPtsPassage().get(tournee.getPtsPassage().size()-1).getTime().minusHours(8));


        // test code pour recreateTourneeWithOrder
//        matAdj = getOptimalFullGraph(map, planning, intersecIdToIndex);
//        Tournee tournee = greedy(matAdj, planning, intersecIdToIndex);
//
//        ArrayList<TupleRequete> ordre = tournee.getPtsPassage();
//        System.out.println(ordre.get(0).getCurrentGoal().getId());
//        System.out.println(ordre.get(1).getCurrentGoal().getId());
//        System.out.println(ordre.size());
//        TupleRequete first = ordre.get(0);
//        ordre.set(0, ordre.get(1));
//        ordre.set(1, first);
//        System.out.println(ordre.get(0).getCurrentGoal().getId());
//        System.out.println(ordre.get(1).getCurrentGoal().getId());
//        System.out.println(ordre.size());
//        tournee = recreateTourneeWithOrder(map, planning, tournee.getPtsPassage());


    }

    static void testBatch(String mapPath, String reqPath, Heuristique heuristique, int repetitions) {

        // ------------ chargement & parsing des données de test
        Map map = new Map(mapPath);
        PlanningRequest planning = new PlanningRequest();
        try {
            planning.parseRequest(reqPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ------------ calcul de la tournee
        float moyDureeExec = 0;
        for(int i=0; i<repetitions; i++) {
            float startTime = System.currentTimeMillis();
            Tournee tournee = ComputeTour.planTour(map, planning, heuristique);
            float dureeExec = System.currentTimeMillis() - startTime;

            float dureeTournee = tournee.getPtsPassage().get(tournee.getPtsPassage().size() - 1).getTime().minusHours(8).toSecondOfDay();
            writeToFile(String.valueOf(dureeExec) + " " + String.valueOf(dureeTournee));
            moyDureeExec += dureeExec;
        }

        writeToFile("Moyenne : " + String.valueOf(moyDureeExec/(float)repetitions));
    }

    static void initFile() {
        try {
            File myObj = new File("results.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myWriter = new FileWriter("results.txt");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    static void writeToFile(String line) {
        try {
            if (myWriter != null) {
//                System.out.println(line);
                myWriter.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    static void closeFile() {
        try {
            if (myWriter != null) {
                myWriter.close();
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
