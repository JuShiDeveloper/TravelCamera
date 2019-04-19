package com.jushi.library.lottie.parser;

import android.util.JsonReader;


import com.jushi.library.lottie.lottie.LottieComposition;
import com.jushi.library.lottie.model.animatable.AnimatableFloatValue;
import com.jushi.library.lottie.model.content.ShapeTrimPath;

import java.io.IOException;

class ShapeTrimPathParser {

  private ShapeTrimPathParser() {}

  static ShapeTrimPath parse(
      JsonReader reader, LottieComposition composition) throws IOException {
    String name = null;
    ShapeTrimPath.Type type = null;
    AnimatableFloatValue start = null;
    AnimatableFloatValue end = null;
    AnimatableFloatValue offset = null;
    boolean hidden = false;

    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "s":
          start = AnimatableValueParser.parseFloat(reader, composition, false);
          break;
        case "e":
          end = AnimatableValueParser.parseFloat(reader, composition, false);
          break;
        case "o":
          offset = AnimatableValueParser.parseFloat(reader, composition, false);
          break;
        case "nm":
          name = reader.nextString();
          break;
        case "m":
          type = ShapeTrimPath.Type.forId(reader.nextInt());
          break;
        case "hd":
          hidden = reader.nextBoolean();
          break;
        default:
          reader.skipValue();
      }
    }

    return new ShapeTrimPath(name, type, start, end, offset, hidden);
  }
}
