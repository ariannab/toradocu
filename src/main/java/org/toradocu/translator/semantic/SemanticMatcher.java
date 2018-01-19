package org.toradocu.translator.semantic;

import edu.stanford.nlp.ling.CoreLabel;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.toradocu.extractor.DocumentedExecutable;
import org.toradocu.translator.*;

/**
 * Created by arianna on 29/05/17.
 *
 * <p>Main component. Contains all the methods to compute the {@code SemantichMatch}es for a given
 * class. This implements the "basic" semantic semantic, i.e. the one that uses plain vector sums.
 * Other kinds of matcher will extend this class.
 */
public class SemanticMatcher {

  //  private static SemanticMatcher instance = null;

  private static boolean enabled;
  private final ArrayList stopwords;

  //  private boolean stopWordsRemoval;
  //  private float distanceThreshold;
  private float wmdThreshold;

  //  private static GloveRandomAccessReader gloveDB;

  public SemanticMatcher(boolean stopWordsRemoval, float distanceThreshold, float wmdThreshold) {
    //    this.stopWordsRemoval = stopWordsRemoval;
    //    this.distanceThreshold = distanceThreshold;
    this.wmdThreshold = wmdThreshold;

    //TODO can this naive list be improved?
    stopwords =
        new ArrayList<>(
            Arrays.asList(
                "true",
                "false",
                "the",
                "a",
                "if",
                "either",
                "whether",
                "else",
                "otherwise",
                "for",
                "be",
                "have",
                "this",
                "do",
                "not",
                "of",
                "can",
                "in",
                "null",
                "only",
                "already",
                "specify"));

    //    try {
    //      gloveDB = GloveBinModelWrapper.getInstance().getGloveBinaryReader();
    //    } catch (URISyntaxException e) {
    //      e.printStackTrace();
    //      gloveDB = null;
    //    }
  }

  public static boolean isEnabled() {
    return enabled;
  }

  public static void setEnabled(boolean enabled) {
    SemanticMatcher.enabled = enabled;
  }

  /**
   * Entry point to run the semantic matching through vector sums. Takes the list of candidates
   * involved in the matching, the method for which calculating the matching, the comment to match
   * and the corresponding proposition. Returns the best matches.
   *
   * @param codeElements list of potentially candidate {@code CodeElement}s
   * @param method the method which the comment to match belongs
   * @param subject the subject {@code CodeElement}
   * @param proposition the {@code Proposition} extracted from the comment
   * @param comment comment text @return a map containing the best matches together with the
   *     distance computed in respect to the comment
   * @return the best matches
   * @throws IOException if there were problems reading the vector model
   */
  public LinkedHashMap<CodeElement<?>, Double> runVectorMatch(
      List<CodeElement<?>> codeElements,
      DocumentedExecutable method,
      CodeElement<?> subject,
      Proposition proposition,
      String comment)
      throws IOException {

    stopwords.add(method.getDeclaringClass().getSimpleName().toLowerCase());
    return wmdMatch(comment, proposition, subject, method, codeElements);
  }

