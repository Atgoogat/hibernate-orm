/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.type.descriptor.java;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import org.hibernate.HibernateException;
import org.hibernate.internal.util.compare.ComparableComparator;

/**
 * Abstract adapter for Java type descriptors.
 *
 * @apiNote This abstract descriptor implements BasicJavaType
 * because we currently only categorize "basic" JavaTypes,
 * as in the {@link jakarta.persistence.metamodel.Type.PersistenceType#BASIC}
 * sense
 *
 * @author Steve Ebersole
 */
public abstract class AbstractClassJavaType<T> implements BasicJavaType<T>, Serializable {
	private final Class<T> type;
	private final MutabilityPlan<T> mutabilityPlan;
	private final Comparator<T> comparator;

	/**
	 * Initialize a type descriptor for the given type.  Assumed immutable.
	 *
	 * @param type The Java type.
	 *
	 * @see #AbstractClassJavaType(Class, MutabilityPlan)
	 */
	@SuppressWarnings({ "unchecked" })
	protected AbstractClassJavaType(Class<? extends T> type) {
		this( type, (MutabilityPlan<T>) ImmutableMutabilityPlan.INSTANCE );
	}

	/**
	 * Initialize a type descriptor for the given type and mutability plan.
	 *
	 * @param type The Java type.
	 * @param mutabilityPlan The plan for handling mutability aspects of the java type.
	 */
	@SuppressWarnings({ "unchecked" })
	protected AbstractClassJavaType(Class<? extends T> type, MutabilityPlan<? extends T> mutabilityPlan) {
		this(
				type,
				mutabilityPlan,
				Comparable.class.isAssignableFrom( type )
						? (Comparator<T>) ComparableComparator.INSTANCE
						: null
		);
	}

	/**
	 * Initialize a type descriptor for the given type, mutability plan and comparator.
	 *
	 * @param type The Java type.
	 * @param mutabilityPlan The plan for handling mutability aspects of the java type.
	 * @param comparator The comparator for handling comparison of values
	 */
	@SuppressWarnings("unchecked")
	protected AbstractClassJavaType(
			Class<? extends T> type,
			MutabilityPlan<? extends T> mutabilityPlan,
			Comparator<? extends T> comparator) {
		this.type = (Class<T>) type;
		this.mutabilityPlan = (MutabilityPlan<T>) mutabilityPlan;
		this.comparator = (Comparator<T>) comparator;
	}

	@Override
	public MutabilityPlan<T> getMutabilityPlan() {
		return mutabilityPlan;
	}

	public Class<T> getJavaType() {
		return type;
	}

	@Override
	public Class<T> getJavaTypeClass() {
		return getJavaType();
	}

	@Override
	public int extractHashCode(T value) {
		return value.hashCode();
	}

	@Override
	public boolean areEqual(T one, T another) {
		return Objects.equals( one, another );
	}

	@Override
	public Comparator<T> getComparator() {
		return comparator;
	}

	@Override
	public String extractLoggableRepresentation(T value) {
		return (value == null) ? "null" : value.toString();
	}

	protected HibernateException unknownUnwrap(Class<?> conversionType) {
		return JavaTypeHelper.unknownUnwrap( type, conversionType, this );
	}

	protected HibernateException unknownWrap(Class<?> conversionType) {
		return JavaTypeHelper.unknownWrap( conversionType, type, this );
	}
}
