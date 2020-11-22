
package processing;

import objects.Map;

public class processingTest {

    public static void main(String[] args) {

        Map map = new Map("data/smallMap.xml");

        System.out.println(map.getNoOfIntersections() + " " + map.getNoOfSegments());

    }

}
