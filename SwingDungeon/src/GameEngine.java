import java.util.Random;

public class GameEngine {

    private GameUI ui;

    // --- variabili di gioco (stato player) ---
    private final Random rand = new Random();

    private final String[] nemici = {"Guerriero", "Assassino", "Mago"};

    private final String[] frasiAttaccoPlayer = {
            "Colpo diretto! L’avversario barcolla.",
            "Hai sferrato un fendente preciso e potente.",
            "Un colpo rapido mette in difficoltà il nemico.",
            "Hai lanciato un attacco disperato che sorprende l’avversario.",
            "Hai quasi mancato il bersaglio, ma lo graffi comunque."
    };

    private final String[] frasiAttaccoNPC = {
            "Il nemico ti colpisce con forza brutale!",
            "Un attacco inaspettato ti squarcia la difesa.",
            "L’avversario ti colpisce con un colpo rapido.",
            "Hai evitato il colpo peggiore, ma subisci comunque dei danni.",
            "Il nemico approfitta di un tuo momento di distrazione."
    };

    private final String[] frasiCura = {
            "Bevi in fretta la pozione, senti le ferite richiudersi.",
            "La pozione ti riscalda lo stomaco e la tua energia torna.",
            "Ti curi con calma mentre il nemico ti osserva.",
            "Una luce rigenerante ti attraversa, recuperi forze preziose.",
            "Il liquido amarognolo ti ridà un po’ di vita."
    };

    private final String[] frasiFuga = {
            "Corri via a gambe levate!",
            "Sfrutti un momento di distrazione e fuggi.",
            "Il nemico ti rincorre, ma riesci a seminarlo.",
            "Con un balzo disperato lasci l’avversario indietro.",
            "Ti lanci in una fuga rocambolesca."
    };

    private final String[] enigmi = {
            "Sono sempre davanti a te ma non puoi vedermi. Chi sono?",
            "Più ne togli e più divento grande. Cosa sono?",
            "Più è caldo e più divento piccolo. Cosa sono?"
    };

    private final String[][] risposte = {
            {"futuro", "il futuro"},
            {"buco", "un buco"},
            {"candela", "una candela"}
    };

    private final int vitaMaxNemici = 49;
    private final int dannoMaxNemici = 19;

    // stato giocatore
    private int vitaMax = 125;
    private int vita = vitaMax;
    private int danno = 54;
    private int inventario = 3;
    private int cura = 25;
    private int dropRate = 50; // % chance di drop
    private int xp = 0;
    private int critico = 30; // % critico
    private int schivata = 40; // % schivata
    private int portafoglio = 0;
    private int contatore = 0; // numero di nemici sconfitti

    // stato nemico corrente
    private String avversario;
    private int vitaNemico;

    public void setUI(GameUI ui) {
        this.ui = ui;
    }

    // avvia il gioco
    public void startGame() {
        refreshStatus();
        ui.append("Inizia la tua avventura...\n\n");
        nextEncounter();
    }

    // aggiorna la label di stato
    private void refreshStatus() {
        ui.setStatus(String.format("HP: %d/%d   Pozioni: %d   Oro: %d   XP: %d   Nemici sconfitti: %d",
                vita, vitaMax, inventario, portafoglio, xp, contatore));
    }

