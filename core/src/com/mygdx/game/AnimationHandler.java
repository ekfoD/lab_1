package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

enum PlayerState {
    MOVING_LEFT,
    MOVING_RIGHT,
    JUMPING,
    CLIMBING,
    CLIMBING_IDLE,
    FALLING,
    IDLE,
    DYING
}
public class AnimationHandler {
    public static final float ANIMATION_SPEED = 1/10f;
    private Animation<TextureRegion> idleLeftAnimation, idleRightAnimation, runningLeftAnimation, runningRightAnimation, jumpingLeftAnimation, jumpingRightAnimation,
    fallingLeftAnimation, fallingRightAnimation, deathLeftAnimation, deathRightAnimation, climbingAnimation, climbingLockAnimation, selectedAnimation;
    public float stateTime, secondStateTime;
    private MyGdxGame game;
    private Player player;
    public AnimationHandler(MyGdxGame game, Player player) {
        this.game = game;
        this.player = player;

        idleLeftAnimation = loadAnimation(6, "idle", true);
        idleRightAnimation = loadAnimation(6, "idle", false);
        runningLeftAnimation = loadAnimation(8, "running", true);
        runningRightAnimation = loadAnimation(8, "running", false);
        jumpingLeftAnimation = loadAnimation(1, "jumplock", true);
        jumpingRightAnimation = loadAnimation(1, "jumplock", false);
        fallingLeftAnimation = loadAnimation(2, "fallingLock", true);
        fallingRightAnimation = loadAnimation(2, "fallingLock", false);
        climbingAnimation = loadAnimation(2, "ladderClimb", false);
        climbingLockAnimation = loadAnimation(1, "ladderClimb", false);
        deathLeftAnimation = loadAnimation(3,"death", true);
        deathRightAnimation = loadAnimation(3,"death", false);
        stateTime = 0f;
        secondStateTime = 0f;
    }
    private Animation<TextureRegion> loadAnimation(int frameCount, String name, boolean flipOrNo) {
        TextureRegion[] frames = new TextureRegion[frameCount];
        TextureRegion originalFrame;

        for (int i = 0; i < frameCount; ++i) {
            originalFrame = new TextureRegion(new Texture("animations/" + name + "_" + i + ".png"));
            if (flipOrNo) {
                frames[i] = originalFrame;
                frames[i].flip(true, false);
            }
            else {
                frames[i] = originalFrame;
            }
        }
        return new Animation<>(ANIMATION_SPEED, frames);
    }

    public void animationPlayer(float delta, PlayerState playerState, boolean isPlayerFacingLeft) {
        selectedAnimation = animationSelection(playerState, isPlayerFacingLeft);

        stateTime += delta;
        TextureRegion currentFrame = selectedAnimation.getKeyFrame(stateTime, true);

        game.batch.begin();
            player.setRegion(currentFrame);
        game.batch.end();
    }

    public boolean playDeathAnimation(float delta, PlayerState playerState, boolean isPlayerFacingLeft) {
        selectedAnimation = animationSelection(playerState, isPlayerFacingLeft);
        secondStateTime += delta;

        TextureRegion currentFrame = selectedAnimation.getKeyFrame(secondStateTime, false);

            game.batch.begin();
            player.setRegion(currentFrame);
            game.batch.end();

            // kad ilgiau butu death animation
            if (runningLeftAnimation.isAnimationFinished(secondStateTime)) {
                return true;
            }
            else {
                return false;
            }
    }
    private Animation<TextureRegion> animationSelection(PlayerState playerState, boolean isPlayerFacingLeft) {
        switch (playerState) {
            case JUMPING:
                if (isPlayerFacingLeft)
                    return jumpingLeftAnimation;
                else
                    return jumpingRightAnimation;
            case MOVING_LEFT:
                return runningLeftAnimation;
            case MOVING_RIGHT:
                return runningRightAnimation;
            case CLIMBING:
                return climbingAnimation;
            case CLIMBING_IDLE:
                return climbingLockAnimation;
            case FALLING:
                if (isPlayerFacingLeft)
                    return fallingLeftAnimation;
                else
                    return fallingRightAnimation;
            case IDLE:
                if (isPlayerFacingLeft)
                    return idleLeftAnimation;
                else
                    return idleRightAnimation;
            case DYING:
                if (isPlayerFacingLeft)
                    return deathLeftAnimation;
                else
                    return deathRightAnimation;
            default:
                return idleLeftAnimation;
        }
    }
}
