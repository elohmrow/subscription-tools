# subscription-tools

The Subscription Tools module provides commands for managing public instance subscribers on an Magnolia author instance. The module also provides configuration for invoking its commands through the command REST API.

## Installation

Add a dependency for the scheduled activation module to your web app Maven pom file:

```
<dependency>
  <groupId>info.magnolia.services</groupId>
  <artifactId>subscription-tools</artifactId>
  <version>(current scheduled activation module version)</version>
</dependency>
```

## Usage

The configuration of the Subscription Tools module contains subscriptions used to create new subscribers through the **addSubscriber** command. The configuration is defined at:

/modules/subscription-tools/config

/modules/subscription-tools/config/defaultSubscription - defines the default descriptions used when creating a new subscriber without specifying a named subscription.

/modules/subscription-tools/config/subscriptions - defines subscription sets by a name. You can specify a subscription set by name when adding a new subscriber with the **addSubscriber** command.

The Subscription Tools module contains the following defined commands:

**subscriptions-addSubscriber**
**subscriptions-removeSubscriber**
**subscriptions-activateSubscriber**
**subscriptions-deactivateSubscriber**
**subscriptions-getPublicKey**

**subscriptions-addSubscriber** will create a new subscriber. The following attributes in the command context will be used:

  * *name* (required) - the name of the new subscriber. The command will check if there is already a subscriber with the same name.
  * *URL* (required) - the URL for the new subscriber.
  * *subscription* (optional) - the name of a configured set of subscriptions. If not specified, the default subscription set will be used (/modules/subscription-tools/config/defaultSubscription). If defined, and there is not a configured subscription set (e.g. /modules/subscription-tools/config/subscriptions/<subscription>), the command will fail.
  * *configure* (optional) - **true** or **false**. If true, the new subscriber will be added to the subscriber configuration at /server/activation/subscribers. If false, the new subscriber will be added to the Activation Manager only without changing the subscriber configuration. If Magnolia is restarted, the subscriber will not be available.

The public key of the Magnolia instance will be added to the command context (and the REST response) with the name "publicKey" if the command was successfully executed.

**subscriptions-removeSubscriber** will delete the configuration of a subscriber at /server/activation/subscribers. Deleting the configuration will also force the Activation Manager to reload its subscribers and the deleted subscriber will not be available. The following attributes in the command context will be used:

  * *name* (required) - the name of the subscriber to be deleted.

**subscriptions-activateSubscriber** will set the "active" property of a subscriber to "true". The following attributes in the command context will be used:

  * *name* (required) - the name of the new subscriber. The command will check if there is already a subscriber with the same name.
  * *configure* (optional) - **true** or **false**. If true, the "active" property of the subscriber configuration at /server/activation/subscribers/<name> will be updated. If false, the active property of the subscriber will be updated in the Activation Manager only without changing the subscriber configuration. If Magnolia is restarted, the original setting of the active property of the subscriber will be used.

**subscriptions-deactivateSubscriber** will set the "active" property of a subscriber to "false". The following attributes in the command context will be used:

  * *name* (required) - the name of the new subscriber. The command will check if there is already a subscriber with the same name.
  * *configure* (optional) - **true** or **false**. If true, the "active" property of the subscriber configuration at /server/activation/subscribers/<name> will be updated. If false, the active property of the subscriber will be updated in the Activation Manager only without changing the subscriber configuration. If Magnolia is restarted, the original setting of the active property of the subscriber will be used.

**subscriptions-getPublicKey** will return the current public key of the Magnolia instance in the command context and the REST response with the name "publicKey".

## Information on Magnolia CMS


## License


## Contributors

Andrew Warinner
Senior Solution Architect
Magnolia
