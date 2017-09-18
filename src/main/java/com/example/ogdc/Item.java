package com.example.ogdc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.core.Money;

import io.jsonwebtoken.lang.Assert;

public class Item {
  public static enum Attribute {
    ARTIST, ACTOR, DIRECTOR, PRODUCER, LABEL, GENRE, CLOTHING_SIZE;
  }

  private String productNumber;
  private String productName;
  private String detailedDescription;
  private Money estimatedValue;
  private Map<Attribute, String> attributes;

  public String getProductNumber() {
    return productNumber;
  }

  public String getProductName() {
    return productName;
  }

  public String getDetailedDescription() {
    return detailedDescription;
  }

  public Money getEstimatedValue() {
    return estimatedValue;
  }

  public String get(Attribute attr) {
    Assert.isTrue(contains(attr), "Invalid attribaute; check attributeSet() and only ask for defined attributes.");
    return attributes.get(attr);
  }

  public boolean contains(Attribute attr) {
    return attributes == null ? false : attributes.containsKey(attr);
  }

  public Set<Attribute> attributeSet() {
    return attributes == null ? Collections.emptySet() : attributes.keySet();
  }


  @Override
  public String toString() {
    return getProductName() + " [productNumber=" + getProductNumber() + ", detailedDescription="
        + getDetailedDescription() + ", attributes=" + attributeSet().stream()
            .map(a -> String.format("%s='%s'", a.name(), get(a))).collect(Collectors.joining("; ", "[", "]"))
        + ", estimatedValue=" + getEstimatedValue() + "]";
  }


  /**
   * A package local Item builder
   */
  static final class Builder {
    private Item value;

    public Builder() {
      this.value = new Item();
    }

    public Item build() {
      return value;
    }

    public Builder setProductNumber(String productNumber) {
      Assert.hasText(productNumber, "null/blank productNumber");
      this.value.productNumber = productNumber;
      return this;
    }

    public Builder setProductName(String productName) {
      Assert.hasText(productName, "null/blank productName");
      this.value.productName = productName;
      return this;
    }

    public Builder setDetailedDescription(String detailedDescription) {
      Assert.hasText(detailedDescription, "null/blank detailedDescription");
      this.value.detailedDescription = detailedDescription;
      return this;
    }

    public Builder setEstimatedValue(Money estimatedValue) {
      Assert.notNull(estimatedValue, "null estimatedValue");
      this.value.estimatedValue = estimatedValue;
      return this;
    }

    public Builder addAttribute(Attribute attribute, String value) {
      Assert.notNull(attribute, "null attribute");
      Assert.hasText(value, "null/blank value");
      Assert.state(this.value.contains(attribute), "Invalid attribute; already defined");

      if (this.value.attributes == null) {
        this.value.attributes = new HashMap<>();
      }
      this.value.attributes.put(attribute, value);
      return this;
    }
  }
}
