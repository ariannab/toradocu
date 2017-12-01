package org.toradocu.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to store and manage the matches to log in order to generate stats about our candidates
 * search space.
 */
public class MatchesLogger {
  /**
   * Map holding the matches. Keys are composed by the qualified class name of the CUT, the
   * executable member's signature, and the comment text - the three elements are separated by a
   * dot.
   */
  private static Map<String, MatchToLog> storedMatches = new HashMap<>();

  /**
   * Getter for the stored matches.
   *
   * @return the Map {@code storedMatches}
   */
  public static Map<String, MatchToLog> getStoredMatches() {
    return storedMatches;
  }

  /** Prints the stored matches in a CSV file with semicolon separator. */
  static void printSubjectCandidates() {
    if (storedMatches.values().stream().findFirst().isPresent()) {
      String className = storedMatches.values().stream().findFirst().get().getClassName();
      try {
        String log = "stats/candidates/" + className + ".csv";
        PrintWriter writer = new PrintWriter(new FileWriter(log, true));
        for (MatchToLog match : storedMatches.values()) {
          writer.append(prettyPrint(match));
        }
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Set up a {@code StringBuilder} for a pretty print of the specified {@code MatchToLog}.
   *
   * @param match the {@code MatchToLog} to print
   * @return the {@code StringBuilder} representing the {@code MatchToLog}
   */
  private static StringBuilder prettyPrint(MatchToLog match) {
    StringBuilder writer = new StringBuilder();

    writer.append("Method");
    writer.append(";");
    writer.append(match.getMethodName());
    writer.append("\n");

    writer.append("Comment");
    writer.append(";");
    writer.append(match.getCommentText());
    writer.append("\n");

    writer.append("Subject");
    writer.append(";");
    writer.append(match.getSubjectMatch());

    writer.append("\n");

    writer.append("Number of candidates");
    writer.append(";");
    writer.append(match.getNumberOfSubjectCandidates());
    writer.append("\n");

    writer.append("Predicate");
    writer.append(";");
    writer.append(match.getPredicateMatch());
    writer.append("\n");

    writer.append("Number of candidates");
    writer.append(";");
    writer.append(match.getNumberOfPredicateCandidates());
    writer.append("\n");

    writer.append("Is match expected");
    writer.append(";");
    writer.append(match.isMatchExpected());
    writer.append("\n");

    writer.append("Did match succeded");
    writer.append(";");
    writer.append(match.isSuccess());

    writer.append("\n\n");

    return writer;
  }
}
