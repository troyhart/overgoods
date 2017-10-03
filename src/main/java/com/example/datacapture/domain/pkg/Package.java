package com.example.datacapture.domain.pkg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.example.datacapture.domain.agent.Agent;

public final class Package {

  private PackageId id;
  private long version = 0;
  private Cause cause;
  private Map<Long, Line> lines;
  private Set<PackageId> children;
  private Set<PackageId> parents;
  private long lastLineId = 0;
  private ArrayList<PackageEvent> changes;

  /**
   * For replay. Replay starts with this constructor, then invokes {@link #play(PackageEvent[])}.
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

    PackageCreated pce = new PackageCreated(id, capturedBy, cause);

    // the invocation of apply() below is valid because this class is final! If that changes, then the package local
    // apply() and play() methods must be declared to be final!!!
    apply(pce);
  }

  public PackageId getId() {
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


  // ------------------------------------------------------------------------------
  // begin: command logic
  // ------------------------------------------------------------------------------


  public void handle(DescribeLineCommand command) {
    Assert.notNull(command, "null command");
    Assert.state(getId().equals(command.getPackageId()),
        String.format("Invalid command! this.id=%s, which is not consistent with command.packageId=%s", getId(),
            command.getPackageId()));
    Assert.state(!(command.getSerialNumbers().isEmpty() && command.getMiscPieceCount() <= 0),
        "Invalid command! Must be itemized with serial numbers or include a misc. piece count, but not both!");
    Assert.state(StringUtils.hasText(command.getLineDescription()), "Invalid command! Missing line description.");

    Line.Builder lineBuilder = new Line.Builder().setDescription(command.getLineDescription());

    if (command.getSerialNumbers().isEmpty()) {
      lineBuilder.setMiscPieceCount(command.getMiscPieceCount());
    }
    else {
      command.getSerialNumbers().forEach(sn -> {
        lineBuilder.addSerialNumber(sn);
      });
    }

    if (command.getEstimatedUnitValue() != null) {
      lineBuilder.setEstimatedUnitValue(command.getEstimatedUnitValue());
    }

    if (StringUtils.hasText(command.getDetailedItemDescription()) || StringUtils.hasText(command.getItemName())
        || StringUtils.hasText(command.getItemNumber()) || !command.getAttributes().isEmpty()) {
      // line is itemized
      Item.Builder itemBuilder = new Item.Builder().setDetailedDescription(command.getDetailedItemDescription())
          .setProductName(command.getItemName()).setProductNumber(command.getItemNumber());
      command.getAttributes().forEach((k, v) -> {
        itemBuilder.addAttribute(k, v);
      });
      lineBuilder.setItem(itemBuilder.build());
    }

    LineDescribed lae = new LineDescribed(id, nextVersion(), command.getAgent(), lineBuilder.build(), takeNextLineId());
    apply(lae);
  }


  // ------------------------------------------------------------------------------
  // end: command logic
  // ------------------------------------------------------------------------------


  // ------------------------------------------------------------------------------
  // begin: event logic
  // ------------------------------------------------------------------------------

  PackageEvent[] consumeChanges() {
    PackageEvent[] consumedChanges;
    if (this.changes == null) {
      consumedChanges = new PackageEvent[] {};
    }
    else {
      consumedChanges = this.changes.toArray(new PackageEvent[] {});
    }
    this.changes = null;
    return consumedChanges;
  }

  void play(Stream<PackageEvent> events) {
    Assert.notNull(events, "null events stream");

    events.forEach(e -> play(e));
  }

  void play(PackageEvent event) {
    Assert.notNull(event, "null event");
    long nextExpectedVersion = nextVersion();
    Assert.isTrue(nextExpectedVersion == event.getVersion(), String
        .format("Invalid event; wrong version! Expecting v-%s but got v-%s", nextExpectedVersion, event.getVersion()));

    this.version = event.getVersion();
    dispatch(event);
  }

  /**
   * {@link #play(PackageCreated) Play} the given event and record it in
   * 
   * @param event
   *          the event to apply.
   */
  private void apply(PackageEvent event) {
    play(event);
    if (changes == null) {
      changes = new ArrayList<>();
    }
    changes.add(event);
  }

  private void dispatch(PackageEvent event) {
    System.out.println(String.format("[event-play-dispatch] %s -> %s", event.getClass().getSimpleName(), event));

    if (event.getVersion() == 1) {
      play(PackageCreated.class.cast(event));
    }
    else {
      // After PackageCreated has been played (see first dispatch case above), the id MUST BE NON-NULL!!!!!
      Assert.state(id != null, "Invalid event! Can not play the given event on an this unidentified Package; event: "
          + event.toString() + ", package: " + this.toString());
      Assert.state(id.equals(event.getAggregateId()),
          "Invalid event; wrong id! this.id: " + id + "; event.aggregateId: " + event.getAggregateId());

      // handle remainder of event dispatches.
      if (LineDescribed.class.isInstance(event)) {
        play(LineDescribed.class.cast(event));

      }
      else {
        throw new IllegalArgumentException("Unexpected event: " + event.getClass().getSimpleName());
      }
    }
  }


  // ----------------------------------------------------------
  // event specific play methods...each event type must be represented.
  // ----------------------------------------------------------

  private void play(PackageCreated event) {
    this.id = event.getAggregateId();// the only event that set's the OvergoodId.
    this.cause = event.getCause();
  }

  private void play(LineDescribed event) {
    Assert.notNull(event.getLine(), "null line");
    if (this.lines == null) {
      this.lines = new HashMap<>();
    }
    this.lastLineId = event.getLineId();
    this.lines.put(event.getLineId(), event.getLine());
  }

  // ------------------------------------------------------------------------------
  // end: event logic
  // ------------------------------------------------------------------------------

  /**
   * @return the value for the next expected version.
   */
  private long nextVersion() {
    return version + 1;
  }

  /**
   * @return increment and return the last taken line id.
   */
  private Long takeNextLineId() {
    return ++lastLineId;
  }

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
    return "{\"id\": " + getId() + ", \"version\": " + getVersion() + ", \"cause\": " + getCause() + ", \"parents\": "
        + getParents().stream().map(p -> p.getRawId().toString()).collect(Collectors.joining(", ", "[", "]"))
        + ", \"children\": "
        + getChildren().stream().map(c -> c.getRawId().toString()).collect(Collectors.joining(", ", "[", "]"))
        + ", \"lines\": " + getLines().keySet().stream().map(lk -> String.format("%s: %s", lk, getLine(lk)))
            .collect(Collectors.joining(", ", "{", "}"))
        + "}";
  }
}
