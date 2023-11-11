package com.dima.game;

import java.sql.Time;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;


public class ParticleManager{
    Array<Particle> particles;
    Texture particleTexture;
    Vector2 center; Vector2 acceleration = new Vector2(0,0);
    long particleLifetime;

    long pulseStart; long pulseDuration; int numParticlesPulsed; int totalParticlesToPulse;

    int minEmissionAngle=0; int maxEmissionAngle=360;
    int minVelocity; int maxVelocity;

    ParticleManager(Texture particle_texture, Vector2 center,int minSpeed, int maxSpeed,long particleLifetime){
        this.particleTexture = particle_texture;
        this.center = center;
        this.minVelocity = minSpeed; this.maxVelocity = maxSpeed;
        particles = new Array<Particle>();
        this.particleLifetime = particleLifetime;
    }

    void generateParticle(){
        int randAngle = MathUtils.random(minEmissionAngle,maxEmissionAngle);
        int randSpeed = MathUtils.random(minVelocity,maxVelocity);
        Vector2 randVelocity = new Vector2((randSpeed*MathUtils.cosDeg(randAngle)),(randSpeed*MathUtils.sinDeg(randAngle)));
        particles.add(new Particle(center.cpy(), randVelocity, TimeUtils.nanoTime() + particleLifetime));
    }

    void updateParticles(float deltaTime){
        for (Iterator<Particle> iter = particles.iterator(); iter.hasNext();){
            Particle currParticle = iter.next();
            currParticle.velocity.x += acceleration.x * deltaTime;
            currParticle.velocity.y += acceleration.y * deltaTime;
            currParticle.position.x += currParticle.velocity.x * deltaTime;
            currParticle.position.y += currParticle.velocity.y * deltaTime;
            if (currParticle.deathTime < TimeUtils.nanoTime()){
                iter.remove();
            }
        }

        if (pulseDuration > TimeUtils.nanoTime()-pulseStart ){
            //calculate how many particles should have been pulsed

            float proportion = (float)(TimeUtils.nanoTime() - pulseStart)/(float)pulseDuration;
            System.out.println(proportion);
            int particlesOwed = (int)(proportion*totalParticlesToPulse);

            while (particlesOwed > numParticlesPulsed){
                numParticlesPulsed++;
                generateParticle();
            }

        }
        else if (numParticlesPulsed < totalParticlesToPulse){
            while (totalParticlesToPulse > numParticlesPulsed){
                numParticlesPulsed++;
                generateParticle();
            }
        }
    }

    void drawParticles(SpriteBatch batch){
        batch.begin();
        for (Particle currParticle : particles) {
            batch.draw(particleTexture, currParticle.position.x, currParticle.position.y);
        }
        batch.end();
    }

    void pulse(float numSeconds, int numParticles) {
        pulseDuration = (long) (numSeconds * Constants.SECOND);
        pulseStart  = TimeUtils.nanoTime();
        numParticlesPulsed = 0;
        totalParticlesToPulse = numParticles;
    }

    void setEmissionAngle(int minAngle, int maxAngle){
        minEmissionAngle = minAngle; maxEmissionAngle = maxAngle;
    }

    void setCenter(Vector2 newCenter){
        center = newCenter;
    }

    void addAcceleration(Vector2 acceleration){
        this.acceleration = acceleration;
    }
}

