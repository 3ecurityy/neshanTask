package ir.mohammadrezaarabi.neshan;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.carto.graphics.Color;
import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.LineStyle;
import com.carto.styles.LineStyleBuilder;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;

import org.neshan.mapsdk.internal.utils.BitmapUtils;

public class Helper {
    public LineStyle getLineStyle() {
        LineStyleBuilder lineStCr = new LineStyleBuilder();
        lineStCr.setColor(new Color((short) 2, (short) 119, (short) 189, (short) 190));
        lineStCr.setWidth(10f);
        lineStCr.setStretchFactor(0f);
        return lineStCr.buildStyle();
    }

    public MarkerStyle markerStyleUser(Context context) {
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_marker)));
        markStCr.setAnimationStyle(animationStyle());
        return markStCr.buildStyle();
    }

    public MarkerStyle markerStyleLocation(Context context) {
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.location)));
        markStCr.setAnimationStyle(animationStyle());
        return markStCr.buildStyle();
    }

    private AnimationStyle animationStyle() {
        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        return animStBl.buildStyle();
    }
}
