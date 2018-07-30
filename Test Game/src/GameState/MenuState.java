package GameState;

import Audio.JukeBox;
import Handlers.Keyboard;
import Main.GamePanel;
import TileMap.Background;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class MenuState extends GameState {

    private BufferedImage selector;

    private Background bg;

    private String titleName = "A I R A S A";
    private int currentChoice = 0;
    private String[] options = {
            "Start",
            "Help",
            "Quit"
    };

    private Color titleColor;
    private Font titleFont;
    private Font font;


    public MenuState(GameStateManager gsm) {

        super(gsm);

        try {

            selector = ImageIO.read(getClass().getResourceAsStream("/Other/selector.png"));

            bg = new Background("/Backgrounds/menubg.gif", 1);
            bg.setVector(-0.3 , 0);

            titleColor = new Color(128, 0, 0);
            titleFont = new Font(
                    "Narkisim",
                    Font.PLAIN,
                    52);

            font = new Font("Arial", Font.PLAIN, 13);

            JukeBox.load("/SFX/menuselect.mp3", "menuselect");
            JukeBox.load("/SFX/menuoption.mp3", "menuoption");

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {}

    public void update() {

        bg.update();
        handleInput();
    }

    public void draw(Graphics2D g) {

        // draw bg
        bg.draw(g);

        // draw title
        g.setColor(titleColor);
        g.setFont(titleFont);
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(titleName, g);
        int x = ((GamePanel.WIDTH) - (int) r.getWidth()) / 2;
        int y = ((GamePanel.HEIGHT) - (int) r.getHeight()) / 2 + fm.getAscent() / 2 - 30;
        g.drawString(titleName, x, y);

        if(currentChoice == 0) g.drawImage(selector, 130, 130, null);
        else if(currentChoice == 1) g.drawImage(selector, 130, 145, null);
        else if(currentChoice == 2) g.drawImage(selector, 130, 160, null);

        // draw menu options
        g.setFont(font);
        for(int i = 0; i < options.length; i++) {
            if(i == currentChoice) {
                g.setColor(Color.BLACK);
            }
            else {
                g.setColor(Color.RED);
            }
            g.drawString(options[i], 145, 140 + i * 15);
        }
    }

    private void select() {
        if(currentChoice == 0) {
            JukeBox.play("menuselect", 0);
            gsm.setState(GameStateManager.LEVEL1STATE);
        }
        if(currentChoice == 1) {
            // help
        }
        if(currentChoice == 2) {
            System.exit(0);
        }
    }

    public void handleInput() {
        if(Keyboard.isPressed(Keyboard.ENTER)) select();
        if(Keyboard.isPressed(Keyboard.UP)) {
            if(currentChoice > 0) {
                JukeBox.play("menuoption", 0);
                currentChoice--;
            }
        }
        if(Keyboard.isPressed(Keyboard.DOWN)) {

            if(currentChoice < options.length - 1) {
                JukeBox.play("menuoption", 0);
                currentChoice++;
            }
        }
    }
    public void keyReleased(int k) {}

}










