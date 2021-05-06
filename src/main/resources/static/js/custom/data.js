/***********************************************************************
 *********************************CONFIDENTIAL**************************
 ***********************************************************************/

/** @author Louis K. Gamor */

export const DOMStrings = {
	filterDay: 'day',
	filterWeek: 'week',
	filterMonth: 'month',
	filterYear: 'year',
	filterAll: 'all',
	filterEmployeeCookie: 'FILTER_EMPLOYEE',
	filterDateCookie: 'FILTER_DATE',
	filterSizeCookie: 'FILTER_SIZE',
	filterShow: 'filter__options--show',
	notAvailable: 'N/A',
	searchQueryParam: 'query_param',
	flatpickerMode: 'range',
	flatpickerDateFormat: 'd-m-Y',
	flatpickerMaxDate: 'today',
	roleManager: 'manager',
};

export const DOMClasses = {
	paginationContainer: '.pagination',
	searchField: '.resource__search',
	tableBody: '.application__table--body',
	applicationBody: '.application__body',
	filterOption: '.filter__option',
	filterOptionsContainer: '.filter__options',
};

export const DOMIds = {
	totalAccounts: '#total-accounts',
	totalEmployees: '#total-employees',
	totalTransactions: '#total-transactions',
	totalDeposits: '#total-deposits',
	totalWithdrawals: '#total-withdrawals',
	totalLoans: '#total-loans',
	totalRecords: '#header-count',
	currentFilter: '#filter-value',
	filterSizePicker: '#filter-size',
	filterDatePicker: '#filter-date',
	filterEmployeePicker: '#filter-issuer',
	filterSelectedEmployee: '#filter__option-employee',
	filterSelectedDate: '#filter__option-date',
	filterCloseEmployee: '#close-employee',
	filterCloseDate: '#close-date',
	
};

export const DOMElements = {
	filterRadioButton: 'input:radio[name="radio-filter"]',
};

export const DOMEndpoints = {
	application: 'application/json',
	getTotalAccounts: 'api/v1/accounts/total',
	getTotalEmployees: 'api/v1/employees/total',
	getTotalLoans: 'api/v1/loans/total',
	getTotalDeposits: 'api/v1/deposits/total',
	getTotalWithdrawals: 'api/v1/withdrawals/total',
	getTotalTransactions: 'api/v1/transactions/total',
	getTotalTransactionsForGraph: 'api/v1/transactions/graph',
	getTransactions: 'api/v1/transactions',
	getRecentTransactions: 'api/v1/transactions/recent',
};

export const DOMEvents = {
	click: 'click',
	change: 'change',
	onClick: 'onclick',
	checked: 'checked',
	keyUp: 'keyup',
	keyDown: 'keydown',
	mouseover: 'mouseover'
};

export const DOMHTTPMethods = {
	putMethod: 'PUT',
	getMethod : 'GET',
	postMethod : 'POST',
	deleteMethod : 'DELETE'
};