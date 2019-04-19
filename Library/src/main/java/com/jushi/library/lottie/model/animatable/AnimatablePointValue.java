package com.jushi.library.lottie.model.animatable;

import android.graphics.PointF;


import com.jushi.library.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.jushi.library.lottie.animation.keyframe.PointKeyframeAnimation;
import com.jushi.library.lottie.value.Keyframe;

import java.util.List;

public class AnimatablePointValue extends BaseAnimatableValue<PointF, PointF> {
  public AnimatablePointValue(List<Keyframe<PointF>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<PointF, PointF> createAnimation() {
    return new PointKeyframeAnimation(keyframes);
  }
}
