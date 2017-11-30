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
import info.magnolia.context.Context;
import info.magnolia.repository.RepositoryConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

import javax.inject.Inject;

import javax.jcr.Session;

/**
 * TBD.
 */
public class RemoveSubscriberCommand extends MgnlCommand {
    private static final Logger logger = LoggerFactory.getLogger(RemoveSubscriberCommand.class);

    protected static final String SUBSCRIBER_PATH = "/server/activation/subscribers/{0}";

    public static final String NAME_KEY = "name";

    ActivationManager activationManager;

    @Inject
    public RemoveSubscriberCommand(ActivationManager activationManager) {
        this.activationManager = activationManager;
    }

    @Override
    public boolean execute(Context context) throws Exception {
        logger.debug("execute called");

        if (context.getAttribute(NAME_KEY) == null) {
            throw new IllegalArgumentException("Missing required parameter name");
        }

        String name = context.getAttribute(NAME_KEY);

        // check if a subscription with the same name exists
        if (!subscriberExists(name)) {
            throw new IllegalArgumentException("No subscriber named " + name + " is defined");
        }

        Session session = context.getJCRSession(RepositoryConstants.CONFIG);

        session.removeItem(MessageFormat.format(SUBSCRIBER_PATH, name));

        session.save();

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

}
