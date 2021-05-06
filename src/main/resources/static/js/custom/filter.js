import { DOMStrings, DOMClasses, DOMIds } from './data.js';
import { initSearchRequest } from './loans.js';
import { initSearchRequest } from './deposits.js';
import { initSearchRequest } from './withdrawals.js';
import { initSearchRequest } from './transactions.js';

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
			
			initSearchRequest();
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
		
		initSearchRequest();
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
		
		initSearchRequest();
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
		}
		else {
			Cookies.remove(DOMStrings.filterEmployeeCookie, { path: CONTEXT });
			localStorage.removeItem(DOMStrings.filterEmployeeCookie);
			clearEmployeeFilter();
		}

		initSearchRequest();
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

		initSearchRequest();
	}
});

/** FILTER TABLE BY SIZE: END*/


const removeFilterOptionsContainer = () => {
	if ($(DOMClasses.filterOption).length === 0) $(DOMClasses.filterOptionsContainer).remove();
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
		initSearchRequest();
	}
});


//FUNCTION TO GET INPUT VALUES FROM SEARCH FIELD
$(DOMClasses.searchField).on(DOMEvents.keyUp, function() {
	initSearchRequest();
});