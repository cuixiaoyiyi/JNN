package cn.ac.ios.ann;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ac.ios.ann.parse.bool.BoolParserFactory;
import cn.ac.ios.ann.parse.number.NumberParserFactory;
import cn.ac.ios.ann.parse.object.ObjectParserFactory;
import cn.ac.ios.ann.parse.set.SetParserFactory;
import cn.ac.ios.ann.parse.string.StringParserFactory;
import cn.ios.ju.base.Pair;
import cn.ios.ju.base.Util;
import soot.ArrayType;
import soot.BooleanType;
import soot.PrimType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.tagkit.AbstractHost;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.tagkit.VisibilityParameterAnnotationTag;

public class AAPI {

	private static final String BigDecimal = "java.math.BigDecimal";

	private static final Map<SootMethod, Map<Integer, List<Pair<String, Object>>>> paraConstraints = new HashMap<>();
	private static final Map<SootMethod, List<Pair<String, Object>>> retConstraints = new HashMap<>();
	private static final Map<SootClass, Map<SootField, List<Pair<String, Object>>>> fieldConstraints = new HashMap<>();

	public static Map<SootMethod, Map<Integer, List<Pair<String, Object>>>> getConstraints(
			Set<SootClass> projectClasses) {
		if (projectClasses != null) {
			for (SootClass clazz : projectClasses) {
				try {
					if (!Util.isIgnoredClass(clazz)) {
						processField(clazz);
						for (SootMethod method : clazz.getMethods()) {
							Map<Integer, List<Pair<String, Object>>> methodConstraints = new HashMap<>();
							processAssert(method, retConstraints);
							processMethod(method, methodConstraints);
							if (!methodConstraints.isEmpty()) {
								paraConstraints.put(method, methodConstraints);
							}
						}
					}

				} catch (Exception | Error e) {
					System.out.println("Exception in getConstraints");
				}
			}
		}
		return paraConstraints;
	}

	public static Map<SootClass, Map<SootField, List<Pair<String, Object>>>> getFieldConstraints() {
		return fieldConstraints;
	}

	public static Map<SootMethod, List<Pair<String, Object>>> getRetConstraints() {
		return retConstraints;
	}

	private static void processField(SootClass sootClass) {
		if (fieldConstraints.containsKey(sootClass)) {
			return;
		}
		Map<SootField, List<Pair<String, Object>>> fieldConstraintsInObject = new HashMap<>();
		try {

			for (SootField sootField : sootClass.getFields()) {
				Type fieldType = sootField.getType();
				Set<AnnotationTag> fieldAnnotations = getFieldAnnotations(sootField);
				List<Pair<String, Object>> eachFieldConstraints = new ArrayList<>();
				processParas(fieldType, fieldAnnotations, eachFieldConstraints);
				if (!eachFieldConstraints.isEmpty()) {
					fieldConstraintsInObject.put(sootField, eachFieldConstraints);
				}
			}
			if (!fieldConstraintsInObject.isEmpty()) {
				fieldConstraints.put(sootClass, fieldConstraintsInObject);
			}
		} catch (Exception | Error e) {
			System.out.println("Exception in processField");
		}

	}

	private static Set<AnnotationTag> getFieldAnnotations(SootField sootField) {
		return getAnnotationTags(sootField);
	}

	private static void processMethod(SootMethod method, Map<Integer, List<Pair<String, Object>>> methodConstraints) {
		int parameterCount = method.getParameterCount();
		if (parameterCount > 0) {
			for (int paraIndex = 0; paraIndex < parameterCount; paraIndex++) {
				Set<AnnotationTag> annotations = getParaAnnotations(method, paraIndex);
				List<Pair<String, Object>> parameterConstraints = new ArrayList<>();
				Type type = method.getParameterType(paraIndex);
				processParas(type, annotations, parameterConstraints);
				if (!parameterConstraints.isEmpty()) {
					methodConstraints.put(paraIndex, parameterConstraints);
				}
			}
		}

	}

	public static Set<AnnotationTag> getAnnotationTags(AbstractHost host) {
		Set<AnnotationTag> tags = new HashSet<>();
		for (Tag tag : host.getTags()) {
			if (tag instanceof VisibilityAnnotationTag) {
				VisibilityAnnotationTag visibilityAnnotationTag = (VisibilityAnnotationTag) tag;
				ArrayList<AnnotationTag> annotations = visibilityAnnotationTag.getAnnotations();
				tags.addAll(annotations);
			}
		}
		return tags;
	}

