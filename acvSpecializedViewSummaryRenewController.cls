/*
Author          : Lei Zhou
Release         : 06/18/21 Release
Created         : 06/18/21
Functionality   : Controller for specialistLinesRenewals lightning component


Modification History:
*------------------------------------------------------------------------------------
Itrack#        Date        Author                             Change  
*------------------------------------------------------------------------------------
 
*------------------------------------------------------------------------------------
*/


public without sharing class acvSpecializedViewSummaryRenewController {
    
    @AuraEnabled
    public static summaryWrap getSummaryViewList(Id OpptyId) {
        //added by akoteshw
        //OpptyId = '0062T00001IyzuKQAR'  ; 
        List<summaryViewDetails> summaryViewDetlsLst = new List<summaryViewDetails>();
        //Software Summary view
        List<softwareSummaryViewDetails> swsummaryViewDetlsLst = new List<softwareSummaryViewDetails>();
        Boolean isError = False;
        List<OpportunityLinesSV__c> opptyLinesSVList = new List<OpportunityLinesSV__c>();
        List<OpportunityLineItem> opptyLinesList = new List<OpportunityLineItem>();        
        Map<Id,OpportunityLineItem> opptyLinesIdLine = new Map<Id,OpportunityLineItem>();
        Map<Id,OpportunityLinesSV__c> opptyLinesSVMap = new Map<Id,OpportunityLinesSV__c>();
        Map<String,OpportunityLineItem> opptyLinesMap = new Map<String,OpportunityLineItem>();
        String keyStr; 
        List<OpportunityLinesSV__c> opp=new List<OpportunityLinesSV__c>();
        
        try {            
            // Getting the list of OpportunityLinesSV, OpportunityLineItems from Opp where Id is in OpptyId                        
      //PMG Changes
      opp = [SELECT Id,Technology_Service__c,Technology_Service__r.Name,Business_Entity__c,
                                Sub_Business_Entity__c,Expected_000_Value__c,recordtypeId,                                                     
                                Opportunity_Stage__c,Incubation_Specialist__c,Incubation_Specialist__r.Name,Expected_Book_Date__c, 
                                Architecture_Specialist__c,Architecture_Specialist__r.Name,Opportunity__r.StageName,
                                Service_Category__c,Service_Program__c,Service_Level_Tech__c,Service_Level_Description__c,Product_Code__c,
                                Software_Specialist__c,Software_Specialist__r.Name,Opportunity_Line_Item_Reference__c,Specialist_Forecast_Status__c,
                                Virtual_Sales_Specialist__c,Virtual_Sales_Specialist__r.Name,AM_Opportunity_Line_Value_000s__c,Opportunity_Line_Value__c,
                                Opportunity__r.CurrencyIsoCode,Opportunity__r.Forecast_Status__c,Opportunity__r.CloseDate,xAM_ACV_MultiYear_Amount__c,SV_ACV_MultiYear_Amount__c,
                                SV_ACV_Annual_Amount_Value__c,xAM_ACV_Annual_Amount_Value__c,SV_Term_in_Months__c,Term_in_Months__c,
                PID_Map__c, PID_Map_Ref__c
                                FROM OpportunityLinesSV__c where DOO__c='Yes' AND Opportunity__c =:OpptyId
                              ];
            
            If(!opp.isEmpty()){
                IF((opp[0].Opportunity__r.StageName).containsIgnoreCase('1')) {
                isError = True;
                }
            }
            else{
                Opportunity oppty=[Select id,stageName from Opportunity where id=:OpptyId limit 1];
                if(oppty.stagename.containsIgnoreCase('1')){
                    isError = True;
                }
            }
            
            
            
            opptyLinesSVList.addAll(opp);
            //opptyLinesList = opp.OpportunityLineItems;
            system.debug('-----------opptyLinesList------------'+opptyLinesList);
            /*if(!opptyLinesList.isEmpty()) {
                for(OpportunityLineItem oppLines : opptyLinesList) {
                    opptyLinesIdLine.put(oppLines.Id,oppLines); // for the duplicate Tech/Services 
                    if(String.isNotBlank(oppLines.Product_Family__c)) {
                        keyStr = oppLines.Product2Id + oppLines.Product_Family__c;    
                    }else {
                        keyStr = oppLines.Product2Id;
                    }                    
                    opptyLinesMap.put(keyStr,oppLines);
                }
            }*/
            
            system.debug('-----------opptyLinesMap------------'+opptyLinesMap);
            
            //stage should be 3/4/5
            if(isError) {
                summaryViewDetails summaryViewDetls = new summaryViewDetails();
                summaryViewDetls.error = true;
                summaryViewDetls.techOrService = 'Summary view will be available after Stage 1';
                summaryViewDetlsLst.add(summaryViewDetls);
                
                //Software Summary view
                softwareSummaryViewDetails swSummaryViewDetls = new softwareSummaryViewDetails();
                swSummaryViewDetls.error = true;
                swSummaryViewDetls.techOrService = 'Summary view will be available after Stage 1';
                swsummaryViewDetlsLst.add(swSummaryViewDetls);
                
            }else if(opptyLinesSVList.isEmpty()) { //no Specialist lines available
                summaryViewDetails summaryViewDetls = new summaryViewDetails();
                summaryViewDetls.error = false;
                summaryViewDetls.techOrService = '';                
                summaryViewDetls.forecastStage = '';
                summaryViewDetls.forecastStatus = '';
                summaryViewDetls.expectedValueDiscrepancy ='';
                summaryViewDetls.bookDateDiscrepancy='';
                summaryViewDetls.owner='';
                summaryViewDetlsLst.add(summaryViewDetls);  
                //Software Summary view
                softwareSummaryViewDetails swSummaryViewDetls = new softwareSummaryViewDetails();
                swSummaryViewDetls.error = false;
                swSummaryViewDetls.techOrService = '';                
                swSummaryViewDetls.forecastStage = '';
                swSummaryViewDetls.forecastStatus = '';
                swSummaryViewDetls.expectedValueDiscrepancy ='';
                swSummaryViewDetls.bookDateDiscrepancy='';
                swSummaryViewDetls.owner='';
                swsummaryViewDetlsLst.add(swSummaryViewDetls);  
                
            } else {
                //Oppty Specialist Lines available
                if(!opptyLinesSVList.isEmpty()) {
                    
                    for(OpportunityLinesSV__c svDetls : opptyLinesSVList) {   
                        
                        String Owner = '';
                        Boolean isArchSpecialistAssocted = String.isNotBlank(svDetls.Architecture_Specialist__r.Name);
                        Boolean isIncubSpecialistAssocted = String.isNotBlank(svDetls.Incubation_Specialist__r.Name);
                        Boolean isVirtuSpecialistAssocted = String.isNotBlank(svDetls.Virtual_sales_specialist__r.Name);
                        Boolean isSoftwareSpecialistAssocted;// = String.isNotBlank(svDetls.Software_Specialist__c);
                        
                        if(svDetls.RecordTypeId == ObjectRecordTypeIds__c.getInstance().Software_Specialist_Record_Type__c) {
                            isSoftwareSpecialistAssocted = true;
                        }else {
                            isSoftwareSpecialistAssocted = false;
                        }                        
                        
                        Integer numberOfDays = 0;
                        Decimal annualValue = 0;
                        Decimal totalValue=0;
                        Decimal multiyearValue=0;
                        Decimal termValue=0;
                        
                        if(String.isNotBlank(svDetls.PID_Map__c)) {
                            keyStr = svDetls.Technology_Service__c + svDetls.PID_Map__c;    
                        }else {
                            keyStr = svDetls.Technology_Service__c;
                        }
                        
                        summaryViewDetails summaryViewDetls = new summaryViewDetails();
                        
                        //Software Summary view
                        softwareSummaryViewDetails swSummaryViewDetls = new softwareSummaryViewDetails();
                        
                        if(isSoftwareSpecialistAssocted) {
                            swSummaryViewDetls.error = false;
                        }
                        
                        if(!isSoftwareSpecialistAssocted) { //Archi/Incubation
                            summaryViewDetls.error = false;
                        }
                        
                        
                        if((svDetls.Product_Code__c).equalsIgnoreCase('Technology') ) {
                            
                            if(String.isBlank(svDetls.Business_Entity__c) && String.isBlank(svDetls.Sub_Business_Entity__c)) {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) {
                                    swSummaryViewDetls.techOrService = svDetls.Technology_Service__r.Name;                                    
                                }else {//Archi/Incubation
                                    summaryViewDetls.techOrService = svDetls.Technology_Service__r.Name;
                                }
                                
                            }else {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) { 
                                    swSummaryViewDetls.techOrService = svDetls.Business_Entity__c; 
                                }else { //Archi/Incubation
                                    summaryViewDetls.techOrService = svDetls.Business_Entity__c; 
                                }
                                
                                //assign sub BE if not null
                                if(String.isNotBlank(svDetls.Sub_Business_Entity__c)) {                                    
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) { 
                                        swSummaryViewDetls.techOrService +=  '-' + svDetls.Sub_Business_Entity__c;
                                    }else { //Archi/Incubation
                                        summaryViewDetls.techOrService +=  '-' + svDetls.Sub_Business_Entity__c;
                                    }
                                } 
                                //assign PID map if not null
                //PMG changes
                if(String.isNotBlank(svDetls.PID_Map__c)) {                                    
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) { 
                                        swSummaryViewDetls.techOrService += ':'+svDetls.PID_Map__c;
                                    }else { //Archi/Incubation
                                        summaryViewDetls.techOrService += ':'+svDetls.PID_Map__c;
                                    }
                                }
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) {
                                    swSummaryViewDetls.techOrService = (swSummaryViewDetls.techOrService).removeStart('-');                                                                 
                                }else { //Archi/Incubation
                                    summaryViewDetls.techOrService = (summaryViewDetls.techOrService).removeStart('-');                                                             
                                }
                            }
                        } else {
                            if(String.isNotBlank(svDetls.Service_Category__c)) {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) { 
                                    swSummaryViewDetls.techOrService = svDetls.Service_Category__c;
                                }else { //Archi/Incubation
                                    summaryViewDetls.techOrService = svDetls.Service_Category__c;
                                }
                            }
                            if(String.isNotBlank(svDetls.Service_Program__c)) {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) { 
                                    swSummaryViewDetls.techOrService += '-' +svDetls.Service_Program__c;
                                }else { //Archi/Incubation
                                    summaryViewDetls.techOrService += '-' +svDetls.Service_Program__c;
                                }
                            }
                            if(String.isNotBlank(svDetls.Service_Level_Tech__c)) {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) { 
                                    swSummaryViewDetls.techOrService += '-' +svDetls.Service_Level_Tech__c;
                                }else { //Archi/Incubation
                                    summaryViewDetls.techOrService += '-' +svDetls.Service_Level_Tech__c;
                                }
                            }
                            if(String.isNotBlank(svDetls.Service_Level_Description__c)) {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) { 
                                    swSummaryViewDetls.techOrService += '-' +svDetls.Service_Level_Description__c;
                                } else { //Archi/Incubation
                                    summaryViewDetls.techOrService += '-' +svDetls.Service_Level_Description__c;
                                }
                            }                            
                            
                            //Software Summary view    
                            if(isSoftwareSpecialistAssocted) {   
                                if(String.isNotBlank(swSummaryViewDetls.techOrService)) {
                                    swSummaryViewDetls.techOrService = (swSummaryViewDetls.techOrService).removeStart('-');
                                    swSummaryViewDetls.techOrService = (swSummaryViewDetls.techOrService).removeEnd('-');                                                                
                                }                                
                            } else { //Archi/Incubation
                                if(String.isNotBlank(summaryViewDetls.techOrService)) {
                                    summaryViewDetls.techOrService = (summaryViewDetls.techOrService).removeStart('-');
                                    summaryViewDetls.techOrService = (summaryViewDetls.techOrService).removeEnd('-');                                   
                                }
                            }
                        }   
                        
                        if(String.isBlank(svDetls.Opportunity_Stage__c)) {                            
                            //Software Summary view
                            if(isSoftwareSpecialistAssocted) {                         
                                swSummaryViewDetls.forecastStage = svDetls.opportunity__r.StageName;
                                swSummaryViewDetls.isStageDiscrepancy = False;  
                            }else { //Archi/Incubation
                                summaryViewDetls.forecastStage = svDetls.opportunity__r.StageName;
                                summaryViewDetls.isStageDiscrepancy = False;  
                            }
                        }else {
                            if((svDetls.opportunity__r.StageName).equalsIgnoreCase(svDetls.Opportunity_Stage__c)) {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) {                                                          
                                    swSummaryViewDetls.isStageDiscrepancy = False;  
                                }else { //Archi/Incubation
                                    summaryViewDetls.isStageDiscrepancy = False; 
                                }
                            }else {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) { 
                                    swSummaryViewDetls.isStageDiscrepancy = True;  
                                }else { //Archi/Incubation
                                    summaryViewDetls.isStageDiscrepancy = True; 
                                }
                            }                                                        
                            //Software Summary view
                            if(isSoftwareSpecialistAssocted) {                         
                                swSummaryViewDetls.forecastStage = svDetls.Opportunity_Stage__c;
                            }else { //Archi/Incubation
                                summaryViewDetls.forecastStage = svDetls.Opportunity_Stage__c;
                            }
                        }
                        //Forecast Status added by R.Hari Krishna(ratharik) : US398822: Relaxed Stage & Forecast Status - New Dependency
                        if(String.isBlank(svDetls.Specialist_Forecast_Status__c)) {                            
                            //Software Summary view
                            if(isSoftwareSpecialistAssocted) {                         
                                swSummaryViewDetls.forecastStatus = svDetls.opportunity__r.Forecast_Status__c;
                                swSummaryViewDetls.isforecastStusDiscrepancy = False;  
                            }else { //Archi/Incubation
                                summaryViewDetls.forecastStatus = svDetls.opportunity__r.Forecast_Status__c;
                                summaryViewDetls.isforecastStusDiscrepancy = False;  
                            }
                        }else {
                            if((svDetls.opportunity__r.Forecast_Status__c).equalsIgnoreCase(svDetls.Specialist_Forecast_Status__c)) {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) {                                                          
                                    swSummaryViewDetls.isforecastStusDiscrepancy = False;  
                                }else { //Archi/Incubation
                                    summaryViewDetls.isforecastStusDiscrepancy = False; 
                                }
                            }else {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) { 
                                    swSummaryViewDetls.isforecastStusDiscrepancy = True;  
                                }else { //Archi/Incubation
                                    summaryViewDetls.isforecastStusDiscrepancy = True; 
                                }
                            }                                                        
                            //Software Summary view
                            if(isSoftwareSpecialistAssocted) {                         
                                swSummaryViewDetls.forecastStatus = svDetls.Specialist_Forecast_Status__c;
                            }else { //Archi/Incubation
                                summaryViewDetls.forecastStatus = svDetls.Specialist_Forecast_Status__c;
                            }
                        }
                        
                        //logical assignment of Owner
                        //Software Summary view
                        if(isSoftwareSpecialistAssocted) {                         
                            swSummaryViewDetls.owner = svDetls.Software_Specialist__r.Name;
                        }else { //Archi/Incubation  
                            if( isArchSpecialistAssocted &&  isIncubSpecialistAssocted && isVirtuSpecialistAssocted) {
                                Owner = svDetls.Architecture_Specialist__r.Name + ' / '+ svDetls.Incubation_Specialist__r.Name+' / '+svDetls.Virtual_sales_specialist__r.Name;
                            } else if(isArchSpecialistAssocted &&  isIncubSpecialistAssocted){
                                Owner = svDetls.Architecture_Specialist__r.Name + ' / '+ svDetls.Incubation_Specialist__r.Name;
                            }else if(isIncubSpecialistAssocted && isVirtuSpecialistAssocted){
                                Owner = svDetls.Incubation_Specialist__r.Name+' / '+svDetls.Virtual_sales_specialist__r.Name;
                            }else if(isArchSpecialistAssocted && isVirtuSpecialistAssocted){
                                Owner = svDetls.Architecture_Specialist__r.Name + ' / '+svDetls.Virtual_sales_specialist__r.Name;
                            }else if(isArchSpecialistAssocted) {
                                Owner = svDetls.Architecture_Specialist__r.Name;
                            } else if(isIncubSpecialistAssocted) {
                                Owner = svDetls.Incubation_Specialist__r.Name;
                            }
                            else if(isVirtuSpecialistAssocted) {
                                Owner = svDetls.Virtual_Sales_Specialist__r.Name;
                            }
                            summaryViewDetls.owner = Owner;                            
                        }
                        //Logic to check Book Date Discrepancy
                        if(svDetls.opportunity__r.CloseDate < svDetls.Expected_Book_Date__c) {
                            numberOfDays = (svDetls.opportunity__r.CloseDate).daysBetween(svDetls.Expected_Book_Date__c);                            
                            //Software Summary view
                            if(isSoftwareSpecialistAssocted) {                         
                                swSummaryViewDetls.bookDateDiscrepancy = 'Pushed out '+ numberOfDays +' days '+ String.valueOf(svDetls.Expected_Book_Date__c);  
                                swSummaryViewDetls.isDateDiscrepancy = True;
                            } else { //Archi/Incubation  
                                summaryViewDetls.bookDateDiscrepancy = 'Pushed out '+ numberOfDays +' days '+ String.valueOf(svDetls.Expected_Book_Date__c);  
                                summaryViewDetls.isDateDiscrepancy = True;
                            }
                        }else if(svDetls.opportunity__r.CloseDate > svDetls.Expected_Book_Date__c) {
                            numberOfDays = (svDetls.Expected_Book_Date__c).daysBetween(svDetls.opportunity__r.CloseDate);                            
                            //Software Summary view
                            if(isSoftwareSpecialistAssocted) {                         
                                swSummaryViewDetls.bookDateDiscrepancy = 'Pulled in '+ numberOfDays +' days '+ String.valueOf(svDetls.Expected_Book_Date__c);                        
                                swSummaryViewDetls.isDateDiscrepancy = True;
                            } else { //Archi/Incubation  
                                summaryViewDetls.bookDateDiscrepancy = 'Pulled in '+ numberOfDays +' days '+ String.valueOf(svDetls.Expected_Book_Date__c);                        
                                summaryViewDetls.isDateDiscrepancy = True;
                            }
                        }else { // no Discrepancy
                            
                            //Software Summary view
                            if(isSoftwareSpecialistAssocted) {              
                                swSummaryViewDetls.bookDateDiscrepancy = String.valueOf(svDetls.opportunity__r.CloseDate);   
                                swSummaryViewDetls.isDateDiscrepancy = False; 
                            } else { //Archi/Incubation  
                                summaryViewDetls.bookDateDiscrepancy = String.valueOf(svDetls.opportunity__r.CloseDate);   
                                summaryViewDetls.isDateDiscrepancy = False; 
                            }
                        }
                        //Put all 0 values to null, will be easier to write logic
                        if(svDetls.AM_Opportunity_Line_Value_000s__c==null){svDetls.AM_Opportunity_Line_Value_000s__c=0;}                       
                        if(svDetls.Opportunity_Line_Value__c==null){svDetls.Opportunity_Line_Value__c=0;}
                        if(svDetls.Term_in_Months__c==null){svDetls.Term_in_Months__c=0;}                       
                        if(svDetls.SV_Term_in_Months__c==null){svDetls.SV_Term_in_Months__c=0;}
                        
                            
                           // if(svDetls.AM_Opportunity_Line_Value_000s__c != 0 && svDetls.Opportunity_Line_Value__c !=0) {
                                //Logic to check Expected Value Discrepancy.
                                System.debug('>>> '+svDetls.AM_Opportunity_Line_Value_000s__c);
                                System.debug('>>> '+svDetls.Opportunity_Line_Value__c);
                                if(svDetls.AM_Opportunity_Line_Value_000s__c  < svDetls.Opportunity_Line_Value__c) {
                                    totalValue = svDetls.Opportunity_Line_Value__c - svDetls.AM_Opportunity_Line_Value_000s__c;//Software Summary view
                                    System.debug('+'+totalValue);
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.expectedValueDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' +'+String.valueOf(totalValue.setScale(2));
                                        swSummaryViewDetls.isValueDiscrepancy = True;
                                    }else { //Archi/Incubation  
                                        summaryViewDetls.expectedValueDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' +'+String.valueOf(totalValue.setScale(2));
                                        summaryViewDetls.isValueDiscrepancy = True;
                                    }
                                }else if(svDetls.AM_Opportunity_Line_Value_000s__c  > svDetls.Opportunity_Line_Value__c) {                                
                                    totalValue = svDetls.AM_Opportunity_Line_Value_000s__c - svDetls.Opportunity_Line_Value__c;                                         
                                    System.debug('-'+totalValue);
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.expectedValueDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' -'+String.valueOf(totalValue.setScale(2));
                                        swSummaryViewDetls.isValueDiscrepancy = True;
                                    } else { //Archi/Incubation  
                                        summaryViewDetls.expectedValueDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' -'+String.valueOf(totalValue.setScale(2));//.round(System.RoundingMode.CEILING); 
                                        summaryViewDetls.isValueDiscrepancy = True;
                                    }
                                }else { 
                                    System.debug('+'+totalValue);
                                    // No Discrepancy                                    
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) {                                                  
                                        swSummaryViewDetls.expectedValueDiscrepancy = '';
                                        swSummaryViewDetls.isValueDiscrepancy = False; 
                                    }else {  //Archi/Incubation  
                                        summaryViewDetls.expectedValueDiscrepancy = '';
                                        summaryViewDetls.isValueDiscrepancy = False; 
                                    }
                                } 
                           // } 
                                //Specialist didn't add value default to Oppty Line Value if not null 
                               /* else if(svDetls.Opportunity_Line_Value__c == 0 && svDetls.AM_Opportunity_Line_Value_000s__c != 0) {                                     
                                    //Software Summary view
                                    System.debug('+'+totalValue);
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.expectedValueDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00';//opp.CurrencyIsoCode+' '+String.valueOf((opptyLineValue).setScale(2));
                                        swSummaryViewDetls.isValueDiscrepancy = True;  
                                    }else { //Archi/Incubation  
                                        summaryViewDetls.expectedValueDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00';//opp.CurrencyIsoCode+' '+String.valueOf((opptyLineValue).setScale(2));
                                        summaryViewDetls.isValueDiscrepancy = True;    
                                    }
                                }
                                
                          //new Lines added by Specialist                            
                            else if(svDetls.Opportunity_Line_Value__c != 0 && svDetls.AM_Opportunity_Line_Value_000s__c  == 0) {                                
                                //Software Summary view
                                System.debug('+'+totalValue);
                                if(isSoftwareSpecialistAssocted) {              
                                    swSummaryViewDetls.expectedValueDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' '+ String.valueOf(svDetls.Opportunity_Line_Value__c.setScale(2));
                                    swSummaryViewDetls.isValueDiscrepancy = True;
                                } else { //Archi/Incubation  
                                    summaryViewDetls.expectedValueDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' '+ String.valueOf(svDetls.Opportunity_Line_Value__c.setScale(2));
                                    summaryViewDetls.isValueDiscrepancy = True; 
                                }
                            }
                            else {        
                                System.debug('+'+totalValue);
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) {              
                                    swSummaryViewDetls.expectedValueDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00';
                                    swSummaryViewDetls.isValueDiscrepancy = False;  
                                } else { //Archi/Incubation  
                                    summaryViewDetls.expectedValueDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00'; 
                                    summaryViewDetls.isValueDiscrepancy = False; 
                                }
                            }*/ 
                        
                        //Add Multiyear values for DOO
                         // if(svDetls.xAM_ACV_MultiYear_Amount__c != 0 && svDetls.SV_ACV_MultiYear_Amount__c !=0) {
                                //Logic to check Expected Value Discrepancy.
                                if(svDetls.xAM_ACV_MultiYear_Amount__c  < svDetls.SV_ACV_MultiYear_Amount__c) {
                                    multiyearValue = svDetls.SV_ACV_MultiYear_Amount__c - svDetls.xAM_ACV_MultiYear_Amount__c;//Software Summary view
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.multiYearDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' +'+String.valueOf(multiyearValue.setScale(2));
                                        swSummaryViewDetls.ismultiYearDiscrepancy = True;
                                    }else { //Archi/Incubation  
                                        summaryViewDetls.multiYearDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' +'+String.valueOf(multiyearValue.setScale(2));
                                        summaryViewDetls.ismultiYearDiscrepancy = True;
                                    }
                                }else if(svDetls.xAM_ACV_MultiYear_Amount__c  > svDetls.SV_ACV_MultiYear_Amount__c) {                                
                                    multiyearValue = svDetls.xAM_ACV_MultiYear_Amount__c - svDetls.SV_ACV_MultiYear_Amount__c;                                         
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.multiYearDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' -'+String.valueOf(multiyearValue.setScale(2));
                                        swSummaryViewDetls.ismultiYearDiscrepancy = True;
                                    } else { //Archi/Incubation  
                                        summaryViewDetls.multiYearDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' -'+String.valueOf(multiyearValue.setScale(2));//.round(System.RoundingMode.CEILING); 
                                        summaryViewDetls.ismultiYearDiscrepancy = True;
                                    }
                                }else { // No Discrepancy                                    
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) {                                                  
                                        swSummaryViewDetls.multiYearDiscrepancy = '';
                                        swSummaryViewDetls.ismultiYearDiscrepancy = False; 
                                    }else {  //Archi/Incubation  
                                        summaryViewDetls.multiYearDiscrepancy = '';
                                        summaryViewDetls.ismultiYearDiscrepancy = False; 
                                    }
                                } 
                           /* } 
                                //Specialist didn't add value default to Oppty Line Value if not null 
                                else if(svDetls.SV_ACV_MultiYear_Amount__c == 0 && svDetls.xAM_ACV_MultiYear_Amount__c != 0) {                                     
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.multiYearDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00';//opp.CurrencyIsoCode+' '+String.valueOf((opptyLineValue).setScale(2));
                                        swSummaryViewDetls.ismultiYearDiscrepancy = True;  
                                    }else { //Archi/Incubation  
                                        summaryViewDetls.multiYearDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00';//opp.CurrencyIsoCode+' '+String.valueOf((opptyLineValue).setScale(2));
                                        summaryViewDetls.ismultiYearDiscrepancy = True;    
                                    }
                                }
                                
                          //new Lines added by Specialist                            
                            else if(svDetls.SV_ACV_MultiYear_Amount__c != 0 && svDetls.xAM_ACV_MultiYear_Amount__c  == 0) {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) {              
                                    swSummaryViewDetls.multiYearDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' '+ String.valueOf(svDetls.SV_ACV_MultiYear_Amount__c.setScale(2));
                                    swSummaryViewDetls.ismultiYearDiscrepancy = True;
                                } else { //Archi/Incubation  
                                    summaryViewDetls.multiYearDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' '+ String.valueOf(svDetls.SV_ACV_MultiYear_Amount__c.setScale(2));
                                    summaryViewDetls.ismultiYearDiscrepancy = True; 
                                }
                            }
                            else {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) {              
                                    swSummaryViewDetls.multiYearDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00';
                                    swSummaryViewDetls.ismultiYearDiscrepancy = False;  
                                } else { //Archi/Incubation  
                                    summaryViewDetls.multiYearDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00'; 
                                    summaryViewDetls.ismultiYearDiscrepancy = False; 
                                }
                            }*/
                        //Annual Discrepancy calculation DOO
                         //if(svDetls.xAM_ACV_Annual_Amount_Value__c != 0 && svDetls.SV_ACV_Annual_Amount_Value__c !=0) {
                                //Logic to check Expected Value Discrepancy.
                                if(svDetls.xAM_ACV_Annual_Amount_Value__c  < svDetls.SV_ACV_Annual_Amount_Value__c) {
                                    annualValue = svDetls.SV_ACV_Annual_Amount_Value__c - svDetls.xAM_ACV_Annual_Amount_Value__c;//Software Summary view
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.annualDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' +'+String.valueOf(annualValue.setScale(2));
                                        swSummaryViewDetls.isannualDiscrepancy = True;
                                    }else { //Archi/Incubation  
                                        summaryViewDetls.annualDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' +'+String.valueOf(annualValue.setScale(2));
                                        summaryViewDetls.isannualDiscrepancy = True;
                                    }
                                }else if(svDetls.xAM_ACV_Annual_Amount_Value__c  > svDetls.SV_ACV_Annual_Amount_Value__c) {                                
                                    annualValue = svDetls.xAM_ACV_Annual_Amount_Value__c - svDetls.SV_ACV_Annual_Amount_Value__c;                                         
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.annualDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' -'+String.valueOf(annualValue.setScale(2));
                                        swSummaryViewDetls.isannualDiscrepancy = True;
                                    } else { //Archi/Incubation  
                                        summaryViewDetls.annualDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' -'+String.valueOf(annualValue.setScale(2));//.round(System.RoundingMode.CEILING); 
                                        summaryViewDetls.isannualDiscrepancy = True;
                                    }
                                }else { // No Discrepancy                                    
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) {                                                  
                                        swSummaryViewDetls.annualDiscrepancy = '';
                                        swSummaryViewDetls.isannualDiscrepancy = False; 
                                    }else {  //Archi/Incubation  
                                        summaryViewDetls.annualDiscrepancy = '';
                                        summaryViewDetls.isannualDiscrepancy = False; 
                                    }
                                } 
                           /* } 
                                //Specialist didn't add value default to Oppty Line Value if not null 
                                else if(svDetls.SV_ACV_Annual_Amount_Value__c == 0 && svDetls.xAM_ACV_Annual_Amount_Value__c != 0) {                                     
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.annualDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00';//opp.CurrencyIsoCode+' '+String.valueOf((opptyLineValue).setScale(2));
                                        swSummaryViewDetls.isannualDiscrepancy = True;  
                                    }else { //Archi/Incubation  
                                        summaryViewDetls.annualDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00';//opp.CurrencyIsoCode+' '+String.valueOf((opptyLineValue).setScale(2));
                                        summaryViewDetls.isannualDiscrepancy = True;    
                                    }
                                }
                                
                          //new Lines added by Specialist                            
                            else if(svDetls.SV_ACV_Annual_Amount_Value__c != 0 && svDetls.xAM_ACV_Annual_Amount_Value__c  == 0) {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) {              
                                    swSummaryViewDetls.annualDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' '+ String.valueOf(svDetls.SV_ACV_Annual_Amount_Value__c.setScale(2));
                                    swSummaryViewDetls.isannualDiscrepancy = True;
                                } else { //Archi/Incubation  
                                    summaryViewDetls.annualDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' '+ String.valueOf(svDetls.SV_ACV_Annual_Amount_Value__c.setScale(2));
                                    summaryViewDetls.isannualDiscrepancy = True; 
                                }
                            }
                            else {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) {              
                                    swSummaryViewDetls.annualDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00';
                                    swSummaryViewDetls.isannualDiscrepancy = False;  
                                } else { //Archi/Incubation  
                                    summaryViewDetls.annualDiscrepancy = svDetls.opportunity__r.CurrencyIsoCode+' 0.00'; 
                                    summaryViewDetls.isannualDiscrepancy = False; 
                                }
                            }                 */
                        
                        
                        //Term Discrepancy DOO
                        
                         //if(svDetls.Term_in_Months__c != 0 && svDetls.SV_Term_in_Months__c !=0) {
                                //Logic to check Expected Value Discrepancy.
                                if(svDetls.Term_in_Months__c  < svDetls.SV_Term_in_Months__c) {
                                    termValue = svDetls.SV_Term_in_Months__c - svDetls.Term_in_Months__c;//Software Summary view
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.termDiscrepancy =' +'+ String.valueOf(termValue);
                                        swSummaryViewDetls.istermDiscrepancy = True;
                                    }else { //Archi/Incubation  
                                        summaryViewDetls.termDiscrepancy = ' +'+String.valueOf(termValue);
                                        summaryViewDetls.istermDiscrepancy = True;
                                    }
                                }else if(svDetls.Term_in_Months__c  > svDetls.SV_Term_in_Months__c) {                                
                                    termValue = svDetls.SV_Term_in_Months__c - svDetls.Term_in_Months__c;                                         
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.termDiscrepancy =String.valueOf(termValue);
                                        swSummaryViewDetls.istermDiscrepancy = True;
                                    } else { //Archi/Incubation  
                                        summaryViewDetls.termDiscrepancy = String.valueOf(termValue);//.round(System.RoundingMode.CEILING); 
                                        summaryViewDetls.istermDiscrepancy = True;
                                    }
                                }else { // No Discrepancy                                    
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) {                                                  
                                        swSummaryViewDetls.termDiscrepancy = '0';
                                        swSummaryViewDetls.istermDiscrepancy = False; 
                                    }else {  //Archi/Incubation  
                                        summaryViewDetls.termDiscrepancy = '0';
                                        summaryViewDetls.istermDiscrepancy = False; 
                                    }
                                } 
                          /*  } 
                                //Specialist didn't add value default to Oppty Line Value if not null 
                                else if(svDetls.SV_Term_in_Months__c == 0 && svDetls.Term_in_Months__c != 0) {                                     
                                    //Software Summary view
                                    if(isSoftwareSpecialistAssocted) {              
                                        swSummaryViewDetls.termDiscrepancy = String.valueOf(svDetls.SV_Term_in_Months__c);
                                        swSummaryViewDetls.istermDiscrepancy = True;  
                                    }else { //Archi/Incubation  
                                        summaryViewDetls.termDiscrepancy = String.valueOf(svDetls.SV_Term_in_Months__c);
                                        summaryViewDetls.istermDiscrepancy = True;    
                                    }
                                }
                                
                          //new Lines added by Specialist                            
                            else if(svDetls.SV_Term_in_Months__c != 0 && svDetls.Term_in_Months__c  == 0) {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) {              
                                    swSummaryViewDetls.termDiscrepancy = String.valueOf(svDetls.SV_Term_in_Months__c);
                                    swSummaryViewDetls.istermDiscrepancy = True;
                                } else { //Archi/Incubation  
                                    summaryViewDetls.termDiscrepancy = String.valueOf(svDetls.SV_Term_in_Months__c);
                                    summaryViewDetls.istermDiscrepancy = True; 
                                }
                            }
                            else {                                
                                //Software Summary view
                                if(isSoftwareSpecialistAssocted) {              
                                    swSummaryViewDetls.termDiscrepancy = String.valueOf(0);
                                    swSummaryViewDetls.istermDiscrepancy = False;  
                                } else { //Archi/Incubation  
                                    summaryViewDetls.termDiscrepancy = String.valueOf(0); 
                                    summaryViewDetls.istermDiscrepancy = False; 
                                }
                            }                 
                       */
                                                                     
                            //Software Summary view
                        if(isSoftwareSpecialistAssocted) {              
                            swSummaryViewDetlsLst.add(swSummaryViewDetls);  
                        }else { //Archi/Incubation  
                            summaryViewDetlsLst.add(summaryViewDetls);      
                        }
                    }            
                } 
            } 
            //if swSummaryViewDetlsLst blank
            if(summaryViewDetlsLst.isEmpty() && summaryViewDetlsLst.size() == 0) {
                summaryViewDetails summaryViewDetls = new summaryViewDetails();
                summaryViewDetls.error = false;
                summaryViewDetls.techOrService = '';                
                summaryViewDetls.forecastStage = '';
                summaryViewDetls.forecastStatus = '';
                summaryViewDetls.expectedValueDiscrepancy ='';
                summaryViewDetls.bookDateDiscrepancy='';
                summaryViewDetls.owner='';
                //summaryViewDetls.ismultiYearDiscrepancy='';
                summaryViewDetls.multiYearDiscrepancy='';
                //summaryViewDetls.isannualDiscrepancy='';
                summaryViewDetls.annualDiscrepancy='';
                //summaryViewDetls.istermDiscrepancy='';
                summaryViewDetls.termDiscrepancy=''; 
                summaryViewDetlsLst.add(summaryViewDetls);  
                
            }
            
            //if swSummaryViewDetlsLst blank
            if(swSummaryViewDetlsLst.isEmpty() && swSummaryViewDetlsLst.size() == 0) {
                softwareSummaryViewDetails swSummaryViewDetls = new softwareSummaryViewDetails();
                //EA Oppty or not
                
                swSummaryViewDetls.error = false;
                swSummaryViewDetls.techOrService = '';                
                swSummaryViewDetls.forecastStage = '';
                swSummaryViewDetls.forecastStatus = '';
                swSummaryViewDetls.expectedValueDiscrepancy ='';
                swSummaryViewDetls.bookDateDiscrepancy='';
                swSummaryViewDetls.owner='';
                //summaryViewDetls.ismultiYearDiscrepancy='';
                swSummaryViewDetls.multiYearDiscrepancy='';
                //summaryViewDetls.isannualDiscrepancy='';
                swSummaryViewDetls.annualDiscrepancy='';
                //summaryViewDetls.istermDiscrepancy='';
                swSummaryViewDetls.termDiscrepancy=''; 
                swSummaryViewDetlsLst.add(swSummaryViewDetls);  
            }
        }catch(Exception e) {
            system.debug('----------Exception --------'+e.getLineNumber()+'@@'+e.getCause()+'@@@'+e.getMessage()+'@@@@'+e.getStackTraceString());
        }
        
        system.debug('-----summaryViewDetlsLst---------'+summaryViewDetlsLst);
        system.debug('-----swSummaryViewDetlsLst---------'+swSummaryViewDetlsLst);
        
        summaryWrap sumryWrap = new summaryWrap();
        sumryWrap.specialistDtls = summaryViewDetlsLst;
        sumryWrap.swSpecialistDtls = swSummaryViewDetlsLst;
        // Returning the Summary details
        return sumryWrap;
    }
    
    /***************
* @Author : R.Hari Krishna(ratharik)
* @Description : Wrapper class to hold summarized View Details
*****************************/
    public class summaryViewDetails {
        @AuraEnabled
        public String techOrService{get;set;}       
        @AuraEnabled
        public String expectedValueDiscrepancy{get;set;}
        @AuraEnabled
        public String bookDateDiscrepancy{get;set;}
        @AuraEnabled
        public String forecastStage{get;set;}        
        @AuraEnabled
        public String owner{get;set;}
        @AuraEnabled
        public Boolean isValueDiscrepancy{get;set;}
        @AuraEnabled
        public Boolean isDateDiscrepancy{get;set;}
        @AuraEnabled
        public Boolean isStageDiscrepancy{get;set;}        
        @AuraEnabled
        public Boolean error{get;set;}   
        //Forecast Status added by R.Hari Krishna(ratharik) : US398822: Relaxed Stage & Forecast Status - New Dependency
        @AuraEnabled
        public String forecastStatus{get;set;}
        @AuraEnabled
        public Boolean isforecastStusDiscrepancy{get;set;}
        @AuraEnabled
        public Boolean ismultiYearDiscrepancy{get;set;}
        @AuraEnabled
        public String multiYearDiscrepancy{get;set;}
        @AuraEnabled
        public Boolean isannualDiscrepancy{get;set;}
        @AuraEnabled
        public String annualDiscrepancy{get;set;}
         @AuraEnabled
        public Boolean istermDiscrepancy{get;set;}
        @AuraEnabled
        public String termDiscrepancy{get;set;}
    }
    
    
    /***************
* @Author : R.Hari Krishna(ratharik)
* @Description : Wrapper class to hold Software summarized View Details
*****************************/
    public class softwareSummaryViewDetails {
        @AuraEnabled
        public String techOrService{get;set;}        
        @AuraEnabled
        public String expectedValueDiscrepancy{get;set;}
        @AuraEnabled
        public String bookDateDiscrepancy{get;set;}
        @AuraEnabled
        public String forecastStage{get;set;}        
        @AuraEnabled
        public String owner{get;set;}
        @AuraEnabled
        public Boolean isValueDiscrepancy{get;set;}
        @AuraEnabled
        public Boolean isDateDiscrepancy{get;set;}
        @AuraEnabled
        public Boolean isStageDiscrepancy{get;set;}        
        @AuraEnabled
        public Boolean error{get;set;}
        @AuraEnabled
        public Boolean isEAOppty{get;set;} 
        //Forecast Status added by R.Hari Krishna(ratharik) : US398822: Relaxed Stage & Forecast Status - New Dependency
        @AuraEnabled
        public String forecastStatus{get;set;}
        @AuraEnabled
        public Boolean isforecastStusDiscrepancy{get;set;}
         @AuraEnabled
        public Boolean ismultiYearDiscrepancy{get;set;}
        @AuraEnabled
        public String multiYearDiscrepancy{get;set;}
         @AuraEnabled
        public Boolean isannualDiscrepancy{get;set;}
        @AuraEnabled
        public String annualDiscrepancy{get;set;}   
         @AuraEnabled
        public Boolean istermDiscrepancy{get;set;}
        @AuraEnabled
        public String termDiscrepancy{get;set;}
        
    }
    
    /***************
* @Author : R.Hari Krishna(ratharik)
* @Description : Wrapper class to hold both Software & Specialist Summary details
*****************************/
    public class summaryWrap {
        @AuraEnabled
        public List<summaryViewDetails> specialistDtls{get;set;}        
        @AuraEnabled
        public List<softwareSummaryViewDetails> swSpecialistDtls{get;set;}
    }
    @AuraEnabled
    public static void deleteRecords(){
        List<OpportunityLinesSV__c> opptySV =[Select id,Software_Specialist__c,Incubation_Specialist__c,Architecture_Specialist__c from OpportunityLinesSV__c WHERE Software_Specialist__c=null and Incubation_Specialist__c= null and Architecture_Specialist__c =null limit 10];
        if(opptySV.size()>0){
            delete opptySV;
        }
    }
    
   // Method for checking the EA opportunity   
    @AuraEnabled
    public static string checkEAOppty(String recId){
        // added by akoteshw
       // recId = '0062T00001IyzuKQAR' ;
        Opportunity  eaAccess = [ SELECT id, Enterprise_Agreement_Opportunity__c FROM Opportunity WHERE id =:recId limit 1];
        return eaAccess.Enterprise_Agreement_Opportunity__c;
    }
    
    //SE Evol - SE Multi Edit with Specialist View fields US273620 start
    @AuraEnabled
    public static boolean checkSEUser(){
        boolean isSEUser=false;
        User usr=[Select id, Additional_User_Permissions__c,Profile.name from User where id=:UserInfo.getUserId()];
        ProfileID__c p = ProfileID__c.getOrgDefaults();
        if(String.valueOf(usr).contains('Additional_User_Permissions__c')){
        if((usr.Additional_User_Permissions__c.contains('Enable_ePSS')||usr.Additional_User_Permissions__c.contains('Enable_VSS'))&&
           (usr.ProfileId==p.Cisco_Sales_PSS_Pilot__c || usr.ProfileId==p.Cisco_ADMIN__c)){ 
               // added by akoteshw admin condition to be removed
               isSEUser=false;
           }
            else{
                isSEUser=true;
            }
        }
        else{
            isSEUser=true;
        }
        return isSEUser;
    }
    //SE Evol - SE Multi Edit with Specialist View fields US273620 end
    
      @AuraEnabled
    public static VsUserwrapper ifVssUser(String recId){

        VsUserwrapper VSS = new VsUserwrapper();

        user u = [select id,name,profile.name from user where id=:userinfo.getuserid()];
        list<opportunity> oplist = [select id,name,Enterprise_Agreement_Opportunity__c from Opportunity where Id=:recId];
        list<OpportunityTeamMember> RAList = [ select id,OpportunityId,UserId,TeamMemberRole from OpportunityTeamMember where OpportunityId=:recId and  UserId=:userInfo.getUserId()];
        if(u.profile.name == 'Cisco_Sales_PSS_Pilot'){
        if(RAList.size()>0){
            if(RAList[0].TeamMemberRole != 'Software Specialist' && RAList[0].TeamMemberRole != 'Architecture Specialist' && RAList[0].TeamMemberRole != 'Incubation Specialist' && RAList[0].TeamMemberRole != 'Virtual Sales Specialist'){
                VSS.validated = 'true';
                VSS.EaValue = 'Vss';
            }
            if(RAList[0].TeamMemberRole == 'Software Specialist' && oplist[0].Enterprise_Agreement_Opportunity__c == 'yes'){
                VSS.validated = 'true';
                VSS.EaValue = 'true';
            }
            if(RAList[0].TeamMemberRole == 'Software Specialist' && oplist[0].Enterprise_Agreement_Opportunity__c == 'no'){
                VSS.validated = 'true';
                VSS.EaValue = 'false';
            }
        }
        }
        // added by akoteshw 
        VSS.validated = 'true';
        VSS.EaValue = 'true';
        return VSS;
    }
    public class VsUserwrapper{
        @auraEnabled public string validated{get;set;}
        @auraEnabled public string EaValue{get;set;}
    }
}
