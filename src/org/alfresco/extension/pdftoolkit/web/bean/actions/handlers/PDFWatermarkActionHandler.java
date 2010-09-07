package org.alfresco.extension.pdftoolkit.web.bean.actions.handlers;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFWatermarkActionExecuter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.actions.handlers.BaseActionHandler;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.IWizardBean;

public class PDFWatermarkActionHandler extends BaseActionHandler 

{
	protected static final HashMap<String, String> OPTIONS_PAGE = new HashMap<String, String>();
	protected static final HashMap<String, String> OPTIONS_POSITION = new HashMap<String, String>();
	protected static final HashMap<String, String> OPTIONS_DEPTH = new HashMap<String, String>();
	
	protected static final String PROP_WATERMARK_IMAGE = "WatermarkImage";
	protected static final String PROP_WATERMARK_PAGES = "WatermarkPages";
	protected static final String PROP_WATERMARK_POSITION = "WatermarkPosition";
	protected static final String PROP_WATERMARK_DEPTH = "WatermarkDepth";
	protected static final String PROP_OPTIONS_PAGE = "PageOptions";
	protected static final String PROP_OPTIONS_POSITION = "PositionOptions";
	protected static final String PROP_OPTIONS_DEPTH = "DepthOptions";

	public String getJSPPath() 
	{
		return getJSPPath(PDFWatermarkActionExecuter.NAME);
	}

	public void prepareForSave(Map<String, Serializable> actionProps,
			Map<String, Serializable> repoProps) 
	{	
		// add the destination space id to the action properties
		NodeRef destNodeRef = (NodeRef) actionProps.get(PROP_DESTINATION);
		repoProps.put(PDFWatermarkActionExecuter.PARAM_DESTINATION_FOLDER,
				destNodeRef);

		// add watermark image
		NodeRef watermarkImage = (NodeRef) actionProps.get(PROP_WATERMARK_IMAGE);
		repoProps.put(PDFWatermarkActionExecuter.PARAM_WATERMARK_IMAGE,
				watermarkImage);

		// add the watermark position, pages, depth to the action properties
		String pages = (String)actionProps.get(PROP_WATERMARK_PAGES);
		repoProps.put (PDFWatermarkActionExecuter.PARAM_WATERMARK_PAGES, pages);
		
		String position = (String)actionProps.get(PROP_WATERMARK_POSITION);
		repoProps.put (PDFWatermarkActionExecuter.PARAM_WATERMARK_POSITION, position);
		
		String depth = (String)actionProps.get(PROP_WATERMARK_DEPTH);
		repoProps.put (PDFWatermarkActionExecuter.PARAM_WATERMARK_DEPTH, depth);
	}

	public void prepareForEdit(Map<String, Serializable> actionProps,
			Map<String, Serializable> repoProps) 
	{	
		//call setupUIDefaults first to make sure maps are initialized and populated
		setupUIDefaults(actionProps);
		actionProps.put(PROP_OPTIONS_PAGE, OPTIONS_PAGE);
		actionProps.put(PROP_OPTIONS_POSITION, OPTIONS_POSITION);
		actionProps.put(PROP_OPTIONS_DEPTH, OPTIONS_DEPTH);
		
		NodeRef destNodeRef = (NodeRef) repoProps.get(PDFWatermarkActionExecuter.PARAM_DESTINATION_FOLDER);
		actionProps.put(PROP_DESTINATION, destNodeRef);

		NodeRef watermarkImage = (NodeRef) repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_IMAGE);
		actionProps.put(PROP_WATERMARK_IMAGE, watermarkImage);
		
		String pages = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_PAGES);
		actionProps.put (PROP_WATERMARK_PAGES, pages);
		
		String position = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_POSITION);
		actionProps.put (PROP_WATERMARK_POSITION, position);	
		
		String depth = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_DEPTH);
		actionProps.put (PROP_WATERMARK_DEPTH, depth);
	}

	public String generateSummary(FacesContext context, IWizardBean wizard,
			Map<String, Serializable> actionProps) 
	{
		NodeRef space = (NodeRef) actionProps.get(PROP_DESTINATION);
		String name = Repository.getNameForNode(Repository.getServiceRegistry(
				context).getNodeService(), space);

		String watermarkImage = actionProps.get(PROP_WATERMARK_IMAGE).toString();
		String watermarkPages = actionProps.get(PROP_WATERMARK_PAGES).toString();
		String watermarkPosition = actionProps.get(PROP_WATERMARK_POSITION).toString();
		String watermarkDepth = actionProps.get(PROP_WATERMARK_DEPTH).toString();
		
		return MessageFormat.format(Application.getMessage(context,
				"action_pdf_watermark"), new Object[] { watermarkImage,
				name, watermarkPages, watermarkPosition, watermarkDepth });
	}

	@Override
	public void setupUIDefaults(Map<String, Serializable> actionProps) {
		
		OPTIONS_PAGE.clear();
		OPTIONS_POSITION.clear();
		OPTIONS_DEPTH.clear();
		
		// set up page options list
		OPTIONS_PAGE.put("All", PDFWatermarkActionExecuter.PAGE_ALL);
		OPTIONS_PAGE.put("First", PDFWatermarkActionExecuter.PAGE_FIRST);
		OPTIONS_PAGE.put("Last", PDFWatermarkActionExecuter.PAGE_LAST);
		OPTIONS_PAGE.put("Odd", PDFWatermarkActionExecuter.PAGE_ODD);
		OPTIONS_PAGE.put("Even", PDFWatermarkActionExecuter.PAGE_EVEN);
		
		//set up position options list
		OPTIONS_POSITION.put("Top left", PDFWatermarkActionExecuter.POSITION_TOPLEFT);
		OPTIONS_POSITION.put("Top right", PDFWatermarkActionExecuter.POSITION_TOPRIGHT);
		OPTIONS_POSITION.put("Center", PDFWatermarkActionExecuter.POSITION_CENTER);
		OPTIONS_POSITION.put("Bottom left", PDFWatermarkActionExecuter.POSITION_BOTTOMLEFT);
		OPTIONS_POSITION.put("Bottom right", PDFWatermarkActionExecuter.POSITION_BOTTOMRIGHT);

		//set up depth options
		OPTIONS_DEPTH.put("Over", PDFWatermarkActionExecuter.DEPTH_OVER);
		OPTIONS_DEPTH.put("Under", PDFWatermarkActionExecuter.DEPTH_UNDER);
		
		//add lists
		actionProps.put(PROP_OPTIONS_PAGE, OPTIONS_PAGE);
		actionProps.put(PROP_OPTIONS_POSITION, OPTIONS_POSITION);
		actionProps.put(PROP_OPTIONS_DEPTH, OPTIONS_DEPTH);
		
		super.setupUIDefaults(actionProps);
	}
	
	
}
