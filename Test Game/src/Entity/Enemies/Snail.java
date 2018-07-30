package Entity.Enemies;

import Entity.Animation;
import Entity.Enemy;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Snail extends Enemy {

    private BufferedImage[] sprites;

    public Snail(TileMap tm) {

        super(tm);

        moveSpeed = 0.3;
        maxSpeed = 0.3;
        fallSpeed = 0.2;
        maxFallSpeed = 10.0;

        width = height = 30;
        cwidth = cheight = 20;

        health = maxHealth = 2;
        damage = 1;

        //load the enemy sprites
        try{
            BufferedImage spritesheet = ImageIO.read(
                    getClass().getResourceAsStream("/Sprites/Enemies/slugger.gif")
            );

            sprites = new BufferedImage[3];
            for(int i = 0; i < sprites.length; i++) {
                sprites[i] = spritesheet.getSubimage(
                        i * width,
                        0,
                        width,
                        height
                );
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        animation = new Animation();
        animation.setFrames(sprites);
        animation.setDelay(300);

        right = facingRight =  true;

    }

    public void getNextPosition() {
        super.getNextPosition();
        if(falling){
            dy += fallSpeed;
        }

    }

    public void update() {

        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp, ytemp);

        //check flinching
        if(flinching) {
            long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
            if (elapsed > 400) {
                flinching = false;
            }
        }

        //hits the wall, goes the other way.
        if(right && dx == 0) {
            right = false;
            left = true;
            facingRight = false;
        }
        else if(left && dx == 0){
            right = true;
            left = false;
            facingRight = true;
        }

        animation.update();
    }

    public void draw(Graphics2D g){

        setMapPosition();

        isFacingRight(g);
    }
}