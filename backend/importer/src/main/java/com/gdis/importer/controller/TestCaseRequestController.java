package com.gdis.importer.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gdis.importer.model.JSONWrapper;
import com.gdis.importer.util.PreCondition;
import com.gdis.importer.util.DBClient;

@RestController
@RequestMapping("/importer")
public class TestCaseRequestController {
	
	private JSONWrapper testCaseToImport;

	private List<ObjectNode> attributeChunksToImport;

	private String storyTypeImport;
	
	private ObjectNode jsonToExport;
	
	@Autowired
	private DBClient dbClient;
	
	@RequestMapping(value = "/i/test-case", method = RequestMethod.POST, consumes = 
	{ MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})		
	public ResponseEntity<?> handleImportRequest(@RequestBody JSONWrapper jsonWrapper) {	

		PreCondition.notNull(jsonWrapper, "JSON is empty!");
		
		setTestCaseToImport(jsonWrapper);
		
		// Return HTTP 400 if the JSON is missing the Story Type or The test data
		if(missingStoryType(getTestCaseToImport()) == true) {
			setTestCaseToImport(null);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {
			setStoryTypeImport(getTestCaseToImport().getStoryType());
		}
		
		if(missingStoryName(getTestCaseToImport()) == true) {
			setTestCaseToImport(null);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		if(missingTestData(getTestCaseToImport()) == true) {
			setTestCaseToImport(null);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		checkTestName(getTestCaseToImport());
		
		attributeChunksToImport = new ArrayList<ObjectNode>();
		chunkJSON(getTestCaseToImport());
		
		ObjectMapper mapper = new ObjectMapper();
		jsonToExport = mapper.createObjectNode();
		
		buildJsonToExport(getJsonToExport(), getTestCaseToImport(), attributeChunksToImport);
		
		//DBClient dbClient = new DBClient();
		
		HttpStatus dbClientResponse = dbClient.importTestInDB(getJsonToExport(), getStoryTypeImport());
		
		if(dbClientResponse.value() != HttpStatus.CREATED.value()) {
			return new ResponseEntity<>(dbClientResponse);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
		//return new ResponseEntity<JSONWrapper>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/u/test-case/{id}", method = RequestMethod.PUT, consumes = 
		{ MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<?> handleUpdateRequest(@PathVariable("id") long id, @RequestBody JSONWrapper jsonWrapper) {
		
		PreCondition.notNull(jsonWrapper, "JSON is empty!");
		PreCondition.require(id >= 0, "Test ID can't be negative!");
		
		setTestCaseToImport(jsonWrapper);
		
		// Return HTTP 400 if the JSON is missing the Story Type or The test data
		if(missingStoryType(getTestCaseToImport()) == true) {
			setTestCaseToImport(null);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {
			setStoryTypeImport(getTestCaseToImport().getStoryType());
		}
		
		if(missingStoryName(getTestCaseToImport()) == true) {
			setTestCaseToImport(null);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		if(missingTestData(getTestCaseToImport()) == true) {
			setTestCaseToImport(null);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		checkTestName(getTestCaseToImport());
		
		attributeChunksToImport = new ArrayList<ObjectNode>();
		chunkJSON(getTestCaseToImport());
		
		ObjectMapper mapper = new ObjectMapper();
		jsonToExport = mapper.createObjectNode();
		
		buildJsonToExport(getJsonToExport(), getTestCaseToImport(), attributeChunksToImport);
		
		//DBClient dbClient = new DBClient();
		
		HttpStatus dbClientResponse = dbClient.updateTestInDB(getJsonToExport(), getStoryTypeImport(), id);
		
		if(dbClientResponse.value() != HttpStatus.OK.value()) {
			return new ResponseEntity<>(dbClientResponse);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/d/test-case/{storyType}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> handleDeleteRequest(@PathVariable("id") long id, 
			@PathVariable("storyType") String storyType) {
		
		PreCondition.require(id >= 0, "Test ID can't be negative!");
		
		HttpStatus dbClientResponse = dbClient.deleteTestFromDB(storyType, id);
		
		if(dbClientResponse.value() != HttpStatus.OK.value()) {
			return new ResponseEntity<>(dbClientResponse);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@ExceptionHandler({HttpMessageNotReadableException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseEntity<?> resolveException() {
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode response = mapper.createObjectNode();
		
		response.put("reason", "Empty Body");
		
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	
	private boolean missingStoryType(JSONWrapper jsonWrapper) {
		
		// No Story Type. Can't import
		if( (jsonWrapper.getStoryType() == null) || (jsonWrapper.getStoryType().trim().length() == 0)) {
			return true;
		}
		return false;
	}
	
	private boolean missingStoryName(JSONWrapper jsonWrapper) {
		
		// No Story Name. Can't import
		if( (jsonWrapper.getStoryName() == null) || (jsonWrapper.getStoryName().trim().length() == 0)) {
			return true;
		}
		return false;
	}
	
	private boolean missingTestData(JSONWrapper jsonWrapper) {
		
		// No Test Data. Nothing to export
		 if(jsonWrapper.getTestData() == null) {
			return true;
		} else if(jsonWrapper.getTestData().isEmpty()) {
			return true;
		}	
		
		
		return false;
	}
	
	 private void checkTestName(JSONWrapper jsonWrapper) {
		
		// If there was no name specified for the exported test, just
		// generate one
		if(jsonWrapper.getTestName() == null) {
			jsonWrapper.setTestName(new String());
		}
				
				
		boolean testNameExportEmpty = (jsonWrapper.getTestName().trim().length() == 0) 
			|| (jsonWrapper.getTestName().isEmpty());
				
		if(testNameExportEmpty == true)  {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyy-HH:mm:ss");
			String date = dtf.format(LocalDateTime.now());
					
			String generatedTestName = jsonWrapper.getStoryType().toString()
					+ "-TEST-" + date;
			jsonWrapper.setTestName(generatedTestName);
		}
	}
	
	

	
	/**
	 * Chunk the received JSON into individual JSONs, that will be sent to the 
	 * Global Variable for all the chunks
	 * JSON Format: 
	 * "storyType": "basicStoryTest",
	 * "storyName": "newContract", 
	 * "testName": "name",
	 * "testData": [
	 * 	 {"name": "jack", "age": "20"}, 
	 *   {"name": "john", "age": "20"}
	 * ]
	 */
	 private void chunkJSON(JSONWrapper jsonWrapper) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ObjectNode chunk;
		
		for(ObjectNode j : jsonWrapper.getTestData()) {
			
			chunk = objectMapper.createObjectNode();
			
			//chunk.put("storyType", jsonWrapper.getStoryType());
			//chunk.put("testName", jsonWrapper.getTestName());
			//chunk.put("storyName", jsonWrapper.getStoryName());
			// chunk.setAll(j);
			
			// With List<ObjectNode>
			//String row = j.elements().next().toString();
			
			// With HashSet<ObjectNode>
			String row = j.toString();
			
			JsonNode jsonNode = null;
			
			try {
				jsonNode = objectMapper.readTree(row);
			} catch (IOException e) {
				
			}
			
			
			chunk.set("attributes", jsonNode);
			
			
			/**
			 *  Get Every Key Value Pair so at the end every JSON chunk looks like this: 
			 * "attributes": {"name": "jack", "age": "20"}
	 * ]
			 */
			attributeChunksToImport.add(chunk);
		}	
	}	
	 
	 
	private void buildJsonToExport(ObjectNode json, JSONWrapper testCase, List<ObjectNode> attributes) {
		
		json.put("storyType", testCase.getStoryType());
		json.put("testName", testCase.getTestName());
		json.put("storyName", testCase.getStoryName());
		
		ObjectMapper mapper = new ObjectMapper();
		
		//ArrayNode arrayNode = json.putArray("data");
		ArrayNode arrayNode = mapper.createArrayNode();
		
		for(ObjectNode j : attributes) {
			arrayNode.add(j);
		}
		
		json.putArray("data").addAll(arrayNode);
		
		setJsonToExport(json);
	}
	
	
	
	public JSONWrapper getTestCaseToImport() {
		return testCaseToImport;
	}

	public void setTestCaseToImport(JSONWrapper objectToExport) {
		this.testCaseToImport = objectToExport;
	}


	public String getStoryTypeImport() {
		return storyTypeImport;
	}


	public void setStoryTypeImport(String storyTypeImport) {
		this.storyTypeImport = storyTypeImport;
	}


	public ObjectNode getJsonToExport() {
		return jsonToExport;
	}


	public void setJsonToExport(ObjectNode jsonToExport) {
		this.jsonToExport = jsonToExport;
	}
	
}