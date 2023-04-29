package com.ehrbridge.gateway.dto.Hospital;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Visit {
    public String ehrbID;
    public String hospitalID;
    public String hospitalName;
    public String department;
    public Date timestamp;
}
