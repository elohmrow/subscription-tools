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

import info.magnolia.cms.exchange.Subscription;
import info.magnolia.services.SubscriptionTools;
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
import static org.junit.Assert.assertFalse;
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
public class AddSubscriberCommandTest extends MgnlTestCase {

    private static final Logger logger = LoggerFactory.getLogger(GetPublicKeyCommandTest.class);

    SubscriptionTools subscriptionTools;
    ActivationManager activationManager;
    AddSubscriberCommand command;

    @Before
    public void setUp() throws Exception {
        logger.debug("setUp() called");

        super.setUp();

        // do some set up here
        activationManager = mock(ActivationManager.class);
        subscriptionTools = mock(SubscriptionTools.class);
        command = new AddSubscriberCommand(subscriptionTools, activationManager);
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
        assertNotNull("Expected a AddSubscriptionCommand instance", command);
        assertNotNull("Expected a SubscriptionTools instance", subscriptionTools);
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

    @Test
    public void testNoURLParameter() throws Exception {
        logger.debug("testNoURLParameter called");

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

        context.setAttribute(ActivateSubscriberCommand.NAME_KEY, "scoobydoo", Context.APPLICATION_SCOPE);
        context.setAttribute(ActivateSubscriberCommand.CONFIGURE_KEY, Boolean.FALSE, Context.APPLICATION_SCOPE);

        try {
            command.execute(context);
            fail("Excepted an exception");
        } catch (IllegalArgumentException iax) {
            // expected
        }
    }

    @Test
    public void testAddSubscriberNoConfigDefault() throws Exception {
        logger.debug("testAddSubscriberNoConfigDefault called");

        final HashSet<Subscriber> subscribers = new HashSet<Subscriber>();

        when(activationManager.getSubscribers()).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                return subscribers;
            }
        });

        when(subscriptionTools.getDefaultSubscriptions()).thenAnswer(new Answer() {
           public Object answer(InvocationOnMock invocation) {
               return new HashSet<Subscriber>();
           }
        });

        when(activationManager.getPublicKey()).thenReturn("thisisthepublickey");

        ArgumentCaptor valueCapture = ArgumentCaptor.forClass(Subscriber.class);
        doNothing().when(activationManager).addSubscribers((Subscriber) valueCapture.capture());

        Context context = MgnlContext.getInstance();
        assertNotNull("Expected a Context instance", context);

        context.setAttribute(AddSubscriberCommand.NAME_KEY, "scoobydoo", Context.APPLICATION_SCOPE);
        context.setAttribute(AddSubscriberCommand.URL_KEY, "http://foobar.com/magnolia", Context.APPLICATION_SCOPE);
        context.setAttribute(AddSubscriberCommand.CONFIGURE_KEY, Boolean.FALSE, Context.APPLICATION_SCOPE);

        command.execute(context);

        assertNotNull("Expected a Subscriber", valueCapture.getValue());
        assertTrue("Expected name scoobydoo", "scoobydoo".equals(((Subscriber) valueCapture.getValue()).getName()));
        assertTrue("Expected URL http://foobar.com/magnolia", "http://foobar.com/magnolia".equals(((Subscriber) valueCapture.getValue()).getURL()));
    }

    @Test
    public void testNamedSubscriptionNotFound() throws Exception {
        logger.debug("testNamedSubscriptionNotFound called");

        final HashSet<Subscriber> subscribers = new HashSet<Subscriber>();

        when(subscriptionTools.hasSubscriptions(anyString())).thenReturn(false);

        Context context = MgnlContext.getInstance();
        assertNotNull("Expected a Context instance", context);

        context.setAttribute(AddSubscriberCommand.NAME_KEY, "scoobydoo", Context.APPLICATION_SCOPE);
        context.setAttribute(AddSubscriberCommand.URL_KEY, "http://foobar.com/magnolia", Context.APPLICATION_SCOPE);
        context.setAttribute(AddSubscriberCommand.CONFIGURE_KEY, Boolean.FALSE, Context.APPLICATION_SCOPE);
        context.setAttribute(AddSubscriberCommand.SUBSCRIPTION_KEY, "kablooie", Context.APPLICATION_SCOPE);

        try {
            command.execute(context);
            fail("Expected an exception to be thrown");
        } catch (IllegalArgumentException iax) {
            // expected
        }
    }

    @Test
    public void testAddSubscriberNoConfig() throws Exception {
        logger.debug("testAddSubscriberNoConfigDefault called");

        final HashSet<Subscriber> subscribers = new HashSet<Subscriber>();

        when(activationManager.getSubscribers()).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                return subscribers;
            }
        });

        when(subscriptionTools.getDefaultSubscriptions()).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                return new HashSet<Subscriber>();
            }
        });

        when(subscriptionTools.hasSubscriptions(anyString())).thenReturn(true);
        when(subscriptionTools.getSubscriptions(anyString())).thenReturn(new HashSet<Subscription>());

        when(activationManager.getPublicKey()).thenReturn("thisisthepublickey");

        ArgumentCaptor valueCapture = ArgumentCaptor.forClass(Subscriber.class);
        doNothing().when(activationManager).addSubscribers((Subscriber) valueCapture.capture());

        Context context = MgnlContext.getInstance();
        assertNotNull("Expected a Context instance", context);

        context.setAttribute(AddSubscriberCommand.NAME_KEY, "zippythepinhead", Context.APPLICATION_SCOPE);
        context.setAttribute(AddSubscriberCommand.URL_KEY, "http://kablooie.com/magnolia", Context.APPLICATION_SCOPE);
        context.setAttribute(AddSubscriberCommand.CONFIGURE_KEY, Boolean.FALSE, Context.APPLICATION_SCOPE);

        command.execute(context);

        assertNotNull("Expected a Subscriber", valueCapture.getValue());
        assertTrue("Expected name scoobydoo", "zippythepinhead".equals(((Subscriber) valueCapture.getValue()).getName()));
        assertTrue("Expected URL http://kablooie.com/magnolia", "http://kablooie.com/magnolia".equals(((Subscriber) valueCapture.getValue()).getURL()));
    }

}
