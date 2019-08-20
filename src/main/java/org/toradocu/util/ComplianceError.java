package org.toradocu.util;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.toradocu.extractor.CodeSnippet;
import org.toradocu.extractor.DocumentedExecutable;

public class ComplianceError {
  private static final String MISSING_SYMBOL_PATTERN = "message=cannot find symbol";
  private static final String EXCEPTION_PATTERN = "message=unreported exception";
  private static final String INCOMPARABLE_TYPES = "message=incomparable types";
  private static final String CANNOT_APPLY_TYPE = "cannot be applied to given types;";
  private static final String SYMBOL_TOKEN = "symbol:";
  private static final String METHOD_TOKEN = "method ";
  private boolean errorSolved;
  private List<String> missingSymbols;
  private String unreportedException;
  private boolean incompatibleTypes;

  // private Map<String, List<String>> incompatibleArgs = new HashMap<>();
  private List<String> swappableMethods;

  public ComplianceError() {
    this.missingSymbols = new ArrayList<>();
    this.swappableMethods = new ArrayList<>();
    this.errorSolved = false;
    this.unreportedException = "";
    this.incompatibleTypes = false;
  }

  /**
   * Manage missing symbols, cannot apply type
   *
   * @param errorMessage
   * @param method
   * @param snippet
   */
  void chooseErrorStrategy(String errorMessage, DocumentedExecutable method, CodeSnippet snippet) {
    if (errorMessage.contains(MISSING_SYMBOL_PATTERN)) {
      collectMissingSymbols(errorMessage);
    } else if (errorMessage.contains(CANNOT_APPLY_TYPE)) {
      if (mustSwapArgs(errorMessage)) {
        collectMethodWithRightArgs(errorMessage);
      }
    } else if (errorMessage.contains(INCOMPARABLE_TYPES)) {
      // addElementsForClone(method, snippet);
      snippet.setComplexSignatureWithIncompatibleTypes(true);
      this.incompatibleTypes = true;
      this.errorSolved = true;
    }
  }

  /**
   * Manage unreported exception and incompatible types
   *
   * @param errorMessage
   * @param method
   * @param oracle
   */
  void chooseErrorStrategy(String errorMessage, DocumentedExecutable method, String oracle) {
    if (errorMessage.contains(EXCEPTION_PATTERN)) {
      manageUnreportedException(errorMessage);
    } else if (errorMessage.contains(INCOMPARABLE_TYPES)) {
      this.incompatibleTypes = true;
    }
  }

  private void manageUnreportedException(String errorMessage) {
    int startOfPattern = errorMessage.indexOf(ComplianceError.EXCEPTION_PATTERN);
    if (startOfPattern != -1) {
      this.unreportedException =
          errorMessage
              .substring(
                  startOfPattern + ComplianceError.EXCEPTION_PATTERN.length(),
                  errorMessage.indexOf(";"))
              .trim();
    }
  }

  //    private void addElementsForClone(DocumentedExecutable method, CodeSnippet snippet) {
  //        String newOracle = FreeTextTranslator.manageVoidAndUncompatibleMethods(method,
  // snippet.getSnippet());
  //        snippet.substitutePart(snippet.getSnippet(), newOracle);
  //        this.errorSolved = true;
  //    }

  private void collectMethodWithRightArgs(String errorMessage) {
    if (errorMessage.contains(METHOD_TOKEN)) {
      String methodName =
          errorMessage.substring(errorMessage.indexOf(METHOD_TOKEN) + METHOD_TOKEN.length());
      methodName = methodName.substring(0, methodName.indexOf(" "));
      swappableMethods.add(methodName);
      this.errorSolved = true;
    }
  }

  private boolean mustSwapArgs(String errorMessage) {
    String errorMessageStructure = "Unable to compile the source\\n\\[.*\\n(.*)\\n(.*)";
    String methodName = "";
    java.util.regex.Matcher structureMatcher =
        Pattern.compile(errorMessageStructure).matcher(errorMessage);
    if (structureMatcher.find()) {
      String required = structureMatcher.group(1).trim();
      String found = structureMatcher.group(2).trim();
      String symbolsRequired[] = required.replace("required:", "").trim().split(",");
      String symbolsFound[] = found.replace("found:", "").trim().split(",");
      ArrayList<String> symbolsReq = new ArrayList<>(Arrays.asList(symbolsRequired));
      ArrayList<String> symbolsFou = new ArrayList<>(Arrays.asList(symbolsFound));
      List<String> newItemsRe =
          symbolsReq
              .stream()
              .map(o -> ComplianceChecks.isGenericType(o) ? "upperCase" : o)
              .collect(toList());
      List<String> newItemsFou =
          symbolsFou
              .stream()
              .map(o -> ComplianceChecks.isGenericType(o) ? "upperCase" : o)
              .collect(toList());

      for (String r : newItemsRe) {
        if (!newItemsFou.contains(r)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private void collectMissingSymbols(String errorMessage) {
    String[] missingMessages = errorMessage.split(MISSING_SYMBOL_PATTERN);
    for (String message : missingMessages) {
      int startOfPattern = message.indexOf(SYMBOL_TOKEN);
      if (startOfPattern != -1) {
        String missingSymbols = message.substring(startOfPattern + SYMBOL_TOKEN.length());
        String[] messageTokens = missingSymbols.replaceAll("[\n ]+", " ").trim().split(" ");
        this.missingSymbols.add(messageTokens[1]);
        this.errorSolved = true;
      }
    }
  }

  public List<String> getMissingSymbols() {
    return this.missingSymbols;
  }

  public List<String> getSwappableMethods() {
    return swappableMethods;
  }

  public boolean isErrorSolved() {
    return this.errorSolved;
  }

  public String getUnreportedException() {
    return unreportedException;
  }

  public boolean isIncompatibleTypes() {
    return incompatibleTypes;
  }
}
