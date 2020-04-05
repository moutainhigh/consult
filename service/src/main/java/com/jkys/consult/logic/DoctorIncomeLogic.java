package com.jkys.consult.logic;

import com.jkys.consult.common.BasePage;
import com.jkys.consult.reponse.DoctorConsultPriceResponse;
import com.jkys.consult.request.DoctorIncomeRequest;
import com.jkys.consult.request.DoctorPriceRequest;
import com.jkys.consult.request.PageRequest;
import com.jkys.consult.reponse.DoctorIncomeResponse;

public interface DoctorIncomeLogic {

  DoctorIncomeResponse queryDoctorIncome(DoctorIncomeRequest request, BasePage page);

  DoctorIncomeResponse queryDoctorIncome(PageRequest<DoctorIncomeRequest> pageRequest);

  DoctorConsultPriceResponse getDoctorPrice(Long doctorId);

  Boolean updateDoctorPrice(DoctorPriceRequest request);
}
