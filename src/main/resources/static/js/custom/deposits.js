import { DOMStrings, DOMClasses, DOMIds, DOMEvents } from './data.js';

/** FILTER TABLE BY DATE: START*/

const filterOptions = document.querySelector(DOMClasses.filterOptionsContainer);

const flatPickrConfig = {
	mode: DOMStrings.flatpickerMode,
	dateFormat: DOMStrings.flatpickerDateFormat,
	maxDate: DOMStrings.flatpickerMaxDate,
	onChange: (selectedDates, dateStr, instance) => {
		if (selectedDates.length == 2) {
			const filterDateStartUI = new Date(selectedDates[0]).toDateString();
			const filterDateEndUI = new Date(selectedDates[1]).toDateString();
			const filterDateStartCookie = new Date(selectedDates[0]).toISOString();
			const filterDateEndCookie = new Date(selectedDates[1]).toISOString();
			
			Cookies.set(DOMStrings.filterDateCookie, `${filterDateStartCookie}_${filterDateEndCookie}`, { path: CONTEXT });
			localStorage.setItem(DOMStrings.filterDateCookie, `${filterDateStartUI} - ${filterDateEndUI}`);

			initFilterByDate();
			const TRANSACTION_ID = $(DOMClasses.searchField).val().toUpperCase().trim().replace(/\s/g, '');
			initSearchRequest(TRANSACTION_ID);
		}
	}
};

$(DOMIds.filterDatePicker).flatpickr(flatPickrConfig);

const initFilterByDate = () => {
	const filterDateCookie = Cookies.get(DOMStrings.filterDateCookie);
	const filterDateStart = filterDateCookie.split('_')[0].split('T')[0];
	const filterDateEnd = filterDateCookie.split('_')[1].split('T')[0];
	const filterDateUI = localStorage.getItem(DOMStrings.filterDateCookie);
	flatPickrConfig.defaultDate = [filterDateStart, filterDateEnd];

	if ($(DOMIds.filterSelectedDate).length > 0) {
		$(DOMIds.filterSelectedDate).find('.text').text(`PERIOD: ${filterDateUI}`);
	} else {
		const filterOption = `<div class="filter__option" id="filter__option-date">
										<span class="text">PERIOD: ${filterDateUI}</span> 
										<span class="close" id="close-date"><i class="fas fa-times-circle"></i></span>
									  </div>`;
		filterOptions.insertAdjacentHTML('beforeend', filterOption);
	}

	$(DOMIds.filterCloseDate).click(function() {
		clearDateFilter();
		setTimeout(() => {
			$(DOMIds.filterSelectedDate).remove();
			removeFilterOptionsContainer();
		}, 200);
		const TRANSACTION_ID = $(DOMClasses.searchField).val().toUpperCase().trim().replace(/\s/g, '');
		initSearchRequest(TRANSACTION_ID);
	});

	$(DOMClasses.filterOptionsContainer).addClass(DOMStrings.filterShow);
};

const clearDateFilter = () => {
	$(DOMIds.filterSelectedDate).fadeOut();
	Cookies.remove(DOMStrings.filterDateCookie, { path: CONTEXT });
	localStorage.removeItem(DOMStrings.filterDateCookie);
};

/** FILTER TABLE BY DATE: END*/



/** FILTER TABLE BY EMPLOYEE: START*/

const initFilterByEmployee = () => {
	const filterEmployeeId = Cookies.get(DOMStrings.filterEmployeeCookie);
	const filterEmployeeName = localStorage.getItem(DOMStrings.filterEmployeeCookie);
	$(DOMIds.filterEmployeePicker).multiselect('select', filterEmployeeId);

	if ($(DOMIds.filterSelectedEmployee).length > 0) {
		$(DOMIds.filterSelectedEmployee).find('.text').text(`ISSUED BY: ${filterEmployeeName}`);
	} else {
		const filterOption = `<div class="filter__option" id="filter__option-employee">
										<span class="text">ISSUED BY: ${filterEmployeeName}</span> 
										<span class="close" id="close-employee"><i class="fas fa-times-circle"></i></span>
									  </div>`;
		filterOptions.insertAdjacentHTML('beforeend', filterOption);
	}

	$(DOMIds.filterCloseEmployee).click(function() {
		clearEmployeeFilter();

		setTimeout(() => {
			$(DOMIds.filterSelectedEmployee).remove();
			removeFilterOptionsContainer();
		}, 200);
		const TRANSACTION_ID = $(DOMClasses.searchField).val().toUpperCase().trim().replace(/\s/g, '');
		initSearchRequest(TRANSACTION_ID);
	});

	$(DOMClasses.filterOptionsContainer).addClass(DOMStrings.filterShow);
};

