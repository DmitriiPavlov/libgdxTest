package com.dima.game;

import com.badlogic.gdx.math.Vector2;
public class Particle {
    Vector2 velocity;
    Vector2 position;
    long deathTime;

    Particle(Vector2 position, Vector2 velocity,long deathTime){
        this.velocity = velocity;
        this.position = position;
        this.deathTime= deathTime;
    }

}
