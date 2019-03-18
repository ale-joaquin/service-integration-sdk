package com.appdirect.sdk.vendorFields.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appdirect.sdk.appmarket.migration.Subscription;
import com.appdirect.sdk.vendorFields.converter.FlowTypeConverter;
import com.appdirect.sdk.vendorFields.converter.OperationTypeConverter;
import com.appdirect.sdk.vendorFields.handler.VendorFieldValidationHandler;
import com.appdirect.sdk.vendorFields.model.FlowType;
import com.appdirect.sdk.vendorFields.model.OperationType;
import com.appdirect.sdk.vendorFields.model.VendorFieldsValidationRequest;
import com.appdirect.sdk.vendorFields.model.VendorFieldsValidationResponse;

/**
 * Defines the endpoint for enforcing vendor required requiredFields on their products
 */
@RestController
public class VendorFieldValidationController {
	private final VendorFieldValidationHandler vendorFieldValidationHandler;

	@Autowired
	public VendorFieldValidationController(VendorFieldValidationHandler handler) {
		this.vendorFieldValidationHandler = handler;
	}

	@RequestMapping(method = POST,
			value = "/api/v1/admin/vendorvalidations/",
			consumes = APPLICATION_JSON_VALUE,
			produces = APPLICATION_JSON_VALUE)
	public Callable<VendorFieldsValidationResponse> validateFields(@RequestParam(value = "sku") String sku,
																   @RequestParam(value = "flowType") FlowType flowType,
																   @RequestParam(value = "operationType") OperationType operationType,
																   @RequestBody Subscription subscription,
																   @RequestBody Map<String, String> fieldValues) {
		VendorFieldsValidationRequest vendorFieldsValidationRequest = VendorFieldsValidationRequest.builder()
				.fieldValues(fieldValues)
				.flowType(flowType)
				.operationType(operationType)
				.sku(sku)
				.subscription(subscription)
				.build();
		return () -> vendorFieldValidationHandler.validateFields(vendorFieldsValidationRequest);
	}

	@InitBinder
	public void initBinder(final WebDataBinder webdataBinder) {
		webdataBinder.registerCustomEditor(FlowType.class, new FlowTypeConverter());
		webdataBinder.registerCustomEditor(OperationType.class, new OperationTypeConverter());
	}
}