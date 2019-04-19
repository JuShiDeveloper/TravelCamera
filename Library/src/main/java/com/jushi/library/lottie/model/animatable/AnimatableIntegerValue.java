package com.jushi.library.lottie.model.animatable;


import com.jushi.library.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.jushi.library.lottie.animation.keyframe.IntegerKeyframeAnimation;
import com.jushi.library.lottie.value.Keyframe;

import java.util.List;

public class AnimatableIntegerValue extends BaseAnimatableValue<Integer, Integer> {

  public AnimatableIntegerValue() {
    super(100);
  }

  public AnimatableIntegerValue(List<Keyframe<Integer>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<Integer, Integer> createAnimation() {
    return new IntegerKeyframeAnimation(keyframes);
  }
}
