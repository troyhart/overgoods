package com.example.datacapture.domain.pkg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.example.common.domain.EventPublisher;
import com.example.common.domain.OptimisticConcurrencyException;
import com.stormpath.sdk.lang.Assert;

// @Repository
// @Service
public class PackageRepository {

  private Map<PackageId, List<PackageEvent>> packages;

  public PackageRepository() {
    packages = new HashMap<>();
  }

  public PackageId nextIdentity() {
    return new PackageId(UUID.randomUUID().toString().toUpperCase());
  }


  public Optional<Package> find(PackageId id) {
    Assert.notNull(id, "null id");
    System.out.println("lookup packageId: " + id);
    Optional<Package> op = Optional.empty();
    if (packages.containsKey(id)) {
      List<PackageEvent> packageEvents = packages.get(id);
      if (CollectionUtils.isNotEmpty(packageEvents)) {
        Package p = new Package();
        p.play(packageEvents.stream());
        op = Optional.of(p);
      }
    }
    return op;
  }

  public void save(Package pkg) {
    Assert.notNull(pkg, "null package");
    Assert.notNull(pkg.getId(), "null package.id");
    EventPublisher eventPublisher = EventPublisher.instance();
    PackageEvent[] changes = pkg.consumeChanges();
    if (changes.length > 0) {
      List<PackageEvent> aggregateEvents = lazyGetEventList(pkg.getId());
      if (CollectionUtils.isNotEmpty(aggregateEvents)) {
        PackageEvent lastEvent = aggregateEvents.get(aggregateEvents.size() - 1);
        if (lastEvent.getVersion() + 1 == changes[0].getVersion()) {
          System.out.println(String.format("[SAVED] %s={%s}", pkg.getClass().getSimpleName(), pkg));
          Arrays.stream(changes).forEach(e -> {
            aggregateEvents.add(e);
            eventPublisher.publish(e);
          });
        }
        else {
          throw new OptimisticConcurrencyException(aggregateEvents.stream()
              .filter(e -> e.getVersion() >= changes[0].getVersion()).collect(Collectors.toList()));
        }
      }
      else {
        System.out.println(String.format("[SAVED] %s={%s}", pkg.getClass().getSimpleName(), pkg));
        Arrays.stream(changes).forEach(e -> {
          aggregateEvents.add(e);
          eventPublisher.publish(e);
        });
      }
    }
  }

  /**
   * @param packageId
   *          a PackageId
   * 
   * @return the persistent list of events for the given packageId. An empty list if the given packageId is previously
   *         unknow in this repository.
   */
  private List<PackageEvent> lazyGetEventList(PackageId packageId) {
    if (!packages.containsKey(packageId)) {
      packages.put(packageId, new ArrayList<>());
    }
    return packages.get(packageId);
  }
}
