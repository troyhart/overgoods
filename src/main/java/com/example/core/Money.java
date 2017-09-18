package com.example.core;

import java.math.BigDecimal;

import org.springframework.util.Assert;

/**
 * An immutable monetary value comprised of a {@link BigDecimal} and a {@link Currency}, plus a set of operations to
 * enable calculations and combinations.
 * 
 * @author troyh
 *
 */
public class Money {
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

  public Money(BigDecimal amount, Currency currency) {
    this.amount = amount;
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return amount;
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
    return new Money(this.amount.divide(divisor), this.currency);
  }

  private void validateCurrency(Money value) {
    Assert.notNull(value, "null value");
    Assert.isTrue(this.currency == value.currency,
        String.format("Mixed currency (%s, %s) operation not supported!", this.currency, value.currency));
  }

  @Override
  public String toString() {
    return (!isPositiveValue() ? "-" : "") + getCurrency().getSymbol() + getAmount().abs().toString();
  }

  public static void main(String[] args) {
    Money m0 = new Money(new BigDecimal(10), Currency.USD);
    Money m1 = new Money(new BigDecimal(10), Currency.YEN);
    System.out.println("m1: " + m1);
    Money m2 = new Money(new BigDecimal(-2), Currency.YEN);
    System.out.println("m2: " + m2);
    BigDecimal bdzero = new BigDecimal(0);
    BigDecimal bdnegone = new BigDecimal(-1);
    System.out.println(String.format("%s + %s = %s", m1, m2, m1.add(m2)));
    System.out.println(String.format("%s - %s = %s", m1, m2, m1.subtract(m2)));
    System.out.println(String.format("%s + %s = %s", m2, m1, m2.add(m1)));
    System.out.println(String.format("%s - %s = %s", m2, m1, m2.subtract(m1)));
    System.out
        .println(String.format("%s * %s = %s", m1.multiply(bdnegone), bdzero, m1.multiply(bdnegone).multiply(bdzero)));
    System.out.println(
        String.format("%s / %s = %s", m1.multiply(bdnegone), bdnegone, m1.multiply(bdnegone).divide(bdnegone)));

    System.out.println(String.format("%s + %s = %s", m0, m2, m0.add(m2)));
  }
}
