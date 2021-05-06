package com.louisga.banking.utility;

public interface AppConstants {

	final static String EMAIL_BODY = "body";
	final static String EMAIL_DATETIME = "dateTime";
	static final String EMAIL_COMPANY = "company";
	final static String EMAIL_TEMPLATE_FILE = "fragments/email-template.html";
	final static String EMAIL_SENT_SUCCESS_LOG = "SUCCESFULLY SENT EMAIL TO ==>> {} ";
	final static String EMAIL_SENT_FAILURE_LOG = "FAILED SENT EMAIL TO ==>> {} ";
	final static String EMAIL_SENT_FAILURE_REASON_LOG = "REASON ==>> {} ";
	
	static final String ADMINISTRATOR = "Admin";
	static final String ANONYMOUS = "ANONYMOUS";
	static final String BRANCH_MANAGER = "Manager";
	static final String BRANCH_OFFICER = "Officer";
	static final String BRANCH_TELLER = "Teller";
	static final String BRANCH_CASHIER = "Cashier";
	
	static final String KEYCLOAK_CLIENTS_ROLE_MAPPINGS = "/role-mappings/clients/";
	static final String KEYCLOAK_REALM_ROLE_MAPPINGS = "/role-mappings/realm";

	static final String MODEL_ATTRIBUTE_ROLES = "roles";
	static final String MODEL_ATTRIBUTE_CARDS = "cards";
	static final String MODEL_ATTRIBUTE_BRANCHES = "branches";
	static final String MODEL_ATTRIBUTE_GENDERS = "genderTypes";
	static final String MODEL_ATTRIBUTE_ACCOUNT_NAME = "accountName";
	static final String MODEL_ATTRIBUTE_ACCOUNT_STATUS = "accountStatus";
	static final String MODEL_ATTRIBUTE_ACCOUNT_LOAN_STATUS = "accountOwing";
	static final String MODEL_ATTRIBUTE_ACCOUNT_LOAN_PAYMENT_RECORDS = "loanPaymentRecords";
	static final String MODEL_ATTRIBUTE_ACCOUNT_AUDITS = "accountAudits";
	static final String MODEL_ATTRIBUTE_COMPANY_NAME = "companyName";
	static final String MODEL_ATTRIBUTE_EMPLOYEE = "employee";
	static final String MODEL_ATTRIBUTE_EMPLOYEE_ID = "employeeId";
	static final String MODEL_ATTRIBUTE_EMPLOYEE_NAME = "employeeName";
	static final String MODEL_ATTRIBUTE_EMPLOYEES = "employees";
	static final String MODEL_ATTRIBUTE_EMPLOYEE_ROLE = "employeeRole";
	static final String MODEL_ATTRIBUTE_ACCOUNT = "account";
	static final String MODEL_ATTRIBUTE_ACCOUNT_IMAGE = "accountImage";
	static final String MODEL_ATTRIBUTE_ACCOUNTS = "accounts";
	static final String MODEL_ATTRIBUTE_TRANSACTION = "transaction";
	static final String MODEL_ATTRIBUTE_TRANSACTION_ACCOUNT = "transactionAccount";
	static final String MODEL_ATTRIBUTE_TRANSACTION_AUDITS = "transactionAudits";
	static final String MODEL_ATTRIBUTE_TRANSACTIONS = "transactions";
	static final String MODEL_ATTRIBUTE_LOANS = "loans";
	static final String MODEL_ATTRIBUTE_DEPOSITS = "deposits";
	static final String MODEL_ATTRIBUTE_WITHDRAWALS = "withdrawals";
	static final String MODEL_ATTRIBUTE_PAGE_TITLE = "pageTitle";
	static final String MODEL_ATTRIBUTE_CENTRIFUGO_WEBSOCKET = "centrifugoWebSocket";
	static final String MODEL_ATTRIBUTE_CENTRIFUGO_TOKEN = "centrifugoAccessToken";
	static final String MODEL_ATTRIBUTE_ACCOUNT_CHANNEL = "accountChannel";
	static final String MODEL_ATTRIBUTE_TRANSACTION_CHANNEL = "transactionChannel";
	static final String MODEL_ATTRIBUTE_FIRST_NAME = "firstname";
	static final String MODEL_ATTRIBUTE_LOGGED_IN_USER_ID = "loggedInUserId";
	static final String MODEL_ATTRIBUTE_TRANSACTION_PREFIX_LENGTH = "transactionIdPrefixLength";
	static final String MODEL_ATTRIBUTE_PAGE_COUNT = "pageCount";
	
	static final String FILTER_BY_DAY = "day";
	static final String FILTER_BY_WEEK = "week";
	static final String FILTER_BY_MONTH = "month";
	static final String FILTER_BY_YEAR = "year";
	static final String FILTER_BY_ALL = "all";
	static final String FILTER_BY_EMPLOYEE = "FILTER_EMPLOYEE";
	static final String FILTER_BY_SIZE = "FILTER_SIZE";
	static final String FILTER_BY_DATE = "FILTER_DATE";
	
	static final String TRANSACTION_TYPE_LOAN = "LOAN";
	static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
	static final String TRANSACTION_TYPE_WITHDRAWAL = "WITHDRAWAL";
}
