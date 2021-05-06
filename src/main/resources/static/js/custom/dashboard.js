import {DOMStrings, DOMClasses, DOMIds, DOMElements, DOMEndpoints, DOMEvents} from './data.js';

(()=> {
		
	const buildOptionsObject = (series = [], categories = []) => {
		const options = {
			series: series,
			chart: {
				type: 'bar',
				height: 230
			},
			plotOptions: {
			  bar: {
			    horizontal: false,
			    columnWidth: '55%',
			    endingShape: 'rounded'
			  },
			},
			dataLabels: {
			  enabled: false
			},
			stroke: {
			  show: true,
			  width: 2,
			  colors: ['transparent']
			},
			xaxis: {
			  categories: categories,
			},
			yaxis: {
			  title: {
			    text: 'Number of Transactions'
			  }
			},
			fill: {
			  opacity: 1
			},
			tooltip: {
			  y: {
			    formatter: function (val) {
			      return + val + " transactions"
			    }
			  }
			}
		};
		
		return options;
	};
	
	const chart = new ApexCharts(document.querySelector("#graph"), buildOptionsObject());
	chart.render();
	
	/**
	 * CHECKBOX EVENT TO GET CHECKED FILTERS
	 * ---------------------------------------------
	 */
	$(DOMElements.filterRadioButton).change(function(){
		
		/** Get the {value} related to the currently checked filter. */
		const filter = $(this).val();
		const isChecked = $(this).is(':checked');
	
		if (isChecked) {
			 switch (filter) {
				case DOMStrings.filterDay:
					$(DOMIds.currentFilter).text(`RECORDS FOR TODAY`);
				break;
				case DOMStrings.filterWeek:
					$(DOMIds.currentFilter).text(`RECORDS FOR THIS WEEK`);
				break;
				case DOMStrings.filterMonth:
					$(DOMIds.currentFilter).text(`RECORDS FOR THIS MONTH`);
				break;
				case DOMStrings.filterYear:
					$(DOMIds.currentFilter).text(`RECORDS FOR THIS YEAR`);
				break;
				case DOMStrings.filterAll:
					$(DOMIds.currentFilter).text(`ALL RECORDS`);
				break;
				default:
					$(DOMIds.currentFilter).text(`ALL RECORDS`);
				break;
			}	        
			loadDataForGraph(filter);	        
		} 	
	});
	
	const loadDataForGraph = param => {
		$.ajax({
			url: `${CONTEXT}${DOMEndpoints.getTotalTransactionsForGraph}?q=${param}`,
			success: (series)=> {
				
				const totalLoans = series.filter(value => value.name.includes('Loan'))[0].data.reduce((a, b) => a + b, 0);     
				const totalDeposits = series.filter(value => value.name.includes('Deposit'))[0].data.reduce((a, b) => a + b, 0);     
				const totalWithdrawals = series.filter(value => value.name.includes('Withdrawal'))[0].data.reduce((a, b) => a + b, 0);     
				const totalTransactions = totalLoans + totalDeposits + totalWithdrawals;     

				$(DOMIds.totalLoans).text(totalLoans);
				$(DOMIds.totalDeposits).text(totalDeposits);
				$(DOMIds.totalWithdrawals).text(totalWithdrawals);
				$(DOMIds.totalTransactions).text(totalTransactions);
				
				let categories = new Array();
	
				switch (param) {
					case `day`: 
						categories = ['Today'];
					break;
					
					case `week`: 
						categories = ['Mon', 'Tue', 'Wed', 'Thur', 'Friday'];
					break;
					
					case `month`: 
						categories = ['Week 1', 'Week 2', 'Week 3', 'Week 4'];
					break;
					
					case `year`: 
						categories = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
					break;
					
					case `all`:
					default: 
						categories = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
					break;
				}	
							
				chart.updateOptions(buildOptionsObject(series, categories));
				
			},
			error: ()=> {
				$(DOMIds.totalLoans).text(DOMStrings.notAvailable);
				$(DOMIds.totalDeposits).text(DOMStrings.notAvailable);
				$(DOMIds.totalWithdrawals).text(DOMStrings.notAvailable);
				$(DOMIds.totalTransactions).text(DOMStrings.notAvailable);
			}
		});
	};
	
	const loadAllAccounts = () => {
		$.ajax({
			url: `${CONTEXT}${DOMEndpoints.getTotalAccounts}`,
			success: (totalAccounts)=> {	
				$(DOMIds.totalAccounts).text(totalAccounts);
			},
			error: ()=> {
				$(DOMIds.totalAccounts).text(DOMStrings.notAvailable);
			}
		});
	};
	
	
	const loadAllEmployees = () => {
		$.ajax({
			url: `${CONTEXT}${DOMEndpoints.getTotalEmployees}`,
			success: (totalEmployees)=> {	
				$(DOMIds.totalEmployees).text(totalEmployees);
			},
			error: ()=> {
				$(DOMIds.totalEmployees).text(DOMStrings.notAvailable);
			}
		});
	};
	
	const loadRecentTransactions = () => {
		$.ajax({
			url: `${CONTEXT}${DOMEndpoints.getRecentTransactions}`,
			beforeSend: ()=> {
				$('.recent-transactions__content').addClass('loading');
			},
			success: (recentTransactions)=> {
				$('.recent-transactions__content').removeClass('loading');
				if(recentTransactions.totalElements > 0) {
					const recentTransactionsContainer = document.querySelector('.recent-transactions__content');
					recentTransactions.content.forEach(transaction => {
						const {totalBalance, transactionAccountName, transactionAccountNumber, transactionAmount, transactionDate, transactionType, transactionId} = transaction;			
						const recentTransactionCard = `<a href="${CONTEXT}${transactionType.toLowerCase()}s/${transactionId}" title="View Transaction" class="recent-transactions__card">
							                                <span class="recent-transactions__type ${transactionType.toLowerCase()}"></span>
							                                <div class="recent-transactions__details">
							                                    <span class="account-num">${transactionType}</span>
							                                    <span class="account-date">${new Date(transactionDate).toLocaleDateString()}</span>
							                                </div>
							                                <span class="recent-transactions__amount">${transactionAmount}</span>			                                
							                            </a>`;
						recentTransactionsContainer.insertAdjacentHTML('beforeend', recentTransactionCard);
					});
				} else {
					$('.recent-transactions__content').addClass('no-transactions');
					$('.recent-transactions__content').html('<i class="far fa-clipboard"></i>');
				}
			},
			error: ()=> {
				$(DOMIds.totalTransactions).text(DOMStrings.notAvailable);
			}
		});
	};

	document.onreadystatechange = () => {
		if (document.readyState === 'complete') {								
			setTimeout(()=> {								
				$('.dashboard__body--bottom').removeClass('loading-graph', 1000);				
			},1000);
		}
	};
	
	loadAllAccounts();
	if (USER_ROLE.toLowerCase().includes(DOMStrings.roleManager)) loadAllEmployees();
	loadRecentTransactions();
	loadDataForGraph('all');	
})();