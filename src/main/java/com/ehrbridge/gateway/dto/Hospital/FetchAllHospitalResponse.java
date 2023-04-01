package com.ehrbridge.gateway.dto.Hospital;

import java.util.List;

import com.ehrbridge.gateway.entity.Hospital;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FetchAllHospitalResponse {
    private List<Hospital> hospitals;
}
