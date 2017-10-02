package com.example.demo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.common.domain.Handler;
import com.example.common.infrastructure.TransactionHandler;
import com.example.datacapture.domain.agent.AgentRepository;
import com.example.datacapture.domain.pkg.PackageCommand;
import com.example.datacapture.domain.pkg.PackageFactory;
import com.example.datacapture.domain.pkg.PackageRepository;

public class DataCapturePackageTest {
  
  PackageFactory packageFactory = PackageFactory.instance();
  PackageRepository packageRepository = new PackageRepository();
  AgentRepository agentRepository = new AgentRepository();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCreateItemizedPackage() {

  }

  @Test
  public void testCreateLowValueMiscellaneousPackage() {
    
  }
  

  private <T extends PackageCommand> void doHandle(T command, Handler<T> handler) {
    new TransactionHandler<T>(handler).handle(command);
  }
}