    // --------------- ciclo di incontri (event-driven) ---------------
    private void spawnEnemy() {
        vitaNemico = rand.nextInt(vitaMaxNemici) + 1;
        avversario = nemici[rand.nextInt(nemici.length)];

        if (avversario.equals("Guerriero")) {
            vitaNemico += 50;
        } else if (avversario.equals("Assassino")) {
            vitaNemico += 30;
        } else if (avversario.equals("Mago")) {
            vitaNemico += 20;
        }

        ui.append("-------------------------------------\n");
        ui.append("Un " + avversario + " è apparso.\n\n");

        // ASCII art per i tre tipi
        if (avversario.equals("Guerriero")) {
            ui.append("     ,                 {}\n" +
        			"      / \\, | ,        .--.\n" +
        			"     |    =|= >      /.--.\\\n" +
        			"      \\ /` | `       |====|\n" +
        			"       `   |         |`::`|\n" +
        			"           |     .-;`\\..../`;.-.\n" +
        			"          /\\/  /  |...::...|  \\\n" +
        			"          |:'\\ |   /'''::'''\\   |\n" +
        			"           \\ /\\;-,/\\   ::   /\\--; \n" +
        			"           |\\ <` >  >._::_.<,<__>\n" +
        			"           | `\"\"`  /   ^^   \\\n" +
        			"           |       |        |\n" +
        			"           |       |        |\n" +
        			"           |       |        |\n" +
        			"           |       |        |\n");
        } else if (avversario.equals("Mago")) {
            ui.append("               /\\\n" +
                    "              /  \\\n" +
                    "             /\\  /\\\n" +
                    "              (oo)\n" +
                    "             /(  )\\    *\n" +
                    "            // || \\\\   *\n" +
                    "           //  ||  \\\\  *\n" +
                    "          ||   ||   || *\n" +
                    "          ||   ||   ||\n" +
                    "          ||   ||   ||\n" +
                    "          ||   ||   ||\n" +
                    "          ||   ||   ||\n" +
                    "           \\   ||   //\n" +
                    "             \\ || //\n" +
                    "              \\||//\n" +
                    "               `--´\n");
        } else {
            ui.append("                        {}\n" +
                    "                       .--.\n" +
                    "                      /.--.\\\n" +   
                    "                      |====|\n" +
                    "                      |`::`|\n" +
                    "                  .-;`\\..../`;-.\n" +
                    "                /  |...::...|   \\\n" +
                    "                |   /'''::'''\\   |\n" +
                    "                |   \\   ::   /   |\n" +
                    "                |    \\  ::  /    |\n" +
                    "                |     '::::'     |\n" +
                    "                |                |\n" +
                    "                |              | /\n" +
                    "                |              |/\n" +
                    "                |              |/\n" +
                    "                `              `\n");
        }
    }

    private void nextEncounter() {
        // se giocatore è morto -> fine
        if (vita <= 0) {
            ui.showGameOver("Il Dungeon è stato più forte di te. GAME OVER.");
            return;
        }

        spawnEnemy();
        refreshStatus();
        // setto le opzioni principali: Attacca / Cura / Scappa
        ui.setOptions(
                new GameUI.Option("1. Attaccare", this::chooseAttackType),
                new GameUI.Option("2. Curarti", this::playerHeal),
                new GameUI.Option("3. Scappare", this::playerRun)
        );
    }

    // scelta del tipo di attacco (rapid/pesante)
    private void chooseAttackType() {
        ui.append("\tCome vuoi attaccare?\n");
        ui.setOptions(
                new GameUI.Option("1. Attacco rapido", () -> playerAttack(false)),
                new GameUI.Option("2. Attacco pesante", () -> playerAttack(true)),
                new GameUI.Option("Indietro", this::nextEncounter)
        );
    }

