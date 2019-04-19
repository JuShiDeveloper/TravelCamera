package com.jushi.library.lottie.parser;

import android.graphics.PointF;
import android.util.JsonReader;
import android.util.JsonToken;


import com.jushi.library.lottie.lottie.LottieComposition;
import com.jushi.library.lottie.animation.keyframe.PathKeyframe;
import com.jushi.library.lottie.utils.Utils;
import com.jushi.library.lottie.value.Keyframe;

import java.io.IOException;

class PathKeyframeParser {

  private PathKeyframeParser() {}

  static PathKeyframe parse(
      JsonReader reader, LottieComposition composition) throws IOException {
    boolean animated = reader.peek() == JsonToken.BEGIN_OBJECT;
    Keyframe<PointF> keyframe = KeyframeParser.parse(
        reader, composition, Utils.dpScale(), PathParser.INSTANCE, animated);

    return new PathKeyframe(composition, keyframe);
  }
}
