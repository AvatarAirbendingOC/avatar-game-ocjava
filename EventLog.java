import javax.swing.JTextArea;

public final class EventLog {

    private static JTextArea area;
    private static String lastEncounter;

    private EventLog() {
    }

    public static void attach(JTextArea textArea) {
        area = textArea;
    }

    // General text feed (step headers, scores). Does NOT flag "an encounter
    // just happened" - only logEncounter() below does that.
    public static synchronized void log(String message) {
        System.out.println(message);
        if (area != null) {
            area.append(message + "\n");
            area.setCaretPosition(area.getDocument().getLength());
        }
    }

    // An encounter/confrontation description: goes through the same feed
    // AND is remembered so the engine knows to pop a modal for it.
    public static synchronized void logEncounter(String message) {
        log(message);
        lastEncounter = message;
    }

    public static synchronized String consumeLastEncounter() {
        String msg = lastEncounter;
        lastEncounter = null;
        return msg;
    }
}