  //  /**
  //   * Computes semantic distances through GloVe vectors. The vector corresponding to the comment must
  //   * be compared with each vector representing a code element among the candidates. Both comment and
  //   * code elements names must be parsed first (stopwords removal, trailing spaces removal, lowercase
  //   * normalization). The computed distances are stored in a map that will be filtered at the end of
  //   * the process.
  //   *
  //   * @param comment the comment text for which computing the distances
  //   * @param subjectCodeElement the subject {@code CodeElement}
  //   * @param proposition the {@code Proposition} extracted from the comment
  //   * @param method the method which the comment to match belongs
  //   * @param codeElements ist of potentially candidate {@code CodeElement}s
  //   * @return a map containing the best matches together with the distance computed in respect to the
  //   *     comment
  //   * @throws IOException if there were problems reading the vector model
  //   */
  //  private LinkedHashMap<CodeElement<?>, Double> vectorsMatch(
  //      String comment,
  //      CodeElement<?> subjectCodeElement,
  //      Proposition proposition,
  //      DocumentedExecutable method,
  //      List<CodeElement<?>> codeElements)
  //      throws IOException {
  //
  //    String rightSentence = splitInSentences(comment, proposition);
  //    Set<String> commentWordSet = parseComment(rightSentence);
  //
  //    if (commentWordSet.size() > 3) {
  //      // Vectors sum doesn't work well with long comments: use WMD
  //      if (commentWordSet.size() > 7)
  //        //Increase the threshold in case of very long comments
  //        this.wmdThreshold = (float) 6.05;
  //
  //      return wmdMatch(comment, proposition, subjectCodeElement, method, codeElements);
  //    }
  //
  //    String parsedComment = String.join(" ", commentWordSet).replaceAll("\\s+", " ").trim();
  //
  //    DoubleVector originalCommentVector = getCommentVector(commentWordSet);
  //
  //    Map<CodeElement<?>, Double> distances = new LinkedHashMap<>();
  //
  //    String subject = proposition.getSubject().getSubject().toLowerCase();
  //    String wordToIgnore = "";
  //    if (codeElements != null && !codeElements.isEmpty()) {
  //      for (CodeElement<?> codeElement : codeElements) {
  //        //For each code element, compute the corresponding vector and compute the distance
  //        //between it and the comment vector. Store the distances and filter them lately.
  //        if (codeElement instanceof MethodCodeElement
  //            && !((MethodCodeElement) codeElement).getReceiver().equals("target")
  //            && !areComplementary((MethodCodeElement) codeElement, method)) {
  //          // if receiver is not target, this is a method invoked from the subject, which for the reason
  //          // is implicit and will be excluded from the vector computation
  //          if (subject.lastIndexOf(" ") != -1)
  //            // in case of composed subject take just the last word (may be the most significant)
  //            wordToIgnore = subject.substring(subject.lastIndexOf(" ") + 1, subject.length());
  //          else wordToIgnore = subject;
  //
  //          DoubleVector codeElementVector =
  //              getCodeElementVector((MethodCodeElement) codeElement, wordToIgnore);
  //
  //          Set<String> modifiedComment = new LinkedHashSet<String>(commentWordSet);
  //          modifiedComment.remove(wordToIgnore);
  //          DoubleVector modifiedCommentVector = getCommentVector(modifiedComment);
  //
  //          measureAndStoreDistance(modifiedCommentVector, codeElementVector, codeElement, distances);
  //        } else if (codeElement instanceof MethodCodeElement
  //            && ((MethodCodeElement) codeElement).getReceiver().equals("target")
  //            && !areComplementary((MethodCodeElement) codeElement, method)) {
  //          if (proposition.getSubject().isPassive()
  //              || subjectCodeElement.toString().startsWith("target:")) {
  //            // assume it's legit to invoke a method of the target class if the subject is the receiver
  //            // object itself or if the subject was passive (thus the action could be invoked not from,
  //            // but on it, typically as an argument)
  //            DoubleVector methodVector = getCodeElementVector(codeElement, null);
  //            measureAndStoreDistance(originalCommentVector, methodVector, codeElement, distances);
  //          }
  //        }
  //      }
  //      return retainMatches(parsedComment, method.getSignature(), distances);
  //    }
  //    return null;
  //  }

  //  /**
  //   * If the comment is made of more than one sentence, identify the one containing the proposition
  //   * (thus the right one to translate).
  //   *
  //   * @param comment the whole comment
  //   * @param proposition the proposition to translate
  //   * @return sub-sentence containing the proposition
  //   */
  //  private String splitInSentences(String comment, Proposition proposition) {
  //    String rightSentence = comment;
  //
  //    String[] sentences = comment.split("\\.");
  //    for (String sentence : sentences) {
  //      if (sentence.contains(proposition.getPredicate())) rightSentence = sentence;
  //    }
  //    return rightSentence;
  //  }

  //  /**
  //   * Measure the cosine distance between two vectors.
  //   *
  //   * @param commentVector vector representing the comment
  //   * @param codeElementVector the vector representing the code element
  //   * @param codeElement the code element
  //   * @param distances map where to store the code element together with the distance from the
  //   *     comment
  //   */
  //  private void measureAndStoreDistance(
  //      DoubleVector commentVector,
  //      DoubleVector codeElementVector,
  //      CodeElement<?> codeElement,
  //      Map<CodeElement<?>, Double> distances) {
  //    CosineDistance cos = new CosineDistance();
  //    if (codeElementVector != null && commentVector != null) {
  //      double dist = cos.measureDistance(codeElementVector, commentVector);
  //      distances.put(codeElement, dist);
  //    }
  //  }

  /**
   * Returns true if the {@code DocumentedExecutable} is a setter and the possible candidate is the
   * symmetric getter
   *
   * @param candidate the possible candidate {@code MethodCodeElement}
   * @param method the {@code DocumentedExecutable}
   * @return true if candidate and method are respectively the setter and the getter
   */
  private boolean areComplementary(MethodCodeElement candidate, DocumentedExecutable method) {
    String candidateName = candidate.getJavaCodeElement().getName();
    if (candidateName.matches("(.*)get[A-Z](.*)")) {
      String property = candidateName.split("get")[1];
      if (method.getName().equals("set" + property)) {
        return true;
      }
    }

    return false;
  }