$(DOMIds.filterEmployeePicker).multiselect({
	enableFiltering: true,
	enableCaseInsensitiveFiltering: false,
	onChange: function(element, checked) {
		const employeeId = this.$select.val();
		if (element[0].attributes.getNamedItem('data-name')) {
			const employeeName = element[0].attributes.getNamedItem('data-name').value;
			Cookies.set(DOMStrings.filterEmployeeCookie, employeeId, { path: CONTEXT });
			localStorage.setItem(DOMStrings.filterEmployeeCookie, employeeName);

			initFilterByEmployee();
		} else {
			Cookies.remove(DOMStrings.filterEmployeeCookie, { path: CONTEXT });
			localStorage.removeItem(DOMStrings.filterEmployeeCookie);
			clearEmployeeFilter();
		}

		const TRANSACTION_ID = $(DOMClasses.searchField).val().toUpperCase().trim().replace(/\s/g, '');
		initSearchRequest(TRANSACTION_ID);
	}
});

const clearEmployeeFilter = () => {
	$(DOMIds.filterSelectedEmployee).fadeOut();
	Cookies.remove(DOMStrings.filterEmployeeCookie, { path: CONTEXT });
	localStorage.removeItem(DOMStrings.filterEmployeeCookie);
};

/** FILTER TABLE BY EMPLOYEE: END*/


/** FILTER TABLE BY SIZE: START*/

$(DOMIds.filterSizePicker).multiselect({
	enableFiltering: false,
	enableCaseInsensitiveFiltering: false,
	onChange: function(element, checked) {
		
		const rowSize = this.$select.val();
		Cookies.set(DOMStrings.filterSizeCookie, rowSize, { path: CONTEXT });

		const TRANSACTION_ID = $(DOMClasses.searchField).val().toUpperCase().trim().replace(/\s/g, '');
		initSearchRequest(TRANSACTION_ID);
	}
});

/** FILTER TABLE BY SIZE: END*/


const removeFilterOptionsContainer = () => {
	if ($(DOMClasses.filterOption).length === 0) $(DOMClasses.filterOptionsContainer).removeClass(DOMStrings.filterShow);
};


const initCookies = () => {
	if (typeof Cookies.get(DOMStrings.filterSizeCookie) !== 'undefined')
		$(DOMIds.filterSizePicker).multiselect('select', Cookies.get(DOMStrings.filterSizeCookie));
	else {
		Cookies.set(DOMStrings.filterSizeCookie, 10, { path: CONTEXT });
		$(DOMIds.filterSizePicker).multiselect('select', 10);
	}

	if (typeof Cookies.get(DOMStrings.filterEmployeeCookie) !== 'undefined') {
		initFilterByEmployee();
	}

	if (typeof Cookies.get(DOMStrings.filterDateCookie) !== 'undefined') {
		initFilterByDate();
	}
};
initCookies();




//TRIGGER FIND FINCTION WHEN USER CLICKS ON ENTER KEY
$(DOMClasses.searchField).on(DOMEvents.keyDown, (event) => {
	if (event.keyCode === 13) {
		event.preventDefault(); // Ensure it is only this code that runs
		initSearchRequest($(this).val());
	}
});


//FUNCTION TO GET INPUT VALUES FROM SEARCH FIELD
$(DOMClasses.searchField).on(DOMEvents.keyUp, function() {
	initSearchRequest($(this).val());
});


const initSearchRequest = value => {
	if (value) {
		if (value.length > TRANSACTION_PREFIX_LENGTH) {
			setTimeout(() => {
				findResource();
			}, 500);
		}
	} else {
		findAllResources();
	}
};


//SEARCH FUNCTION
const findResource = () => {

	//GET USER SEARCH VALUE AND REMOVE ANY WHITE SPACES AROUND IT
	const TRANSACTION_ID = $(DOMClasses.searchField).val().toUpperCase().trim().replace(/\s/g, '');

	//STORE SEARCH VALUE IN SESSION FOR BUILDING PAGINATION LINKS
	sessionStorage.setItem(DOMStrings.searchQueryParam, TRANSACTION_ID);

	//BUILD QUERY URL
	const URL = `${CONTEXT}api/v1/deposits/search?q=${TRANSACTION_ID}`;

	//PASS URL TO FUNCTION
	getDepositsList(URL);
};


