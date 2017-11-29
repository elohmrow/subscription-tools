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

import info.magnolia.cms.exchange.ActivationManager;
import info.magnolia.cms.exchange.Subscriber;
import info.magnolia.commands.MgnlCommand;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.context.Context;
import info.magnolia.repository.RepositoryConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.text.MessageFormat;

/**
 * TBD.
 */
public class ActivateSubscriberCommand extends MgnlCommand {
    private static final Logger logger = LoggerFactory.getLogger(ActivateSubscriberCommand.class);

    protected final static String SUBSCRIBER_PATH = "/server/activation/subscribers/{0}";

    public final static String NAME_KEY = "name";
    public final static String CONFIGURE_KEY = "configure";
    public final static String ACTIVE_KEY = "active";

    ActivationManager activationManager;

    @Inject
    public ActivateSubscriberCommand(ActivationManager activationManager) {
        this.activationManager = activationManager;
    }

    @Override
    public boolean execute(Context context) throws Exception {

        if (context.getAttribute(NAME_KEY) == null) {
            throw new IllegalArgumentException("Missing required parameter name");
        }

        String name = context.getAttribute(NAME_KEY);

        // check if a subscription with the same name exists
        if (!subscriberExists(name)) {
            throw new IllegalArgumentException("No subscriber named " + name + " is defined");
        }

        boolean configure = (context.getAttribute(CONFIGURE_KEY) != null) ? Boolean.parseBoolean(context.getAttribute(CONFIGURE_KEY).toString()) : false;

        if (configure) {
            configureSubscriber(context, name);
        } else {
            activateSubscriber(name);
        }

        MgnlContext.setAttribute(ACTIVE_KEY, Boolean.TRUE);

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

    protected void configureSubscriber(Context context, String name) throws RepositoryException {
        Session session = context.getJCRSession(RepositoryConstants.CONFIG);

        Node subscriber = session.getNode(MessageFormat.format(SUBSCRIBER_PATH, name));
        PropertyUtil.setProperty(subscriber,"active", Boolean.TRUE);

        session.save();
    }

    protected void activateSubscriber(String name) {
        for (Subscriber subscriber : this.activationManager.getSubscribers()) {
            if (name.equals(subscriber.getName())) {
                subscriber.setActive(true);
            }
        }
    }

}
