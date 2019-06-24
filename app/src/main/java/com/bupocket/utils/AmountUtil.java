package com.bupocket.utils;

import java.math.BigDecimal;

public class AmountUtil {
	
	/**
	 *
	 * @param amount
	 * @return
	 */
	public static String amountDivision10For8(long amount){
		return (new BigDecimal(amount).divide(new BigDecimal(100000000)).setScale(8, BigDecimal.ROUND_HALF_UP)).toString();
	}
	/**
	 *
	 * @param amount
	 * @return
	 */
	public static long amountMultiply100(String amount){
		return (new BigDecimal(amount).multiply(new BigDecimal(100))).longValue();
	}
	
	public static long amountMultiply10For8(long amount){
		return (new BigDecimal(amount).multiply(new BigDecimal(100000000))).longValue();
	}
	
	/**
	 *
	 * @param amount
	 * @return
	 */
	public static long amountMultiply10For8(String amount){
		return (new BigDecimal(amount).multiply(new BigDecimal(100000000))).longValue();
	}
	
	public static Long amountDivision10For8Long(long amount){
		return (new BigDecimal(amount).divide(new BigDecimal(100000000))).longValue();
		
	}
	
	public static String amountMultiplyAmount(String amount1,String amount2){
		return (new BigDecimal(amount1).multiply(new BigDecimal(amount2))).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public static String subtractionFee(String sourceAmount,String amount2){
		return new BigDecimal(sourceAmount).subtract(new BigDecimal(amount2)).toString();
	}

	public static double availableSubtractionFee(String sourceAmount,Double amount2){
		return (new BigDecimal(sourceAmount).subtract(new BigDecimal(amount2)).setScale(8, BigDecimal.ROUND_HALF_UP)).doubleValue();
	}
	
	public static String amountDivision10For8(String amount){
		return (new BigDecimal(amount).divide(new BigDecimal(100000000)).setScale(8, BigDecimal.ROUND_HALF_UP)).toString();
	}

	public static String amountDivision10ForDecimal(String amount, int decimal){
		return (new BigDecimal(amount).divide(new BigDecimal(Math.pow(10,decimal))).setScale(decimal, BigDecimal.ROUND_DOWN)).toString();
	}

	public static String amountAddition(String amount,String amount2){
		return new BigDecimal(amount).add(new BigDecimal(amount2)).stripTrailingZeros().toPlainString();
	}

	public static String amountSubtraction(String amount,String amount2){
		return new BigDecimal(amount).subtract(new BigDecimal(amount2)).stripTrailingZeros().toPlainString();
	}

	public static void main(String[] args) {
		System.out.println(amountMultiplyAmount("1.268","234.56"));
	}
}