	private static void processParas(Type parameterType, Set<AnnotationTag> annotations,
			List<Pair<String, Object>> parameterConstraints) {
		boolean isNumericalType = isNumberType(parameterType);
		boolean isBoolean = isBooleanType(parameterType);
		boolean isString = isStringType(parameterType);
		boolean isArray = isSet(parameterType);

		for (AnnotationTag annotation : annotations) {
			// unroll the AnnotationTags in AnnotationTag
			Set<AnnotationTag> annotationTags = geAnnotationTagsByAnnotationTag(annotation);
			for (AnnotationTag annotationTag : annotationTags) {
				if (isNumericalType) {
					numberConstraints(annotationTag, parameterConstraints);
				} else if (isBoolean) {
					booleanConstraints(annotationTag, parameterConstraints);
				} else if (isString) {
					stringConstraints(annotationTag, parameterConstraints);
				} else if (isArray) {
					setConstrints(annotationTag, parameterConstraints);
				} else {
					objectConstraints(annotationTag, parameterConstraints);
				}
			}

		}
	}

	private static Set<AnnotationTag> geAnnotationTagsByAnnotationTag(AnnotationTag annotationTag) {
		Set<AnnotationTag> annotationTags = new HashSet<>();
		annotationTags.add(annotationTag);
		for (AnnotationElem elem : annotationTag.getElems()) {
			annotationTags.addAll(geAnnotationTagsByAnnotationElem(elem));
			if(elem instanceof AnnotationArrayElem) {
				AnnotationArrayElem annotationElem = (AnnotationArrayElem) elem;
				for (AnnotationElem subElem : annotationElem.getValues()) {
					annotationTags.addAll(geAnnotationTagsByAnnotationElem(subElem));
				}
				
			}
		}
		return annotationTags;
	}

	private static Set<AnnotationTag> geAnnotationTagsByAnnotationElem(AnnotationElem elem) {
		Set<AnnotationTag> annotationTags = new HashSet<>();
		if (elem instanceof AnnotationAnnotationElem) {
			AnnotationAnnotationElem annotationElem = (AnnotationAnnotationElem) elem;
			annotationTags.addAll(geAnnotationTagsByAnnotationTag(annotationElem.getValue()));
		}
		return annotationTags;
	}

	private static void objectConstraints(AnnotationTag annotation, List<Pair<String, Object>> parameterConstraints) {
		ObjectParserFactory.start(annotation, parameterConstraints);
	}

	private static void setConstrints(AnnotationTag annotation, List<Pair<String, Object>> parameterConstraints) {
		SetParserFactory.start(annotation, parameterConstraints);
	}

	private static void stringConstraints(AnnotationTag annotation, List<Pair<String, Object>> parameterConstraints) {
		StringParserFactory.start(annotation, parameterConstraints);

	}

	private static void booleanConstraints(AnnotationTag annotation, List<Pair<String, Object>> parameterConstraints) {
		BoolParserFactory.start(annotation, parameterConstraints);

	}

	private static void numberConstraints(AnnotationTag annotation, List<Pair<String, Object>> parameterConstraints) {
		NumberParserFactory.start(annotation, parameterConstraints);

	}

	public static boolean isNumberType(Type type) {
		return type instanceof PrimType || type.toString().equals(BigDecimal);
	}

	public static boolean isBooleanType(Type type) {
		return type instanceof BooleanType;
	}

	public static boolean isStringType(Type type) {
		return "java.lang.String".equals(type.toString());
	}

	public static boolean isSet(Type type) {
		// todo map / list
		// || InheritanceUtil.isInheritedFromGivenClass(type, Collection.class)
		// || InheritanceUtil.isInheritedFromGivenClass(type, Map.class)
		return type instanceof ArrayType;
	}

	public static boolean isObject(Type type) {
		return !(isSet(type) || isStringType(type) || isBooleanType(type) || isNumberType(type));
	}

	// deal annotation for return
	private static void processAssert(SootMethod method, Map<SootMethod, List<Pair<String, Object>>> retConstraints) {

		Type returnType = method.getReturnType();
		Set<AnnotationTag> annotations = getMethodReturnAnnotations(method);
		List<Pair<String, Object>> paraConstraints = new ArrayList<>();
		processParas(returnType, annotations, paraConstraints);
		if (!paraConstraints.isEmpty()) {
			retConstraints.put(method, paraConstraints);
		}
	}

	private static Set<AnnotationTag> getMethodReturnAnnotations(SootMethod method) {
		return getAnnotationTags(method);
	}

	public static Set<AnnotationTag> getParaAnnotations(SootMethod method, int paraIndex) {
		Set<AnnotationTag> tags = new HashSet<>();
		for (Tag tag : method.getTags()) {
			if (tag instanceof VisibilityParameterAnnotationTag) {
				VisibilityParameterAnnotationTag parameterAnnotationTag = (VisibilityParameterAnnotationTag) tag;
				ArrayList<VisibilityAnnotationTag> visibilityAnnotations = parameterAnnotationTag
						.getVisibilityAnnotations();
				VisibilityAnnotationTag visibilityAnnotationTag = visibilityAnnotations.get(paraIndex);
				if (visibilityAnnotationTag != null) {
					tags.addAll(visibilityAnnotationTag.getAnnotations());
				}
			}
		}
		return tags;
	}
}
