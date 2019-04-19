package com.jushi.library.lottie.model.content;

import android.graphics.Paint;
import android.support.annotation.Nullable;

import com.jushi.library.lottie.lottie.LottieDrawable;
import com.jushi.library.lottie.animation.content.Content;
import com.jushi.library.lottie.animation.content.StrokeContent;
import com.jushi.library.lottie.model.animatable.AnimatableColorValue;
import com.jushi.library.lottie.model.animatable.AnimatableFloatValue;
import com.jushi.library.lottie.model.animatable.AnimatableIntegerValue;
import com.jushi.library.lottie.model.layer.BaseLayer;

import java.util.List;

public class ShapeStroke implements ContentModel {
  public enum LineCapType {
    BUTT,
    ROUND,
    UNKNOWN;

    public Paint.Cap toPaintCap() {
      switch (this) {
        case BUTT:
          return Paint.Cap.BUTT;
        case ROUND:
          return Paint.Cap.ROUND;
        case UNKNOWN:
        default:
          return Paint.Cap.SQUARE;
      }
    }
  }

  public enum LineJoinType {
    MITER,
    ROUND,
    BEVEL;

    public Paint.Join toPaintJoin() {
      switch (this) {
        case BEVEL:
          return Paint.Join.BEVEL;
        case MITER:
          return Paint.Join.MITER;
        case ROUND:
          return Paint.Join.ROUND;
      }
      return null;
    }
  }

  private final String name;
  @Nullable
  private final AnimatableFloatValue offset;
  private final List<AnimatableFloatValue> lineDashPattern;
  private final AnimatableColorValue color;
  private final AnimatableIntegerValue opacity;
  private final AnimatableFloatValue width;
  private final LineCapType capType;
  private final LineJoinType joinType;
  private final float miterLimit;
  private final boolean hidden;

  public ShapeStroke(String name, @Nullable AnimatableFloatValue offset,
                     List<AnimatableFloatValue> lineDashPattern, AnimatableColorValue color,
                     AnimatableIntegerValue opacity, AnimatableFloatValue width, LineCapType capType,
                     LineJoinType joinType, float miterLimit, boolean hidden) {
    this.name = name;
    this.offset = offset;
    this.lineDashPattern = lineDashPattern;
    this.color = color;
    this.opacity = opacity;
    this.width = width;
    this.capType = capType;
    this.joinType = joinType;
    this.miterLimit = miterLimit;
    this.hidden = hidden;
  }

  @Override public Content toContent(LottieDrawable drawable, BaseLayer layer) {
    return new StrokeContent(drawable, layer, this);
  }

  public String getName() {
    return name;
  }

  public AnimatableColorValue getColor() {
    return color;
  }

  public AnimatableIntegerValue getOpacity() {
    return opacity;
  }

  public AnimatableFloatValue getWidth() {
    return width;
  }

  public List<AnimatableFloatValue> getLineDashPattern() {
    return lineDashPattern;
  }

  public AnimatableFloatValue getDashOffset() {
    return offset;
  }

  public LineCapType getCapType() {
    return capType;
  }

  public LineJoinType getJoinType() {
    return joinType;
  }

  public float getMiterLimit() {
    return miterLimit;
  }

  public boolean isHidden() {
    return hidden;
  }
}
