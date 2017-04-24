/*******************************************************************************
 * Copyright (c) 2017 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.capra.core.handlers;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class AbstractArtifactHandler<T> implements IArtifactHandler<T> {

	@Override
	public <R> Optional<R> withCastedHandler(Object artifact, BiFunction<IArtifactHandler<T>, T, R> handleFunction) {
		if (canHandleArtifact(artifact)) {
			@SuppressWarnings("unchecked")
			T a = (T) artifact;
			return Optional.of(handleFunction.apply(this, a));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public <R> R withCastedHandlerUnchecked(Object artifact, BiFunction<IArtifactHandler<T>, T, R> handleFunction) {
		return withCastedHandler(artifact, handleFunction).orElseThrow(
			() -> new IllegalArgumentException("withCastedHanderUnchecked called with unhandleble artifact."
				+ " Artifact: " + artifact + ", handler: " + this));
	}

	@Override
	public boolean canHandleArtifact(Object artifact) {
		try {
			Class<?> genericType = ((Class<?>) ((ParameterizedType) this.getClass().getGenericSuperclass())
					.getActualTypeArguments()[0]);

			return genericType.isAssignableFrom(artifact.getClass());
		} catch (NoClassDefFoundError e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getHandledClass() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

}
