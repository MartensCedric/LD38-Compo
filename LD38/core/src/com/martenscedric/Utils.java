package com.martenscedric;

/**
 * Created by Cedric on 2017-04-22.
 */
public class Utils
{
    public static boolean isBetween(float n, float lowerLimit, float higherLimit)
    {
        return (n >= lowerLimit && n <= higherLimit);
    }

    public static boolean isInside(float x, float y, float lowX, float lowY, float highX, float highY)
    {
        return isBetween(x, lowX, highX) && isBetween(y, lowY, highY);
    }

    public static float distanceToPoint(float x1, float y1, float x2, float y2)
    {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

}
