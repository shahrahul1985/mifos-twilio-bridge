package org.mifos.module.sms.event;


public class SavingsAccountCloseEvent extends AbstractEvent{
	public SavingsAccountCloseEvent(final Object source,final Long eventId) {
		super(source, eventId);
	}

}