//FIND ALL CLIENTS
const findAllResources = () => {

	//BUILD QUERY URL
	const url = `${CONTEXT}api/v1/deposits/page`;

	//REMOVE ANY REFERENCE TO 'query_param' FROM SESSIONSTORAGE
	sessionStorage.removeItem(DOMStrings.searchQueryParam);

	//PASS URL TO FUNCTION
	getDepositsList(url);
};


const getDepositsList = url => {

	$.ajax({
		url: url,
		beforeSend: () => {
			NProgress.start();
		},
		success: (response) => {			
			$('.void').remove();
			const totalElements = response.page.totalElements;
			const elements = response.content;
			const links = response.links;
			const page = response.page;
			
			if (totalElements > 0) {	
				$(DOMIds.totalRecords).text(totalElements);
				buildTable(elements);
				buildLinks(links, page);
			} else {
				
				$(DOMClasses.tableBody).html('');
				$(DOMClasses.paginationContainer).html('');
				
				const employeeMsg = typeof Cookies.get(DOMStrings.filterEmployeeCookie) !== 'undefined' ? `<u>${$(DOMIds.filterSelectedEmployee).find('.text').text()}</u>` : '';
				const periodMsg = typeof Cookies.get(DOMStrings.filterDateCookie) !== 'undefined' ? `BETWEEN <u>${localStorage.getItem(DOMStrings.filterDateCookie)}</u>` : '';
				const searchParamMsg = sessionStorage.getItem(DOMStrings.searchQueryParam) !== null ? `MATCHING <u>${sessionStorage.getItem(DOMStrings.searchQueryParam)}</u>`: '';
				const MSG = `NO DEPOSITS ${employeeMsg} ${periodMsg} ${searchParamMsg}`;
				
				const applicationBody = document.querySelector(DOMClasses.applicationBody);
				const noResultsElement = `<div class="void">
											<span class="void__text">${MSG}</span>
											<img class="void__image" alt="No Transactions" src="${CONTEXT}images/transactions.svg">
										 </div>`;
				applicationBody.insertAdjacentHTML('beforeend', noResultsElement);
			}
		},
		error: (jXHR, statusText, statusCode) => {
			swal({
				type: 'error',
				title: 'Sorry',
				text: jXHR.responseJSON.message,
				showConfirmButton: false,
				timer: 2000
			});
		},
		complete: () => {
			NProgress.done();
			NProgress.remove();
		}
	});
};


//DYNAMICALLY BUILD THE TABLE ROWS
const buildTable = deposits => {

	$(DOMClasses.tableBody).html('');
	const TABLE = document.querySelector(DOMClasses.tableBody);

	deposits.forEach((deposit) => {

		const depositId = deposit.depositId;
		const depositAccountName = deposit.depositedAccountName || DOMStrings.notAvailable;
		const depositAccountNumber = deposit.depositedAccountNumber || DOMStrings.notAvailable;
		const depositAmount = deposit.depositAmount.toFixed(1) || DOMStrings.notAvailable;
		const depositIssuedBy = setTransactionIssuedBy(deposit.employee.employeeFirstName + ' ' + deposit.employee.employeeLastName);
		const branchName = deposit.branch.branchName || DOMStrings.notAvailable;
		const depositDate = new Date(deposit.depositDate).toGMTString() || DOMStrings.notAvailable;

		const row = `<tr class="transaction__record" id=${depositId}>
						<td class="transaction__number">
							<a href="${CONTEXT}deposits/${depositId}" title="#${depositId}">#${depositId}</a>
						</td>
						<td class="transaction__account-number">
							<a href="${CONTEXT}accounts/${depositAccountNumber}" title="${depositAccountNumber}">#${depositAccountNumber}</a>
						</td>
						<td class="transaction__account-name" title="${depositAccountName}">${depositAccountName}</td>									
						<td class="transaction__amount ghana-cedi-symbol" title="GH₵ ${depositAmount}">${depositAmount}</td>						
						${depositIssuedBy}
						<td class="transaction__branch" title="${branchName}">${branchName}</td>
						<td class="transaction__date" title="${depositDate}">${depositDate}</td>
					</tr>`;

		TABLE.insertAdjacentHTML('beforeend', row);
	});
};


