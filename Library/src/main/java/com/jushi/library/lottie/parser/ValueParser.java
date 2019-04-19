package com.jushi.library.lottie.parser;

import android.util.JsonReader;

import java.io.IOException;

interface ValueParser<V> {
  V parse(JsonReader reader, float scale) throws IOException;
}
