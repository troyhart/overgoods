package com.example.datacapture.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.example.common.domain.EntityId;
import com.example.datacapture.domain.agent.Agent;

public final class Package {

  private PackageId id;
  private long version = 0;
  private Cause cause;
  private Map<Long, Line> lines;
  private Set<PackageId> children;
  private Set<PackageId> parents;
  private List<PackageDataCapturedEvent> changes;
  private long lastLineId = 0;

  /**
   * For replay. Replay starts with this constructor, then invokes {@link #play(PackageDataCapturedEvent[])}.
   */
  Package() {
  }

  /**
   * A new Package with its cause and unique identifier.
   * 
   * @param id
   * @param cause
   * 
   * @see PackageFactory#newOvergood(Cause)
   */
  Package(PackageId id, Agent capturedBy, Cause cause) {
    Assert.notNull(id, "null id");
    Assert.notNull(capturedBy, "null capturedBy");
    Assert.notNull(cause, "null cause");

    PackageCreatedEvent pce = new PackageCreatedEvent(id, capturedBy, cause);

    // the invocation of apply() below is valid because this class is final! If that changes, then the package local
    // apply() and play() methods must be declared to be final!!!
    apply(pce);
  }

  public EntityId getId() {
    return id;
  }

  public long getVersion() {
    return version;
  }

  public Cause getCause() {
    return cause;
  }

  public boolean hasLines() {
    return lines != null && !lines.isEmpty();
  }

  /**
   * NOTE: line identifiers are stable, meaning that once a line is associated to an identifier, that line will always
   * be associated to that identifier until it is removed. When a line is removed, its identifier will always and
   * forever produce a null result from: {@link #getLine(long)}.
   * 
   * @return a non-null set of line identifiers. It may be empty, but never null.
   */
  public Set<Long> getLineIds() {
    return getLines().keySet().stream().collect(Collectors.toSet());
  }

  public Map<Long, Line> getLines() {
    return lines == null ? Collections.emptyMap() : Collections.unmodifiableMap(lines);
  }

  /**
   * @param id
   *          the line identifier.
   * 
   * @return the {@link Line} associated to the given identifier, or null if the line identifier is not associated with
   *         a line.
   * 
   * @see #getLineIds()
   * @see #getLines()
   */
  public Line getLine(long id) {
    return getLines().get(id);
  }

  public Set<PackageId> getChildren() {
    return children == null ? Collections.emptySet() : Collections.unmodifiableSet(children);
  }

  public Set<PackageId> getParents() {
    return parents == null ? Collections.emptySet() : Collections.unmodifiableSet(parents);
  }

  private long nextVersion() {
    return (changes == null || changes.size() == 0) ? version + 1 : changes.get(changes.size() - 1).getVersion() + 1;
  }

  private Long nextLineId() {
    return ++lastLineId;
  }

  public void addLine(Agent dcAgent, Line line) {
    LineAddedEvent lae = new LineAddedEvent(id, nextVersion(), dcAgent, line);
    apply(lae);
  }

  public void split(Agent splitBy, Cause cause, PackageFactory factory) {
    // TODO: implement me....
    throw new UnsupportedOperationException("not yet implemented..........");
  }

  public void split(Agent splitBy, Cause cause, String[] serialNumbersToCleave, PackageFactory factory) {
    // TODO: implement me....
    throw new UnsupportedOperationException("not yet implemented..........");
  }

  // ------------------------------------------------------------------------------
  // begin: event logic
  // ------------------------------------------------------------------------------

  List<PackageDataCapturedEvent> clearChanges() {
    List<PackageDataCapturedEvent> changes = this.changes;
    this.changes = null;
    return changes;
  }

  void apply(PackageDataCapturedEvent[] events) {
    Assert.isTrue(events != null && events.length > 0, "null/empty events");
    for (PackageDataCapturedEvent event : events) {
      apply(event);
    }
  }

  /**
   * {@link #play(PackageCreatedEvent) Play} the given event and record it in
   * 
   * @param event
   *          the event to apply.
   */
  void apply(PackageDataCapturedEvent event) {
    play(event);
    if (changes == null) {
      changes = new ArrayList<>();
    }
    changes.add(event);
  }

  void play(PackageDataCapturedEvent[] events) {
    Assert.isTrue(events != null && events.length > 0, "null/empty events");

    for (PackageDataCapturedEvent event : events) {
      play(event);
    }
  }

  void play(PackageDataCapturedEvent event) {
    Assert.notNull(event, "null event");
    Assert.isTrue(version + 1 == event.getVersion(),
        String.format("Invalid event; wrong version! Expecting v-%s but got v-%s", version + 1, event.getVersion()));

    this.version = event.getVersion();
    dispatch(event);
  }

  private void dispatch(PackageDataCapturedEvent event) {
    if (event.getVersion() == 1) {
      play(PackageCreatedEvent.class.cast(event));
    }
    else {
      // After PackageCreatedEvent has been played (see first dispatch case above), the id MUST BE NON-NULL!!!!!
      Assert.state(id != null, "Invalid state! Can not play the given event on an this unidentified Package; Event="
          + event.toString() + ", Package=" + this.toString());
      Assert.isTrue(id.equals(event.getAggregateId()),
          "Invalid event, wrong id! This Package id==" + id + ", but given event.id==" + event.getAggregateId());

      // handle remainder of event dispatches.
      if (LineAddedEvent.class.isInstance(event)) {
        play(LineAddedEvent.class.cast(event));

      }
      else if (LineSerialNumberAddedEvent.class.isInstance(event)) {
        play(LineSerialNumberAddedEvent.class.cast(event));

        // TODO: .........do rest of dispatching
      }
      else {
        throw new IllegalArgumentException("Unexpected event: " + event.getClass().getSimpleName());
      }
    }
  }


  // ----------------------------------------------------------
  // event specific play methods...each event type must be represented.
  // ----------------------------------------------------------

  private void play(PackageCreatedEvent event) {
    this.id = event.getAggregateId();// the only event that set's the OvergoodId.
    this.cause = event.getCause();
    System.out.println("event played!: " + event);
  }

  private void play(LineAddedEvent event) {
    Assert.notNull(event.getLine(), "null line");
    if (this.lines == null) {
      this.lines = new HashMap<>();
    }
    this.lines.put(nextLineId(), event.getLine());
    System.out.println("event played!: " + event);
  }

  private void play(LineSerialNumberAddedEvent event) {
    System.out.println("event played!: " + event);
  }

  // ------------------------------------------------------------------------------
  // end: event logic
  // ------------------------------------------------------------------------------

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    if (!(obj instanceof Package)) {
      return false;
    }
    Package other = (Package) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Package [id=" + getId() + ", version=" + getVersion() + ", cause=" + getCause() + ", parent="
        + getParents().stream().map(p -> p.getRawId().toString()).collect(Collectors.joining(", ", "[", "]"))
        + ", children="
        + getChildren().stream().map(c -> c.getRawId().toString()).collect(Collectors.joining(", ", "[", "]"))
        + ", lines=" + getLines().keySet().stream().map(lk -> String.format("%s: %s", lk, getLine(lk)))
            .collect(Collectors.joining(", ", "{", "}"))
        + "]";
  }
}
