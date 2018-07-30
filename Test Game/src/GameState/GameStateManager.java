package GameState;

import Audio.JukeBox;
import Main.GamePanel;

import java.awt.*;

public class GameStateManager {

    private GameState[] gameStates;
    private int currentState;

    public static final int NUMGAMESTATES = 16;
    public static final int MENUSTATE = 0;
    public static final int LEVEL1STATE = 1;

    public GameStateManager() {

        JukeBox.init();

        gameStates = new GameState[NUMGAMESTATES];

        currentState = MENUSTATE;
        loadState(currentState);

    }

    private void loadState(int state) {
        if(state == MENUSTATE)
            gameStates[state] = new MenuState(this);
        else if(state == LEVEL1STATE)
            gameStates[state] = new Level1State(this);
    }

    private void unloadState(int state) {
        gameStates[state] = null;
    }

    public void setState(int state) {
        unloadState(currentState);
        currentState = state;
        loadState(currentState);
    }

    public void update() {

        if(gameStates[currentState] != null) gameStates[currentState].update();

    }

    public void draw(Graphics2D g) {
        if (gameStates[currentState] != null) gameStates[currentState].draw(g);
        else {
            g.setColor(java.awt.Color.BLACK);
            g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        }
    }
}









