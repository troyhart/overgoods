package com.example.infrastructure;


import java.io.Serializable;

public class DomainEntityId implements Serializable {

  private static final long serialVersionUID = 1L;

  private String rawId;

  protected DomainEntityId(String rawId) {
    this.rawId = rawId;
  }

  public String getRawId() {
    return rawId;
  }

  @Override
  public String toString() {
    return rawId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((rawId == null) ? 0 : rawId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DomainEntityId)) {
      return false;
    }
    DomainEntityId other = (DomainEntityId) obj;
    if (rawId == null) {
      if (other.rawId != null) {
        return false;
      }
    }
    else if (!rawId.equals(other.rawId)) {
      return false;
    }
    return true;
  }

}
