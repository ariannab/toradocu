package org.toradocu.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DocumentedMethod represents the documentation for a method in a class. It identifies the method itself
 * and key Javadoc information associated with it, such as throws tags and parameters.
 */
public final class DocumentedMethod {
	
	/** The fully qualified name of the method return type. */ 
	private final String returnType;
	/** The fully qualified name of the method. */
	private final String name;
	/** The simple name of the method. */
	private final String simpleName;
	/** A list of parameters in the method. */
	private final List<Parameter> parameters;
	/** A list of throws tags specified in the method's Javadoc. */
	private final List<ThrowsTag> throwsTags;
	/** The signature of the method (excluding return type). */
	private final String signature;
	/** The class in which the method is contained. */
	private final String containingClass;
	/** True when this method is a constructor. */
	private final boolean isAConstructor;
	
	/**
	 * Constructs a {@code DocumentedMethod} using the information in the provided {@code Builder}.
	 * 
	 * @param builder the {@code Builder} containing information about this {@code DocumentedMethod}
	 */
	private DocumentedMethod(Builder builder) {
		this.name = builder.name;
		this.simpleName = name.substring(name.lastIndexOf(".") + 1);
		this.containingClass = name.substring(0, name.lastIndexOf("."));
		if (this.containingClass.contains(".")) {
			this.isAConstructor = simpleName.equals(this.containingClass.substring(this.containingClass.lastIndexOf(".") + 1));
		} else {
			this.isAConstructor = simpleName.equals(containingClass);
		}
		if (this.isAConstructor) {
			this.returnType = "";
		} else {
			this.returnType = builder.returnType;
		}
		this.parameters = builder.parameters;
		this.throwsTags = builder.throwsTags;
		// Create the method signature using the method name and parameters.
		StringBuilder signature = new StringBuilder(name + "(");
		for (Parameter param : parameters) {
			signature.append(param);
			signature.append(",");
		}
		if (signature.charAt(signature.length() - 1) == ',') { // Remove last comma when needed
			signature.deleteCharAt(signature.length() - 1);
		}
		signature.append(")");
		this.signature = signature.toString();
	}
	
	/**
	 * Returns an unmodifiable list view of the throws tags in this method.
	 * 
	 * @return an unmodifiable list view of the throws tags in this method
	 */
	public List<ThrowsTag> throwsTags() {
		return Collections.unmodifiableList(throwsTags);
	}
	
	/**
	 * Checks whether this method is a regular method or a constructor. 
	 * 
	 * @return {@code true} if this method is a constructor, {@code false} otherwise
	 */
	public boolean isAConstructor() {
		return isAConstructor;
	}
	
	/**
	 * Returns the simple name of this method.
	 * 
	 * @return the simple name of this method
	 */
	public String getSimpleName() {
		return simpleName;
	}

	/**
	 * Returns an unmodifiable list view of the parameters in this method.
	 * 
	 * @return an unmodifiable list view of the parameters in this method
	 */
	public List<Parameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
	
	/**
	 * Returns the signature of this method.
	 * 
	 * @return the signature of this method
	 */
	public String getSignature() {
		return signature;
	}
	
	/**
	 * Returns the return type of this method when this method is not a constructor.
	 * 
	 * @return the return type of this method (empty string if this method is a constructor)
	 */
	public String getReturnType() {
		return returnType;
	}
	
	/**
	 * Returns the fully qualified name of the class in which this method is contained.
	 * 
	 * @return the fully qualified name of the class in which this method is contained
	 */
	public String getContainingClass() {
		return containingClass;
	}
	
	/**
	 * Returns true if this {@code DocumentedMethod} and the specified object are equal.
	 * 
	 * @param obj the object to test for equality
	 * @return true if this object and {@code obj} are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DocumentedMethod)) return false;
		if (this == obj) return true;
		
		DocumentedMethod that = (DocumentedMethod) obj;
		if (this.returnType.equals(that.returnType) &&
			this.name.equals(that.name) &&
			this.parameters.equals(that.parameters) &&
			this.throwsTags.equals(that.throwsTags)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the hash code of this object.
	 * 
	 * @return the hash code of this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(returnType, name, parameters, throwsTags);
	}
	
	/**
	 * Returns the signature of this method.
	 * 
	 * @return the signature of this method
	 */
	@Override
	public String toString() {
		if (returnType.isEmpty()) {
			return signature;
		} else {
			return returnType + " " + signature;
		}
	}
	
	/**
	 * Builds a {@code DocumentedMethod} using the provided information.
	 */
	public static class Builder implements org.apache.commons.lang3.builder.Builder<DocumentedMethod> {

		/** The fully qualified name of the {@code DocumentedMethod} return type. */ 
		private final String returnType;		
		/** The fully qualified name of the {@code DocumentedMethod} to build. */
		private final String name;
		/** The parameters of the {@code DocumentedMethod} to build. */
		private final List<Parameter> parameters;
		/** The throws tags of the {@code DocumentedMethod} to build. */
		private final List<ThrowsTag> throwsTags;
		
		/** Constructs a builder for a {@code DocumentedMethod} with the given {@code name} and {@parameters}.
		 * 
		 * @param name the fully qualified name of the {@code DocumentedMethod} to build
		 * @param parameters the parameters of the {@code DocumentedMethod} to build
		 */
		public Builder(String returnType, String name, Parameter... parameters) {
			Objects.requireNonNull(returnType);
			Objects.requireNonNull(name);
			Objects.requireNonNull(parameters);
			
			if (name.startsWith(".") || name.endsWith(".") || !name.contains(".")) {
			    throw new IllegalArgumentException("Name must be a valid qualified name of a method of the form"
			        + "<package>.<class>.<method name> where <package> is optional.");
			}
			
			this.returnType = returnType;
			this.name = name;
			this.parameters = Arrays.asList(parameters);
			this.throwsTags = new ArrayList<>();
		}
		
		/**
		 * Adds the specified throws tag to the {@code DocumentedMethod} to build.
		 * 
		 * @param tag the throws tag in the {@code DocumentedMethod} to build
		 * @return this {@code Builder}
		 */
		public Builder tag(ThrowsTag tag) {
			if (!throwsTags.contains(tag)) {
				throwsTags.add(tag);
			}
			return this;
		}
		
		/**
		 * Builds and returns a {@code DocumentedMethod} with the information given to this {@code Builder}.
		 * 
		 * @return a {@code DocumentedMethod} containing the information passed to this builder
		 */
		@Override
		public DocumentedMethod build() {
			return new DocumentedMethod(this);
		}
	}
	
}
