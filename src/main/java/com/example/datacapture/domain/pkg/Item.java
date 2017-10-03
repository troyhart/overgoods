package com.example.datacapture.domain.pkg;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.jsonwebtoken.lang.Assert;

public class Item {
  public static enum Attribute {
    ARTIST, ACTOR, DIRECTOR, PRODUCER, LABEL, GENRE, CLOTHING_SIZE;
  }

  private String productNumber;
  private String productName;
  private String detailedDescription;
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

  public String getAttributeValue(Attribute attr) {
    Assert.isTrue(contains(attr), "Invalid attribaute; check attributeSet() and only ask for defined attributes.");
    return attributes.get(attr);
  }

  public boolean contains(Attribute attr) {
    return attributes == null ? false : attributes.containsKey(attr);
  }

  public Set<Attribute> getAttributeSet() {
    return attributes == null ? Collections.emptySet() : Collections.unmodifiableSet(attributes.keySet());
  }


  @Override
  public String toString() {
    // @formatter:off

    return 
        "{\"productNumber\": " + getProductNumber() 
        + ", \"productName\": \"" + getProductName()
        + "\", \"detailedDescription\": \"" + getDetailedDescription() 
        + "\", \"attributes\": " + getAttributeSet().stream().map(a -> 
                  String.format("\"%s\": \"%s\"", a.name(), getAttributeValue(a)))
                  .collect(Collectors.joining(", ", "{", "}"))
        + "}";
    
    // @formatter:on
  }


  /**
   * A package local Item builder
   */
  public static final class Builder {
    private Item value;

    public Builder() {
      value = new Item();
    }

    public Item build() {
      return value;
    }

    public Builder setProductNumber(String productNumber) {
      Assert.hasText(productNumber, "null/blank productNumber");
      value.productNumber = productNumber;
      return this;
    }

    public Builder setProductName(String productName) {
      Assert.hasText(productName, "null/blank productName");
      value.productName = productName;
      return this;
    }

    public Builder setDetailedDescription(String detailedDescription) {
      Assert.hasText(detailedDescription, "null/blank detailedDescription");
      value.detailedDescription = detailedDescription;
      return this;
    }

    public Builder addAttribute(Attribute attribute, String attrVal) {
      Assert.notNull(attribute, "null attribute");
      Assert.hasText(attrVal, "null/blank attribute value");
      Assert.state(!value.contains(attribute), "Invalid attribute; already defined");

      if (value.attributes == null) {
        value.attributes = new HashMap<>();
      }
      value.attributes.put(attribute, attrVal);
      return this;
    }
  }
}
