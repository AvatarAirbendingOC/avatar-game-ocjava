public class EncounterTest {
    public static void main(String[] args) {
        System.out.println("=== Same population: union, both gain ===");
        FireNation a = new FireNation(50, 0, 0);
        FireNation b = new FireNation(50, 1, 0);
        a.receiveMessage("Fire-Scroll-1");
        b.receiveMessage("Fire-Scroll-2");
        EncounterResolver.resolve(a, b);
        System.out.println("A knows: " + a.getMessages());
        System.out.println("B knows: " + b.getMessages());

        System.out.println("\n=== Same alliance, different population: partial trade ===");
        FireNation c = new FireNation(50, 0, 0);
        EarthKingdom d = new EarthKingdom(50, 1, 0);
        c.receiveMessage("Fire-Scroll-3");
        c.receiveMessage("Fire-Scroll-4");
        d.receiveMessage("Earth-Scroll-1");
        EncounterResolver.resolve(c, d);
        System.out.println("Fire soldier knows: " + c.getMessages());
        System.out.println("Earth soldier knows: " + d.getMessages());

        System.out.println("\n=== Rival alliances: confrontation, loser actually loses ===");
        FireNation e = new FireNation(50, 0, 0);
        WaterTribe f = new WaterTribe(50, 1, 0);
        e.receiveMessage("Fire-Scroll-5");
        e.receiveMessage("Fire-Scroll-6");
        f.receiveMessage("Water-Scroll-1");
        EncounterResolver.resolve(e, f);
        System.out.println("Fire soldier knows: " + e.getMessages());
        System.out.println("Water soldier knows: " + f.getMessages());

        System.out.println("\n=== Master receives messages 'for free' via same-population Meeting ===");
        MasterFireNation master = MasterFireNation.getInstance(100, 0, 0);
        FireNation soldier = new FireNation(50, 1, 0);
        soldier.receiveMessage("Fire-Scroll-7");
        EncounterResolver.resolve(soldier, master);
        System.out.println("Master's collection (this is the scored count): " + master.getMessages());
    }
}
