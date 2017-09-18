package com.example.ogdc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.example.infrastructure.DomainEntityId;

public final class Overgoods {

  private OvergoodsId id;
  private long version = 0;
  private Cause cause;
  private Map<Long, Line> lines;
  private Set<OvergoodsId> children;
  private Set<OvergoodsId> parents;
  private List<OvergoodsDataCaptureEvent> changes;
  private long lastLineId = 0;

  /**
   * For replay. Replay starts with this constructor, then invokes {@link #play(OvergoodsDataCaptureEvent[])}.
   */
  Overgoods() {
  }

  /**
   * A new Overgoods with its cause and unique identifier.
   * 
   * @param id
   * @param cause
   * 
   * @see OvergoodsFactory#newOvergood(Cause)
   */
  Overgoods(OvergoodsId id, DCAgent capturedBy, Cause cause) {
    Assert.notNull(id, "null id");
    Assert.notNull(capturedBy, "null capturedBy");
    Assert.notNull(cause, "null cause");

    OvergoodsCreatedEvent ogce = new OvergoodsCreatedEvent(id, capturedBy, cause);

    // the invocation of apply() below is valid because this class is final! If that changes, then the package local
    // apply() and play() methods must be declared to be final!!!
    apply(ogce);
  }

  public DomainEntityId getId() {
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

  public Set<OvergoodsId> getChildren() {
    return children == null ? Collections.emptySet() : Collections.unmodifiableSet(children);
  }

  public Set<OvergoodsId> getParents() {
    return parents == null ? Collections.emptySet() : Collections.unmodifiableSet(parents);
  }

  private long nextVersion() {
    return (changes == null || changes.size() == 0) ? version + 1 : changes.get(changes.size() - 1).version() + 1;
  }

  private Long nextLineId() {
    return ++lastLineId;
  }

  public void addLine(DCAgent dcAgent, Line line) {
    LineAddedEvent lae = new LineAddedEvent(id, nextVersion(), dcAgent, line);
    apply(lae);
  }

  public void split(DCAgent splitBy, Cause cause, OvergoodsFactory factory) {
    // TODO: implement me....
    throw new UnsupportedOperationException("not yet implemented..........");
  }

  public void split(DCAgent splitBy, Cause cause, String[] serialNumbersToCleave, OvergoodsFactory factory) {
    // TODO: implement me....
    throw new UnsupportedOperationException("not yet implemented..........");
  }

  // ------------------------------------------------------------------------------
  // begin: event logic
  // ------------------------------------------------------------------------------

  List<OvergoodsDataCaptureEvent> clearChanges() {
    List<OvergoodsDataCaptureEvent> changes = this.changes;
    this.changes = null;
    return changes;
  }

  void apply(OvergoodsDataCaptureEvent[] events) {
    Assert.isTrue(events != null && events.length > 0, "null/empty events");
    for (OvergoodsDataCaptureEvent event : events) {
      apply(event);
    }
  }

  /**
   * {@link #play(OvergoodsCreatedEvent) Play} the given event and record it in
   * 
   * @param event
   *          the event to apply.
   */
  void apply(OvergoodsDataCaptureEvent event) {
    play(event);
    if (changes == null) {
      changes = new ArrayList<>();
    }
    changes.add(event);
  }

  void play(OvergoodsDataCaptureEvent[] events) {
    Assert.isTrue(events != null && events.length > 0, "null/empty events");

    for (OvergoodsDataCaptureEvent event : events) {
      play(event);
    }
  }

  void play(OvergoodsDataCaptureEvent event) {
    Assert.notNull(event, "null event");
    Assert.isTrue(version + 1 == event.version(),
        String.format("Invalid event; wrong version! Expecting v-%s but got v-%s", version + 1, event.version()));

    this.version = event.version();
    dispatch(event);
  }

  private void dispatch(OvergoodsDataCaptureEvent event) {
    if (event.version() == 1) {
      play(OvergoodsCreatedEvent.class.cast(event));
    }
    else {
      // After OvergoodsCreatedEvent has been played (see first dispatch case above), the id MUST BE NON-NULL!!!!!
      Assert.state(id != null, "Invalid state! Can not play the given event on an this unidentified Overgoods; Event="
          + event.toString() + ", Overgoods=" + this.toString());
      Assert.isTrue(id.equals(event.id()),
          "Invalid event, wrong id! This Overgoods id==" + id + ", but given event.id==" + event.id());

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

  private void play(OvergoodsCreatedEvent event) {
    this.id = event.id();// the only event that set's the OvergoodId.
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
    if (!(obj instanceof Overgoods)) {
      return false;
    }
    Overgoods other = (Overgoods) obj;
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
    return "Overgoods [id=" + getId() + ", version=" + getVersion() + ", cause=" + getCause() + ", parent="
        + getParents().stream().map(p -> p.getRawId().toString()).collect(Collectors.joining(", ", "[", "]"))
        + ", children="
        + getChildren().stream().map(c -> c.getRawId().toString()).collect(Collectors.joining(", ", "[", "]"))
        + ", lines=" + getLines().keySet().stream().map(lk -> String.format("%s: %s", lk, getLine(lk)))
            .collect(Collectors.joining(", ", "{", "}"))
        + "]";
  }

  public static void main(String[] args) {
    DCAgent agent = new DCAgent.Builder().setId(new DCAgentId("agent-id-1")).setName("Mr. DCAgent").build();
    Cause cause = new Cause.Builder().setDescription("Testing testing.....this is the cause...........").build();

    OvergoodsFactory fac = new OvergoodsFactory();
    Overgoods og = fac.newOvergood(agent, cause);

    Item item = new Item.Builder().setProductName("Some Product Name").build();
    Line line = new Line.Builder("The first test line.").setItem(item).addSerialNumber("item-1").build();
    og.addLine(agent, line);

    System.out.println("Added line to new overgoods: " + og.toString());
  }
}