    // logica di attacco
    private void playerAttack(boolean heavy) {
        // calcolo danno player
        int danniFatti = rand.nextInt(danno) + 1;

        // applico modifiche in base al tipo di attacco ed al tipo di avversario
        if (!heavy && avversario.equals("Guerriero")) {
            danniFatti = Math.max(1, danniFatti / 2);
        } else if (heavy && avversario.equals("Assassino")) {
            danniFatti = 0; // assasino schiva attacchi pesanti
        } else if (heavy && avversario.equals("Mago")) {
            danniFatti *= 2;
        }

        // controllo critico (lo applichiamo PRIMA che il danno venga sottratto,
        // in modo che il critico abbia effetto reale)
        if (rand.nextInt(100) < critico) {
            danniFatti *= 2;
            ui.append("COLPO CRITICO!!\n");
        }

        // calcolo danni subiti
        int danniSubiti = rand.nextInt(dannoMaxNemici) + 1;
        if (rand.nextInt(100) < schivata) {
            danniSubiti = 0;
        }

        if (avversario.equals("Guerriero")) {
            danniSubiti += 10;
        } else if (avversario.equals("Assassino")) {
            danniSubiti += 15;
        } else if (avversario.equals("Mago")) {
            danniSubiti += 20;
        }

        String fAP = frasiAttaccoPlayer[rand.nextInt(frasiAttaccoPlayer.length)];
        String fAN = frasiAttaccoNPC[rand.nextInt(frasiAttaccoNPC.length)];

        // Applico danni
        vitaNemico -= danniFatti;
        vita -= danniSubiti;

        // messaggi
        if (danniSubiti == 0) {
            ui.append("Lo hai evitato per un pelo.\n");
        } else {
            ui.append(fAN + " L'avversario ti ha fatto " + danniSubiti + " danni.\n");
        }

        if (danniFatti == 0) {
            ui.append("Il tuo attacco era troppo lento e il nemico è riuscito a schivarlo!\n");
        } else {
            ui.append(fAP + " Hai fatto " + danniFatti + " danni al " + avversario + ".\n");
        }

        if (vita <= 0) {
            ui.showGameOver("Sei schiattato! GAME OVER.");
            return;
        }

        if (vitaNemico <= 0) {
            handleVictory();
            return;
        }

        refreshStatus();
        // dopo turno, il giocatore può agire di nuovo (simuliamo turni manuali)
        ui.setOptions(
                new GameUI.Option("1. Attaccare", this::chooseAttackType),
                new GameUI.Option("2. Curarti", this::playerHeal),
                new GameUI.Option("3. Scappare", this::playerRun)
        );
    }

    private void playerHeal() {
        if (inventario > 0) {
            String fC = frasiCura[rand.nextInt(frasiCura.length)];
            vita = Math.min(vita + cura, vitaMax);
            inventario--;
            ui.append(fC + " Ora la tua vita è: " + vita + "\n");
        } else {
            ui.append("Non hai più pozioni nell'inventario, mi dispiace.\n");
        }
        refreshStatus();
        ui.setOptions(
                new GameUI.Option("1. Attaccare", this::chooseAttackType),
                new GameUI.Option("2. Curarti", this::playerHeal),
                new GameUI.Option("3. Scappare", this::playerRun)
        );
    }

    private void playerRun() {
        String fF = frasiFuga[rand.nextInt(frasiFuga.length)];
        ui.append(fF + " Ma.....\n");
        // torniamo direttamente a nuovo incontro (come nel continue GAME;)
        nextEncounter();
    }

    // vittoria contro un nemico normale
    private void handleVictory() {
        ui.append("\nIl nemico è stato sconfitto, sei ancora vivo. Per ora...\n");
        contatore++;
        // drop
        if (rand.nextInt(100) < dropRate) {
            inventario++;
            ui.append("\nHai ottenuto una pozione curativa dallo scontro!\n");
        }

        xp += 20;
        if (xp >= 100) {
            ui.append("\nSEI AUMENTATO DI LIVELLO!!\n");
            vita += 15;
            vitaMax += 15;
            danno += 5;
            xp -= 100;
        }

        refreshStatus();

        // chiediamo se continuare o uscire
        ui.setOptions(
                new GameUI.Option("1. Continuare", this::afterContinueChecks),
                new GameUI.Option("2. Uscire", this::exitGame)
        );
    }

    private void exitGame() {
        ui.append("\nScelta saggia. Fine partita.\n");
        ui.showGameOver("GRAZIE PER AVER GIOCATO");
    }

