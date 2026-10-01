import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;

public class MainClass {
    public static void main(String[] args) {
        GameMap map = new GameMap(10, 10, 2);

        MasterFireNation masterFire    = MasterFireNation.getInstance(100, 0, 0);
        MasterEarthKingdom masterEarth = MasterEarthKingdom.getInstance(100, 9, 0);
        MasterWaterTribe masterWater   = MasterWaterTribe.getInstance(100, 0, 9);
        MasterAirNomad masterAir       = MasterAirNomad.getInstance(100, 9, 9);

        // Each faction: 1 stationary Master + 3 mobile individuals.
        FireNation fire1 = new FireNation(50, 2, 0);
        FireNation fire2 = new FireNation(50, 0, 2);
        FireNation fire3 = new FireNation(50, 2, 2);

        EarthKingdom earth1 = new EarthKingdom(50, 7, 0);
        EarthKingdom earth2 = new EarthKingdom(50, 9, 2);
        EarthKingdom earth3 = new EarthKingdom(50, 7, 2);

        WaterTribe water1 = new WaterTribe(50, 2, 9);
        WaterTribe water2 = new WaterTribe(50, 0, 7);
        WaterTribe water3 = new WaterTribe(50, 2, 7);

        AirNomad air1 = new AirNomad(50, 7, 9);
        AirNomad air2 = new AirNomad(50, 9, 7);
        AirNomad air3 = new AirNomad(50, 7, 7);

        List<Individual> roster = new ArrayList<>(List.of(
            masterFire, masterEarth, masterWater, masterAir,
            fire1, fire2, fire3,
            earth1, earth2, earth3,
            water1, water2, water3,
            air1, air2, air3
        ));
        List<Individual> masters = List.of(masterFire, masterEarth, masterWater, masterAir);

        for (Individual ind : roster) {
            ind.placeOn(map);
        }

        masterFire.setIdentity('M');
        masterEarth.setIdentity('M');
        masterWater.setIdentity('M');
        masterAir.setIdentity('M');

        fire1.setIdentity('A');  fire2.setIdentity('B');  fire3.setIdentity('C');
        earth1.setIdentity('A'); earth2.setIdentity('B'); earth3.setIdentity('C');
        water1.setIdentity('A'); water2.setIdentity('B'); water3.setIdentity('C');
        air1.setIdentity('A');   air2.setIdentity('B');   air3.setIdentity('C');

        // 4 random obstacles, fresh placement every run (never on a SafeZone,
        // and never on a tile an individual is already standing on).
        map.placeRandomObstacles(4);

        // 4 distinct messages per faction, 16 total - the full universe a
        // Master must eventually collect (from every faction) to win outright.
        String[] fireMessages  = { "Firebending: Lightning Redirection", "Firebending: Dragon's Breath",
                                    "Firebending: Agni Kai Technique", "Firebending: Combustion Strike" };
        String[] earthMessages = { "Earthbending: Seismic Sense", "Earthbending: Metal Bending",
                                    "Earthbending: Stone Pillar", "Earthbending: Sandbending" };
        String[] waterMessages = { "Waterbending: Healing Technique", "Waterbending: Ice Spike",
                                    "Waterbending: Octopus Form", "Waterbending: Bloodbending" };
        String[] airMessages   = { "Airbending: Air Scooter", "Airbending: Sky Bison Call",
                                    "Airbending: Air Shield", "Airbending: Flying Technique" };

        Set<String> allMessages = new HashSet<>();
        allMessages.addAll(List.of(fireMessages));
        allMessages.addAll(List.of(earthMessages));
        allMessages.addAll(List.of(waterMessages));
        allMessages.addAll(List.of(airMessages));

        // Distribute each faction's 4 messages across its 3 mobile individuals.
        fire1.receiveMessage(fireMessages[0]);
        fire1.receiveMessage(fireMessages[1]);
        fire2.receiveMessage(fireMessages[2]);
        fire3.receiveMessage(fireMessages[3]);

        earth1.receiveMessage(earthMessages[0]);
        earth1.receiveMessage(earthMessages[1]);
        earth2.receiveMessage(earthMessages[2]);
        earth3.receiveMessage(earthMessages[3]);

        water1.receiveMessage(waterMessages[0]);
        water1.receiveMessage(waterMessages[1]);
        water2.receiveMessage(waterMessages[2]);
        water3.receiveMessage(waterMessages[3]);

        air1.receiveMessage(airMessages[0]);
        air1.receiveMessage(airMessages[1]);
        air2.receiveMessage(airMessages[2]);
        air3.receiveMessage(airMessages[3]);

        GameFrame frame = new GameFrame(map);
        EventLog.attach(frame.getLogArea());

        SimulationEngine engine = new SimulationEngine(map, roster, masters, allMessages, 25);
        engine.setBoardPanel(frame.getBoardPanel());
        engine.setScoreboardPanel(frame.getScoreboardPanel());
        engine.setInteractive(true); // Space / Next Step advances one whole step at a time
        frame.setOnAdvance(engine::advance);
        frame.setOnStop(engine::requestStop);

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
        SoundEngine.startBackgroundAmbience();

        // Runs on its own thread, never the EDT - it blocks on dialogs and,
        // in interactive mode, on the advance gate.
        new Thread(engine::run, "simulation").start();
    }
}
