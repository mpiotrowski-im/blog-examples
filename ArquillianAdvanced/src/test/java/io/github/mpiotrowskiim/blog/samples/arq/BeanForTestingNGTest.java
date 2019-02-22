/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.mpiotrowskiim.blog.samples.arq;

import java.io.File;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * Sample Arquillian using test file.
 *
 * If you want to use Arquillian with TestNG you have to extened {@link Arquillian} abstract class.
 *
 * @author Michał Piotrowski mpiotrowski@im.gda.pl
 */
public class BeanForTestingNGTest extends Arquillian {

    /**
     * We can inject EJBs, persistence units just as in a web app.
     */
    @EJB
    private BeanForTesting anEjb;

    /**
     * We need at least one static method annotated with {@link Deployment} and
     * which returns {@link WebArchive}. This is the method which prepares
     * archive which will be deployed on a server.
     */
    @Deployment
    public static WebArchive createDeployment() {
        /* Creating embedded maven for building a project to test */
        var maven = EmbeddedMaven
                .forProject("pom.xml") //use pom in CWD (which is proejct dir in maven build)
                .useLocalInstallation();

        /* Embedded maven resolver configured based on current pom file.
           Used to resolve and add additional maven dependencies to deployment archive */
        var resolver = Maven.configureResolver().loadPomFromFile("pom.xml");

        // Building our project for testing purposes
        WebArchive a = (WebArchive) maven
                .setGoals("package")
                .skipTests(true)
                .build()
                .getDefaultBuiltArchive();

        //Adding TestNG runtime dependencies needed in deployment archive
        //The executed test class is automatically added but not other test dependencies
        //or other test classes.
        File testNgLib1 = resolver.resolve("org.testng:testng:jar:6.14.3").withoutTransitivity().asSingleFile();
        File testNgLib2 = resolver.resolve("com.beust:jcommander:jar:1.72").withoutTransitivity().asSingleFile();
        return a.addAsLibraries(testNgLib1, testNgLib2);
    }

    /**
     * Test of someGreatMethod method, of class BeanForTesting to see a test fail.
     */
    @Test
    public void testSomeGreatMethodFail() {
        assertEquals(anEjb.someGreatMethod(), 22);
    }

    /**
     * Test of someGreatMethod method, of class BeanForTesting to see a test pass.
     */
    @Test
    public void testSomeGreatMethodPass() {
        assertEquals(anEjb.someGreatMethod(), 33);
    }

}
