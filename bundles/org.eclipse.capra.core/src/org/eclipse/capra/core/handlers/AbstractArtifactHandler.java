/*******************************************************************************
 * Copyright (c) 2016, 2019 Chalmers | University of Gothenburg, rt-labs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *      Chalmers | University of Gothenburg and rt-labs - initial API and implementation and/or initial documentation
 *      Chalmers | University of Gothenburg - additional features, updated API
 *******************************************************************************/
package org.eclipse.capra.core.handlers;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class AbstractArtifactHandler<T> implements IArtifactHandler<T> {

	private Object unpack(Object artifact) {
		Object result = artifact;
		if (IArtifactUnpacker.class.isAssignableFrom(this.getClass())) {
			try {
				result = IArtifactUnpacker.class.cast(this).unpack(artifact);
			} catch (ClassCastException ex) {
				/*
				 * Deliberately do nothing. This is not uncommon when trying to
				 * find the right handler. In such cases, canHandleArtifact() is
				 * called on all handlers, but for most the artifact can not be
				 * casted.
				 */
			}
		}
		return result;
	}

	@Override
	public <R> Optional<R> withCastedHandler(Object artifact, BiFunction<IArtifactHandler<T>, T, R> handleFunction) {
		Object unpackedArtifact = unpack(artifact);
		if (canHandleArtifact(unpackedArtifact)) {
			@SuppressWarnings("unchecked")
			T a = (T) unpackedArtifact;
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
		Object unpackedArtifact = unpack(artifact);
		try {
			Class<?> genericType = ((Class<?>) ((ParameterizedType) this.getClass().getGenericSuperclass())
					.getActualTypeArguments()[0]);

			return genericType.isAssignableFrom(unpackedArtifact.getClass());
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
