package PokerGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Card {

    String suit;
    String rank;
    int value;

    public Card(String suit, String rank, int value) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
    }

    public String toString() {
        return rank + " of " + suit;
    }
}

class Deck {

    List<Card> cards = new ArrayList<>();

    String[] suits = {"♥ Hearts", "♦ Diamonds", "♣ Clubs", "♠ Spades"};

    String[] ranks = {
            "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "J", "Q", "K", "A"
    };

    public Deck() {

        for (String suit : suits) {

            for (int i = 0; i < ranks.length; i++) {

                cards.add(new Card(suit, ranks[i], i + 2));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card deal() {
        return cards.remove(0);
    }
}

class HandEvaluator {

    public static int evaluate(List<Card> hand) {

        Map<Integer, Integer> count = new HashMap<>();

        for (Card c : hand) {
            count.put(c.value, count.getOrDefault(c.value, 0) + 1);
        }

        boolean pair = false;
        boolean three = false;
        boolean four = false;

        for (int v : count.values()) {

            if (v == 2)
                pair = true;

            if (v == 3)
                three = true;

            if (v == 4)
                four = true;
        }

        if (four)
            return 7;

        if (three && pair)
            return 6;

        if (three)
            return 3;

        if (pair)
            return 1;

        return 0;
    }
}

public class PokerGameGUI extends JFrame implements ActionListener {

    // COLORS
    Color bgPink = new Color(255, 192, 203);
    Color softPink = new Color(255, 228, 236);
    Color darkPink = new Color(255, 105, 180);

    JTextArea infoArea;

    JPanel playerPanel;
    JPanel communityPanel;

    JButton dealButton;
    JButton swapButton;
    JButton revealButton;

    JLabel chipsLabel;
    JLabel titleLabel;

    JButton[] playerButtons = new JButton[2];

    boolean[] selected = new boolean[2];

    Deck deck;

    List<Card> playerHand = new ArrayList<>();
    List<Card> dealerHand = new ArrayList<>();
    List<Card> communityCards = new ArrayList<>();

    int chips = 100;

    public PokerGameGUI() {

        setTitle("Poker Casino");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15,15));

        getContentPane().setBackground(bgPink);

        // TITLE
        titleLabel = new JLabel("♠ POKER CASINO ♠");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(darkPink);

        // CHIPS
        chipsLabel = new JLabel("💰 Chips: " + chips);
        chipsLabel.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel topPanel = new JPanel(new GridLayout(2,1));
        topPanel.setBackground(bgPink);

        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        chipsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(titleLabel);
        topPanel.add(chipsLabel);

        // INFO AREA
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.BOLD, 16));
        infoArea.setBackground(softPink);
        infoArea.setForeground(Color.BLACK);
        infoArea.setBorder(BorderFactory.createLineBorder(darkPink, 3));

        JScrollPane scrollPane = new JScrollPane(infoArea);

