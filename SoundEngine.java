import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.util.Random;

// All sounds here are synthesized at runtime (plain sine waves / noise) -
// no audio files, no external libraries, no copyrighted recordings. This is
// a placeholder for real music; swap in file-based playback if you have a
// licensed track you want to use instead.
public final class SoundEngine {

    private static final float SAMPLE_RATE = 44100f;
    private static volatile boolean backgroundRunning = false;

    private SoundEngine() {
    }

    public static void playMergeCue() {
        playAsync(() -> playTones(new double[]{523.25, 659.25}, 120));  // friendly rising pair
    }

    public static void playTradeCue() {
        playAsync(() -> playTones(new double[]{440.00, 554.37}, 120));
    }

    public static void playClashCue() {
        playAsync(SoundEngine::playNoiseBurst);
    }

    public static void playVictoryFanfare() {
        playAsync(() -> playTones(new double[]{523.25, 659.25, 783.99, 1046.50}, 160)); // C-E-G-C
    }

    public static void startBackgroundAmbience() {
        if (backgroundRunning) return;
        backgroundRunning = true;
        Thread t = new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();
                double[] pad = {130.81, 164.81, 196.00}; // soft C3 major chord, looping
                while (backgroundRunning) {
                    for (double freq : pad) {
                        if (!backgroundRunning) break;
                        byte[] buffer = sineWave(freq, 800);
                        line.write(buffer, 0, buffer.length);
                    }
                }
                line.drain();
                line.close();
            } catch (Exception e) {
                // No audio device available in this environment - fail silently.
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static void stopBackgroundAmbience() {
        backgroundRunning = false;
    }

    private static void playAsync(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }

    private static void playTones(double[] freqsHz, int msPerNote) {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            for (double freq : freqsHz) {
                byte[] buffer = sineWave(freq, msPerNote);
                line.write(buffer, 0, buffer.length);
            }
            line.drain();
            line.close();
        } catch (Exception e) {
            // ignore - no audio device
        }
    }

    private static void playNoiseBurst() {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            int samples = (int) (SAMPLE_RATE * 0.25);
            byte[] buffer = new byte[samples];
            Random rng = new Random();
            for (int i = 0; i < samples; i++) {
                double envelope = 1.0 - (double) i / samples; // fade out
                int raw = rng.nextInt(256) - 128;
                buffer[i] = (byte) (raw * envelope);
            }
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
        } catch (Exception e) {
            // ignore
        }
    }

    private static byte[] sineWave(double freqHz, int durationMs) {
        int samples = (int) (SAMPLE_RATE * durationMs / 1000.0);
        byte[] buffer = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double angle = 2.0 * Math.PI * i * freqHz / SAMPLE_RATE;
            double envelope = Math.sin(Math.PI * i / samples); // smooth in/out, no clicks
            buffer[i] = (byte) (Math.sin(angle) * envelope * 100);
        }
        return buffer;
    }
}
