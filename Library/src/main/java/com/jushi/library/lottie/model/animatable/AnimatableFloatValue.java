package com.jushi.library.lottie.model.animatable;


import com.jushi.library.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.jushi.library.lottie.animation.keyframe.FloatKeyframeAnimation;
import com.jushi.library.lottie.value.Keyframe;

import java.util.List;

public class AnimatableFloatValue extends BaseAnimatableValue<Float, Float> {

  AnimatableFloatValue() {
    super(0f);
  }

  public AnimatableFloatValue(List<Keyframe<Float>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<Float, Float> createAnimation() {
    return new FloatKeyframeAnimation(keyframes);
  }
}
