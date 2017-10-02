package com.example.datacapture.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.domain.Handler;
import com.example.common.infrastructure.TransactionHandler;
import com.example.datacapture.domain.agent.AgentRepository;
import com.example.datacapture.domain.pkg.DescribePackageCommand;
import com.example.datacapture.domain.pkg.Package;
import com.example.datacapture.domain.pkg.PackageCommand;
import com.example.datacapture.domain.pkg.PackageFactory;
import com.example.datacapture.domain.pkg.PackageId;
import com.example.datacapture.domain.pkg.PackageRepository;

@RestController
@RequestMapping("/dc/pkgs")
public class PackageCommandController {

  AgentRepository agentRepository;
  PackageRepository packageRepository;

  @Autowired
  public PackageCommandController(AgentRepository agentRepository, PackageRepository packageRepository) {
    this.agentRepository = agentRepository;
    this.packageRepository = packageRepository;
  }

  @RequestMapping(method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<PackageId> execute(DescribePackageCommand cmd) {
    doHandle(cmd, new Handler<DescribePackageCommand>() {
      @Override
      public void handle(DescribePackageCommand command) {
        Package pkg = PackageFactory.instance().newPackage(packageRepository, agentRepository, command);
        packageRepository.save(pkg);
      }
    });
    // TODO: build response...
    throw new RuntimeException();
  }

  private <T extends PackageCommand> void doHandle(T cmd, Handler<T> handler) {
    new TransactionHandler<T>(handler).handle(cmd);
  }

}
