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
import info.magnolia.cms.exchange.Subscriber;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.ArgumentCaptor;

import java.util.HashSet;

/**
 * TBD.
 */
public class ActivateSubscriberCommandTest extends MgnlTestCase {

    private static final Logger logger = LoggerFactory.getLogger(GetPublicKeyCommandTest.class);

    ActivationManager activationManager;
    ActivateSubscriberCommand command;

    @Before
    public void setUp() throws Exception {
        logger.debug("setUp() called");

        super.setUp();

        // do some set up here
        activationManager = mock(ActivationManager.class);
        command = new ActivateSubscriberCommand(activationManager);
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
        assertNotNull("Expected a ActivateSubscriberCommand instance", command);
        assertNotNull("Expected a ActivationManager instance", activationManager);
    }

    @Test
    public void testNoParameters() throws Exception {
        logger.debug("testNoParameters called");

        Context context = MgnlContext.getInstance();
        assertNotNull("Expected a Context instance", context);

        try {
            command.execute(context);
            fail("Expected an exception");
        } catch (IllegalArgumentException iax) {
            // expected
        }
    }

    @Test
    public void testNoSubscriber() throws Exception {
        logger.debug("testNoSubscriber called");

        when(activationManager.getPublicKey()).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                return new HashSet<Subscriber>();
            }
        });

        Context context = MgnlContext.getInstance();
        assertNotNull("Expected a Context instance", context);

        context.setAttribute(ActivateSubscriberCommand.NAME_KEY, "scoobydoo", Context.APPLICATION_SCOPE);

        try {
            command.execute(context);
            fail("Expected an exception");
        } catch (IllegalArgumentException iax) {
            // expected
        }
    }

    @Test
    public void testActivateSubscriberNoConfig() throws Exception {
        logger.debug("testActivateSubscriberNoConfig called");

        final HashSet<Subscriber> subscribers = new HashSet<Subscriber>();
        final Subscriber subscriber = mock(Subscriber.class);
        subscribers.add(subscriber);

        when(activationManager.getSubscribers()).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                return subscribers;
            }
        });

        when(subscriber.getName()).thenReturn("scoobydoo");
        ArgumentCaptor valueCapture = ArgumentCaptor.forClass(Boolean.class);
        doNothing().when(subscriber).setActive((Boolean) valueCapture.capture());

        Context context = MgnlContext.getInstance();
        assertNotNull("Expected a Context instance", context);

        context.setAttribute(ActivateSubscriberCommand.NAME_KEY, "scoobydoo", Context.APPLICATION_SCOPE);
        context.setAttribute(ActivateSubscriberCommand.CONFIGURE_KEY, Boolean.FALSE, Context.APPLICATION_SCOPE);

        command.execute(context);

        assertTrue("Expected active property of subscriber to be true", (Boolean) valueCapture.getValue());
        assertTrue("Expected active attribute in context", context.containsKey(ActivateSubscriberCommand.ACTIVE_KEY));
        assertTrue("Expected active attribute in context to be true", (Boolean) context.getAttribute(ActivateSubscriberCommand.ACTIVE_KEY));
    }

    @Test
    public void testSubscriberNotFound() throws Exception {
        logger.debug("testSubscriberNotFound called");

        final HashSet<Subscriber> subscribers = new HashSet<Subscriber>();
        final Subscriber subscriber = mock(Subscriber.class);
        subscribers.add(subscriber);

        when(activationManager.getSubscribers()).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                return subscribers;
            }
        });

        when(subscriber.getName()).thenReturn("scoobydoo");

        Context context = MgnlContext.getInstance();
        assertNotNull("Expected a Context instance", context);

        context.setAttribute(ActivateSubscriberCommand.NAME_KEY, "binkyboo", Context.APPLICATION_SCOPE);
        context.setAttribute(ActivateSubscriberCommand.CONFIGURE_KEY, Boolean.FALSE, Context.APPLICATION_SCOPE);

        try {
            command.execute(context);
            fail("Excepted an exception");
        } catch (IllegalArgumentException iax) {
            // expected
        }
    }

}
