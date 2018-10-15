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

import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link ConfigurationProperties} for configuring Humio metrics export.
 *
 * @author Martin Westergaard Lassen
 * @since 2.0.0
 */
@ConfigurationProperties(prefix = "management.metrics.export.humio")
public class HumioProperties extends StepRegistryProperties {

	/**
	 * Humio API token.
	 */
	private String apiToken;

	/**
	 * The repository to ship metrics to.
	 */
	private String repository = "sandbox";

	/**
	 * The URI to ship metrics to. If you need to publish metrics to an internal proxy en
	 * route to Humio, you can define the location of the proxy with this.
	 */
	private String uri = "https://cloud.humio.com";

	/**
	 * Humio uses a concept called "tags" to decide which datasource to store metrics in.
	 * This concept is distinct from Micrometer's notion of tags, which divides a metric
	 * along dimensional boundaries. All metrics from this registry will be stored under a
	 * datasource defined by these tags.
	 */
	private Map<String, String> tags = null;

	public String getApiToken() {
		return this.apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}

	public String getRepository() {
		return this.repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public Map<String, String> getTags() {
		return this.tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
