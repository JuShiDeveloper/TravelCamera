package com.jushi.library.lottie.model.animatable;


import com.jushi.library.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.jushi.library.lottie.animation.keyframe.ColorKeyframeAnimation;
import com.jushi.library.lottie.value.Keyframe;

import java.util.List;

public class AnimatableColorValue extends BaseAnimatableValue<Integer, Integer> {
  public AnimatableColorValue(List<Keyframe<Integer>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<Integer, Integer> createAnimation() {
    return new ColorKeyframeAnimation(keyframes);
  }
}
