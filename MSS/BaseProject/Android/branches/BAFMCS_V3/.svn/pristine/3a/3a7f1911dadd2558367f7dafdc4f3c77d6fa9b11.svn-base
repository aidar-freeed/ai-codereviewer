package com.adins.mss.foundation.print;

import java.io.Serializable;


public class PrintItemBean implements Serializable {
    private String schemeId;
    private String type;
    private int questionGroupId;
    private int questionId;
    private String label;
    private String value;

    public PrintItemBean() {
    }

	/*public PrintItemBean(String s) {
		if (s == null || "".equals(s.trim()))
			throw new IllegalArgumentException("Invalid print item setting from server!");
		
		String[] arrS = Tool.split(s, Global.DELIMETER_DATA);
		if (arrS != null && arrS.length > 0) {
			int idx = 0;
			this.type = arrS[idx++];
			String qgId = arrS[idx++];
			String qId = arrS[idx++];
			this.label = arrS[idx++];
			
			if (Tool.isInteger(qgId))
				this.questionGroupId = Integer.parseInt(qgId);
			if (Tool.isInteger(qId))
				this.questionId = Integer.parseInt(qId);
		}
	}*/

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuestionGroupId() {
        return questionGroupId;
    }

    public void setQuestionGroupId(int questionGroupId) {
        this.questionGroupId = questionGroupId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getLabel() {
        return (label == null) ? "" : label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    ////////////////////
	/*public static List<PrintItemBean> parseToDataList(String printItemSetStr) {
		if (printItemSetStr == null || "".equals(printItemSetStr.trim()))
			return null;
		
		List<PrintItemBean> list = new ArrayList<PrintItemBean>();
		String[] printItemSet = Tool.split(printItemSetStr, Global.DELIMETER_ROW);
		if (printItemSet != null && printItemSet.length > 0) {
			for (String s : printItemSet) {
				PrintItemBean bean = new PrintItemBean(s);
				list.add(bean);
			}
		}		
		
		return list;
	}*/
}
