/*
 * Copyright 2011 the original author or authors.
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
package org.jenkinsci.gradle.plugins.jpi

import org.gradle.api.Project

import java.util.jar.Attributes
import java.util.jar.Manifest

import static java.util.jar.Attributes.Name.MANIFEST_VERSION

/**
 * Encapsulates the Jenkins plugin manifest and its generation.
 *
 * @author Kohsuke Kawaguchi
 */
class JpiManifest extends Manifest {
    JpiManifest(Project project) {
        def conv = project.extensions.getByType(JpiExtension)

        mainAttributes[MANIFEST_VERSION] = '1.0'

        mainAttributes.putValue('Mask-Classes', conv.maskClasses)

        def dep = project.plugins.getPlugin(JpiPlugin).dependencyAnalysis.analyse().manifestPluginDependencies
        if (dep.length() > 0) {
            mainAttributes.putValue('Plugin-Dependencies', dep)
        }

        if (conv.developers) {
            mainAttributes.putValue(
                    'Plugin-Developers',
                    conv.developers.collect { "${it.name ?: ''}:${it.id ?: ''}:${it.email ?: ''}" }.join(',')
            )
        }

        // remove empty values
        mainAttributes.entrySet().removeAll { it.value == null || it.value.toString().empty }
    }

    static Map<String, ?> attributesToMap(Attributes attributes) {
        attributes.collectEntries { k, v -> [k.toString(), v] } as Map<String, ?>
    }
}
