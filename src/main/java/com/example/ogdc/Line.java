package com.example.ogdc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import io.jsonwebtoken.lang.Assert;

/**
 * A child of {@link Overgoods} that represents some quantity stuff. Said stuff may be itemized with great care and
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
  private Set<String> serialNumbers;
  private Item item;

  /**
   * @return
   */
  public int getQty() {
    // Not itemized when serialNumbers is null; not itemized uses miscPieceCount as the quantity. Otherwise, the number
    // of serial numbers in the set is the quantity. NOTE: serial number can be made up by DCAgent (eg. item1, item2,
    // ...).
    return isItemized() ? serialNumbers.size() : (miscPieceCount <= 0 ? 1 : miscPieceCount);
  }

  /**
   * @return a basic description of the contents represents by this line, never null or blank.
   */
  public String getDescription() {
    return description;
  }

  public boolean isItemized() {
    return getSerialNumbers().size() > 0;
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

  /**
   * Set some approximation of the number of items when not itemizing.
   * 
   * @param miscPieceCount
   * @return this
   * 
   * @throws IllegalStateException
   *           if this line is {@link #isItemized() itemized}.
   * @throws IllegalArgumentException
   *           if miscPieceCount is less then or equal to zero.
   */
  // TODO: determine the validity of this piece count estimate....not sure where it would provide value...
  Line setMiscPieceCount(int miscPieceCount) {
    Assert.state(!isItemized(), "Invalid state; itemized lines don't get a miscPieceCount.");
    Assert.isTrue(miscPieceCount > 0, "must be some quantity greater than zero.");
    this.miscPieceCount = miscPieceCount;
    return this;
  }

  Line addSerialNumber(String serialNumber) {
    Assert.state(miscPieceCount == 0, "Invalid state; itemized lines don't get a miscPieceCount.");
    Assert.hasText(serialNumber, "null/blank serialNumber");
    miscPieceCount = 0;// reset value
    if (serialNumbers == null) {
      serialNumbers = new HashSet<>();
    }
    Assert.isTrue(!serialNumbers.contains(serialNumber), "Already included: " + serialNumber);
    serialNumbers.add(serialNumber);
    return this;
  }

  Line removeSerialNumber(String serialNumber) {
    if (!serialNumbers.remove(serialNumber)) {
      throw new IllegalArgumentException("Unknow serialNumber: " + serialNumber);
    }
    return this;
  }

  Line updateDescription(String description) {
    Assert.hasText(description, "null/blank description");
    this.description = description.trim();
    return this;
  }

  @Override
  public String toString() {
    return getDescription() + " [qty=" + getQty() + ", serialNumbers="
        + getSerialNumbers().stream().collect(Collectors.joining(", ", "[", "]")) + ", item=" + item + "]";
  }

  /**
   * A package local Line builder
   */
  static final class Builder {
    private Line value;

    public Builder(String description) {
      this.value = new Line();
      this.value.description = description;
    }

    public Line build() {
      return value;
    }

    public Builder setMiscPieceCount(int cnt) {
      Assert.state(value.serialNumbers == null,
          "Invalid builder state; can not set miscPieceCount on line when serialNumbers is not null.");
      Assert.isTrue(cnt > 0, "invalid miscPieceCount: " + cnt + "; must be > 0");
      value.miscPieceCount = cnt;
      return this;
    }

    public Builder addSerialNumber(String serialNumber) {
      Assert.state(value.miscPieceCount == 0, "Invalid builder state; can not add serialNumber");
      Assert.hasText(serialNumber, "null/blank serialNumber");
      if (value.serialNumbers == null) {
        value.serialNumbers = new HashSet<>();
      }
      value.serialNumbers.add(serialNumber);
      return this;
    }

    public Builder setItem(Item item) {
      value.item = item;
      return this;
    }
  }
}
