package com.data.dealrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DealRequest {
    private String dealid;
    private String fromISOCODE;//currency code
    private String toISOCODE;//Currency Code
    private String dealtime;
    private String dealamount;
}
