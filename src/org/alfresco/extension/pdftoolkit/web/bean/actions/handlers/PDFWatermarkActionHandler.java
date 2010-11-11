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

public class PDFWatermarkActionHandler extends BasePDFStampActionHandler 

{
	protected static final HashMap<String, String> OPTIONS_DEPTH = new HashMap<String, String>();
	protected static final HashMap<String, String> OPTIONS_TYPE = new HashMap<String, String>();
	protected static final HashMap<String, String> OPTIONS_FONT = new HashMap<String, String>();
	protected static final HashMap<String, String> OPTIONS_DATA = new HashMap<String, String>();
	protected static final HashMap<String, String> OPTIONS_SIZE = new HashMap<String, String>();
	
	protected static final String PROP_WATERMARK_IMAGE = "WatermarkImage";
	protected static final String PROP_WATERMARK_PAGES = "WatermarkPages";

	protected static final String PROP_WATERMARK_DEPTH = "WatermarkDepth";
	protected static final String PROP_WATERMARK_TYPE = "WatermarkType";
	protected static final String PROP_WATERMARK_FONT = "WatermarkFont";
	protected static final String PROP_WATERMARK_TEXT = "WatermarkText";
	protected static final String PROP_WATERMARK_SIZE = "WatermarkSize";
	
	protected static final String PROP_OPTIONS_DEPTH = "DepthOptions";
	protected static final String PROP_OPTIONS_TYPE = "TypeOptions";
	protected static final String PROP_OPTIONS_FONT = "FontOptions";
	protected static final String PROP_OPTIONS_SIZE = "SizeOptions";
	
	public String getJSPPath() 
	{
		return getJSPPath(PDFWatermarkActionExecuter.NAME);
	}

	public void prepareForSave(Map<String, Serializable> actionProps,
			Map<String, Serializable> repoProps) 
	{	
		super.prepareForSave(actionProps, repoProps);
		
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
		
		String depth = (String)actionProps.get(PROP_WATERMARK_DEPTH);
		repoProps.put (PDFWatermarkActionExecuter.PARAM_WATERMARK_DEPTH, depth);
		
		String type = (String)actionProps.get(PROP_WATERMARK_TYPE);
		repoProps.put (PDFWatermarkActionExecuter.PARAM_WATERMARK_TYPE, type);
		
		String text = (String)actionProps.get(PROP_WATERMARK_TEXT);
		repoProps.put (PDFWatermarkActionExecuter.PARAM_WATERMARK_TEXT, text);
		
		String font = (String)actionProps.get(PROP_WATERMARK_FONT);
		repoProps.put (PDFWatermarkActionExecuter.PARAM_WATERMARK_FONT, font);
		
		String size = (String)actionProps.get(PROP_WATERMARK_SIZE);
		repoProps.put (PDFWatermarkActionExecuter.PARAM_WATERMARK_SIZE, size);
	}

	public void prepareForEdit(Map<String, Serializable> actionProps,
			Map<String, Serializable> repoProps) 
	{	
		//call setupUIDefaults first to make sure maps are initialized and populated
		populateLists();
		
		super.prepareForEdit(actionProps, repoProps);
		
		actionProps.put(PROP_OPTIONS_PAGE, OPTIONS_PAGE);
		actionProps.put(PROP_OPTIONS_POSITION, OPTIONS_POSITION);
		actionProps.put(PROP_OPTIONS_DEPTH, OPTIONS_DEPTH);
		actionProps.put(PROP_OPTIONS_TYPE, OPTIONS_TYPE);
		actionProps.put(PROP_OPTIONS_FONT, OPTIONS_FONT);
		actionProps.put(PROP_OPTIONS_SIZE, OPTIONS_SIZE);
		
		NodeRef destNodeRef = (NodeRef) repoProps.get(PDFWatermarkActionExecuter.PARAM_DESTINATION_FOLDER);
		actionProps.put(PROP_DESTINATION, destNodeRef);

		NodeRef watermarkImage = (NodeRef) repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_IMAGE);
		actionProps.put(PROP_WATERMARK_IMAGE, watermarkImage);
		
