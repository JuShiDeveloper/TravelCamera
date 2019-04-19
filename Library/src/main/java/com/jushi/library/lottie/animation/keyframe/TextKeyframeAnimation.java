package com.jushi.library.lottie.animation.keyframe;


import com.jushi.library.lottie.model.DocumentData;
import com.jushi.library.lottie.value.Keyframe;

import java.util.List;

public class TextKeyframeAnimation extends KeyframeAnimation<DocumentData> {
  public TextKeyframeAnimation(List<Keyframe<DocumentData>> keyframes) {
    super(keyframes);
  }

  @Override DocumentData getValue(Keyframe<DocumentData> keyframe, float keyframeProgress) {
    return keyframe.startValue;
  }
}
