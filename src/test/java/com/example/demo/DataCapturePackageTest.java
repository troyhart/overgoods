package com.example.demo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;

import com.example.common.commerce.Money;
import com.example.common.domain.EventPublisher;
import com.example.common.domain.EventSubscriber;
import com.example.common.domain.Handler;
import com.example.common.infrastructure.TransactionHandler;
import com.example.datacapture.domain.agent.Agent;
import com.example.datacapture.domain.agent.AgentId;
import com.example.datacapture.domain.agent.AgentRepository;
import com.example.datacapture.domain.pkg.DescribeLineCommand;
import com.example.datacapture.domain.pkg.Item;
import com.example.datacapture.domain.pkg.Item.Attribute;
import com.example.datacapture.domain.pkg.LineDescribed;
import com.example.datacapture.domain.pkg.Package;
import com.example.datacapture.domain.pkg.PackageCommand;
import com.example.datacapture.domain.pkg.PackageCreated;
import com.example.datacapture.domain.pkg.PackageEvent;
import com.example.datacapture.domain.pkg.PackageFactory;
import com.example.datacapture.domain.pkg.PackageRepository;

public class DataCapturePackageTest {

  PackageFactory packageFactory = PackageFactory.instance();
  PackageRepository packageRepository = new PackageRepository();
  AgentRepository agentRepository = new AgentRepository();

  @Test
  public void testCreatePackage() {
    List<PackageEvent> events = new ArrayList<>();
    subscribeToEvents(EventPublisher.instance().reset(), events);
    Agent agent = agentRepository.find(new AgentId("12345"));

    Package pkg = createPackage(agent, "Label not readable", events);
    createLines(describeLineCommands(agent, pkg), events);

    System.out.println("Test package creation -> " + packageRepository.find(pkg.getId()));
  }

  private List<DescribeLineCommand> describeLineCommands(Agent agent, Package pkg) {
    List<DescribeLineCommand> describeLineCommands = new ArrayList<>();
    describeLineCommands.add(newCommandForASuperCoolThingLine(agent, pkg));
    describeLineCommands.add(newCommandForAMiscelaneousLine(agent, pkg));
    return describeLineCommands;
  }

  private DescribeLineCommand newCommandForAMiscelaneousLine(Agent agent, Package pkg) {
    DescribeLineCommand cmd = new DescribeLineCommand();
    cmd.setAgent(agent);
    cmd.setPackageId(pkg.getId());
    cmd.setLineDescription("A bunch of misc. junk!!!");
    cmd.setMiscPieceCount(50);
    return cmd;
  }

  private DescribeLineCommand newCommandForASuperCoolThingLine(Agent agent, Package pkg) {
    DescribeLineCommand cmd = new DescribeLineCommand();
    cmd.setAgent(agent);
    cmd.setPackageId(pkg.getId());
    cmd.setLineDescription("A super cool thing");
    cmd.setDetailedItemDescription("A super cool thing with a ton cool features");
    Set<String> serialNumbers = new HashSet<>();
    serialNumbers.add("123456");
    serialNumbers.add("789101");
    cmd.setSerialNumbers(serialNumbers);
    cmd.setItemName("Wizzy Banger");
    cmd.setItemNumber("1234567890");
    Map<Item.Attribute, String> attributes = new HashMap<>();
    attributes.put(Attribute.ACTOR, "Some Actor");
    attributes.put(Attribute.ARTIST, "Some Artist");
    attributes.put(Attribute.DIRECTOR, "Some Director");
    attributes.put(Attribute.GENRE, "Some Genre");
    attributes.put(Attribute.LABEL, "Some Label");
    cmd.setAttributes(attributes);
    cmd.setEstimatedUnitValue(new Money(new BigDecimal(1000000)));
    return cmd;
  }

  private void createLines(List<DescribeLineCommand> describeLineCommands, List<PackageEvent> events) {
    describeLineCommands.forEach(c -> {
      int expectedEventSizeAfterCommand = events.size() + 1;
      int expectedLineCount = packageRepository.find(c.getPackageId()).get().getLineIds().size() + 1;

      doHandle(c, new Handler<DescribeLineCommand>() {
        @Override
        public void handle(DescribeLineCommand command) {
          Optional<Package> op = packageRepository.find(command.getPackageId());
          op.get().handle(command);
          packageRepository.save(op.get());
        }
      });

      assertTrue("Unexpected event state; expecting " + expectedEventSizeAfterCommand + " event, but received "
          + events.size(), events.size() == expectedEventSizeAfterCommand);
      LineDescribed event = (LineDescribed) events.get(expectedEventSizeAfterCommand - 1);
      assertNotNull("null event", event);
      Optional<Package> op = packageRepository.find(event.getPackageId());
      assertTrue("Could not find persisted package", op.isPresent());
      assertTrue(("Wrong version; expecting " + event.getVersion() + " but found " + op.get().getVersion()),
          op.get().getVersion() == event.getVersion());
      assertTrue(
          "Wrong description; expectiong \"" + event.getLine().getDescription() + "\" but found \""
              + op.get().getLine(event.getLineId()).getDescription() + "\"",
          op.get().getLine(event.getLineId()).getDescription().equals(event.getLine().getDescription()));
      assertTrue(
          "Invalid state; expecting " + expectedLineCount + " line items, but found " + op.get().getLines().size(),
          op.get().getLineIds().size() == expectedLineCount);
    });
  }

  private Package createPackage(Agent agent, String description, List<PackageEvent> events) {
    int expectedEventSizeAfterCommand = events.size() + 1;
    CreatePackageCommand command = new CreatePackageCommand(agent, description);
    doHandle(command, new Handler<CreatePackageCommand>() {
      @Override
      public void handle(CreatePackageCommand command) {
        Package pkg = packageFactory.newPackage(packageRepository, command.getDescription(), agent);
        packageRepository.save(pkg);
      }
    });

    assertTrue(
        "Unexpected event state; expecting " + expectedEventSizeAfterCommand + " event, but received " + events.size(),
        events.size() == expectedEventSizeAfterCommand);
    PackageCreated event = (PackageCreated) events.get(expectedEventSizeAfterCommand - 1);
    assertNotNull("null event", event);
    Optional<Package> op = packageRepository.find(event.getPackageId());
    assertTrue("Could not find persisted package", op.isPresent());
    assertTrue(("Wrong version; expecting " + event.getVersion() + " but found " + op.get().getVersion()),
        op.get().getVersion() == event.getVersion());
    assertTrue("Wrong description; expectiong \"" + description + "\" but found \""
        + op.get().getCause().getDescription() + "\"", op.get().getCause().getDescription().equals(description));
    assertTrue("Invalid state; expecting to NOT find line items, but found " + op.get().getLines().size(),
        op.get().getLines().isEmpty());
    return op.get();
  }

  private void subscribeToEvents(EventPublisher publisher, final List<PackageEvent> events) {
    publisher.subscribe(new EventSubscriber<PackageEvent>() {
      @Override
      public void handleEvent(PackageEvent domainEvent) {
        events.add(domainEvent);// simply add events to the list so they can be verified...
      }

      @Override
      public Class<PackageEvent> subscribedToEventType() {
        return PackageEvent.class;
      }
    });
  }

  private <T extends PackageCommand> void doHandle(T command, Handler<T> handler) {
    new TransactionHandler<T>(handler).handle(command);
  }

  static final class CreatePackageCommand extends PackageCommand {

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
}
