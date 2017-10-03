package com.example.demo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.common.domain.EntityNotFoundException;
import com.example.common.domain.Event;
import com.example.common.domain.EventPublisher;
import com.example.common.domain.EventSubscriber;
import com.example.common.domain.Handler;
import com.example.common.infrastructure.TransactionHandler;
import com.example.datacapture.domain.agent.Agent;
import com.example.datacapture.domain.agent.AgentId;
import com.example.datacapture.domain.agent.AgentRepository;
import com.example.datacapture.domain.pkg.DescribeLineCommand;
import com.example.datacapture.domain.pkg.LineDescribed;
import com.example.datacapture.domain.pkg.Package;
import com.example.datacapture.domain.pkg.PackageCommand;
import com.example.datacapture.domain.pkg.PackageCreated;
import com.example.datacapture.domain.pkg.PackageEvent;
import com.example.datacapture.domain.pkg.PackageFactory;
import com.example.datacapture.domain.pkg.PackageRepository;

public class PackageCommandsTests {

  PackageRepository packageRepository;
  AgentRepository agentRepository;

  public PackageCommandsTests() {
    this.packageRepository = new PackageRepository();
    this.agentRepository = new AgentRepository();
  }

  public static final class CreatePackageCommand extends PackageCommand {

    private String description;

    public CreatePackageCommand(Agent agent, String description) {
      setAgent(agent);
      setDescription(description);
    }

    private void setDescription(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }

  }


  public void handleCreatePackageCommands(CreatePackageCommand... commands) {
    for (CreatePackageCommand createPackageCommand : commands) {
      Handler<CreatePackageCommand> handler = new Handler<CreatePackageCommand>() {
        @Override
        public void handle(CreatePackageCommand command) {
          Package pkg =
              PackageFactory.instance().newPackage(packageRepository, command.getDescription(), command.getAgent());
          packageRepository.save(pkg);
        }
      };
      doHandle(createPackageCommand, handler);
    }
  }

  private void handleDescribeLineCommands(Set<PackageCreated> packageCreateds) {
    final DescribeLineCommand cmd =
        new DescribeLineCommand().setLineDescription("a bunch of misc. junk").setMiscPieceCount(25);
    packageCreateds.forEach(pce -> {
      cmd.setPackageId(pce.getAggregateId()).setAgent(pce.capturedBy());
      Handler<DescribeLineCommand> handler = new Handler<DescribeLineCommand>() {

        @Override
        public void handle(DescribeLineCommand command) {
          Optional<Package> pkg = packageRepository.find(pce.getAggregateId());
          if (!pkg.isPresent()) {
            throw new EntityNotFoundException(Package.class, pce.getAggregateId());
          }
          pkg.get().handle(command);
          packageRepository.save(pkg.get());
        }

      };
      doHandle(cmd, handler);
    });
  }

  private <T extends PackageCommand> void doHandle(T cmd, Handler<T> handler) {
    new TransactionHandler<T>(handler).handle(cmd);
  }

  public static void main(String[] args) {
    final Set<PackageCreated> packageCreateds = new HashSet<>();
    final PackageCommandsTests tests = new PackageCommandsTests();
    EventPublisher publisher = EventPublisher.instance().reset();
    doSubscriptions(packageCreateds, publisher);
    Agent agent = tests.agentRepository.find(new AgentId("1234"));
    tests.handleCreatePackageCommands(new CreatePackageCommand(agent, "label missing"),
        new CreatePackageCommand(agent, "box torn"), new CreatePackageCommand(agent, "loose contents"),
        new CreatePackageCommand(agent, "fraud stop"));

    tests.handleDescribeLineCommands(packageCreateds);

    System.out
        .println(packageCreateds.stream().map(pce -> "{" + tests.packageRepository.find(pce.getAggregateId()) + "}")
            .collect(Collectors.joining(",\n", "[\n", "\n]")));
  }

  private static void doHandling(Event event, Class<? extends Event> subscribedToType) {
    System.out.println(String.format("[HANDLING::%s] %s.eventId={%s} ", subscribedToType.getSimpleName(),
        event.getClass().getSimpleName(), event.getEventId()));
  }

  private static void doSubscriptions(final Set<PackageCreated> packageCreateds, EventPublisher publisher) {
    publisher.subscribe(new EventSubscriber<PackageEvent>() {
      @Override
      public void handleEvent(PackageEvent domainEvent) {
        doHandling(domainEvent, subscribedToEventType());
      }

      @Override
      public Class<PackageEvent> subscribedToEventType() {
        return PackageEvent.class;
      }
    });

    publisher.subscribe(new EventSubscriber<PackageCreated>() {
      @Override
      public void handleEvent(PackageCreated domainEvent) {
        doHandling(domainEvent, subscribedToEventType());
        packageCreateds.add(domainEvent);
      }

      @Override
      public Class<PackageCreated> subscribedToEventType() {
        return PackageCreated.class;
      }
    });

    publisher.subscribe(new EventSubscriber<LineDescribed>() {
      @Override
      public void handleEvent(LineDescribed domainEvent) {
        doHandling(domainEvent, subscribedToEventType());
      }

      @Override
      public Class<LineDescribed> subscribedToEventType() {
        return LineDescribed.class;
      }
    });
  }
}
