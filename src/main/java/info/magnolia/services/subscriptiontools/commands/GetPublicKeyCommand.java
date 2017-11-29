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

import info.magnolia.commands.MgnlCommand;
import info.magnolia.cms.exchange.ActivationManager;
import info.magnolia.context.Context;

import info.magnolia.context.MgnlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * TBD.
 */
public class GetPublicKeyCommand extends MgnlCommand {
    private static final Logger logger = LoggerFactory.getLogger(ActivateSubscriberCommand.class);

    public final static String PUBLICKEY_KEY = "publicKey";

    ActivationManager activationManager;

    @Inject
    public GetPublicKeyCommand(ActivationManager activationManager) {
        this.activationManager = activationManager;
    }

    @Override
    public boolean execute(Context context) throws Exception {
        MgnlContext.setAttribute(PUBLICKEY_KEY, this.activationManager.getPublicKey());
        return true;
    }
}
