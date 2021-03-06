/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.plugin.finagle.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

public class AnnotationInstrumentation {

    private static final String ANNOTATION_CLASS = "com.twitter.finagle.tracing.Annotation$";
    private static final String INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.finagle.AnnotationInterceptor$";

    abstract static class Abstract extends AbstractInstrumentation {

        @Override
        protected ClassMatch enhanceClass() {
            return NameMatch.byName(ANNOTATION_CLASS + getAnnoationName());
        }

        @Override
        public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
            return new ConstructorInterceptPoint[]{
                new ConstructorInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getConstructorMatcher() {
                        return getConstructor();
                    }

                    @Override
                    public String getConstructorInterceptor() {
                        return INTERCEPT_CLASS + getAnnoationName();
                    }
                }
            };
        }

        @Override
        public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
            return new InstanceMethodsInterceptPoint[0];
        }

        protected abstract String getAnnoationName();

        protected abstract ElementMatcher<MethodDescription> getConstructor();
    }

    public static class Rpc extends Abstract {

        @Override
        protected String getAnnoationName() {
            return "Rpc";
        }

        @Override
        protected ElementMatcher<MethodDescription> getConstructor() {
            return takesArgument(0, String.class);
        }
    }
}
