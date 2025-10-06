import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameEngine engine = new GameEngine();
            GameUI ui = new GameUI(engine);
            engine.setUI(ui);
            ui.start(); // mostra la UI e avvia il gioco
        });
    }
}
