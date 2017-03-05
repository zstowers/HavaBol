package havabol;

public class ResultValue {

	public String type; // usually data type of the result
	public String value; // value of the result
	public String structure; // primitive, fixed array, unbounded array
	public String terminatingStr; // used for end of lists of things (e.g., a list of
							// statements might be
							// terminated by "endwhile")
	
	
	public ResultValue() {
		
		this.type = "";
		this.value = "";
		this.structure = "";
		this.terminatingStr = "";
	}

//	public String getType() {
//		return type;
//	}

//	public void setType(String type) {
//		this.type = type;
//	}

//	public String getValue() {
//		return value;
//	}

//	public void setValue(String value) {
//		this.value = value;
//	}

//	public String getStructure() {
//		return structure;
//	}

//	public void setStructure(String structure) {
//		this.structure = structure;
//	}

//	public String getTerminatingStr() {
//		return terminatingStr;
//	}

//	public void setTerminatingStr(String terminatingStr) {
//		this.terminatingStr = terminatingStr;
//	}

	

}
