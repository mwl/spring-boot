/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.boot.actuate.autoconfigure.metrics.export.humio;

import java.util.Map;

import io.micrometer.core.instrument.Clock;
import io.micrometer.humio.HumioConfig;
import io.micrometer.humio.HumioMeterRegistry;
import org.junit.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link HumioMetricsExportAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class HumioMetricsExportAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(
					AutoConfigurations.of(HumioMetricsExportAutoConfiguration.class));

	@Test
	public void backsOffWithoutAClock() {
		this.contextRunner.run((context) -> assertThat(context)
				.doesNotHaveBean(HumioMeterRegistry.class));
	}

	@Test
	public void autoConfiguresConfigAndMeterRegistry() {
		this.contextRunner.withUserConfiguration(BaseConfiguration.class)
				.run((context) -> assertThat(context)
						.hasSingleBean(HumioMeterRegistry.class)
						.hasSingleBean(HumioConfig.class));
	}

	@Test
	public void autoConfigurationCanBeDisabled() {
		this.contextRunner.withUserConfiguration(BaseConfiguration.class)
				.withPropertyValues("management.metrics.export.humio.enabled=false")
				.run((context) -> assertThat(context)
						.doesNotHaveBean(HumioMeterRegistry.class)
						.doesNotHaveBean(HumioConfig.class));
	}

	@Test
	public void allowsCustomConfigToBeUsed() {
		this.contextRunner.withUserConfiguration(CustomConfigConfiguration.class)
				.run((context) -> assertThat(context)
						.hasSingleBean(HumioMeterRegistry.class)
						.hasSingleBean(HumioConfig.class).hasBean("customConfig"));
	}

	@Test
	public void allowsCustomRegistryToBeUsed() {
		this.contextRunner.withUserConfiguration(CustomRegistryConfiguration.class)

				.run((context) -> assertThat(context)
						.hasSingleBean(HumioMeterRegistry.class).hasBean("customRegistry")
						.hasSingleBean(HumioConfig.class));
	}

	@Test
	public void stopsMeterRegistryWhenContextIsClosed() {
		this.contextRunner.withUserConfiguration(BaseConfiguration.class)
				.run((context) -> {
					HumioMeterRegistry registry = spyOnDisposableBean(
							HumioMeterRegistry.class, context);
					context.close();
					verify(registry).stop();
				});
	}

	@SuppressWarnings("unchecked")
	private <T> T spyOnDisposableBean(Class<T> type,
			AssertableApplicationContext context) {
		String[] names = context.getBeanNamesForType(type);
		assertThat(names).hasSize(1);
		String registryBeanName = names[0];
		Map<String, Object> disposableBeans = (Map<String, Object>) ReflectionTestUtils
				.getField(context.getAutowireCapableBeanFactory(), "disposableBeans");
		Object registryAdapter = disposableBeans.get(registryBeanName);
		T registry = (T) spy(ReflectionTestUtils.getField(registryAdapter, "bean"));
		ReflectionTestUtils.setField(registryAdapter, "bean", registry);
		return registry;
	}

	@Configuration
	static class BaseConfiguration {

		@Bean
		public Clock clock() {
			return Clock.SYSTEM;
		}

	}

	@Configuration
	@Import(BaseConfiguration.class)
	static class CustomConfigConfiguration {

		@Bean
		public HumioConfig customConfig() {
			return (k) -> null;
		}

	}

	@Configuration
	@Import(BaseConfiguration.class)
	static class CustomRegistryConfiguration {

		@Bean
		public HumioMeterRegistry customRegistry(HumioConfig config, Clock clock) {
			return new HumioMeterRegistry(config, clock);
		}

	}

}
