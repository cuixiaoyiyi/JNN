package cn.ac.ios.ann.parse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Type;

import cn.ios.ju.base.Pair;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationBooleanElem;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationDoubleElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationEnumElem;
import soot.tagkit.AnnotationFloatElem;
import soot.tagkit.AnnotationIntElem;
import soot.tagkit.AnnotationLongElem;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;

public abstract class AbstractParser implements IParser {

	protected AnnotationTag annotation = null;

	protected String annotationType = null;

	protected List<Pair<String, Object>> parameterConstraints = null;

	protected IParser next = null;

	protected AbstractParser(IParser next) {
		this.next = next;
	}

	@Override
	public void start(AnnotationTag annotation, List<Pair<String, Object>> parameterConstraints) {
		if (annotation == null || parameterConstraints == null) {
			return;
		}
		annotationType = org.objectweb.asm.Type.getType(annotation.getType()).getClassName();
		this.annotation = annotation;
		this.parameterConstraints = parameterConstraints;

		if (isCorrespondingAnnotation()) {
			Pair<String, Object> constraint = getConstraint();
			if (constraint != null) {
				parameterConstraints.add(constraint);
			}
		}

		if (hasNextParser()) {
			next.start(annotation, parameterConstraints);
		}
	}

	@Override
	public boolean hasNextParser() {
		return next != null;
	}

	protected AnnotationTag getAnnotation() {
		return annotation;
	}

	protected boolean isEndWith(String simpleNameLowerCase) {
		return annotationType.toLowerCase().endsWith(simpleNameLowerCase.toLowerCase());
	}

	protected Object getAnnotationValue(String name) {
		return getAnnotationValue(annotation, name);
	}

	protected Object getAnnotationValue(AnnotationTag anno, String name) {
		for (AnnotationElem elem : anno.getElems()) {
			if (name.toLowerCase().equals(elem.getName().toLowerCase())) {
				return getAnnotationElemValue(elem);
			}
		}
		return null;
	}

	protected Object getAnnotationElemValue(AnnotationElem elem) {
		if (elem instanceof AnnotationIntElem) {
			AnnotationIntElem annotationElem = (AnnotationIntElem) elem;
			return annotationElem.getValue();
		} else if (elem instanceof AnnotationBooleanElem) {
			AnnotationBooleanElem annotationElem = (AnnotationBooleanElem) elem;
			return annotationElem.getValue();
		} else if (elem instanceof AnnotationClassElem) {
			AnnotationClassElem annotationElem = (AnnotationClassElem) elem;
			return Type.getType(annotationElem.getDesc()).getClassName();
		} else if (elem instanceof AnnotationDoubleElem) {
			AnnotationDoubleElem annotationElem = (AnnotationDoubleElem) elem;
			return annotationElem.getValue();
		} else if (elem instanceof AnnotationEnumElem) {
			AnnotationEnumElem annotationElem = (AnnotationEnumElem) elem;
			return annotationElem.getTypeName() + "." + annotationElem.getConstantName();
		} else if (elem instanceof AnnotationFloatElem) {
			AnnotationFloatElem annotationElem = (AnnotationFloatElem) elem;
			return annotationElem.getValue();
		} else if (elem instanceof AnnotationLongElem) {
			AnnotationLongElem annotationElem = (AnnotationLongElem) elem;
			return annotationElem.getValue();
		} else if (elem instanceof AnnotationStringElem) {
			AnnotationStringElem annotationElem = (AnnotationStringElem) elem;
			return annotationElem.getValue();
		} else if (elem instanceof AnnotationArrayElem) {
			AnnotationArrayElem arrayElem = (AnnotationArrayElem) elem;
			Set<Object> objects = new HashSet<>();
			for (AnnotationElem subElem : arrayElem.getValues()) {
				Object object = getAnnotationElemValue(subElem);
				if (object instanceof Set) {
					Set<?> set = (Set<?>) object;
					objects.addAll(set);
				} else {
					objects.add(object);
				}
			}
			return objects;
		}
		return null;
	}

	protected Object getAnnotationValue() {
		return getAnnotationValue("value");
	}

	protected abstract Pair<String, Object> getConstraint();

	protected abstract boolean isCorrespondingAnnotation();

}
