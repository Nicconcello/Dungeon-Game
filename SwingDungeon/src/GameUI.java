import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class GameUI extends JFrame {

    // Oggetto Option usato per costruire le scelte dinamiche (etichetta + azione)
    public static class Option {
        public final String label;
        public final Runnable action;
        public Option(String label, Runnable action) {
            this.label = label;
            this.action = action;
        }
    }

    private final JTextArea textArea;
    private final JLabel statusLabel;
    private final JButton btn1, btn2, btn3, btn4;
    private final JPanel buttonPanel;

    // pannello per input testuale (usato dagli enigmi)
    private final JPanel inputPanel;
    private final JTextField inputField;
    private final JButton submitInput;

    private final GameEngine engine;

    public GameUI(GameEngine engine) {
        super("DUNGEON");
        this.engine = engine;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // Top: status (vita, inventario, portafoglio...)
        statusLabel = new JLabel("Benvenuto");
        add(statusLabel, BorderLayout.NORTH);

        // Center: area di testo (log del gioco)
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane sc = new JScrollPane(textArea);
        add(sc, BorderLayout.CENTER);

        // Bottom: bottoni
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btn1 = new JButton("1");
        btn2 = new JButton("2");
        btn3 = new JButton("3");
        btn4 = new JButton("4");

        buttonPanel.add(btn1);
        buttonPanel.add(btn2);
        buttonPanel.add(btn3);
        buttonPanel.add(btn4);

        add(buttonPanel, BorderLayout.SOUTH);

        // Input panel (nascosto finché non serve per gli enigmi)
        inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        inputField = new JTextField(30);
        submitInput = new JButton("Invia");
        inputPanel.add(new JLabel("Risposta: "));
        inputPanel.add(inputField);
        inputPanel.add(submitInput);
        inputPanel.setVisible(false);
        add(inputPanel, BorderLayout.EAST);

        setVisible(true);
    }

    // Avvia il gioco (chiamata da MainApp)
    public void start() {
        append("BENVENUTO NEL DUNGEON AVVENTURIERO\n\n");
        engine.startGame();
    }

    // Metodi utility per UI
    public void append(String text) {
        // garantiamo che le modifiche GUI avvengano sull'EDT
        SwingUtilities.invokeLater(() -> {
            textArea.append(text);
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }

    public void setStatus(String text) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(text));
    }

    // Imposta fino a 4 opzioni (bottoni). Passa array di Option (lunghezza 0-4)
    public void setOptions(Option... options) {
        SwingUtilities.invokeLater(() -> {
            // prima nascondiamo/puliamo l'input panel (se mostrato)
            hideInputPrompt();

            JButton[] buttons = {btn1, btn2, btn3, btn4};
            // rimuovo tutti gli action listener esistenti
            for (JButton b : buttons) {
                for (var al : b.getActionListeners()) b.removeActionListener(al);
                b.setVisible(false);
                b.setEnabled(false);
            }

            // assegno le nuove opzioni
            for (int i = 0; i < options.length && i < buttons.length; i++) {
                JButton b = buttons[i];
                Option opt = options[i];
                b.setText(opt.label);
                b.setVisible(true);
                b.setEnabled(true);
                b.addActionListener(e -> {
                    // disabilito subito i bottoni per evitare doppie esecuzioni
                    disableAllButtons();
                    // eseguo l'azione (già su EDT) — l'azione deve essere rapida
                    opt.action.run();
                });
            }
        });
    }

    public void disableAllButtons() {
        SwingUtilities.invokeLater(() -> {
            for (var b : new JButton[]{btn1, btn2, btn3, btn4}) {
                b.setEnabled(false);
            }
        });
    }

    // Mostra un prompt testuale (usato per gli enigmi). onSubmit riceve la stringa.
    public void showInputPrompt(String promptText, Consumer<String> onSubmit) {
        SwingUtilities.invokeLater(() -> {
            append("\n" + promptText + "\n");
            inputPanel.setVisible(true);
            // rimuovo listener esistenti
            for (var al : submitInput.getActionListeners()) submitInput.removeActionListener(al);
            inputField.setText("");
            inputField.requestFocusInWindow();

            ActionListener submit = ev -> {
                String text = inputField.getText().trim();
                hideInputPrompt();
                onSubmit.accept(text.toLowerCase().trim());
            };

            submitInput.addActionListener(submit);
            // supporta invio dalla tastiera
            inputField.addActionListener(submit);
        });
    }

    public void hideInputPrompt() {
        SwingUtilities.invokeLater(() -> {
            submitInput.removeActionListener(submitInput.getActionListeners().length > 0 ? submitInput.getActionListeners()[0] : null);
            inputPanel.setVisible(false);
        });
    }

    // Mostra messaggio di fine gioco (disabilita i bottoni)
    public void showGameOver(String message) {
        append("\n" + message + "\n");
        disableAllButtons();
    }
}
