package com.example.common.commerce;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.util.Assert;

/**
 * An immutable monetary value comprised of a {@link BigDecimal} and a {@link Currency}, plus a set of operations to
 * enable calculations and combinations.
 * 
 * @author troyh
 *
 */
public class Money {
  private static final RoundingMode ROUNDINGMODE = RoundingMode.HALF_EVEN;
  private static final BigDecimal ZERO = new BigDecimal(0);

  public static enum Currency {
    USD('$'), YEN('\u00A5');

    private char symbol;

    private Currency(char symbol) {
      this.symbol = symbol;
    }

    public char getSymbol() {
      return symbol;
    }
  }

  private BigDecimal amount;
  private Currency currency;

  public Money(BigDecimal amount) {
    this(amount, Currency.USD);
  }

  public Money(BigDecimal amount, Currency currency) {
    Assert.notNull(amount, "null amount");
    Assert.notNull(currency, "null currency");
    this.amount = amount.setScale(4, ROUNDINGMODE);
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return getAmount(false);// by default return the value with two digits of precision.
  }

  public BigDecimal getAmount(boolean fullscale) {
    return fullscale ? amount : amount.setScale(2, ROUNDINGMODE);
  }

  public Currency getCurrency() {
    return currency;
  }

  public boolean isPositiveValue() {
    return getAmount().doubleValue() >= 0;
  }

  public Money add(Money value) {
    Assert.notNull(value, "null value");
    validateCurrency(value);
    return new Money(this.amount.add(value.amount), this.currency);
  }

  public Money subtract(Money value) {
    Assert.notNull(value, "null value");
    validateCurrency(value);
    return new Money(this.amount.subtract(value.amount), this.currency);
  }

  public Money multiply(BigDecimal multiplicand) {
    Assert.notNull(multiplicand, "null multiplicand");
    return new Money(this.amount.multiply(multiplicand), this.currency);
  }

  public Money divide(BigDecimal divisor) {
    Assert.notNull(divisor, "null divisor");
    Assert.isTrue(divisor.compareTo(ZERO) != 0, "can not divide by zero");
    return new Money(this.amount.divide(divisor), this.currency);
  }

  private void validateCurrency(Money value) {
    Assert.isTrue(this.currency == value.currency,
        String.format("Mixed currency (%s, %s) operation not supported!", this.currency, value.currency));
  }

  @Override
  public String toString() {
    return toString(false);
  }

  public String toString(boolean fullscale) {
    return String.format("%s%s%,." + (fullscale ? (amount.scale() + "f") : "2f"), (!isPositiveValue() ? "-" : ""),
        getCurrency().getSymbol(), getAmount(fullscale).abs());
  }

  public static void main(String[] args) {
    Money m0 = new Money(new BigDecimal(10000000.12345), Currency.USD);
    System.out.println("m0: " + m0.toString(true));
    Money m1 = new Money(new BigDecimal(10.67899), Currency.YEN);
    System.out.println("m1: " + m1.toString(true));
    Money m2 = new Money(new BigDecimal(-2), Currency.YEN);
    System.out.println("m2: " + m2.toString(true));
    System.out.println("");
    BigDecimal ZERO = new BigDecimal(0);
    BigDecimal NEG_ONE = new BigDecimal(-1);
    Money ma1 = m1;
    Money ma2 = m2;
    BigDecimal bda1;
    System.out.println(String.format("m1 + m2 ->\t%s\t+\t%s\t=\t%s", ma1, ma2, ma1.add(ma2).toString(true)));
    System.out.println(String.format("m2 + m1 ->\t%s\t+\t%s\t=\t%s", ma2, ma1, ma2.add(ma1).toString(true)));
    System.out.println(String.format("m1 - m2 ->\t%s\t-\t%s\t=\t%s", ma1, ma2, ma1.subtract(ma2).toString(true)));
    System.out.println(String.format("m2 - m1 ->\t%s\t-\t%s\t=\t%s", ma2, ma1, ma2.subtract(ma1).toString(true)));
    ma1 = m1.multiply(NEG_ONE);
    bda1 = ZERO;
    System.out.println(String.format("-m1 * 0 ->\t%s\t*\t%s\t=\t%s", ma1, bda1, ma1.multiply(bda1).toString(true)));
    bda1 = new BigDecimal(-2);
    System.out.println(String.format("-m1 / -2 ->\t%s\t/\t%s\t=\t%s", ma1, bda1, ma1.divide(bda1).toString(false)));
    ma1 = m0;
    ma2 = m2;
    System.out.println("");
    System.out.println("m0 + m2 ->\twill fail for mixed currency operation...");
    System.out.println(String.format("If you can read this message the something is wrong!\nm0 + m2:\t%s\t+\t%s\t=\t%s",
        ma1, ma2, ma1.add(ma2).toString(true)));
  }
}