  //  /**
  //   * Build the vector representing a code element, made by its name camelCase-splitted
  //   *
  //   * @param codeElement the code element
  //   * @param wordToIgnore word to exclude from the building, if any
  //   * @return a {@code DoubleVector} representing the code element vector
  //   * @throws IOException if the GloVe model couldn't be read
  //   */
  //  private DoubleVector getCodeElementVector(CodeElement<?> codeElement, String wordToIgnore)
  //      throws IOException {
  //    int index;
  //    DoubleVector codeElementVector = null;
  //    String name = "";
  //    if (codeElement instanceof MethodCodeElement)
  //      name = ((MethodCodeElement) codeElement).getJavaCodeElement().getName();
  //    else if (codeElement instanceof GeneralCodeElement)
  //      name = ((GeneralCodeElement) codeElement).getIdentifiers().stream().findFirst().get();
  //    else return null;
  //    ArrayList<String> camelId = new ArrayList<String>(Arrays.asList(name.split("(?<!^)(?=[A-Z])")));
  //    if (camelId.size() > 3) return null;
  //
  //    String joinedId = String.join(" ", camelId).replaceAll("\\s+", " ").toLowerCase().trim();
  //    ArrayList<String> parsedId = new ArrayList<String>(parseComment(joinedId));
  //
  //    if (wordToIgnore != null) parsedId.remove(wordToIgnore);
  //
  //    for (int i = 0; i < parsedId.size(); i++) {
  //      DoubleVector v = gloveDB.get(parsedId.get(i).toLowerCase());
  //      if (this.stopWordsRemoval && this.stopwords.contains(parsedId.get(i).toLowerCase())) continue;
  //      if (v != null) {
  //        if (codeElementVector == null) codeElementVector = v;
  //        else codeElementVector = codeElementVector.add(v);
  //      }
  //    }
  //
  //    return codeElementVector;
  //  }

  //  /**
  //   * Build the vector representing the comment.
  //   *
  //   * @param wordComment the {@code Set<String>} of words componing the comment
  //   * @return a {@code DoubleVector} representing the comment vector
  //   * @throws IOException if the GloVe model couldn't be read
  //   */
  //  private static DoubleVector getCommentVector(Set<String> wordComment) throws IOException {
  //    DoubleVector commentVector = null;
  //    Iterator<String> wordIterator = wordComment.iterator();
  //    while (wordIterator.hasNext()) {
  //      String word = wordIterator.next();
  //      if (word != null) {
  //        DoubleVector v = gloveDB.get(word.toLowerCase());
  //        if (v != null) {
  //          if (commentVector == null) commentVector = v;
  //          else commentVector = commentVector.add(v);
  //        } else return null;
  //      }
  //    }
  //    return commentVector;
  //  }

  /**
   * Parse the original tag comment. Special characters are removed. Then the comment is normalized
   * to lower case and lemmatization is applied. As a last step, stopwords are removed.
   *
   * @return the parsed comment in form of array of strings (words retained from the original
   *     comment)
   */
  private List<String> parseComment(String comment) {
    comment = comment.replaceAll("[^A-Za-z0-9 ]", "").toLowerCase();

    ArrayList<String> wordComment = new ArrayList<String>(Arrays.asList(comment.split(" ")));
    int index = 0;
    List<CoreLabel> lemmas = StanfordParser.lemmatize(comment);
    for (CoreLabel lemma : lemmas) {
      if (lemma != null) {
        if (index < wordComment.size()) {
          wordComment.remove(index);
        }
        wordComment.add(index, lemma.lemma());
      }
      index++;
    }

    return this.removeStopWords(wordComment);
  }