    // Questo metodo esegue in sequenza TUTTI i controlli che l'originale faceva
    // dopo la scelta "Continuare": forziere, mercante, boss, enigma, easteregg.
    private void afterContinueChecks() {
        ui.append("\nCoraggioso vuoi continuare.\n\n");
        refreshStatus();

        // FORZIERE (30%)
        if (rand.nextInt(100) < 30) {
            ui.append("Mentre avanzi nel Dungeon trovi un forziere........\n");
            ui.setOptions(
                    new GameUI.Option("1. Apri il baule", this::openChest),
                    new GameUI.Option("2. Non aprire", this::afterChestNoOpen)
            );
            return; // il resto dei controlli avverrà alla fine del flusso di forziere
        }

        // MERCANTE (40%) (richiede almeno 50 monete per apparire)
        if (rand.nextInt(100) < 40 && portafoglio >= 50) {
            ui.append("Un misterioso venditore si avvicina nell’ombra.\n");
            ui.setOptions(
                    new GameUI.Option("1. Avvicinarsi", this::merchantBuyMenu),
                    new GameUI.Option("2. Ignorare", this::afterMerchantIgnore)
            );
            return;
        }

        // BOSS
        if (contatore >= 10) {
            startBossFight();
            return;
        }

        // ENIGMA (30%)
        if (rand.nextInt(100) < 30) {
            startRiddle();
            return;
        }

        // EASTER EGG
        if (vita == 69) {
            doEasterEgg();
        }

        // Se nulla di speciale, nuovo incontro
        nextEncounter();
    }

    // forziere: scelta NO
    private void afterChestNoOpen() {
        ui.append("Non sei un amante del rischio.\n");
        afterContinueChecksPostEvent();
    }

    // forziere: scelta SI -> esito
    private void openChest() {
        int esito = rand.nextInt(100);
        if (esito < 40) {
            inventario++;
            ui.append("Hai trovato una pozione!\n");
        } else if (esito < 80) {
            portafoglio += 50;
            ui.append("Hai trovato 50 monete d'oro!!\n");
        } else {
            vita -= 15;
            ui.append("Il baule esplode.....\n");
            ui.append("Ora i tuoi HP sono: " + vita + "\n");
            if (vita <= 0) {
                ui.showGameOver("Sei morto a causa dell'esplosione del baule...");
                return;
            }
        }
        refreshStatus();
        afterContinueChecksPostEvent();
    }

    // dopo un evento (baule/mercante/enigma) ritorna alla sequenza di controlli
    private void afterContinueChecksPostEvent() {
        // dopo un evento controlliamo ancora il mercante/boss/enigma come l'originale:
        // l'originale non ripete gli stessi check, ma qui semplifichiamo chiamando nextEncounter
        // oppure possiamo chiamare il metodo che continua con merchant->boss->enigma
        // per rispettare la sequenza originale, controlliamo merchant -> boss -> enigma -> easteregg
        // (Se vuoi esattamente l'ordine dell'originale, possiamo replicarlo: FORZIERE -> MERCANTE -> BOSS -> ENIGMA -> EASTER)
        // Qui procediamo con merchant check:
        if (rand.nextInt(100) < 40 && portafoglio >= 50) {
            ui.append("Un misterioso venditore si avvicina nell’ombra.\n");
            ui.setOptions(
                    new GameUI.Option("1. Avvicinarsi", this::merchantBuyMenu),
                    new GameUI.Option("2. Ignorare", this::afterMerchantIgnore)
            );
            return;
        }

        if (contatore >= 10) {
            startBossFight();
            return;
        }

        if (rand.nextInt(100) < 30) {
            startRiddle();
            return;
        }

        if (vita == 69) {
            doEasterEgg();
        }

        nextEncounter();
    }

    // mercante: mostra il menu di vendita
    private void merchantBuyMenu() {
        ui.append("Il mercante sorride mostrando denti marci: 'Ho proprio quello che fa per te...'\n");
        ui.setOptions(
                new GameUI.Option("1. Due pozioni (50 monete)", () -> buyFromMerchant(1)),
                new GameUI.Option("2. Armatura (100 monete)", () -> buyFromMerchant(2)),
                new GameUI.Option("3. Spada (75 monete)", () -> buyFromMerchant(3)),
                new GameUI.Option("4. Nulla", this::afterMerchantIgnore)
        );
    }

