package com.example.datacapture.domain.pkg;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.example.common.commerce.Money;
import com.example.datacapture.domain.pkg.Item.Attribute;

public class DescribeLineCommand extends PackageCommand {

  private String lineDescription;
  private int miscPieceCount;
  private Set<String> serialNumbers;
  private String itemNumber;
  private String itemName;
  private String detailedItemDescription;
  private Money estimatedUnitValue;
  private Map<Attribute, String> attributes;

  public String getLineDescription() {
    return lineDescription;
  }

  public DescribeLineCommand setLineDescription(String lineDescription) {
    this.lineDescription = lineDescription;
    return this;
  }

  public int getMiscPieceCount() {
    return miscPieceCount;
  }

  public DescribeLineCommand setMiscPieceCount(int miscPieceCount) {
    this.miscPieceCount = miscPieceCount;
    return this;
  }

  public Set<String> getSerialNumbers() {
    return serialNumbers == null ? Collections.emptySet() : Collections.unmodifiableSet(serialNumbers);
  }

  public DescribeLineCommand setSerialNumbers(Set<String> serialNumbers) {
    this.serialNumbers = serialNumbers;
    return this;
  }

  public String getItemNumber() {
    return itemNumber;
  }

  public DescribeLineCommand setItemNumber(String itemNumber) {
    this.itemNumber = itemNumber;
    return this;
  }

  public String getItemName() {
    return itemName;
  }

  public DescribeLineCommand setItemName(String itemName) {
    this.itemName = itemName;
    return this;
  }

  public String getDetailedItemDescription() {
    return detailedItemDescription;
  }

  public DescribeLineCommand setDetailedItemDescription(String detailedItemDescription) {
    this.detailedItemDescription = detailedItemDescription;
    return this;
  }

  public Money getEstimatedUnitValue() {
    return estimatedUnitValue;
  }

  public DescribeLineCommand setEstimatedUnitValue(Money estimatedUnitValue) {
    this.estimatedUnitValue = estimatedUnitValue;
    return this;
  }

  public Map<Attribute, String> getAttributes() {
    return attributes == null ? Collections.emptyMap() : Collections.unmodifiableMap(attributes);
  }

  public DescribeLineCommand setAttributes(Map<Attribute, String> attributes) {
    this.attributes = attributes;
    return this;
  }
}
