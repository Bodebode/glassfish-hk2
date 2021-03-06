/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.classmodel.reflect;

import java.util.Map;

/**
 * Models an annotation instance
 *
 * @author Jerome Dochez
 */
public interface AnnotationModel {

    /**
     * Returns the annotation type for this model
     * @return the annotation type
     */
    AnnotationType getType();

    /**
     * Returns the annotated element with this annotation instance
     * @return the annotated element
     */
    AnnotatedElement getElement();

    /**
     * Returns an unmodifiable collection of annotation values.
     *
     * @return collection of value elements of this annotation
     */
    Map<String, Object> getValues();

    /**
     * Returns the value to which the key is mapped or specified default value if
     * mapping not found
     *
     * @param key the key whose associated value is to be returned
     * @param type value type
     * @param defaultValue the default mapping of the key
     * @return the annotation value.
     */
    <T> T getValue(String key, Class<T> type, Object defaultValue);

    /**
     * Returns the value to which the key is mapped or annotation default value if
     * mapping not found
     *
     * @param key the key whose associated value is to be returned
     * @param type value type
     * @return the annotation value.
     */
    <T> T getValue(String key, Class<T> type);

}
