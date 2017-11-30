/**
 * This file Copyright (c) 2017 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This program and the accompanying materials are made
 * available under the terms of the Magnolia Network Agreement
 * which accompanies this distribution, and is available at
 * http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.services.subscriptiontools.commands;

import info.magnolia.test.MgnlTestCase;

import info.magnolia.cms.exchange.ActivationManager;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * TBD.
 */
public class GetPublicKeyCommandTest extends MgnlTestCase {

    private static final Logger logger = LoggerFactory.getLogger(GetPublicKeyCommandTest.class);

    ActivationManager activationManager;
    GetPublicKeyCommand command;

    @Before
    public void setUp() throws Exception {
        logger.debug("setUp() called");

        super.setUp();

        // do some set up here
        activationManager = mock(ActivationManager.class);
        command = new GetPublicKeyCommand(activationManager);

        when(activationManager.getPublicKey()).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                return "scoobydoobydoo";
            }
        });

    }

    @After
    public void tearDown() throws Exception {
        logger.debug("tearDown called");

        super.tearDown();

        // do some tear down here
    }

    @Test
    public void testSmoke() {
        logger.debug("testSmoke called");

        // do some testing here
        assertNotNull("Expected a GetPublicKeyCommand instance", command);
        assertNotNull("Expected a ActivationManager instance", activationManager);
    }

    @Test
    public void testGetPublicKey() throws Exception {
        logger.debug("testGetPublicKey called");

        Context context = MgnlContext.getInstance();
        assertNotNull("Expected a Context instance", context);

        assertTrue("Expected GetPublicKeyCommand to return true", command.execute(context));
        assertTrue("Expected to find publicKey", context.containsKey(GetPublicKeyCommand.PUBLICKEY_KEY));
        assertTrue("Expected scoobydoobydoo", "scoobydoobydoo".equals(context.getAttribute(GetPublicKeyCommand.PUBLICKEY_KEY)));
    }

}
