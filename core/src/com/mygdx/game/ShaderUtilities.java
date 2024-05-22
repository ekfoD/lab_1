package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;

import static java.lang.Math.abs;

/**
 * @author Dovydas Girskas 5gr
 */
public class ShaderUtilities {
    private static float colorChangeDuration = 7.5f; // Duration for color change in seconds
    private static float t;
    private static Color currenctColor;
    private static Color lerpColor(Color startColor, Color endColor, float t) {
        float r = MathUtils.lerp(startColor.r, endColor.r, t);
        float g = MathUtils.lerp(startColor.g, endColor.g, t);
        float b = MathUtils.lerp(startColor.b, endColor.b, t);
        return new Color(r, g, b, 1); // Assuming alpha value is always 1
    }
    public static void showAnimatedBackground(float elapsedTime, Color startColor, Color endColor)
    {
        t = calculateTValue(elapsedTime, colorChangeDuration);

        currenctColor = lerpColor(startColor, endColor, t);
        Gdx.gl.glClearColor(currenctColor.r, currenctColor.g, currenctColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private static float calculateTValue(float elapsedTime, float colorChangeDuration) {
        elapsedTime = elapsedTime % (colorChangeDuration*2);  // Repeat every z*2 units
        if (elapsedTime < colorChangeDuration) {
            return elapsedTime / colorChangeDuration;
        } else {
            return 1 - (elapsedTime - colorChangeDuration) / colorChangeDuration;
        }
    }
}
