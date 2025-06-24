import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class MemoryGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameSettings settings = new GameSettings();
            settings.setVisible(true);
        });
    }
}

class GameSettings extends JFrame {
    private JComboBox<String> sizeComboBox;
    
    public GameSettings() {
        setTitle("Настройки игры Мемори");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Выберите размер поля:", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        String[] sizes = {"5x5", "6x6", "8x8"};
        sizeComboBox = new JComboBox<>(sizes);
        
        JButton startButton = new JButton("Начать игру");
        startButton.addActionListener(e -> {
            String selectedSize = (String) sizeComboBox.getSelectedItem();
            int size = Integer.parseInt(selectedSize.split("x")[0]);
            new MemoryGameFrame(size).setVisible(true);
            dispose();
        });
        
        panel.add(titleLabel);
        panel.add(sizeComboBox);
        panel.add(startButton);
        
        add(panel);
    }
}

class MemoryGameFrame extends JFrame {
    private int size;
    private int pairsFound;
    private int attempts;
    private JLabel statusLabel;
    private List<MemoryCard> cards;
    private MemoryCard firstSelectedCard;
    private MemoryCard secondSelectedCard;
    private Timer timer;
    
    public MemoryGameFrame(int size) {
        this.size = size;
        this.pairsFound = 0;
        this.attempts = 0;
        this.cards = new ArrayList<>();
        
        setTitle("Игра Мемори - " + size + "x" + size);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        statusLabel = new JLabel("Найдено пар: 0 | Попытки: 0", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(statusLabel, BorderLayout.NORTH);
 
        JPanel gamePanel = new JPanel(new GridLayout(size, size, 5, 5));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initializeCards(gamePanel);
        
        add(gamePanel, BorderLayout.CENTER);
     
        JButton newGameButton = new JButton("Новая игра");
        newGameButton.addActionListener(e -> {
            new GameSettings().setVisible(true);
            dispose();
        });
        add(newGameButton, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initializeCards(JPanel gamePanel) {
        cards.clear();

        int totalPairs = (size * size) / 2;
        List<Integer> cardValues = new ArrayList<>();
        for (int i = 1; i <= totalPairs; i++) {
            cardValues.add(i);
            cardValues.add(i);
        }

        Collections.shuffle(cardValues);

        for (int value : cardValues) {
            MemoryCard card = new MemoryCard(value);
            card.addActionListener(e -> cardClicked(card));
            cards.add(card);
            gamePanel.add(card);
        }
    }
    
    private void cardClicked(MemoryCard card) {
        if (card.isRevealed() || (firstSelectedCard != null && secondSelectedCard != null)) {
            return;
        }
        
        card.reveal();
        
        if (firstSelectedCard == null) {
            firstSelectedCard = card;
        } else {
            secondSelectedCard = card;
            attempts++;
            updateStatus();

            if (firstSelectedCard.getValue() == secondSelectedCard.getValue()) {
                pairsFound++;
                updateStatus();
                firstSelectedCard.setMatched(true);
                secondSelectedCard.setMatched(true);

                if (pairsFound == (size * size) / 2) {
                    JOptionPane.showMessageDialog(this, 
                            "Поздравляем! Вы нашли все пары за " + attempts + " попыток.",
                            "Игра завершена", 
                            JOptionPane.INFORMATION_MESSAGE);
                }
                
                resetSelection();
            } else {
                timer = new Timer(1000, e -> {
                    firstSelectedCard.hide();
                    secondSelectedCard.hide();
                    resetSelection();
                    timer.stop();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    private void resetSelection() {
        firstSelectedCard = null;
        secondSelectedCard = null;
    }
    
    private void updateStatus() {
        statusLabel.setText("Найдено пар: " + pairsFound + " | Попытки: " + attempts);
    }
}

class MemoryCard extends JButton {
    private int value;
    private boolean revealed;
    private boolean matched;
    
    public MemoryCard(int value) {
        this.value = value;
        this.revealed = false;
        this.matched = false;
        
        setPreferredSize(new Dimension(80, 80));
        setFont(new Font("Arial", Font.BOLD, 24));
        hide();
    }
    
    public int getValue() {
        return value;
    }
    
    public boolean isRevealed() {
        return revealed;
    }
    
    public boolean isMatched() {
        return matched;
    }
    
    public void setMatched(boolean matched) {
        this.matched = matched;
        if (matched) {
            setBackground(Color.GREEN);
        }
    }
    
    public void reveal() {
        revealed = true;
        setText(Integer.toString(value));
        setBackground(Color.WHITE);
    }
    
    public void hide() {
        if (!matched) {
            revealed = false;
            setText("");
            setBackground(Color.BLUE);
        }
    }
}