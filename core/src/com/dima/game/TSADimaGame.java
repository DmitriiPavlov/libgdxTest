package com.dima.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class TSADimaGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture sleigh;
	Rectangle sleighRect;
	Texture giftTexture;
	Texture heartTexture;
	Texture stageTexture;
	Texture snowTexture;
	ParticleManager snowParticleManager;
	ParticleManager heartParticleManager;
	ParticleManager giftStarParticleManager;
	OrthographicCamera camera;
	Array<Rectangle> gifts;
	Array<Vector2> snowParticles;
	BitmapFont font;
	int score;
	int numLives;
	long lastTime;
	long snowTime;
	int minSpeed = 100;
	@Override
	public void create () {
		snowTime = TimeUtils.nanoTime();
		batch = new SpriteBatch();
		sleigh = new Texture("BigSleigh.png");
		giftTexture = new Texture("gift.png");
		stageTexture = new Texture("stage.png");
		heartTexture = new Texture("heart.png");
		snowTexture = new Texture("snowParticle.png");
		camera = new OrthographicCamera();
		gifts = new Array<Rectangle>();
		sleighRect = new Rectangle(0, 5, 96, 48);
		lastTime = TimeUtils.nanoTime();
		camera.setToOrtho(false, 800, 480);
		font = new BitmapFont();
		score = 0;
		numLives = 3;
		snowParticles = new Array<Vector2>();

		Texture heartParticleTexture = new Texture("heartParticle.png");
		Texture giftStarParticleTexture = new Texture("starParticle.png");
		snowParticleManager = new ParticleManager(snowTexture, new Vector2(100, 100), 300, 450, (long) (1.0F * Constants.SECOND));
		snowParticleManager.addAcceleration(new Vector2(0,-700));
		heartParticleManager = new ParticleManager(heartParticleTexture, new Vector2(100, 100), 100, 350, (long) (0.1 * Constants.SECOND));
		giftStarParticleManager = new ParticleManager(giftStarParticleTexture, new Vector2(100, 100), 400, 650, (long) (0.1 * Constants.SECOND));
//		giftStarParticleManager.setEmissionAngle(75, 135);
	}
	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();
		if (numLives == 0){
			batch.begin();
			font.getData().setScale(3.0F,3.0F);
			font.draw(batch, "Game Over! Score: "+Integer.toString(score),150,225);
			batch.end();

			if(Gdx.input.isKeyPressed(Input.Keys.ENTER)){
				numLives = 3;
				score = 0;
				sleighRect.x = 0;
				font.getData().setScale(1.0F, 1.0F);
				gifts.clear();
				snowParticles.clear();
			}
			return;
		}
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(stageTexture,0,0);
		for (Iterator<Rectangle> iter = gifts.iterator(); iter.hasNext();){
			Rectangle gift = iter.next();
			gift.y -= Gdx.graphics.getDeltaTime()*(score*15 + minSpeed);
			if (gift.y+gift.height < 0 ){
				iter.remove();
				if (score>10){
					score-=10;
				}
				else{
					score = 0;
				}
				if (numLives==1){
					heartParticleManager.setCenter(new Vector2(674,454));
				}
				if (numLives==2){
					heartParticleManager.setCenter(new Vector2(724,454));
				}
				if (numLives==3){
					heartParticleManager.setCenter(new Vector2(774,454));
				}
				heartParticleManager.pulse(0.1F, 1000);
				numLives -=1;
			}
			if (gift.overlaps(sleighRect)){
				iter.remove();
				giftStarParticleManager.setCenter(new Vector2(gift.x+16,gift.y+16));
				giftStarParticleManager.pulse(0.01F,20);
				score+=1;
			}
			batch.draw(giftTexture, gift.x, gift.y);
		}

		batch.draw(sleigh, sleighRect.x, sleighRect.y);
		font.draw(batch, "Score: "+Integer.toString(score),0,475);
		font.draw(batch, "FPS" + Integer.toString(Gdx.graphics.getFramesPerSecond()), 100,475);
		if (numLives>0){
			batch.draw(heartTexture, 650,430);
		}
		if (numLives>1){
			batch.draw(heartTexture, 700,430);
		}
		if (numLives>2){
			batch.draw(heartTexture, 750,430);
		}
		batch.end();


		snowParticleManager.updateParticles(Gdx.graphics.getDeltaTime());
		snowParticleManager.drawParticles(batch);

		heartParticleManager.updateParticles(Gdx.graphics.getDeltaTime());
		heartParticleManager.drawParticles(batch);

		giftStarParticleManager.updateParticles(Gdx.graphics.getDeltaTime());
		giftStarParticleManager.drawParticles(batch);


//		drawSnow();

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			sleighRect.x -= 400 * Gdx.graphics.getDeltaTime();
			snowParticleManager.setCenter(new Vector2(sleighRect.x+96,sleighRect.y));
			snowParticleManager.setEmissionAngle(0,45);
			snowParticleManager.pulse(0.02F,10);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			sleighRect.x += 400 * Gdx.graphics.getDeltaTime();
			snowParticleManager.setCenter(new Vector2(sleighRect.x+10,sleighRect.y));
			snowParticleManager.setEmissionAngle(125,180);
			snowParticleManager.pulse(0.02F,10);
		}
		if (isTimeToAct()){
			int randX = MathUtils.random(250,550);
			gifts.add(new Rectangle(randX,500,32, 32));
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		sleigh.dispose();
		giftTexture.dispose();
	}

	private boolean isTimeToAct(){
		if (TimeUtils.nanoTime()- lastTime > 1000000000){
			lastTime = TimeUtils.nanoTime();
			return true;
		}
		return false;
	}


}
