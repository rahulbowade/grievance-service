package org.upsmf.grievance.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.upsmf.grievance.model.GrievanceRaise;
import org.upsmf.grievance.model.GrievanceTicket;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.service.GrievanceRaiseService;
import org.upsmf.grievance.util.*;

import javax.annotation.security.PermitAll;

@RestController
@RequestMapping("/grievance")
public class GrievanceRaiseController {
    @Autowired
    private GrievanceRaiseService service;

    @PostMapping(value ="/grievanceRaise")
    public String addGrievance(@RequestBody GrievanceRaise grievance)
            throws JsonProcessingException {
        GrievanceTicket grievanceTicket = service.addGrievance(grievance);
        // validating response
        if (grievanceTicket != null) {
            // return success response
            return ResponseGenerator.successResponse(grievanceTicket);
        }
        // return error response
        return ResponseGenerator.failureResponse();
    }


}
