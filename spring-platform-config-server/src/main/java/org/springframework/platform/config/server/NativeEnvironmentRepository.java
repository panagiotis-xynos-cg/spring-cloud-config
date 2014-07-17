/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.platform.config.server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.platform.config.Environment;
import org.springframework.platform.config.PropertySource;
import org.springframework.web.context.support.StandardServletEnvironment;

/**
 * Simple implementation of {@link EnvironmentRepository} that just reflects an existing
 * Spring Environment.
 * 
 * @author Dave Syer
 *
 */
public class NativeEnvironmentRepository implements EnvironmentRepository {

	private Set<String> standardSources = new HashSet<String>(Arrays.asList(
			"vcap",
			StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME,
			StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
			StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME,
			StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME,
			StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME));

	private ConfigurableEnvironment environment;

	public NativeEnvironmentRepository(ConfigurableEnvironment environment) {
		this.environment = environment;
	}

	@Override
	public Environment findOne(String application, String env, String label) {
		Environment result = new Environment(env, label);
		for (org.springframework.core.env.PropertySource<?> source : environment.getPropertySources()) {
			String name = source.getName();
			if (!standardSources.contains(name) && source instanceof MapPropertySource) {
				result.add(new PropertySource(name, (Map<?, ?>) source.getSource()));
			}
		}
		return result;

	}

}
