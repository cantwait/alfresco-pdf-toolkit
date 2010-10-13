package org.alfresco.extension.pdftoolkit.web.bean.actions.handlers;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFSignatureActionExecuter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.actions.handlers.BaseActionHandler;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.IWizardBean;

public class PDFSignatureActionHandler extends BaseActionHandler 
{
	protected static final HashMap<String, String> OPTIONS_VISIBLE = new HashMap<String, String>();
	
	protected static final String PROP_PRIVATE_KEY = "PrivateKey";
	protected static final String PROP_VISIBILITY = "SignatureVisibility";
	protected static final String PROP_LOCATION = "Location";
	protected static final String PROP_REASON = "Reason";
	protected static final String PROP_KEY_PASSWORD = "KeyPassword";
	protected static final String PROP_LOCATION_X = "LocationX";
	protected static final String PROP_LOCATION_Y = "LocationY";
	protected static final String PROP_WIDTH = "Width";
	protected static final String PROP_HEIGHT = "Height";
	
	protected static final String PROP_OPTIONS_VISIBLE = "VisibilityOptions";
	
	public String getJSPPath() 
	{
		return getJSPPath(PDFSignatureActionExecuter.NAME);
	}

	public void prepareForSave(Map<String, Serializable> actionProps,
			Map<String, Serializable> repoProps) 
	{	
		// add the destination space id to the action properties
		NodeRef destNodeRef = (NodeRef) actionProps.get(PROP_DESTINATION);
		repoProps.put(PDFSignatureActionExecuter.PARAM_DESTINATION_FOLDER,
				destNodeRef);

		// add private key
		NodeRef signatureFile = (NodeRef) actionProps.get(PROP_PRIVATE_KEY);
		repoProps.put(PDFSignatureActionExecuter.PARAM_PRIVATE_KEY,
				signatureFile);
		
		// add visibility, location, reason, password, location_x, location_y, height and width
		String visibility = (String)actionProps.get(PROP_VISIBILITY);
		repoProps.put(PDFSignatureActionExecuter.PARAM_VISIBILITY, visibility);
		
		String location = (String)actionProps.get(PROP_LOCATION);
		repoProps.put(PDFSignatureActionExecuter.PARAM_LOCATION, location);
		
		String reason = (String)actionProps.get(PROP_REASON);
		repoProps.put(PDFSignatureActionExecuter.PARAM_REASON, reason);
		
		String keyPassword = (String)actionProps.get(PROP_KEY_PASSWORD);
		repoProps.put(PDFSignatureActionExecuter.PARAM_KEY_PASSWORD, keyPassword);
		
		String locationX = (String)actionProps.get(PROP_LOCATION_X);
		repoProps.put(PDFSignatureActionExecuter.PARAM_LOCATION_X, locationX);
		
		String locationY = (String)actionProps.get(PROP_LOCATION_Y);
		repoProps.put(PDFSignatureActionExecuter.PARAM_LOCATION_Y, locationY);
		
		String height = (String)actionProps.get(PROP_HEIGHT);
		repoProps.put(PDFSignatureActionExecuter.PARAM_HEIGHT, height);
		
		String width = (String)actionProps.get(PROP_WIDTH);
		repoProps.put(PDFSignatureActionExecuter.PARAM_WIDTH, width);
	}

	public void prepareForEdit(Map<String, Serializable> actionProps,
			Map<String, Serializable> repoProps) 
	{		
		NodeRef destNodeRef = (NodeRef) repoProps.get(PDFSignatureActionExecuter.PARAM_DESTINATION_FOLDER);
		actionProps.put(PROP_DESTINATION, destNodeRef);

		NodeRef certificateFile = (NodeRef) repoProps.get(PDFSignatureActionExecuter.PARAM_PRIVATE_KEY);
		actionProps.put(PROP_PRIVATE_KEY, certificateFile);
		
		// add visibility, location, reason, password, location_x, location_y, height and width
		String visibility = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_VISIBILITY);
		actionProps.put(PROP_VISIBILITY, visibility);

		String location = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_LOCATION);
		actionProps.put(PROP_LOCATION, location);

		String reason = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_REASON);
		actionProps.put(PROP_REASON, reason);

		String keyPassword = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_KEY_PASSWORD);
		actionProps.put(PROP_KEY_PASSWORD, keyPassword);

		String locationX = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_LOCATION_X);
		actionProps.put(PROP_LOCATION_X, locationX);

		String locationY = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_LOCATION_Y);
		actionProps.put(PROP_LOCATION_Y, locationY);

		String height = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_HEIGHT);
		actionProps.put(PROP_HEIGHT, height);

		String width = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_WIDTH);
		actionProps.put(PROP_WIDTH, width);
	}

	public String generateSummary(FacesContext context, IWizardBean wizard,
			Map<String, Serializable> actionProps) 
	{
		NodeRef space = (NodeRef) actionProps.get(PROP_DESTINATION);
		String name = Repository.getNameForNode(Repository.getServiceRegistry(
				context).getNodeService(), space);

		return MessageFormat.format(Application.getMessage(context,
				"action_pdf_signature"), new Object[] {});
	}
	

	@Override
	public void setupUIDefaults(Map<String, Serializable> actionProps) {
		
		populateLists();
		
		//add lists
		actionProps.put(PROP_OPTIONS_VISIBLE, OPTIONS_VISIBLE);
		
		//set defaults
		actionProps.put(PROP_VISIBILITY, PDFSignatureActionExecuter.VISIBILITY_HIDDEN);
		
		super.setupUIDefaults(actionProps);
	}
	
	/**
	 * Populates lists for UI 
	 */
	private void populateLists() {
		
		OPTIONS_VISIBLE.clear();
		
		// set up visibility options
		OPTIONS_VISIBLE.put("Visible", PDFSignatureActionExecuter.VISIBILITY_VISIBLE);
		OPTIONS_VISIBLE.put("Hidden", PDFSignatureActionExecuter.VISIBILITY_HIDDEN);

	}
}

