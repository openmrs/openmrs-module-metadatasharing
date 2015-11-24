/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.metadatasharing.subscription;

import java.util.LinkedList;
import java.util.List;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.notification.Alert;
import org.openmrs.notification.AlertService;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * A scheduled task which checks for updated for all subscriptions and display an alert to the user
 * if there are any updates available
 */
public class CheckForUpdatesTask extends AbstractTask {
	
	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		startExecuting();
		try {
			if (!Context.isSessionOpen()) {
				Context.openSession();
			}

			Context.addProxyPrivilege(MetadataSharingConsts.MODULE_PRIVILEGE);
			Context.addProxyPrivilege("View Users");
			Context.addProxyPrivilege("Manage Alerts");
			MetadataSharingService subscriptionService = Context.getService(MetadataSharingService.class);
			int updatedSubscriptionsCount = subscriptionService.getSubscriptionUpdater()
			        .checkForUpdatesForAllSubscriptions();
			if (updatedSubscriptionsCount > 0) {
				AlertService alertService = Context.getAlertService();
				
				List<User> users = getUsersByPrivilegeName(MetadataSharingConsts.MODULE_PRIVILEGE);
				String alertMessage = Context.getMessageSourceService().getMessage(
				    "metadatasharing.alert.updatesAvailable",
				    new String[] { Integer.toString(updatedSubscriptionsCount) }, null);
				Alert alert = new Alert(alertMessage, users);
				User user = Context.getAuthenticatedUser();
				if (user == null) { //we are not authenticated, so let admin to be the creator
					user = users.get(0);
				}
				alert.setCreator(user);
				alertService.saveAlert(alert);
			}
		}
		finally {
			Context.removeProxyPrivilege("Manage Alerts");
			Context.removeProxyPrivilege("View Users");
			Context.removeProxyPrivilege(MetadataSharingConsts.MODULE_PRIVILEGE);
			Context.closeSession();
		}
		stopExecuting();
	}
	
	/**
	 * @param privilegeName the privilege name to search users for
	 * @return all users that have the given privilege
	 */
	private List<User> getUsersByPrivilegeName(String privilegeName) {
		List<User> allUsers = Context.getUserService().getAllUsers();
		List<User> usersWithPrivilege = new LinkedList<User>();
		for (User u : allUsers) {
			if (u.hasPrivilege(privilegeName)) {
				usersWithPrivilege.add(u);
			}
		}
		return usersWithPrivilege;
	}
}