  /**
   * Compute semantic distance through Word Mover's Distance.
   *
   * @param comment String comment
   * @param proposition the proposition extracted from the comment that must be translated
   * @param method the method to which the comment belongs
   * @param codeElements list of code elements for which computing the distance @return a map
   *     containing the best matches together with the distance computed in respect to the comment
   */
  private LinkedHashMap<CodeElement<?>, Double> wmdMatch(
      String comment,
      Proposition proposition,
      CodeElement<?> subjectCodeElement,
      DocumentedExecutable method,
      List<CodeElement<?>> codeElements) {
    Map<CodeElement<?>, Double> distances = new LinkedHashMap<>();
    //    WordMovers wm = null;
    //    try {
    //      wm =
    //          WordMovers.Builder()
    //              .wordVectors(GloveModelWrapper.getInstance().getGloveTxtVectors())
    //              .build();
    //    } catch (URISyntaxException e) {
    //      e.printStackTrace();
    //    }

    //    String subject = proposition.getSubject().getSubject();
    List<String> commentWordSet = parseComment(comment);
    if (codeElements != null && !codeElements.isEmpty()) {
      for (CodeElement<?> codeElement : codeElements) {
        //For each code element, compute the corresponding vector and compute the distance
        //between it and the comment vector. Store the distances and filter them lately.
        String name;
        if (codeElement instanceof MethodCodeElement) {
          name = ((MethodCodeElement) codeElement).getJavaCodeElement().getName();
        } else if (codeElement instanceof GeneralCodeElement) {
          name = codeElement.getIdentifiers().stream().findFirst().get();
        } else {
          continue;
        }
        double dist = 10;
        List<String> camelId = parseCodeElementName(name);
        List<String> codeElementWordSet = removeStopWords(camelId);
        //        Set<String> codeElementWordSet = new HashSet<>(camelId);

        String parsedComment =
            String.join(" ", commentWordSet).replaceAll("\\s+", " ").trim().toLowerCase();
        String parsedCodeElement =
            String.join(" ", codeElementWordSet).replaceAll("\\s+", " ").trim().toLowerCase();
        if (codeElement instanceof MethodCodeElement
            && !((MethodCodeElement) codeElement).getReceiver().equals("target")
            && !areComplementary((MethodCodeElement) codeElement, method)) {
          try {
            //            dist = wm.distance(String.join(" ", commentWordSet).replaceAll("\\s+", " ").trim().toLowerCase(),
            //                    String.join(" ", codeElementWordSet).replaceAll("\\s+", " ").trim().toLowerCase());

            dist = WordMoversDistance.distance(parsedComment, parsedCodeElement);
          } catch (Exception e) {
            //do nothing
          }
          distances.put(codeElement, dist);
        } else if (codeElement instanceof MethodCodeElement
            && ((MethodCodeElement) codeElement).getReceiver().equals("target")
            && !areComplementary((MethodCodeElement) codeElement, method)) {
          if (proposition.getSubject().isPassive()
              || subjectCodeElement.toString().startsWith("target:")) {
            try {
              //              dist = wm.distance(String.join(" ", commentWordSet).replaceAll("\\s+", " ").trim().toLowerCase(),
              //                      String.join(" ", codeElementWordSet).replaceAll("\\s+", " ").trim().toLowerCase());
              dist = WordMoversDistance.distance(parsedComment, parsedCodeElement);
            } catch (Exception e) {
              //do nothing
            }
            distances.put(codeElement, dist);
          }
        }
      }
    }
    return retainMatches(commentWordSet, method.getSignature(), distances);
  }

  /**
   * Split code element name according to camel case
   *
   * @param name code element name
   * @return list of words composing the code element name
   */
  private List<String> parseCodeElementName(String name) {
    ArrayList<String> camelId = new ArrayList<>(Arrays.asList(name.split("(?<!^)(?=[A-Z])")));
    String joinedId = String.join(" ", camelId).replaceAll("\\s+", " ").trim().toLowerCase();
    int index = 0;
    for (CoreLabel lemma : StanfordParser.lemmatize(joinedId)) {
      if (lemma != null) {
        if (index < camelId.size()) {
          camelId.remove(index);
        }
        camelId.add(index, lemma.lemma());
      }
      index++;
    }
    return camelId;
  }

  /**
   * Compute and instantiate the {@code SemantiMatch} computed for a tag.
   *
   * @param methodName name of the method the tag belongs to
   * @param distances the computed distance, for every possible code element candidate, from the
   *     comment
   */
  private LinkedHashMap<CodeElement<?>, Double> retainMatches(
      List<String> commentWords, String methodName, Map<CodeElement<?>, Double> distances) {
    if (commentWords.size() > 8) {
      wmdThreshold = 6;
    }

    // Select as candidates only code elements that have a semantic distance below the chosen threshold.
    LinkedHashMap<CodeElement<?>, Double> orderedDistances;
    System.out.println(commentWords.toString());
    for (Map.Entry<CodeElement<?>, Double> d : distances.entrySet()) {
      System.out.println(d.getKey() + ": " + d.getValue());
    }
    if (!distances.isEmpty()) {
      distances.values().removeIf(aDouble -> aDouble > wmdThreshold);
    }

    // Order the retained distances from the lowest (best one) to the highest (worst one).
    orderedDistances =
        distances
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    return orderedDistances;
  }

  /**
   * Remove stopwords from given list of {@code String}s
   *
   * @param words list of {@code String}s to be cleaned
   * @return the cleaned list of words
   */
  private List<String> removeStopWords(List<String> words) {
    for (int i = 0; i < words.size(); i++) {
      String word = words.get(i).toLowerCase();
      if (this.stopwords.contains(word)) {
        words.remove(i);
        words.add(i, "");
      }
    }

    List<String> wordList = new ArrayList<>(words);
    wordList.removeAll(Collections.singletonList(""));
    return wordList;
  }
}
