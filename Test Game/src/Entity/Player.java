package Entity;

import Audio.JukeBox;
import TileMap.*;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject {

    //player stuff
    private int health;
    private int maxHealth;
    private int fire;
    private int maxFire;
    private boolean Dead;
    private boolean Flinching;
    private long flinchTime;
    private boolean doubleJump;
    private boolean alreadyDoubleJump;
    private double doubleJumpStart;

    //fireball
    private boolean firing;
    private int fireCost;
    private int fireBallDamage;
    private ArrayList<FireBall> fireBalls;

    //scratch
    private boolean scratching;
    private int scratchDamage;
    private int scratchRange;

    //gliding
    private boolean gliding;

    //animations
    private ArrayList<BufferedImage[]> sprites;
    private final int[] numFrames = {
            2, 8, 1, 2, 4, 2, 5
    };

    //animation actions
    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 3;
    private static final int GLIDING = 4;
    private static final int FIREBALL = 5;
    private static final int SCRATCHING = 6;

    public Player(TileMap tm){

        super(tm);

        width = 30;
        height = 30;
        cwidth = 20;
        cheight = 20;

        moveSpeed = 0.3;
        maxSpeed = 1.6;
        stopSpeed = 0.4;
        fallSpeed = 0.15;
        maxFallSpeed = 4.0;
        jumpStart = -4.3;
        stopJumpSpeed = 0.3;
        doubleJumpStart = -3;

        facingRight = true;

        health = maxHealth = 100;
        fire = maxFire = 2500;

        fireCost = 200;
        fireBallDamage = 5;
        fireBalls = new ArrayList<FireBall>();

        scratchDamage = 8;
        scratchRange = 40;

        //load sprites
        try{

			BufferedImage spritesheet = ImageIO.read(
				getClass().getResourceAsStream(
                    "/Sprites/Player/playersprites.gif"
                 )
            );

            sprites = new ArrayList<BufferedImage[]>();

            for(int i = 0; i < 7; i++){
                BufferedImage[] bi =
                        new BufferedImage[numFrames[i]];
                for(int j = 0; j < numFrames[i]; j++){
                    if(i != SCRATCHING){
						bi[j] = spritesheet.getSubimage(
                                j * width, i * height, width, height
                        );
                    }
                    else{
						bi[j] = spritesheet.getSubimage(
                                j * width * 2, i * height, width * 2, height
                        );
                    }
                }

                sprites.add(bi);

            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

        JukeBox.load("/SFX/playerjump.mp3", "playerjump");
        JukeBox.load("/SFX/playerattack.mp3", "playerattack");

        animation = new Animation();
        currentAction = IDLE;
        animation.setFrames(sprites.get(IDLE));
        animation.setDelay(400);

    }

    public int getHealth() {return health;}
    public int getMaxHealth() {return maxHealth;}
    public int getFire() {return fire;}
    public int getMaxFire() {return maxFire; }

    public void setFiring() {
        firing = true;
    }

    public void setScratching() {
        scratching = true;
        JukeBox.play("playerattack");
    }

    public void setJumping(boolean b) {
        if(b && !jumping && falling && !alreadyDoubleJump) {
            doubleJump = true;
        }
        jumping = b;
    }

    public void setGliding(boolean b) {
        gliding = b;
    }
    
    public void checkAttack(ArrayList<Enemy> enemies) {
 	
    	// loop through enemies array
    	for(int i = 0; i < enemies.size(); i++) {
    		Enemy enemy = enemies.get(i);
    		
    		if(scratching) {
    			if(facingRight) {
    				if(
    					enemy.getX() > x &&
    					enemy.getX() < x + scratchRange &&
    					enemy.getY() > y - height / 2 &&
    					enemy.getY() < y + height / 2
    				) {
    					enemy.hit(scratchDamage);
    				}
    			}
    			else {
    				if(
    					enemy.getX() < x &&
    					enemy.getX() > x - scratchRange &&
    					enemy.getY() > y - height / 2 &&
    					enemy.getY() < y + height / 2
    				) {
    					enemy.hit(scratchDamage);
    				}
    			}
    		}
    		
	    	// fireballs
	    	for(int j = 0; i < fireBalls.size(); i++) {
	    		if(fireBalls.get(i).intersects(enemy)) {
	    			enemy.hit(fireBallDamage);
	    			fireBalls.get(j).setHit();
	    			break;
	    		}
	    	}
	    	
	    	// check enemy collisions
	    	if(intersects(enemy)) {
	    		hit(enemy.damage);
	    	}
	    }
    	
    }
    
    public void hit(int damage) {
    	if(Flinching) return;
    	health -= damage;
    	if(health < 0) health = 0;
    	if(health == 0) Dead = true;
    	Flinching = true;
    	flinchTime = System.nanoTime();
    }

    public void getNextPosition(){

        super.getNextPosition();

        // cannot move whilst attacking, except in air
        if((currentAction == SCRATCHING || currentAction == FIREBALL) && !(jumping || falling)) {
            dx = 0;
        }

		// jumping
        if(jumping && !falling){
            dy = jumpStart;
            falling = true;
            JukeBox.play("playerjump");
        }

        //double jump
        if(doubleJump) {
            dy = doubleJumpStart;
            alreadyDoubleJump = true;
            doubleJump = false;
            JukeBox.play("playerjump");
        }

        if(!falling) alreadyDoubleJump = false;

		// falling
        if(falling) {
            if(dy > 0 && gliding) dy += fallSpeed * 0.1;
            else dy += fallSpeed;

            if( dy > 0 ) jumping = false;
            if (dy < 0 && !jumping) dy += stopJumpSpeed;

            if(dy > maxFallSpeed) dy = maxFallSpeed;
        }

    }

    public void update() {

        //update position
        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp, ytemp);

        if(currentAction == SCRATCHING){
            if(animation.getPlayedOnce()) scratching = false;
        }
        if(currentAction == FIREBALL){
            if(animation.getPlayedOnce()) firing = false;
        }

        if(scratching == true) firing = false;

        //Fireball attack
        fire += 1;
        if (fire > maxFire) fire = maxFire;
        if(firing && currentAction != FIREBALL) {
            if(fire > fireCost) {
                fire -= fireCost;
                FireBall fb = new FireBall(tileMap, facingRight);
                fb.setPosition(x, y);
                fireBalls.add(fb);
            }
        }
        for(int i = 0; i < fireBalls.size(); i++) {
            fireBalls.get(i).update();
            if(fireBalls.get(i).shouldRemove()) {
                fireBalls.remove(i);
                i--;
            }
        }
        
        // check done flinching
        if(Flinching) {
        	long elapsed = (System.nanoTime() - flinchTime) / 1000000;
        	if(elapsed > 1000) Flinching = false;
        }

        // set animations
        if(scratching) {
            if(currentAction != SCRATCHING) {
                currentAction = SCRATCHING;
                animation.setFrames(sprites.get(SCRATCHING));
                animation.setDelay(50);
                width = 60;
            }
        }
        else if(firing){
            if(currentAction != FIREBALL) {
                currentAction = FIREBALL;
                animation.setFrames(sprites.get(FIREBALL));
                animation.setDelay(100);
                width = 30;
            }
        }
        else if(dy > 0) {
            if(gliding){
                if(currentAction != GLIDING) {
                    currentAction = GLIDING;
                    animation.setFrames(sprites.get(GLIDING));
                    animation.setDelay(100);
                    width = 30;
                }
            }
            else if(currentAction != FALLING) {
                currentAction = FALLING;
                animation.setFrames(sprites.get(FALLING));
                animation.setDelay(100);
				width = 30;
            }
        }
        else if(dy < 0) {
            if (currentAction != JUMPING) {
                currentAction = JUMPING;
                animation.setFrames(sprites.get(JUMPING));
                animation.setDelay(-1);
                width = 30;
            }
        }
        else if(left || right) {
            if(currentAction != WALKING) {
                currentAction = WALKING;
                animation.setFrames(sprites.get(WALKING));
                animation.setDelay(40);
                width = 30;
            }
        }
        else {
            if(currentAction != IDLE) {
                currentAction = IDLE;
                animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
                width = 30;
            }
        }

        animation.update();

        // set the direction of the player
        if(currentAction != SCRATCHING  && currentAction != FIREBALL) {
            if(right) facingRight = true;
            if(left) facingRight = false;
        }
    }

    public void draw(Graphics2D g){

        setMapPosition();

        //draw Fireballs
        for(int i = 0; i < fireBalls.size(); i++) {
            fireBalls.get(i).draw(g);
        }

		// draw player
		if(Flinching) {
			long elapsed =
				(System.nanoTime() - flinchTime) / 1000000;
            if(elapsed / 100 % 2 == 0) {
                return;
            }
        }

        isFacingRight(g);

    }
}


