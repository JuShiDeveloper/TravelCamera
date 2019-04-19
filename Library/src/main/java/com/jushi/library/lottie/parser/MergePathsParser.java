package com.jushi.library.lottie.parser;

import android.util.JsonReader;


import com.jushi.library.lottie.model.content.MergePaths;

import java.io.IOException;

class MergePathsParser {

  private MergePathsParser() {}

  static MergePaths parse(JsonReader reader) throws IOException {
    String name = null;
    MergePaths.MergePathsMode mode = null;
    boolean hidden = false;

    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "nm":
          name = reader.nextString();
          break;
        case "mm":
          mode =  MergePaths.MergePathsMode.forId(reader.nextInt());
          break;
        case "hd":
          hidden = reader.nextBoolean();
          break;
        default:
          reader.skipValue();
      }
    }

    return new MergePaths(name, mode, hidden);
  }
}