    private void buyFromMerchant(int choice) {
        if (choice == 1) {
            if (portafoglio >= 50) {
                inventario += 2;
                portafoglio -= 50;
                ui.append("Hai ottenuto due pozioni nel tuo inventario.\n");
            } else {
                ui.append("Non hai abbastanza soldi per questo acquisto.\n");
            }
        } else if (choice == 2) {
            if (portafoglio >= 100) {
                vitaMax += 20;
                portafoglio -= 100;
                ui.append("Sei resistente come una quercia. Vita massima aumentata.\n");
            } else {
                ui.append("Non hai abbastanza soldi per questo acquisto.\n");
            }
        } else if (choice == 3) {
            if (portafoglio >= 75) {
                danno += 10;
                portafoglio -= 75;
                ui.append("Ora i nemici non avranno scampo! Danno aumentato.\n");
            } else {
                ui.append("Non hai abbastanza soldi per questo acquisto.\n");
            }
        }
        refreshStatus();
        // dopo il mercante procediamo con eventuali eventi successivi
        afterContinueChecksPostEvent();
    }

    private void afterMerchantIgnore() {
        ui.append("Non sei interessato al mercante.\n");
        afterContinueChecksPostEvent();
    }

    // EASTER EGG
    private void doEasterEgg() {
        ui.append("Una donzella si avvicina a te in maniera sensuale...\n");
        ui.append(                        "                                   '.\n" +
                        "                                 .'..'.  .     .\n" +
                        "                             .''..'.'.'....'.:''':\n" +
                        "                           .'.  '..: .  .:''     '' .\n" +
                        "                         .'.'. '. :::' ' .'' '. '  ':. \n" +
                        "                       . '.''''::.::'.. .'.:.'   ' ':.'\n" +
                        "                      .' '...::'.:'::::'::'... '  ':.:' '.'\n" +
                        "                     . .'..::.:...::::::'...'. . ..:.'  '.\n" +
                        "                     . .'.::':..:'' .  ..'..:' . '''... .' '\n" +
                        "                     . .'.::.::'.'....  ..:.'.  '.'...' '   ''.\n" +
                        "                    .':..'.'  .:':. '....:'.'.  '. '..' '.  ''.'\n" +
                        "             ''.'..'.''..::'. '' ' '.  '..'...'....:.:' .'. ..':.\n" +
                        "             '.   '.:': ..::'.'  .. '.  ''.'.'.''.:'. .' .' .' :'  '.\n" +
                        "              '..'  ..:. '::'. .      '. '.'.'.':::'.' .' .' .'   .'\n" +
                        "           '''.'  . .:'::.':            '  '   '.:.::' .  '..'  ' '. '.\n" +
                        "         ' '.   '.' .:'.:'.::'                .   ':'.  '  ''.  .' '. .\n" +
                        "          '.'.  .' ..'.:::.:'.::::.      ..::::''' :'. .' .' '.. .'. .'.\n" +
                        "          '.   . .'.'.:':..:   '...''   ..''.... ' :.' .'.''.  '. .'' ''.\n" +
                        "          ''. . .' '.:::'..  .''MI'..   . '. MI':' ': .'.. '.'. '.'    .'\n" +
                        "          .' ..'.'..:.'.'::    ' '' '.  .   ' '    :' .''...'.   '..'' \n" +
                        "           .'.   .' .:'.:::.            ..      .' : .:..'.':.'. '. '.'.\n" +
                        "          '. .'   .'.:.:::::..          ..    . . .:.::. '. '.'. ' '.'..\n" +
                        "          '..'. '.'...:''H::...     . . '..     ..':::.'. '.  '. '...'.''\n" +
                        "          '. .'.'...:''::III....    '....:'    . ..:I:'.':.'.'.  ' '.'.'.'\n" +
                        "         .''.'.' .'.'..:::III...    .    .  '.  ...:II'.:::.'. '.  '..'.'\n" +
                        "         '.' .'. '.'...:::III...  . .:HIIH:. '. ..'III':.'.'..''..'.' ''.\n" +
                        "          ''''.:.'.'....:::III..   'HHHHHIIH' ....'IIII'.:'...'.:'..'..'\n" +
                        "          '.'. ':. .'...:::III..     '''''' .'.:.':'.:'.:'...'.'..'.'.'.\n" +
                        "             '. :.'.'..::::IIIII.       .'.'.:.'::::.':::...'.'.'.'.'. '\n" +
                        "                '.': ..::'IIIIIII..    .. .'.::::.::....:.'. '.'.'...'.'\n" +
                        "           '..'. '. ...::::'.IIII.... .:.'.:'......'..::'...'..'..'..'\n" +
                        "              '.'.' ..::..:'IIIII.....'.'.'..:::::'.:.:'.:... '.'.'.'\n" +
                        "           . .' .' ..:::::'IIIIII.....' .:.::.:::::.''.'.:'.'. '..'.\n" +
                        "         '  ' .'....':::.:....'..'.:.'  .'::::.::.'.:::.'.'.... '..'\n" +
                        "            ''. ...':::'.'.:'....:'. ..::.'.....''.'::'.'. '..'...:'.\n" +
                        "          . '' .'.'::::::.:.:::.'. .'...::::..' '''.::..'. '..'.'. '\n" +
                        "          '.:.'.'.::::.::.::::.'. ...''...'.:..::. ''::...:.'''..'.\n" +
                        "           '.':. ::::'II:.:...:.' '...'.'.'....:::: '..'''.'.'' '.:\n" +
                        "             '. .:::IIII::.::.' . '..::'  '...'::::. .' '  '. '.'.'.\n" +
                        "            '.'.'.::II::III'.:'.' '' ..''. .':..:::'.''      ' ''':\n" +
                        "         .'  '. '....':III::: .'......'.'... '.'.'' :'          '.:.\n" +
                        "        .'  .'.'. '. '.:::'.'. '...'..'.....'.:''  '          . '.::\n" +
                        "        . . .'  ' ':. '.:.'.:.. '..'..'....'.:'.''              .:::\n" +
                        "        . '.'. '..'.:'....::'  '......':'.  ':'   .          . . .::\n" +
                        "        ' '. '.  '.. '...::' .   .   .     .   .    .       .  ..:::\n" +
                        "    '.   .'.'  '. '..'.'':'' .. . .'         .  .  . .       . ..:::\n" +
                        "      '.' '.'  .' ' .'.:'   . .  .  .         ...   .       . . .:::\n" +
                        "          '. .'.'.. .:''  ... .  .  . .       .::'    .      . ..:::\n" +
                        "            '.'.'.:'  ...'  ' . .  . .  .   .  ::.   .     .  ...::'\n" +
                        "             '.::'  ..   ..    .  . . . .   . . :.    .     . . .::\n" +
                        "           ..:'   ..  .HMHHI.    . . . ..     . :.      .  . . .::'\n" +
                        "          .:'.'  .: 'AIHHHV.    . . . . . . . ..:.   .     .. .::'\n" +
                        "       .::''   . .   HIHH'        .  . . . :  .:.     .    . ..::'\n" +
                        "    .MI:' . . . . :   ''     .  .   .  . .::: ': . .   .   ...::'\n" +
                        "   AMI'. . . . . .:        .  '  '. . . ..:::'.: .      . ...HH'\n" +
                        "   IM' .  . . . . :      .  .    . . . ...:::.:' . .   . . .:HHH.'HH'.\n" +
                        "   :'..   .. .. ..:       .  .. .  .. ...:::'.:' ..   . .':HH'::: :H:\n" +
                        "   :.    '. ..  ...:    .  .  . . .. . .::'...:.'  .   .' 'HH.'::'.'.H:.\n" +
                        "   :..    ...' .  ..:    .   .   .. .:::'. . .:'     ..'  ':::.:::':HH:.\n" +
                        "   ':.    ..' . . ...:        . ..::::'   . ..:'  .  .' 'H.':::'.::':H:.\n" +
                        "    :.   ...' .  .  ...:........:::::' .   . .:'    .'  .H:. :::'::.:::. \n" +
                        "    ':. ...' .  .  . . .':::::::'.''  .  .. .::    .'   'HH: :::'::.:::. \n" +
                        "     :....:'  . .  .  .  .  . . .'   . . . ...'    '    ':::. ::::.:::' \n" +
                        "      ':::' . . . . . . . . .       . .. ....:'    ' .  .':::' :::':' \n" +
                        "        :.'. . . .    .  . . .   .  .. . . ..:    '   '. 'I:::':'II' \n" +
                        "        :.' .  . .  '.  .   . . . .. . ....:::   '    . .'::'III' \n" +
                        "        :. . . .    . .  . . .  .. . .. ..:::'   '   . .IIIII \n" +
                        "        :. . .        . . . .  .. . .:'.:.:::   '   . .:IIII \n" +
                        "        :.. .     .     .  .  . .. . ....::II  '   . ..::III \n" +
                        "        :.' .      .    . . . .. ...:..::III  '  . . ..:III' \n" +
                        "       :. .'.   . .      . . .. . .:'..:IIII.'   . ....::II' \n" +
                        "       :: . .    .    .  . . .. ..:. .:III::.      .....:II' \n" +
                        "       ::'  .     .     . . .. .  .:.:II:::'    . . ....:II \n" +
                        "       :: . .    .    .  . .  . ...::III::'      . . ..:II' \n" +
                        "       :'. . .    .  .   . .. . ...::::::I      . . ...:II \n" +
                        "       ::    .  .      .  .  . .....::::''.    . . ..::I' \n" +
                        "       :'.I'.    .  ' .   .. . ....:::::   .  . . ...:: \n" +
                        "      .:'HI .  .      .  .  .  . .:::::'    '. . . .:' \n" +
                        "      :: I'   .      .  .  .  . . :::::      '. ..:' \n" +
                        "     .:'  .    .     .  . .. . . . .:::       '''' \n" +
                        "     :.'.     .    .  .  . .. . .. ..::.\n" +
                        "     :.''.     .    . .  .. . . . ...:::\n" +
                        "     :.' .    .     . . . . . .. ....:::.\n" +
                        "     :.. .   .       .. . .. . . .....:::\n" +
                        "     :..  .   .       . ..  . .. .....::::\n" +
                        "     :.. .    .    . . . . . . .. .....::::\n" +
                        "     :.. .        .  . .. .  . . . ....::::.\n" +
                        "     :. ..      . .  .. . . . . .. .....::::\n" +
                        "     :.. .     . . . . . . .. . . .......:::.\n" +
                        "     :. ..    .  . . . .. . . . .. ......::::.\n" +
                        "     :.. .   . ..    .. . . ..  . . .....:::::.\n" +
                        "     :. :.   . . .    . . .. . . . ......::::::\n" +
                        "     :..:.    . ..    . . . .. .. ........:::::.\n" +
                        "     : .::.   . .    .  .  . .  . . ......::::::\n" +
                        "    .:.:II:. . . .    . . . . . .. .. ....::::::.\n" +
                        "    :..:III:.' ..    . .  . .  . . . . ...::::::.\n" +
                        "    :..IHHHI:.' .'    .  . . . . .. . .....::::::\n" +
                        "   .:.:IHHHHHI:.'    . . .  . . . . .......::::::\n");
        vita += 21;
        ui.append("La donzella ti cura: +21 HP! Ora i tuoi HP sono: " + vita + "\n");
        refreshStatus();
    }

