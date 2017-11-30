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
package info.magnolia.services;

import info.magnolia.cms.exchange.Subscription;

import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Collection;
import java.text.MessageFormat;

/**
 * TBD.
 */
public class SubscriptionTools implements ModuleLifecycle {
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionTools.class);

    protected static final String DEFAULT_SUBSCRIPTION_PATH = "/modules/subscription-tools/config/defaultSubscription";
    protected static final String NAMED_SUBSCRIPTION_PATH = "/modules/subscription-tools/config/subscriptions/{0}";

    Collection<Subscription> defaultSubscriptions;
    Map<String,NamedSubscriptions> subscriptions;

    @Override
    public void start(ModuleLifecycleContext moduleLifecycleContext) {
        logger.debug("***********************************************");
        logger.debug("***  starting Subscription Tools module!!!  ***");
        logger.debug("***********************************************");
    }

    @Override
    public void stop(ModuleLifecycleContext moduleLifecycleContext) {
        logger.debug("***********************************************");
        logger.debug("***  stopping Subscription Tools module!!!  ***");
        logger.debug("***********************************************");
    }

    public void setDefaultSubscription(Collection<Subscription> defaultSubscriptions) {
        this.defaultSubscriptions = defaultSubscriptions;
    }

    public Collection<Subscription> getDefaultSubscriptions() {
        return defaultSubscriptions;
    }

    public void setSubscriptions(Map<String, NamedSubscriptions> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public boolean hasSubscriptions(String name) {
        return subscriptions.containsKey(name);
    }

    public Collection<Subscription> getSubscriptions(String name) {
        return (subscriptions.containsKey(name)) ? subscriptions.get(name).getSubscriptions() : null;
    }

    public String getSubscriptionPath(String name) {
        if (name == null || !subscriptions.containsKey(name)) {
            return DEFAULT_SUBSCRIPTION_PATH;
        }

        if (subscriptions.containsKey(name)) {
            return MessageFormat.format(NAMED_SUBSCRIPTION_PATH, name);
        }

        return null;
    }

}
