package org.alfresco.extension.pdftoolkit.constraints;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.action.constraint.BaseParameterConstraint;

public class MapConstraint extends BaseParameterConstraint {

	//public static final String NAME = "pdfc-visibility";
	private HashMap<String, String> cm = new HashMap<String, String>();
	
	public MapConstraint() {
	}
	
	public void setConstraintMap(Map m) {
		cm.putAll(m);
	}
	
	public Map<String, String> getAllowableValues() {
		return cm;
	}
	
	public String getValueDisplayLabel(String value)
	{

		for (Map.Entry<String, String> entry : cm.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
		    }
		}
		return "Undefined";
	}
	
	@Override
	protected Map<String, String> getAllowableValuesImpl() {
		// TODO Auto-generated method stub
		return cm;
	}
}
