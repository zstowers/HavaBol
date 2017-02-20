package havabol;

public class ResultValue {

	private String type; // usually data type of the result
	private String value; // value of the result
	private String structure; // primitive, fixed array, unbounded array
	private String terminatingStr; // used for end of lists of things (e.g., a list of
							// statements might be
							// terminated by "endwhile")
	
	public ResultValue() {
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	public String getTerminatingStr() {
		return terminatingStr;
	}

	public void setTerminatingStr(String terminatingStr) {
		this.terminatingStr = terminatingStr;
	}

	

}