    // ENIGMA
    private void startRiddle() {
        int index = rand.nextInt(enigmi.length);
        String domanda = enigmi[index];
        ui.append("Trovi un antico monolite con un enigma inciso:\n");
        // uso inputField per ricevere la risposta
        ui.showInputPrompt("\"" + domanda + "\"", risposta -> {
            boolean corretto = false;
            for (String acc : risposte[index]) {
                if (risposta.equalsIgnoreCase(acc)) {
                    corretto = true;
                    break;
                }
            }
            if (corretto) {
                ui.append("\nLa pietra si illumina... hai risolto l’enigma!\n");
                portafoglio += 50;
                vita = Math.min(vita + 20, vitaMax);
                ui.append("Ottieni 50 monete e recuperi 20 HP!\n");
            } else {
                ui.append("\nLa pietra trema... risposta errata!\n");
                vita -= 15;
                ui.append("Subisci 15 danni! Ora i tuoi HP sono: " + vita + "\n");
                if (vita <= 0) {
                    ui.showGameOver("Sei morto per l'enigma. GAME OVER.");
                    return;
                }
            }
            refreshStatus();
            nextEncounter();
        });
    }

    // BOSS FIGHT
    private void startBossFight() {
        ui.append("Il pavimento trema. Un urlo mostruoso rimbomba nelle caverne.\n");
        ui.append("Dalle ombre emerge un Troll colossale!\n");
        ui.append( "       ,      ,\n" +
                "      /(.-\"\"-.)\\\n" +
                "  |\\  \\/      \\/  /|\n" +
                "  | \\ / =.  .= \\ / |\n" +
                "  \\( \\   o\\/o   / )/\n" +
                "   \\_, '-/  \\-' ,_/\n" +
                "     /   \\__/   \\\n" +
                "   __\\  /||||\\  /__\n" +
                "  /   \\ \\_||_/ /   \\\n" +
                " /     \\      /     \\\n" +
                "|  |   |      |   |  |\n" +
                "|  |   |      |   |  |\n" +
                "|  |   |      |   |  |\n" +
                "|  |   |      |   |  |\n" +
                "|  |   |      |   |  |\n" +
                "\\  |   |      |   |  /\n" +
                " \\ |   |      |   | /\n" +
                "  \\|   |      |   |/\n" +
                "   '---'      '---'\n");
        avversario = "Troll";
        vitaNemico = 200;
        // inizio del combattimento col boss: limitiamo le scelte a attacca/curati (come nell'originale)
        ui.setOptions(
                new GameUI.Option("1. Attaccare", this::chooseBossAttackType),
                new GameUI.Option("2. Curarti", this::playerHeal)
        );
    }

