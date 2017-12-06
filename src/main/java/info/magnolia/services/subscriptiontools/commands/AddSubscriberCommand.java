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

import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.cms.exchange.Subscriber;
import info.magnolia.cms.exchange.Subscription;
import info.magnolia.cms.exchange.ActivationManager;
import info.magnolia.commands.MgnlCommand;
import info.magnolia.module.activation.DefaultSubscriber;

import info.magnolia.services.SubscriptionTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;

import javax.inject.Inject;

/**
 * TBD.
 */
public class AddSubscriberCommand extends MgnlCommand {

    private static final Logger logger = LoggerFactory.getLogger(AddSubscriberCommand.class);

    protected static final String SUBSCRIPTIONS_PATH = "/server/activation/subscribers";

    public static final String NAME_KEY = "name";
    public static final String URL_KEY = "URL";
    public static final String SUBSCRIPTION_KEY = "subscription";
    public static final String CONFIGURE_KEY = "configure";
    public static final String PUBLICKEY_KEY = "publicKey";
    public static final String ACTIVE_KEY = "active";

    SubscriptionTools subscriptionTools;
    ActivationManager activationManager;

    @Inject
    public AddSubscriberCommand(SubscriptionTools subscriptionTools, ActivationManager activationManager) {
        this.subscriptionTools = subscriptionTools;
        this.activationManager = activationManager;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean execute(Context context) throws Exception {
        logger.debug("execute called");

        // TODO: quick return if we are a public instance

        if (context.getAttribute(NAME_KEY) == null) {
            throw new IllegalArgumentException("Missing required parameter name");
        }

        String name = context.getAttribute(NAME_KEY);

        if (context.getAttribute(URL_KEY) == null) {
            throw new IllegalArgumentException("Missing required parameter url");
        }

        // check if a subscription with the same name exists
        if (subscriberExists(name)) {
            throw new IllegalArgumentException("Subscription named " + name + " already exists");
        }

        boolean configure = (context.getAttribute(CONFIGURE_KEY) != null) ? Boolean.parseBoolean(context.getAttribute(CONFIGURE_KEY).toString()) : false;
        String url = context.getAttribute(URL_KEY);
        String subscriptionName = context.getAttribute(SUBSCRIPTION_KEY);

        if (subscriptionName != null && !subscriptionTools.hasSubscriptions(subscriptionName)) {
            throw new IllegalArgumentException("No subscription named " + subscriptionName);
        }

        //
        // Adding a subscriber to the activation configuration forces a rebuild of the subscribers in the Activation
        // Manager. There is no point to building a Subscriber object and adding it to the Activation Manager and
        // modifying the subscriber configuration as well. We do one or the other, depending on the passed parameters.
        //

        if (configure) {
            configureSubscriber(context, name, url, subscriptionName);
        } else {
            addSubscriber(name, url, subscriptionName);
        }

        MgnlContext.setAttribute(PUBLICKEY_KEY, this.activationManager.getPublicKey());
        MgnlContext.setAttribute(ACTIVE_KEY, Boolean.FALSE);

        return true;
    }

    protected boolean subscriberExists(String name) {
        for (Subscriber subscriber : this.activationManager.getSubscribers()) {
            if (name.equals(subscriber.getName())) {
                return true;
            }
        }

        return false;
    }

    protected void configureSubscriber(Context context, String name, String url, String subscriptionName) throws RepositoryException {
        Session session = context.getJCRSession(RepositoryConstants.CONFIG);

        Node root = session.getNode(SUBSCRIPTIONS_PATH);
        Node subscriber = root.addNode(name, NodeTypes.ContentNode.NAME);
        PropertyUtil.setProperty(subscriber, "class", DefaultSubscriber.class.getName());
        PropertyUtil.setProperty(subscriber, "URL", url);
        PropertyUtil.setProperty(subscriber, "active", Boolean.TRUE);

        String subscriptionsPath = this.subscriptionTools.getSubscriptionPath(subscriptionName);
        Node subscriptions = session.getNode(subscriptionsPath);

        clone(subscriptions, subscriber, "subscriptions");

        session.save();
    }

    protected void addSubscriber(String name, String url, String subscriptionsName) {
        Collection<Subscription> subscriptions = (subscriptionsName == null) ? this.subscriptionTools.getDefaultSubscriptions() : this.subscriptionTools.getSubscriptions(subscriptionsName);

        Subscriber subscriber = new DefaultSubscriber();
        subscriber.setName(name);
        subscriber.setActive(true);
        subscriber.setURL(url);
        subscriber.setSubscriptions(subscriptions);

        // N.B. the ActivationManager interface has deprecated the following method but it works for
        // Magnolia 5.5 and earlier.
        // Should figure out an official way of doing this!
        this.activationManager.addSubscribers(subscriber);
    }

    private Node clone(Node node, Node parent, String name) throws RepositoryException {
        // create the new node under the parent
        Node clone = parent.addNode(name, node.getPrimaryNodeType().getName());

        // copy properties
        PropertyIterator properties = node.getProperties();

        while (properties.hasNext()) {
            Property property = properties.nextProperty();
            if (!property.getName().startsWith("jcr:") && !property.getName().startsWith("mgnl:")) {
                PropertyUtil.setProperty(clone, property.getName(), property.getValue());
            }
        }

        // copy subnodes
        NodeIterator children = node.getNodes();

        while (children.hasNext()) {
            Node child = children.nextNode();
            clone(child, clone, child.getName());
        }

        return clone;
    }

}
