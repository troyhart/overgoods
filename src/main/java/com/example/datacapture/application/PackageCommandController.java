package com.example.datacapture.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.datacapture.domain.PackageCreationCommand;
import com.example.datacapture.domain.PackageId;
import com.example.datacapture.domain.PackageRepository;
import com.example.datacapture.domain.agent.AgentRepository;

@RestController
@RequestMapping("/overgoods")
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
  public ResponseEntity<PackageId> execute(PackageCreationCommand cmd) {
    return null;
  }

}