    private void chooseBossAttackType() {
        ui.append("\tCome vuoi attaccare il Troll?\n");
        ui.setOptions(
                new GameUI.Option("1. Attacco rapido", () -> bossAttack(false)),
                new GameUI.Option("2. Attacco pesante", () -> bossAttack(true)),
                new GameUI.Option("Indietro", () -> ui.setOptions(
                        new GameUI.Option("1. Attaccare", this::chooseBossAttackType),
                        new GameUI.Option("2. Curarti", this::playerHeal)
                ))
        );
    }

    private void bossAttack(boolean heavy) {
        int danniFatti = rand.nextInt(danno) + 1;
        if (!heavy) danniFatti = Math.max(1, danniFatti / 2);

        if (rand.nextInt(100) < critico) {
            danniFatti *= 2;
            ui.append("COLPO CRITICO!!\n");
        }

        int danniSubiti = rand.nextInt(dannoMaxNemici) + 1;
        if (rand.nextInt(100) < schivata) danniSubiti = 0;

        String fAP = frasiAttaccoPlayer[rand.nextInt(frasiAttaccoPlayer.length)];
        String fAN = frasiAttaccoNPC[rand.nextInt(frasiAttaccoNPC.length)];

        vitaNemico -= danniFatti;
        vita -= danniSubiti;

        if (danniSubiti == 0) ui.append("Lo hai evitato per un pelo.\n");
        else ui.append(fAN + " Il Troll ti ha fatto " + danniSubiti + " danni.\n");

        if (danniFatti == 0) ui.append("Il tuo attacco era troppo lento e il Troll lo ha schivato!\n");
        else ui.append(fAP + " Hai fatto " + danniFatti + " danni al Troll.\n");

        if (vita <= 0) {
            ui.showGameOver("Il Troll ti ha schiacciato senza pietà... GAME OVER.");
            return;
        }

        if (vitaNemico <= 0) {
            ui.append("Hai sconfitto il Troll!! Il dungeon trema e sembra crollare...\n");
            ui.showGameOver("FINE: Hai completato la sfida del Troll!");
            return;
        }

        refreshStatus();
        // di nuovo: attacca o cura
        ui.setOptions(
                new GameUI.Option("1. Attaccare", this::chooseBossAttackType),
                new GameUI.Option("2. Curarti", this::playerHeal)
        );
    }
}

