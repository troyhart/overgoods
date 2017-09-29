package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.datacapture.domain.agent.AgentRepository;

@SpringBootApplication
public class DemoApplication {
  
  private AgentRepository agentRepository;
  
  @Autowired
  public DemoApplication(AgentRepository agentRepository) {
    this.agentRepository = agentRepository;
  }

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		

//    Agent agent = agentRepository.find("123456");
//    Cause cause = new Cause.Builder().setDescription("Testing testing.....this is the cause...........").build();
//
//    PackageFactory fac = new PackageFactory();
//    Package og = fac.newOvergood(agent, cause);
//
//    Item item = new Item.Builder().setProductName("Some Product Name").build();
//    Line line = new Line.Builder("The first test line.").setItem(item).addSerialNumber("item-1").build();
//    og.addLine(agent, line);
//
//    System.out.println("Added line to new overgoods: " + og.toString());
	}
}
