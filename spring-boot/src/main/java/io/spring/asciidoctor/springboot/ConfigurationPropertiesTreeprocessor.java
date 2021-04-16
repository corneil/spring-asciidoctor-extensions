/*
 * Copyright 2014-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.asciidoctor.springboot;

import java.util.List;

import org.asciidoctor.ast.DescriptionListEntry;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Treeprocessor;

/**
 * {@link Treeprocessor} that validates configuration properties found in structural nodes
 * with the {@code configprops} attribute.
 *
 * @author Andy Wilkinson
 */
class ConfigurationPropertiesTreeprocessor extends Treeprocessor {

	private final ConfigurationPropertyValidator validator;

	ConfigurationPropertiesTreeprocessor(Logger logger) {
		this.validator = new ConfigurationPropertyValidator(logger);
	}

	@Override
	public Document process(Document document) {
		process((StructuralNode) document);
		return document;
	}

	private void process(StructuralNode structuralNode) {
		if (hasConfigpropsAttribute(structuralNode)) {
			this.validator.validateProperties(structuralNode.getContent(),
					(String) structuralNode.getAttribute("language"));
		}
		List<StructuralNode> children = getChildren(structuralNode);
		if (children != null) {
			for (StructuralNode child : children) {
				process(child);
			}
		}
	}

	private boolean hasConfigpropsAttribute(StructuralNode structuralNode) {
		if (structuralNode instanceof DescriptionListEntry) {
			return false;
		}
		return structuralNode.getAttributes().containsValue("configprops");
	}

	private List<StructuralNode> getChildren(StructuralNode structuralNode) {
		if (structuralNode instanceof DescriptionListEntry) {
			return ((DescriptionListEntry) structuralNode).getDescription().getBlocks();
		}
		return structuralNode.getBlocks();
	}

}
