package com.gdis.database.controller;

import java.util.List;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.gdis.database.models.Contract;
import com.gdis.database.services.ContractRepository;
import com.gdis.database.models.NewContract;
import com.gdis.database.services.NewContractRepository;
import util.PreCondition;
import util.CustomErrorType;

@RestController
@RequestMapping("/apiNewContract")
public class NewContractController {
	@Autowired
	private NewContractRepository newContractRepository;
	
	//create new contract in the database
	@RequestMapping(value = "/newContract/",method = RequestMethod.POST)
	public ResponseEntity<?> createNewContract (@RequestBody NewContract newContract,
													UriComponentsBuilder ucBuilder) {
	  PreCondition.notNull(newContract, "id must be greater than 0");
	  
	  if(newContractRepository.existsById(newContract.getId())){
		  return new ResponseEntity(new CustomErrorType("Unable to create a contract with id " + 
		            newContract.getId() + " already exist."),HttpStatus.CONFLICT);
	  } else {
		  newContractRepository.save(newContract);
		  HttpHeaders headers = new HttpHeaders();
	        headers.setLocation(ucBuilder.path("/apiNewContract/newContract/{id}").buildAndExpand(newContract.getId()).toUri());
	        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	  }
	}
	
	//retrieve all contacts
	@RequestMapping(value = "/newContract/", method = RequestMethod.GET)
	public ResponseEntity<List<NewContract>> listAllContracts() {
		Iterable<NewContract> i = newContractRepository.findAll();
		List<NewContract> contracts = new LinkedList<NewContract>();
		for (NewContract nc:i) {
			contracts.add(nc);
		}
		if (contracts.isEmpty()) {
	        return new ResponseEntity(HttpStatus.NO_CONTENT);
	    }
	    return new ResponseEntity<List<NewContract>>(contracts, HttpStatus.OK);
	}
	 
	//retrieve single new contract
	@RequestMapping(value = "/newContract/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getContract(@PathVariable("id") long id) {
		NewContract newContract = newContractRepository.findById(id);
	    if (newContract == null) {
	    	return new ResponseEntity(new CustomErrorType("Contract with id " + id 
	                    + " not found"), HttpStatus.NOT_FOUND);
	    }
	    return new ResponseEntity<NewContract>(newContract, HttpStatus.OK);
	}
	 
	@RequestMapping(value = "/newContract/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateNewContract(@PathVariable("id") long id, @RequestBody NewContract newContract) {
		NewContract currentContract = newContractRepository.findById(id);
	 
	    if (currentContract == null) {
	    	return new ResponseEntity(new CustomErrorType("Unable to update. Contract with id "
	            				+ id + " not found."), HttpStatus.NOT_FOUND);
	    }
	 
	    currentContract.setCustomer(newContract.getCustomer());
	    currentContract.setProductType(newContract.getProductType());
	    currentContract.setContractBegin(newContract.getContractBegin());
	 
	    newContractRepository.save(currentContract);
	    return new ResponseEntity<NewContract>(currentContract, HttpStatus.OK);
	}
	  
	//delete a new contract
	@RequestMapping(value = "/newContract/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteNewContract(@PathVariable("id") long id) {
		NewContract currentContract = newContractRepository.findById(id);
	    if (currentContract == null) {
	       return new ResponseEntity(new CustomErrorType("Unable to delete contract with id " 
	    		   + id + " not found."), HttpStatus.NOT_FOUND);
	    }
	    newContractRepository.delete(currentContract);
	    return new ResponseEntity<NewContract>(HttpStatus.NO_CONTENT);
	}
	 	
	 //delete all new contracts
	@RequestMapping(value = "/newContract/", method = RequestMethod.DELETE)
    public ResponseEntity<NewContract> deleteAllNewContracts() {
        newContractRepository.deleteAll();
        return new ResponseEntity<NewContract>(HttpStatus.NO_CONTENT);
    }
	
}