		String pages = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_PAGES);
		actionProps.put (PROP_WATERMARK_PAGES, pages);	
		
		String depth = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_DEPTH);
		actionProps.put (PROP_WATERMARK_DEPTH, depth);
		
		String type = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_TYPE);
		actionProps.put (PROP_WATERMARK_TYPE, type);
		
		String text = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_TEXT);
		actionProps.put (PROP_WATERMARK_TEXT, text);
		
		String font = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_FONT);
		actionProps.put (PROP_WATERMARK_FONT, font);
		
		String size = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_SIZE);
		actionProps.put (PROP_WATERMARK_SIZE, size);
	}

	public String generateSummary(FacesContext context, IWizardBean wizard,
			Map<String, Serializable> actionProps) 
	{
		NodeRef space = (NodeRef) actionProps.get(PROP_DESTINATION);
		String name = Repository.getNameForNode(Repository.getServiceRegistry(
				context).getNodeService(), space);

		String watermarkPages = actionProps.get(PROP_WATERMARK_PAGES).toString();
		String watermarkDepth = actionProps.get(PROP_WATERMARK_DEPTH).toString();
		
		return MessageFormat.format(Application.getMessage(context,
				"action_pdf_watermark"), new Object[] {name, watermarkPages, 
				watermarkDepth });
	}

	@Override
	public void setupUIDefaults(Map<String, Serializable> actionProps) {
		
		populateLists();
		
		//add lists
		actionProps.put(PROP_OPTIONS_PAGE, OPTIONS_PAGE);
		actionProps.put(PROP_OPTIONS_POSITION, OPTIONS_POSITION);
		actionProps.put(PROP_OPTIONS_DEPTH, OPTIONS_DEPTH);
		actionProps.put(PROP_OPTIONS_TYPE, OPTIONS_TYPE);
		actionProps.put(PROP_OPTIONS_FONT, OPTIONS_FONT);
		actionProps.put(PROP_OPTIONS_SIZE, OPTIONS_SIZE);
		
		//set defaults
		actionProps.put(PROP_WATERMARK_TYPE, PDFWatermarkActionExecuter.TYPE_IMAGE);
		actionProps.put(PROP_WATERMARK_DEPTH, PDFWatermarkActionExecuter.DEPTH_OVER);
		actionProps.put(PROP_POSITION, PDFWatermarkActionExecuter.POSITION_CENTER);
		actionProps.put(PROP_WATERMARK_PAGES, PDFWatermarkActionExecuter.PAGE_ALL);
		actionProps.put(PROP_WATERMARK_SIZE, "34");
		actionProps.put(PROP_WATERMARK_FONT, PDFWatermarkActionExecuter.FONT_OPTION_COURIER);
		
		super.setupUIDefaults(actionProps);
	}
	
	/**
	 * Populates lists for UI 
	 */
	protected void populateLists() {
		
		super.populateLists();
		
		OPTIONS_DEPTH.clear();
		OPTIONS_TYPE.clear();
		OPTIONS_FONT.clear();
		OPTIONS_SIZE.clear();

		//set up depth options
		OPTIONS_DEPTH.put("Over", PDFWatermarkActionExecuter.DEPTH_OVER);
		OPTIONS_DEPTH.put("Under", PDFWatermarkActionExecuter.DEPTH_UNDER);
		
		//set up type options (text and image for now)
		OPTIONS_TYPE.put("Image", PDFWatermarkActionExecuter.TYPE_IMAGE);
		OPTIONS_TYPE.put("Text", PDFWatermarkActionExecuter.TYPE_TEXT);
		
		//set up font options
		OPTIONS_FONT.put("Helvetica", PDFWatermarkActionExecuter.FONT_OPTION_HELVETICA);
		OPTIONS_FONT.put("Times Roman", PDFWatermarkActionExecuter.FONT_OPTION_TIMES_ROMAN);
		OPTIONS_FONT.put("Courier", PDFWatermarkActionExecuter.FONT_OPTION_COURIER);
		
		//set up size options
		OPTIONS_SIZE.put("12", "12");
		OPTIONS_SIZE.put("18", "18");
		OPTIONS_SIZE.put("22", "22");
		OPTIONS_SIZE.put("28", "28");
		OPTIONS_SIZE.put("34", "34");
		OPTIONS_SIZE.put("40", "40");
		OPTIONS_SIZE.put("48", "48");
		OPTIONS_SIZE.put("72", "72");
	}
}
