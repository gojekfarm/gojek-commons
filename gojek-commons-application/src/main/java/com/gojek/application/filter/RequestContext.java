/**
 *
 */
package com.gojek.application.filter;

import java.util.Locale;

/**
 * @author ganeshs
 *
 */
public class RequestContext {

	private ThreadLocal<Context> context = new ThreadLocal<Context>();

	private static final RequestContext INSTANCE = new RequestContext();

	/**
	 * Private constructor
	 */
	private RequestContext() {
	}

	/**
	 * Returns the singleton instance
	 *
	 * @return
	 */
	public static RequestContext instance() {
		return INSTANCE;
	}

	/**
	 * Clears the context
	 */
	public void clear() {
		this.context.remove();
	}

	/**
	 * Returns the context stored in the thread local
	 *
	 * @return
	 */
	public Context getContext() {
		Context context = this.context.get();
		if (context == null) {
			context = new Context();
			this.context.set(context);
		}
		return context;
	}

	/**
	 * @author ganeshs
	 *
	 */
	public static class Context {
		
		/**
		 * @author ganeshs
		 *
		 */
		public enum AccountType {
			merchant, consumer, internal, partner
		}
		
		public enum Role {
			admin, manager, executive
		}

		private String sessionId;

		private String deviceInfo;

		private String userAgent;

		private String clientIp;

		private String applicationLicenseKey;
		
		private String merchantId;
		
		private String merchantBranchId;
		
		private String userId;
		
		private String partnerId;
		
		private Role role = Role.executive;
		
		private AccountType accountType = AccountType.consumer;
		
		private Locale locale = Locale.getDefault();

		/**
		 * @return the merchantId
		 */
		public String getMerchantId() {
			return merchantId;
		}

		/**
		 * @param merchantId the merchantId to set
		 */
		public void setMerchantId(String merchantId) {
			this.merchantId = merchantId;
		}

		/**
		 * @return the merchantBranchId
		 */
		public String getMerchantBranchId() {
			return merchantBranchId;
		}

		/**
		 * @param merchantBranchId the merchantBranchId to set
		 */
		public void setMerchantBranchId(String merchantBranchId) {
			this.merchantBranchId = merchantBranchId;
		}

		/**
		 * @return the userId
		 */
		public String getUserId() {
			return userId;
		}

		/**
		 * @param userId the userId to set
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}

		/**
		 * @return the accountType
		 */
		public AccountType getAccountType() {
			return accountType;
		}

		/**
		 * @param accountType the accountType to set
		 */
		public void setAccountType(AccountType accountType) {
			this.accountType = accountType;
		}

		/**
		 * @return the deviceInfo
		 */
		public String getDeviceInfo() {
			return deviceInfo;
		}

		/**
		 * @param deviceInfo
		 *            the deviceInfo to set
		 */
		public void setDeviceInfo(String deviceInfo) {
			this.deviceInfo = deviceInfo;
		}

		/**
		 * @return the clientIp
		 */
		public String getClientIp() {
			return clientIp;
		}

		/**
		 * @param clientIp
		 *            the clientIp to set
		 */
		public void setClientIp(String clientIp) {
			this.clientIp = clientIp;
		}

		/**
		 * @return the sessionId
		 */
		public String getSessionId() {
			return sessionId;
		}

		/**
		 * @param sessionId
		 *            the sessionId to set
		 */
		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

		/**
		 * @return the role
		 */
		public Role getRole() {
			return role;
		}

		/**
		 * @param role the role to set
		 */
		public void setRole(Role role) {
			this.role = role;
		}

		/**
		 * @return the userAgent
		 */
		public String getUserAgent() {
			return userAgent;
		}

		/**
		 * @param userAgent
		 *            the userAgent to set
		 */
		public void setUserAgent(String userAgent) {
			this.userAgent = userAgent;
		}

		/**
		 * @return the applicationLicenseKey
		 */
		public String getApplicationLicenseKey() {
			return applicationLicenseKey;
		}

		/**
		 * @param applicationLicenseKey
		 *            the applicationLicenseKey to set
		 */
		public void setApplicationLicenseKey(String applicationLicenseKey) {
			this.applicationLicenseKey = applicationLicenseKey;
		}
		
		/**
		 * @return
		 */
		public boolean isMerchant() {
			return accountType == AccountType.merchant;
		}

		/**
		 * @return
		 */
		public boolean isConsumer() {
			return accountType == AccountType.consumer;
		}
		
		/**
		 * @return
		 */
		public boolean isInternalUser() {
			return accountType == AccountType.internal;
		}
		
		/**
		 * @return
		 */
		public boolean isPartner() {
			return accountType == AccountType.partner;
		}

		/**
		 * @return the partnerId
		 */
		public String getPartnerId() {
			return partnerId;
		}

		/**
		 * @param partnerId the partnerId to set
		 */
		public void setPartnerId(String partnerId) {
			this.partnerId = partnerId;
		}

        /**
         * @return the locale
         */
        public Locale getLocale() {
            return locale;
        }

        /**
         * @param locale the locale to set
         */
        public void setLocale(Locale locale) {
            this.locale = locale;
        }
	}
}
