package org.upsmf.grievance.service;

import org.upsmf.grievance.model.GrievanceRaise;
import org.upsmf.grievance.model.GrievanceTicket;


public interface GrievanceRaiseService {

    GrievanceTicket addGrievance(GrievanceRaise grievanceRaise);
}
