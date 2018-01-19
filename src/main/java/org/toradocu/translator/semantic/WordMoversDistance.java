package org.toradocu.translator.semantic;

import com.crtomirmajer.wmd4j.WordMovers;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;

public class WordMoversDistance {
  private static final String word2VecModelPath =
      "/home/arianna/toradocu/glove-txt/GoogleNews-vectors-negative300.bin";
  private static WordMovers wm;

  private WordMoversDistance() {}

  public static double distance(String a, String b) {
    if (wm == null) {
      initWordVectors();
    }
    return wm.distance(a, b);
  }

  private static void initWordVectors() {
    final long startTime, endTime;
    startTime = System.nanoTime();
    final WordVectors vectors = WordVectorSerializer.readWord2VecModel(word2VecModelPath);
    wm = WordMovers.Builder().wordVectors(vectors).build();
    endTime = System.nanoTime();
    long elapsedTimeInSeconds = (endTime - startTime) / 1000000000;
    System.out.println("#### Word vectors initialized in " + elapsedTimeInSeconds + " seconds.");
  }
}
