package com.redhat.sie.model;

import java.math.BigDecimal;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class DataRecord {

    private final String signalSource;
    private final String rtuId;
    private final long time;
    private final int qualityCode;
    private final double value;

    @ProtoFactory
    public DataRecord(String signalSource, String rtuId, long time, int qualityCode, double value) {
    	this.signalSource = signalSource;
        this.rtuId = rtuId;
        this.time = time;
        this.qualityCode = qualityCode;
        this.value = value;
     }

    @ProtoField(number = 1)
	public String getSignalSource() {
		return signalSource;
	}

    @ProtoField(number = 2)
	public String getRtuId() {
		return rtuId;
	}

    @ProtoField(number = 3)
	public long getTime() {
		return time;
	}

    @ProtoField(number = 4)
	public int getQualityCode() {
		return qualityCode;
	}

    @ProtoField(number = 5)
	public double getValue() {
		return value;
	}
	
    
}

