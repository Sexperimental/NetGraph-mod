package com.example.examplemod.utils.render;

import net.minecraft.util.MathHelper;

public class Colors {

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= MathHelper.clamp_int(alpha, 0, 255) << 24;
        color |= MathHelper.clamp_int(red, 0, 255) << 16;
        color |= MathHelper.clamp_int(green, 0, 255) << 8;
        color |= MathHelper.clamp_int(blue, 0, 255);
        return color;
    }

    public static int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }
}