        // PLAYER PANEL
        playerPanel = new JPanel();
        playerPanel.setBackground(bgPink);
        playerPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(darkPink, 3),
                        "YOUR CARDS"
                )
        );

        // COMMUNITY PANEL
        communityPanel = new JPanel();
        communityPanel.setBackground(bgPink);
        communityPanel.setLayout(new GridLayout(5,1,10,10));

        communityPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(darkPink, 3),
                        "COMMUNITY CARDS"
                )
        );

        // BUTTONS
        dealButton = createStyledButton("🎴 Deal Cards");
        swapButton = createStyledButton("🔄 Swap Selected");
        revealButton = createStyledButton("🏆 Reveal Winner");

        dealButton.addActionListener(this);
        swapButton.addActionListener(this);
        revealButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(bgPink);

        buttonPanel.add(dealButton);
        buttonPanel.add(swapButton);
        buttonPanel.add(revealButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(playerPanel, BorderLayout.SOUTH);
        add(communityPanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.WEST);

        setVisible(true);
    }

    JButton createStyledButton(String text) {

        JButton button = new JButton(text);

        button.setBackground(darkPink);
        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setFont(new Font("Arial", Font.BOLD, 15));

        button.setPreferredSize(new Dimension(180, 55));

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == dealButton) {
            startRound();
        }

        if (e.getSource() == swapButton) {
            swapCards();
        }

        if (e.getSource() == revealButton) {
            revealWinner();
        }
    }

    void startRound() {

        deck = new Deck();
        deck.shuffle();

        playerHand.clear();
        dealerHand.clear();
        communityCards.clear();

        chips -= 10;
        chipsLabel.setText("💰 Chips: " + chips);

        for (int i = 0; i < 2; i++) {

            playerHand.add(deck.deal());
            dealerHand.add(deck.deal());
        }

        for (int i = 0; i < 5; i++) {
            communityCards.add(deck.deal());
        }

        displayPlayerCards();
        displayCommunityCards();

        infoArea.setText(
                "══════ TEXAS HOLD'EM ══════\n\n" +
                "1. Select cards you dislike.\n" +
                "2. Click SWAP SELECTED.\n" +
                "3. Reveal the winner.\n\n" +
                "Dealer has one hidden card."
        );
    }

    void displayPlayerCards() {

        playerPanel.removeAll();

        for (int i = 0; i < playerHand.size(); i++) {

            Card c = playerHand.get(i);

            JButton cardButton = new JButton(
                    "<html><center>" +
                    "<div style='font-size:22px'>" +
                    c.rank +
                    "</div><br>" +
                    c.suit +
                    "</center></html>"
            );

            cardButton.setPreferredSize(new Dimension(150, 220));

            cardButton.setBackground(Color.WHITE);

            cardButton.setFont(new Font("Arial", Font.BOLD, 18));

            cardButton.setBorder(
                    BorderFactory.createLineBorder(darkPink, 4)
            );

            int index = i;

            cardButton.addActionListener(ev -> {

                selected[index] = !selected[index];

                if (selected[index]) {

                    cardButton.setBackground(new Color(255, 182, 193));
                }
                else {

                    cardButton.setBackground(Color.WHITE);
                }
            });

            playerButtons[i] = cardButton;

            playerPanel.add(cardButton);
        }

        playerPanel.revalidate();
        playerPanel.repaint();
    }

    void displayCommunityCards() {

        communityPanel.removeAll();

        for (Card c : communityCards) {

            JLabel label = new JLabel(c.toString());

            label.setFont(new Font("Arial", Font.BOLD, 16));

            label.setBorder(
                    BorderFactory.createLineBorder(darkPink, 2)
            );

            label.setOpaque(true);

            label.setBackground(softPink);

            communityPanel.add(label);
        }

        communityPanel.revalidate();
        communityPanel.repaint();
    }

    void swapCards() {

        for (int i = 0; i < selected.length; i++) {

            if (selected[i]) {

                playerHand.set(i, deck.deal());

                selected[i] = false;
            }
        }

        displayPlayerCards();

        JOptionPane.showMessageDialog(
                this,
                "🎴 Cards swapped successfully!"
        );
    }

    void revealWinner() {

        List<Card> playerFull = new ArrayList<>(playerHand);
        playerFull.addAll(communityCards);

        List<Card> dealerFull = new ArrayList<>(dealerHand);
        dealerFull.addAll(communityCards);

        int playerScore = HandEvaluator.evaluate(playerFull);
        int dealerScore = HandEvaluator.evaluate(dealerFull);

        StringBuilder sb = new StringBuilder();

        sb.append("══════ FINAL RESULT ══════\n\n");

        sb.append("YOUR CARDS:\n");

        for (Card c : playerHand) {
            sb.append(c).append("\n");
        }

        sb.append("\nDEALER CARDS:\n");

        for (Card c : dealerHand) {
            sb.append(c).append("\n");
        }

        sb.append("\nCOMMUNITY CARDS:\n");

        for (Card c : communityCards) {
            sb.append(c).append("\n");
        }

        sb.append("\n══════════════════════════\n");

        if (playerScore > dealerScore) {

            sb.append("\n🏆 YOU WIN! +20 CHIPS");

            chips += 20;
        }
        else if (playerScore < dealerScore) {

            sb.append("\n💀 DEALER WINS!");
        }
        else {

            sb.append("\n🤝 DRAW! +10 CHIPS");

            chips += 10;
        }

        chipsLabel.setText("💰 Chips: " + chips);

        infoArea.setText(sb.toString());
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new PokerGameGUI();
        });
    }
}