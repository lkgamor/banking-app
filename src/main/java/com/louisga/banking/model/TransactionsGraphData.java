package com.louisga.banking.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = TransactionsGraphData.TransactionsGraphDataBuilder.class)
public class TransactionsGraphData {

	private LoanGraphData loansGraphData;
	
	private DepositGraphData depositGraphData;
	
	private WithdrawalGraphData withdrawalGraphData;
	
	@Data
	public static class LoanGraphData {
		private String name = "Loans";
		private List<Long> data;
	}
	
	@Data
	public static class DepositGraphData {
		private String name = "Deposits";
		private List<Long> data;
	}

	@Data
	public static class WithdrawalGraphData {
		private String name = "Withdrawals";
		private List<Long> data;
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class TransactionGraphDatabuilder {}
}
