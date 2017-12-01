package org.toradocu.util;

/** Singleton to represent a single match to log. */
public class MatchToLog {

  private static MatchToLog instance = null;

  /** Qualified class name of the CUT */
  private String className;

  /** Executable member's signature */
  private String methodName;

  /** The comment text this match aims to translate */
  private String commentText;

  /** The subject to match from the comment text */
  private String subjectMatch;

  /** The predicate to match from the comment text */
  private String predicateMatch;

  /** Quantity of candidates that could satisfy the subject matching */
  private int numberOfSubjectCandidates;

  /** Quantity of candidates that could satisfy the predicate matching */
  private int numberOfPredicateCandidates;

  /**
   * Tells whether we expected a translation for this comment or not (i.e. if a matching was really
   * needed)
   */
  private boolean matchExpected = false;

  /** Tells whether our match was the expected one */
  private boolean success = false;

  private MatchToLog() {}

  /**
   * Returns the singleton.
   *
   * @return the {@code instance}
   */
  public static MatchToLog getInstance() {
    if (instance == null) {
      instance = new MatchToLog();
    }
    return instance;
  }

  /** Set the instance to null */
  public static void destroy() {
    instance = null;
  }

  /**
   * Getter for the class name
   *
   * @return the {@code className}
   */
  public String getClassName() {
    return className;
  }

  /**
   * Getter for the method signature
   *
   * @return the {@code methodSignature}
   */
  public String getMethodName() {
    return methodName;
  }

  /**
   * Getter for the comment text
   *
   * @return the {@code commentText}
   */
  public String getCommentText() {
    return commentText;
  }

  /**
   * Getter for the subject to match
   *
   * @return the {@code subjectMatch}
   */
  public String getSubjectMatch() {
    return subjectMatch;
  }

  /**
   * Getter for the predicate to match
   *
   * @return the {@code predicateMatch}
   */
  public String getPredicateMatch() {
    return predicateMatch;
  }

  /**
   * Getter for the number of possible subject matches
   *
   * @return the {@code numberOfSubjectCandidates}
   */
  public int getNumberOfSubjectCandidates() {
    return numberOfSubjectCandidates;
  }

  /**
   * Getter for the number of possible predicate matches
   *
   * @return the {@code numberOfPredicateCandidates}
   */
  public int getNumberOfPredicateCandidates() {
    return numberOfPredicateCandidates;
  }

  public void setPredicateMatch(String predicateMatch) {
    this.predicateMatch = predicateMatch;
  }

  public void setNumberOfPredicateCandidates(int numberOfPredicateCandidates) {
    this.numberOfPredicateCandidates = numberOfPredicateCandidates;
  }

  /**
   * Setter for the field {@code commentText}
   *
   * @param commentText the {@code String} comment text this match refers to
   */
  public void setCommentText(String commentText) {
    this.commentText = commentText;
  }

  /**
   * Setter for the field {@code methodName}
   *
   * @param methodName the {@code String} method name this match refers to
   */
  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  /**
   * Setter for the field {@code className}
   *
   * @param className the {@code String} qualified class name this match refers to
   */
  public void setClassName(String className) {
    this.className = className;
  }

  /**
   * Setter for the field {@code subjectMatch}
   *
   * @param subjectMatch the {@code String} subject match this match refers to
   */
  public void setSubjectMatch(String subjectMatch) {
    this.subjectMatch = subjectMatch;
  }

  /**
   * Setter for the field {@code numberOfSubjectCandidates}
   *
   * @param numberOfSubjectCandidates the {@code int} count of possible candidates for this match
   */
  public void setNumberOfSubjectCandidates(int numberOfSubjectCandidates) {
    this.numberOfSubjectCandidates = numberOfSubjectCandidates;
  }

  /**
   * Setter for the field {@code matchExpected}
   *
   * @param matchExpected boolean value that must be true iff a translation for this match is
   *     expected
   */
  public void setMatchExpected(boolean matchExpected) {
    this.matchExpected = matchExpected;
  }

  /**
   * Setter for the field {@code success}
   *
   * @param success boolean value that must be true iff the correct translation for this match was
   *     produced
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * Generates an ID for this {@code MatchToLog} by composing the class name, the method name and
   * the comment text, separated with a dot
   *
   * @return the {@code String} generated ID for this matc
   */
  public String generateID() {
    return className + "." + methodName + "." + commentText;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MatchToLog that = (MatchToLog) o;

    if (numberOfSubjectCandidates != that.numberOfSubjectCandidates) return false;
    if (numberOfPredicateCandidates != that.numberOfPredicateCandidates) return false;
    if (matchExpected != that.matchExpected) return false;
    if (success != that.success) return false;
    if (className != null ? !className.equals(that.className) : that.className != null)
      return false;
    if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null)
      return false;
    if (commentText != null ? !commentText.equals(that.commentText) : that.commentText != null)
      return false;
    if (subjectMatch != null ? !subjectMatch.equals(that.subjectMatch) : that.subjectMatch != null)
      return false;
    return predicateMatch != null
        ? predicateMatch.equals(that.predicateMatch)
        : that.predicateMatch == null;
  }

  @Override
  public int hashCode() {
    int result = className != null ? className.hashCode() : 0;
    result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
    result = 31 * result + (commentText != null ? commentText.hashCode() : 0);
    result = 31 * result + (subjectMatch != null ? subjectMatch.hashCode() : 0);
    result = 31 * result + (predicateMatch != null ? predicateMatch.hashCode() : 0);
    result = 31 * result + numberOfSubjectCandidates;
    result = 31 * result + numberOfPredicateCandidates;
    result = 31 * result + (matchExpected ? 1 : 0);
    result = 31 * result + (success ? 1 : 0);
    return result;
  }

  public boolean isMatchExpected() {
    return matchExpected;
  }

  public boolean isSuccess() {
    return success;
  }
}
