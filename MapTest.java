public class MapTest {
    public static void main(String[] args) {
        GameMap map = new GameMap(10, 10, 2);

        printMap(map);

        System.out.println();
        System.out.println("Neighbors of a corner (0,0): "   + map.getNeighbors(0, 0).size());  // expect 3
        System.out.println("Neighbors of an edge (5,0): "    + map.getNeighbors(5, 0).size());  // expect 5
        System.out.println("Neighbors of the middle (5,5): " + map.getNeighbors(5, 5).size());  // expect 8

        System.out.println("Direction from (5,5) to Fire Nation SafeZone: "
            + map.getDirectionToSafeZone(5, 5, FireNation.class));
        System.out.println("Direction from (5,5) to Air Nomad SafeZone: "
            + map.getDirectionToSafeZone(5, 5, AirNomad.class));
    }

    private static void printMap(GameMap map) {
        for (int y = 0; y < map.getHeight(); y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < map.getWidth(); x++) {
                row.append(map.getTile(x, y).getDisplaySymbol()).append(' ');
            }
            System.out.println(row);
        }
    }
}
