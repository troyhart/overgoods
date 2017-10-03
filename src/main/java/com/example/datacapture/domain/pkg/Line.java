package com.example.datacapture.domain.pkg;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.example.common.commerce.Money;

import io.jsonwebtoken.lang.Assert;

/**
 * A child of {@link Package} that represents some quantity stuff. Said stuff may be itemized with great care and
 * detail, or may be very tersely described.
 * 
 * <p>
 * All lines will include a {@link #getDescription() description} and a {@link #getQty() quantity}. It may optionally
 * include an {@link #getItem() item}. An itemized line on the other hand will include a set of serial numbers to
 * uniquely identify each discrete instance of the itemized thing.
 * 
 * @author troyh
 *
 */
public class Line {

  private String description;
  private int miscPieceCount;
  private Itemization itemization;

  /**
   * @return
   */
  public int getQty() {
    return isItemized() ? itemization.getSerialNumbers().size() : (miscPieceCount <= 0 ? 1 : miscPieceCount);
  }

  /**
   * @return a basic description of the contents represents by this line, never null or blank.
   */
  public String getDescription() {
    return description;
  }

  public boolean isItemized() {
    return itemization != null;
  }

  public Itemization getItemization() {
    return itemization;
  }

  @Override
  public String toString() {
    return String.format("{\"description\": \"%s\", \"qty\": %s, %s", getDescription(), getQty(),
        isItemized() ? "{" + itemization + "}" : "\"miscellaneous\": true}");
  }

  /**
   * A package local Line builder
   */
  static final class Builder {
    private Line value;

    public Builder() {
      value = new Line();
    }

    public Line build() {
      if (value.itemization != null) {
        value.miscPieceCount = 0;
        Assert.state(!value.itemization.getSerialNumbers().isEmpty(), "unspecified serial numbers");
        Assert.state(value.itemization.getEstimatedUnitValue() != null, "unspecified unit value estimate");
      }

      return value;
    }

    private Itemization getItemization() {
      if (value.itemization == null) {
        value.itemization = new Itemization();
      }
      return value.itemization;
    }

    public Builder setDescription(String description) {
      Assert.hasText(description, "null description");

      value.description = description;

      return this;
    }

    public Builder setMiscPieceCount(int cnt) {
      Assert.isTrue(cnt > 0, "invalid miscPieceCount: " + cnt + "; must be > 0");

      value.miscPieceCount = cnt;

      return this;
    }

    public Builder setEstimatedUnitValue(Money estimate) {
      Assert.notNull(estimate, "null estimate");
      getItemization().estimatedUnitValue = estimate;
      return this;
    }

    public Builder addSerialNumber(String serialNumber) {
      Assert.hasText(serialNumber, "null/blank serialNumber");

      if (getItemization().serialNumbers == null) {
        getItemization().serialNumbers = new HashSet<>();
      }
      getItemization().serialNumbers.add(serialNumber);

      return this;
    }

    public Builder setItem(Item item) {
      Assert.notNull(item, "null item");

      getItemization().item = item;

      return this;
    }
  }

  public static final class Itemization {

    private Money estimatedUnitValue;
    private Set<String> serialNumbers;
    private Item item;

    public Money getEstimatedUnitValue() {
      return estimatedUnitValue;
    }

    /**
     * An empty set implies that this line represents content that is considered low value and not worth the effort of
     * describing with any real detail. All content worth describing will
     * 
     * @return the set of serial numbers.
     */
    public Set<String> getSerialNumbers() {
      return (serialNumbers == null) ? Collections.emptySet() : Collections.unmodifiableSet(serialNumbers);
    }

    public Item getItem() {
      return item;
    }

    @Override
    public String toString() {
      return "{\"estimatedUnitValue\": \"" + estimatedUnitValue + "\", \"serialNumbers\":" + serialNumbers + ", \"item\": "
          + item + "}";
    }


  }
}
