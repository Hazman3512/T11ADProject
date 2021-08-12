package sg.edu.iss.ad.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sg.edu.iss.ad.model.CandleModel;
import sg.edu.iss.ad.utility.candleDataConvertor;
import sg.edu.iss.ad.utility.UtilityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandleService{

    public List<CandleModel> getCandleData(String url) {
        RestTemplate restTemplate =new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        headers.add("Accept","application/json");
        //headers.add("x-api-key","eg3Z4ml4ik5Grz5tGNMlc7qsZz18VnEo21ERKTYp");
        headers.add("x-api-key","VTr2Z2gNmk7rVPuHnVMnyWw6tfGcEsbaHFWUixU7");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(null,headers);
        ResponseEntity<String> rawResult = restTemplate.exchange(url, HttpMethod.GET,httpEntity,String.class);

        List<CandleModel> result = candleDataConvertor.candleResultToList(rawResult.getBody());

        return result;
    }
    
    //bullish engulfing signal
    public List<String> getBullishEngulfingCandleSignal(List<CandleModel> result){
    	List<String> Dates= new ArrayList<>();
    	for(int i=0;i<200-1;i++) {
    		if(result.get(i).getOpen()<result.get(i).getClose() //0 opening lesser than 0 close
    			&& result.get(i+1).getOpen()<result.get(i+1).getClose() //1 open lesser than 1 close
    			&& result.get(i+1).getOpen()<result.get(i).getClose() //1 open lesser than 0 close
    			&& result.get(i+1).getClose()>result.get(i).getOpen()) { //1 close > 1 open 
    				Dates.add(UtilityManager.UnixToString(result.get(i+1).getTimestamp()));
    			
    		}

    	}
    	Collections.reverse(Dates); //to get latest first
    	return Dates;
    }
    
    public List<String> getBearishEngulfingCandleSignal(List<CandleModel> result){
       	List<String> Dates= new ArrayList<>();
    	for(int i=0;i<200-1;i++) {
    		if(result.get(i).getOpen()>result.get(i).getClose()
    			&& result.get(i+1).getOpen()>result.get(i+1).getClose() //1 open lesser than 1 close
    			&& result.get(i+1).getOpen()>result.get(i).getClose() //1 open more than 0 close
    			&& result.get(i+1).getClose()<result.get(i).getOpen()) { //1 close less than 0 open 
    				Dates.add(UtilityManager.UnixToString(result.get(i+1).getTimestamp()));   			
    		}
    	}
    	Collections.reverse(Dates); //to get latest first
    	return Dates;
    }
    
    public List<String> getEveningStar(List<CandleModel> result){
    	List<String> Dates=new ArrayList<>();
    	for(int i=9;i<200-1;i++) {
    		//1. Find the max for the past 10 days
    		List<CandleModel> sublist=result.subList(i-9,i);
    		double tendayhigh=0;
    		for(CandleModel close:sublist ) {
    			if(close.getHigh()>tendayhigh)
    				tendayhigh=close.getHigh();
    		}
    		
    		//2. Find higher and lower between day's closing and opening
    		double daymax,daymin=0;
    		if(result.get(i).getClose()>result.get(i).getOpen()) {
    			daymax=result.get(i).getClose();
    			daymin=result.get(i).getOpen();    			
    		}
    		else {
    			daymax=result.get(i).getOpen();
    			daymin=result.get(i).getClose();
    		}

    		//3.Check that top wick is higher than bottom wick by at least 3x
    		boolean isTopHigherThanBottom=false;
    		if(result.get(i).getHigh()-daymax>(daymin-result.get(i).getLow())*3) {
    			isTopHigherThanBottom=true;
    		}
    		
    		//4. Check that the absolute between Open and Close is less than (high-low)*.25
    		boolean isAbsoluteLowerThanHigh=false;
    		if(Math.abs(daymax-daymin)<(result.get(i).getHigh()-result.get(i).getLow())*0.25){
    			isAbsoluteLowerThanHigh=true;
    		}
    		
    		if(result.get(i).getHigh()>tendayhigh
    			&& isTopHigherThanBottom && isAbsoluteLowerThanHigh ) {
    			Dates.add(UtilityManager.UnixToString(result.get(i).getTimestamp()));
    		}
    			
    	}
    	Collections.reverse(Dates); //to get latest first
    	return Dates;
    }
    
    public List<String> getMorningStarCandle (List<CandleModel> result){
    	List<String> Dates=new ArrayList<>();
    	for(int i=9;i<200-1;i++) {
    		//1. Find the min for the past 10 days
    		List<CandleModel> sublist=result.subList(i-9,i);
    		double tendaylow=Double.MAX_VALUE;
    		for(CandleModel close:sublist ) {
    			if(close.getLow()<tendaylow)
    				tendaylow=close.getLow();
    		}
    		
    		//2. Find higher and lower between day's closing and opening
    		double daymax,daymin=0;
    		//if opening is higher than closing, then max = open, min-close
    		if(result.get(i).getOpen()>result.get(i).getClose()) {
    			daymax=result.get(i).getOpen();
    			daymin=result.get(i).getClose();
    		}
    		else {
    			daymax=result.get(i).getClose();
    			daymin=result.get(i).getOpen();
    		}


    		//3.Check that bottom wick is higher than top wick by at least 3x
    		boolean isBottomLowerThanTop=false;
    		//if the lowest point minus minimum
    		if(daymin-result.get(i).getLow()>(result.get(i).getHigh()-daymax)*3) {
    			isBottomLowerThanTop=true;
    		}
    		
    		//4. Check that the absolute between Open and Close is less than (high-low)*.25
    		boolean isAbsoluteLowerThanHigh=false;
    		if(Math.abs(daymax-daymin)<(result.get(i).getHigh()-result.get(i).getLow())*0.25){
    			isAbsoluteLowerThanHigh=true;
    		}
    		
    		if(result.get(i).getLow()<tendaylow
    			&& isBottomLowerThanTop 
    			&& isAbsoluteLowerThanHigh 
    			) {
    			Dates.add(UtilityManager.UnixToString(result.get(i).getTimestamp()));
    		}
    			
    	}
    	Collections.reverse(Dates); //to get latest first
    	return Dates;
    }
    
}