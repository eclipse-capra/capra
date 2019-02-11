/*******************************************************************************
 *  Copyright (c) 2016 Chalmers | University of Gothenburg, rt-labs and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Chalmers|Gothenburg University and rt-labs - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.capra.testsuite;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit rule that allows the JUnit tests to re-run for a specified amount of
 * times in case of failure.
 * 
 * @author Dusan Kalanj
 *
 */
public class TestRetry implements TestRule {
	private int retryCount;

	/**
	 * A constructor that specifies the amount of times that a JUnit test is
	 * re-run in case of failure.
	 * 
	 * @param retryCount
	 *            the amount of retries
	 */
	public TestRetry(int retryCount) {
		this.retryCount = retryCount;
	}

	@Override
	public Statement apply(Statement base, Description description) {
		return modifyStatement(base, description);
	}

	private Statement modifyStatement(Statement base, Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				Throwable caughtThrowable = null;

				for (int i = 0; i < retryCount; i++)
					try {
						base.evaluate();
						return;
					} catch (Throwable t) {
						caughtThrowable = t;
						System.err.println(description.getDisplayName() + ": run " + (i + 1) + " failed");
					}

				System.err.println(description.getDisplayName() + ": giving up after " + retryCount + " failures");
				throw caughtThrowable;
			}
		};
	}
}
