package com.jushi.library.lottie.model.animatable;

import android.graphics.PointF;


import com.jushi.library.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.jushi.library.lottie.animation.keyframe.SplitDimensionPathKeyframeAnimation;
import com.jushi.library.lottie.value.Keyframe;

import java.util.ArrayList;
import java.util.List;

public class AnimatableSplitDimensionPathValue implements AnimatableValue<PointF, PointF> {
  private final AnimatableFloatValue animatableXDimension;
  private final AnimatableFloatValue animatableYDimension;

  public AnimatableSplitDimensionPathValue(
      AnimatableFloatValue animatableXDimension,
      AnimatableFloatValue animatableYDimension) {
    this.animatableXDimension = animatableXDimension;
    this.animatableYDimension = animatableYDimension;
  }

  @Override
  public List<Keyframe<PointF>> getKeyframes() {
    throw new UnsupportedOperationException("Cannot call getKeyframes on AnimatableSplitDimensionPathValue.");
  }

  @Override
  public boolean isStatic() {
    return animatableXDimension.isStatic() && animatableYDimension.isStatic();
  }

  @Override public BaseKeyframeAnimation<PointF, PointF> createAnimation() {
    return new SplitDimensionPathKeyframeAnimation(
        animatableXDimension.createAnimation(), animatableYDimension.createAnimation());
  }
}