//DYNAMICALLY BUILD THE PAGINATION LINKS
const buildLinks = (links, page) => {

	$(DOMClasses.paginationContainer).html('');
		
	if (document.querySelector(DOMClasses.paginationContainer) === null) {
		$('.void').remove();
		const applicationBody = document.querySelector(DOMClasses.applicationBody);
		const navigation = `<nav class="pagination-nav" aria-label="">
								<ul class="pagination"></ul>
							</nav>`;
		applicationBody.insertAdjacentHTML('beforeend', navigation);
	}

	const pagination = document.querySelector(DOMClasses.paginationContainer);

	links.forEach(link => {
		const pageNumber = page.number;
		const totalPages = page.totalPages;
		const URL = buildLinkUrl(link.href);
		switch(link.rel) {
			case "first": {
				//Building first link
				const linkStatus = pageNumber === 0 ? 'disabled' : '';
				const anchor = pageNumber === 0 ? `<a href="javascript:void(0);">Previous</a>` : `<a href="${URL}">Previous</a>`;
				const firstLink = `<li class="${linkStatus}">
								      ${anchor}
								  </li>`;
				pagination.insertAdjacentHTML('beforeend', firstLink);		
			}				
			break;
			case "self": {
				//Building first link
				const linkStatus = pageNumber === 0 ? 'active' : '';
				const anchor = pageNumber === 0 ? `<a href="javascript:void(0);"><span>${pageNumber + 1}</span></a>` : `<a href="${URL}"><span>${pageNumber + 1}</span></a>`;
				const selfLink = `<li class="${linkStatus}">
								      ${anchor}
								  </li>`;
				pagination.insertAdjacentHTML('beforeend', selfLink);		
			}				
			break;
			case "next": {
				//Building middle links
				if(totalPages > 3) {
					for(let i = 1; i < 2; i++) {
						const linkStatus = pageNumber === i ? 'active' : '';
						const anchor  = pageNumber === i ? `<a href="javascript:void(0);"><span>${i + 1}</span></a>` : `<a href="${URL}"><span>${i + 1}</span></a>`;
						const middleLink = `<li class="${linkStatus}">
										       ${anchor}
										  	</li>`;	
						pagination.insertAdjacentHTML('beforeend', middleLink);	
					}
				} else {
					for(let i = 1; i < 2; i++) {
						const linkStatus = pageNumber === i ? 'active' : '';
						const anchor  = pageNumber === i ? `<a href="javascript:void(0);"><span>${i + 1}</span></a>` : `<a href="${URL}"><span>${i + 1}</span></a>`;
						const middleLink = `<li class="${linkStatus}">
										      ${anchor}
										  	</li>`;
						pagination.insertAdjacentHTML('beforeend', middleLink);
					}
				}
			}
			break;
			case "last": {				
				//Building last link
				const linkStatus = pageNumber + 1 === totalPages ? 'disabled' : '';
				const anchor = pageNumber + 1 === totalPages ? `<a href="javascript:void(0);">Next</a>` :  `<a href="${URL}">Next</a>`;
				const lastLink = `<li class="${linkStatus}">
								      ${anchor}
								  </li>`;
				pagination.insertAdjacentHTML('beforeend', lastLink);
			}				
			break;
		}		
	});
};


//DYNAMICALLY GENERATE THE URLS FOR THE PAGINATION LINKS
const buildLinkUrl = url => {

	const link = url.replace('api/v1/','').replace('/page','');
    if(link.includes('search')) {		

		//GET QUERY PARAMETERS
		const inputParam = $(DOMClasses.searchField).val().toUpperCase().trim().replace(/\s/g, '');
		const sessionParam = sessionStorage.getItem(DOMStrings.searchQueryParam);
		//SPLIT URL
		const leftLink = link.split('?')[0];
		const rightLink = link.split('?')[1];
		
		//BUILD URL
		if (inputParam === `` && sessionParam) {			
			return leftLink.concat(`?q=${sessionParam}&`).concat(rightLink).split(',')[0];
		} 
		
		return leftLink.concat(`?q=${inputParam}&`).concat(rightLink).split(',')[0];
	}
	
	return link.split(',')[0];
};


const setTransactionIssuedBy = employeeName => {
	if (USER_ROLE.toLowerCase().includes(DOMStrings.roleManager))
		return `<td class="transaction__issuer" title="${employeeName}">${employeeName}</td>`;
	return ``;
};