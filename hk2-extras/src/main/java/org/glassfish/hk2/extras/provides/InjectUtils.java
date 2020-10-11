/*
 * Copyright (c) 2020 TechEmpower. All rights reserved.
 * Copyright (c) 2020 Payara Services Ltd.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.hk2.extras.provides;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.Self;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Unqualified;
import org.glassfish.hk2.api.UnsatisfiedDependencyException;
import org.glassfish.hk2.utilities.InjecteeImpl;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

/**
 * Utility methods for working with {@link ServiceLocator}, {@link
 * ServiceHandle}, {@link Injectee}, and so on.
 */
final class InjectUtils {
  private InjectUtils() {
    throw new AssertionError("This class cannot be instantiated");
  }

  static Injectee injecteeFromParameter(Parameter parameter, Type parentType) {
    Objects.requireNonNull(parameter);
    Objects.requireNonNull(parentType);

    Executable parent = parameter.getDeclaringExecutable();
    int index = Arrays.asList(parent.getParameters()).indexOf(parameter);
    if (index == -1)
      throw new AssertionError(
          "parameter " + parameter + " not found in parent " + parent);

    Type parameterType =
        TypeUtils.resolveType(
            parentType,
            parameter.getParameterizedType());

    InjecteeImpl injectee = new InjecteeImpl(parameterType);
    injectee.setParent(parent);
    injectee.setPosition(index);

    // This block of code reproduces the behavior of
    // org.jvnet.hk2.internal.Utilities#getParamInformation(Annotation[])
    Set<Annotation> qualifiers = new LinkedHashSet<>();
    for (Annotation annotation : parameter.getAnnotations()) {
      if (ReflectionHelper.isAnnotationAQualifier(annotation)) {
        qualifiers.add(annotation);
      } else if (annotation.annotationType() == org.jvnet.hk2.annotations.Optional.class) {
        injectee.setOptional(true);
      } else if (annotation.annotationType() == Self.class) {
        injectee.setSelf(true);
      } else if (annotation.annotationType() == Unqualified.class) {
        injectee.setUnqualified((Unqualified) annotation);
      }
    }
    injectee.setRequiredQualifiers(Collections.unmodifiableSet(qualifiers));

    return injectee;
  }

  static /*@Nullable*/ ServiceHandle<?> serviceHandleFromParameter(
      Parameter parameter,
      Type parentType,
      ServiceLocator locator) {

    Objects.requireNonNull(parameter);
    Objects.requireNonNull(parentType);
    Objects.requireNonNull(locator);

    Injectee injectee = injecteeFromParameter(parameter, parentType);

    ActiveDescriptor<?> activeDescriptor =
        locator.getInjecteeDescriptor(injectee);

    if (activeDescriptor == null) {
      if (!injectee.isOptional())
        throw new UnsatisfiedDependencyException(injectee, locator.getName());

      return null;
    }

    return locator.getServiceHandle(activeDescriptor, injectee);
  }

  static /*@Nullable*/ Object serviceFromParameter(
      Parameter parameter,
      Type parentType,
      /*@Nullable*/ ServiceHandle<?> root,
      ServiceLocator locator) {

    Objects.requireNonNull(parameter);
    Objects.requireNonNull(parentType);
    Objects.requireNonNull(locator);

    Injectee injectee = injecteeFromParameter(parameter, parentType);

    ActiveDescriptor<?> activeDescriptor =
        locator.getInjecteeDescriptor(injectee);

    if (activeDescriptor == null) {
      if (!injectee.isOptional())
        throw new UnsatisfiedDependencyException(injectee, locator.getName());

      return null;
    }

    return locator.getService(activeDescriptor, root, injectee);
  }
}
