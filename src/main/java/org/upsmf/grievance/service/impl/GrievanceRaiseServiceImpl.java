package org.upsmf.grievance.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.GrievanceRaise;
import org.upsmf.grievance.model.GrievanceTicket;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.service.GrievanceRaiseService;
import org.upsmf.grievance.service.TicketService;

@Service(value = "GrievanceRaiseService")
public class GrievanceRaiseServiceImpl implements GrievanceRaiseService {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private  Ticket ticket;

    @Value("${source_default_request_id}")
    private Long sourceId;

    @Value("${default_app_name}")
    private String appName;

    @Value("${default_app_id}")
    private Long appId;

    @Value("${default_app_key}")
    private String appKey;

    public static final Logger LOGGER = LoggerFactory.getLogger(GrievanceRaiseServiceImpl.class);

    @Override
    public GrievanceTicket addGrievance(GrievanceRaise grievance) {
        LOGGER.info("Enter the addGrievanc method");
        ticket.setUserName(grievance.getName());
        ticket.setHelpdeskId(grievance.getHelpdeskId());
        ticket.setSourceId(sourceId);
        ticket.setAppKey(appKey);
        ticket.setAppId(appId);
        ticket.setDescription(grievance.getDescription());
        ticket.setAppName(appName);
        ticket.setRequesterUser(grievance.getName());
        ticket.setRequesterEmail(grievance.getEmailId());
        ticket.setRequesterPhoneNumber(grievance.getPhone());
         LOGGER.info("added all values to ticket" + ticket);
        ticket=ticketService.addTicket(ticket);

        // checking response
        GrievanceTicket grievanceTicket = null;
        LOGGER.info("after raise and sent a ticket to mail taking ticketid"+ticket.getId()+" date"+ticket.getCreatedTimeTS());
        if(ticket!=null && ticket.getId()>0){
            grievanceTicket = GrievanceTicket.builder().ticketId(ticket.getId())
                    .date(ticket.getCreatedTime().toString()).build();
            // send mail to stakeholder i.e. requestor, admins
        }
        return grievanceTicket;
    }
}
