package org.alfresco.extension.pdftoolkit.repo.action.executer;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PDFWatermarkActionExecuter extends ActionExecuterAbstractBase 

{

    /**
     * The logger
     */
    private static Log logger = LogFactory
            .getLog(PDFAppendActionExecuter.class);

    /**
     * Action constants
     */
    public static final String NAME = "pdf-watermark";
    public static final String PARAM_WATERMARK_IMAGE="watermark-image";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_WATERMARK_PAGES = "watermark-pages";
    public static final String PARAM_WATERMARK_POSITION = "watermark-position";
    public static final String PARAM_WATERMARK_DEPTH = "watermark-depth";
    public static final String PARAM_WATERMARK_TYPE = "watermark-type";
    public static final String PARAM_WATERMARK_TEXT = "watermark-text";
    public static final String PARAM_WATERMARK_FONT = "watermark-font";
    public static final String PARAM_WATERMARK_SIZE = "watermark-size";
    
    private static final String FILE_MIMETYPE = "application/pdf";

    private NodeService nodeService;
    private DictionaryService dictionaryService;
    private ContentService contentService;
    private FileFolderService fileFolderService;

    /**
     * Position and page constants
     */
    public static final String PAGE_ALL = "all";
    public static final String PAGE_ODD = "odd";
    public static final String PAGE_EVEN = "even";
    public static final String PAGE_FIRST = "first";
    public static final String PAGE_LAST = "last";
    
    public static final String POSITION_CENTER = "center";
    public static final String POSITION_TOPLEFT = "topleft";
    public static final String POSITION_TOPRIGHT = "topright";
    public static final String POSITION_BOTTOMLEFT = "bottomleft";
    public static final String POSITION_BOTTOMRIGHT = "bottomright";
    
    public static final String DEPTH_UNDER = "under";
    public static final String DEPTH_OVER = "over";
    
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_TEXT = "text";
    
    public static final String TEXT_OPTION_DATE = "<date>";
    public static final String TEXT_OPTION_TITLE = "<title>";
    public static final String TEXT_OPTION_NAME = "<name>";
    public static final String TEXT_OPTION_MODIFIED = "<modified>";
    public static final String TEXT_OPTION_CREATED = "<created>";
    public static final String TEXT_OPTION_OWNER = "<owner>";
    public static final String TEXT_OPTION_USER = "<user>";
    public static final String TEXT_OPTION_MODIFIER = "<modifier>";
    
    public static final String FONT_OPTION_HELVETICA = BaseFont.HELVETICA;
    public static final String FONT_OPTION_COURIER = BaseFont.COURIER; 
    public static final String FONT_OPTION_TIMES_ROMAN = BaseFont.TIMES_ROMAN;
    
    private static final float pad = 15;
    
    /**
     * Set the node service
     * 
     * @param nodeService
     *            set the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set the dictionary service
     * 
     * @param dictionaryService
     *            the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Set the content service
     * 
     * @param contentService
     *            the content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

    /**
     * Sets the FileFolderService to use
     * 
     * @param fileFolderService
     *            The FileFolderService
     */
    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER,
                DataTypeDefinition.NODE_REF, true,
                getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_IMAGE,
                DataTypeDefinition.NODE_REF, false,
                getParamDisplayLabel(PARAM_WATERMARK_IMAGE)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_PAGES,
                DataTypeDefinition.TEXT, true,
                getParamDisplayLabel(PARAM_WATERMARK_PAGES)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_POSITION,
                DataTypeDefinition.TEXT, true,
                getParamDisplayLabel(PARAM_WATERMARK_POSITION)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_DEPTH,
                DataTypeDefinition.TEXT, true,
                getParamDisplayLabel(PARAM_WATERMARK_DEPTH)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_TEXT,
                DataTypeDefinition.TEXT, false,
                getParamDisplayLabel(PARAM_WATERMARK_TEXT)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_FONT,
                DataTypeDefinition.TEXT, false,
                getParamDisplayLabel(PARAM_WATERMARK_FONT)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_SIZE,
                DataTypeDefinition.TEXT, false,
                getParamDisplayLabel(PARAM_WATERMARK_SIZE)));
    }

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.repository.NodeRef,
     *      org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action ruleAction, NodeRef actionedUponNodeRef)
    {
        if (this.nodeService.exists(actionedUponNodeRef) == false)
        {
            // node doesn't exist - can't do anything
            return;
        }
        
        ContentReader actionedUponContentReader = getReader(actionedUponNodeRef);
        
        if (actionedUponContentReader != null)
        {
            // Add the watermark to the image
            doWatermark(ruleAction, actionedUponNodeRef, actionedUponContentReader);

            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Can't execute rule: \n" + "   node: "
                            + actionedUponNodeRef + "\n" + "   reader: "
                            + actionedUponContentReader + "\n" + "   action: " + this);
                }
            }
        }
    }

    /**
     * @see org.alfresco.repo.action.executer.TransformActionExecuter#doWatermark(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.ContentReader, org.alfresco.service.cmr.repository.ContentWriter)
     */
    protected void doWatermark(Action ruleAction, NodeRef actionedUponNodeRef,
            ContentReader actionedUponContentReader)
    {

        Map<String, Object> options = new HashMap<String, Object>(5);

        options.put(PARAM_DESTINATION_FOLDER, ruleAction
                .getParameterValue(PARAM_DESTINATION_FOLDER));
        options.put(PARAM_WATERMARK_PAGES, ruleAction.
        		getParameterValue(PARAM_WATERMARK_PAGES));
        options.put(PARAM_WATERMARK_POSITION, ruleAction.
        		getParameterValue(PARAM_WATERMARK_POSITION));
        options.put(PARAM_WATERMARK_DEPTH, ruleAction.
        		getParameterValue(PARAM_WATERMARK_DEPTH));
        
        try
        {
        	if(ruleAction.getParameterValue(PARAM_WATERMARK_TYPE) != null &&
        			ruleAction.getParameterValue(PARAM_WATERMARK_TYPE).equals(TYPE_IMAGE)) {
        		
        		NodeRef watermarkNodeRef = (NodeRef) ruleAction.getParameterValue(PARAM_WATERMARK_IMAGE);
        		ContentReader watermarkContentReader = getReader(watermarkNodeRef);
        		
        		//add additional options only used by this specific watermark type
        		options.put(PARAM_WATERMARK_IMAGE, ruleAction
                        .getParameterValue(PARAM_WATERMARK_IMAGE));
        		
	            this.imageAction(ruleAction, actionedUponNodeRef, watermarkNodeRef,
	            		actionedUponContentReader, watermarkContentReader, options);
	            
        	} else if(ruleAction.getParameterValue(PARAM_WATERMARK_TYPE) != null &&
        			ruleAction.getParameterValue(PARAM_WATERMARK_TYPE).equals(TYPE_TEXT)) {
        		
        		// add additional options only used by text types
        		options.put(PARAM_WATERMARK_TEXT, ruleAction
                        .getParameterValue(PARAM_WATERMARK_TEXT));
        		
        		options.put(PARAM_WATERMARK_FONT, ruleAction
                        .getParameterValue(PARAM_WATERMARK_FONT));
        		
        		options.put(PARAM_WATERMARK_SIZE, ruleAction
                        .getParameterValue(PARAM_WATERMARK_SIZE));
        		
        		this.textAction(ruleAction, actionedUponNodeRef,
	            		actionedUponContentReader, options);
        	}
        } 
        catch (AlfrescoRuntimeException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Applies an image watermark
     * 
     * @param reader
     * @param writer
     * @param options
     * @throws Exception
     */
    private final void imageAction(Action ruleAction, NodeRef actionedUponNodeRef,
            NodeRef watermarkNodeRef, ContentReader actionedUponContentReader,
            ContentReader watermarkContentReader, Map<String, Object> options)
            throws AlfrescoRuntimeException
    {

    	PdfStamper stamp = null;
    	File tempDir = null;
    	ContentWriter writer = null;
    	
        try
        {        	
        	//get a temp file to stash the watermarked PDF in before moving to repo
        	File alfTempDir = TempFileProvider.getTempDir();
        	tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
            tempDir.mkdir();
            File file = new File(tempDir, fileFolderService.getFileInfo(actionedUponNodeRef).getName());
            
        	//get the PDF input stream and create a reader for iText
        	PdfReader reader = new PdfReader(actionedUponContentReader.getContentInputStream());
        	stamp = new PdfStamper(reader, new FileOutputStream(file));
        	PdfContentByte pcb;
        	
        	//get a com.itextpdf.text.Image object via java.imageio.ImageIO
        	Image img = Image.getInstance(ImageIO.read(watermarkContentReader.getContentInputStream()), null);
        	
        	//get the PDF pages and position
        	String pages = (String) options.get(PARAM_WATERMARK_PAGES);
        	String position = (String) options.get(PARAM_WATERMARK_POSITION);
        	String depth = (String) options.get(PARAM_WATERMARK_DEPTH);
        	
        	// image requires absolute positioning or an exception will be thrown
        	// set image position according to parameter.  Use PdfReader.getPageSizeWithRotation
        	// to get the canvas size for alignment.
        	img.setAbsolutePosition(100f,100f);
        	
        	//stamp each page
        	int numpages = reader.getNumberOfPages();
        	for(int i = 1; i <= numpages; i++) 
        	{
        		Rectangle r = reader.getPageSizeWithRotation(i);
        		//set stamp position
            	if(position.equals(POSITION_BOTTOMLEFT))
            	{
            		img.setAbsolutePosition(0, 0);
            	}
            	else if(position.equals(POSITION_BOTTOMRIGHT))
            	{
            		img.setAbsolutePosition(r.getWidth() - img.getWidth(), 0);
            	}
            	else if(position.equals(POSITION_TOPLEFT))
            	{
            		img.setAbsolutePosition(0, r.getHeight() - img.getHeight());
            	}
            	else if(position.equals(POSITION_TOPRIGHT))
            	{
            		img.setAbsolutePosition(r.getWidth() - img.getWidth(), r.getHeight() - img.getHeight());
            	}
            	else if(position.equals(POSITION_CENTER))
            	{
            		img.setAbsolutePosition(getCenterX(r, img), getCenterY(r, img));
            	}
            	
            	// if this is an under-text stamp, use getUnderContent.
            	// if this is an over-text stamp, usse getOverContent.
            	if(depth.equals(DEPTH_OVER))
            	{
            		pcb = stamp.getOverContent(i);
            	}
            	else 
            	{
            		pcb = stamp.getUnderContent(i);
            	}
            	
        		// only apply stamp to requested pages
            	if(checkPage(pages, i, numpages)) {
            		pcb.addImage(img);
            	}
        	}
        	
        	stamp.close();
        	
        	String filename = file.getName();
        	
            // Get a writer and prep it for putting it back into the repo
            writer = getWriter(ruleAction, filename);
            writer.setEncoding(actionedUponContentReader.getEncoding());
            writer.setMimetype(FILE_MIMETYPE);

            // Put it in the repo
            writer.putContent(file);
           
            //delete the temp file
            file.delete();
        }
        catch (Exception e)
        {
        	if(stamp != null) {try {stamp.close();} catch(Exception ex){}}
            e.printStackTrace();
        }
        finally 
        {
        	if(tempDir != null) {try {tempDir.delete();} catch(Exception ex){}}
        }
    }

    /**
     * Applies a text watermark (current date, user name, etc, depending on options)
     * 
     * @param reader
     * @param writer
     * @param options
     * @throws Exception
     */
    private final void textAction(Action ruleAction, NodeRef actionedUponNodeRef,
            ContentReader actionedUponContentReader, Map<String, Object> options)
            throws AlfrescoRuntimeException
    {

    	PdfStamper stamp = null;
    	File tempDir = null;
    	ContentWriter writer = null;
    	String watermarkText;
    	StringTokenizer st;
    	Vector<String> tokens = new Vector();
    	
        try
        {        	
        	//get a temp file to stash the watermarked PDF in before moving to repo
        	File alfTempDir = TempFileProvider.getTempDir();
        	tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
            tempDir.mkdir();
            File file = new File(tempDir, fileFolderService.getFileInfo(actionedUponNodeRef).getName());
            
        	//get the PDF input stream and create a reader for iText
        	PdfReader reader = new PdfReader(actionedUponContentReader.getContentInputStream());
        	stamp = new PdfStamper(reader, new FileOutputStream(file));
        	PdfContentByte pcb;
        	
        	//get the PDF pages and position
        	String pages = (String) options.get(PARAM_WATERMARK_PAGES);
        	String position = (String) options.get(PARAM_WATERMARK_POSITION);
        	String depth = (String) options.get(PARAM_WATERMARK_DEPTH);
       
        	//create the base font for the text stamp
        	BaseFont bf = BaseFont.createFont((String)options.get(PARAM_WATERMARK_FONT), 
        			BaseFont.CP1250, BaseFont.EMBEDDED);
        	        
        	//get watermark text and do substitution
        	watermarkText = (String)options.get(PARAM_WATERMARK_TEXT);
        	watermarkText = subText(watermarkText, actionedUponNodeRef);
        	
        	//tokenize watermark text to support multiple lines and copy tokens
        	//to vector for re-use
        	st = new StringTokenizer(watermarkText, "\r\n", false);
        	while(st.hasMoreTokens()) tokens.add(st.nextToken());
        	
        	//stamp each page
        	int numpages = reader.getNumberOfPages();
        	for(int i = 1; i <= numpages; i++) 
        	{
        		Rectangle r = reader.getPageSizeWithRotation(i);
            	
            	// if this is an under-text stamp, use getUnderContent.
            	// if this is an over-text stamp, usse getOverContent.
            	if(depth.equals(DEPTH_OVER))
            	{
            		pcb = stamp.getOverContent(i);
            	}
            	else 
            	{
            		pcb = stamp.getUnderContent(i);
            	}
            	
            	//set the font and size
            	float size = Float.parseFloat((String)options.get(PARAM_WATERMARK_SIZE));
            	pcb.setFontAndSize(bf, size);
            	
            	// only apply stamp to requested pages
            	if(checkPage(pages, i, numpages)) {
            		writeAlignedText(pcb, r, tokens, size, position);
            	}
        	}
        	
        	stamp.close();
        	
        	String filename = file.getName();
        	
            // Get a writer and prep it for putting it back into the repo
            writer = getWriter(ruleAction, filename);
            writer.setEncoding(actionedUponContentReader.getEncoding());
            writer.setMimetype(FILE_MIMETYPE);

            // Put it in the repo
            writer.putContent(file);
           
            //delete the temp file
            file.delete();
        }
        catch (Exception e)
        {
        	if(stamp != null) {try {stamp.close();} catch(Exception ex){}}
            e.printStackTrace();
        }
        finally 
        {
        	if(tempDir != null) {try {tempDir.delete();} catch(Exception ex){}}
        }
    }
    
    /**
     * Writes text watermark to one of the 5 preconfigured locations
     * @param pcb
     * @param r
     * @param tokens
     * @param size
     * @param position
     */
    private void writeAlignedText(PdfContentByte pcb, Rectangle r, Vector<String> tokens, float size, String position) 
    {
    	//get the dimensions of our 'rectangle' for text
    	float height = size * tokens.size();
    	float width = 0;
    	float centerX = 0, startY = 0;
    	for(int i = 0; i < tokens.size(); i++)
    	{
    		if(pcb.getEffectiveStringWidth(tokens.get(i).toString(), false) > width) 
    			width = pcb.getEffectiveStringWidth(tokens.get(i).toString(), false);
    	}
    	
    	//now that we have the width and height, we can calculate the center position for
    	//the rectangle that will contain our text.
    	if(position.equals(POSITION_BOTTOMLEFT))
    	{
    		centerX = width / 2 + pad;
    		startY = 0 + pad;
    	}
    	else if(position.equals(POSITION_BOTTOMRIGHT))
    	{
    		centerX = r.getWidth() - (width / 2) - pad;
    		startY = 0 + pad;
    	}
    	else if(position.equals(POSITION_TOPLEFT))
    	{
    		centerX = width / 2 + pad;
    		startY = r.getHeight() - height - pad;
    	}
    	else if(position.equals(POSITION_TOPRIGHT))
    	{
    		centerX = r.getWidth() - (width / 2) - pad;
    		startY = r.getHeight() - height - pad;
    	}
    	else if(position.equals(POSITION_CENTER))
    	{
    		centerX = r.getWidth() / 2;
    		startY = (r.getHeight() / 2) - (height / 2);
    	}
    	
    	//apply text to PDF
		pcb.beginText();
		
		for(int t = 0; t < tokens.size(); t++)
		{
			pcb.showTextAligned(PdfContentByte.ALIGN_CENTER, tokens.get(t).toString(),
					centerX, startY + (size * t), 0);
		}
		            		
		pcb.endText();
    	
    }
    
    /**
     * Takes the input text and substitutes tags for their values.  Used to embed dates,
     * names and other metadata into the watermark.  Right now this is just a bunch of text
     * substitutions.  I would like to fix this up to let the user use a Freemarker template
     * instead.  Maybe for the next version.
     * @param text
     * @return
     */
    private String subText(String text, NodeRef ref) {
    	
    	//use ISO standard date format for now.  Might support more later if requested
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	
    	//modified date
    	if(fileFolderService.getFileInfo(ref).getModifiedDate() != null)
    	{
    		text = text.replace(TEXT_OPTION_MODIFIED, sdf.format(fileFolderService.getFileInfo(ref).getModifiedDate()));
    	}
    	
    	//created date
    	if(fileFolderService.getFileInfo(ref).getCreatedDate() != null)
    	{
    		text = text.replace(TEXT_OPTION_CREATED, sdf.format(fileFolderService.getFileInfo(ref).getCreatedDate()));
    	}
    	
    	//file name
    	if(fileFolderService.getFileInfo(ref).getName() != null)
    	{
    		text = text.replace(TEXT_OPTION_NAME, fileFolderService.getFileInfo(ref).getName());
    	}
    	
    	//some stuff is only in the properties
    	Map fp = fileFolderService.getFileInfo(ref).getProperties();
    	Iterator<QName> it = fp.keySet().iterator();
    	while(it.hasNext()) {
    		QName name = it.next();
        	if(name != null && fp.get(name) != null && name.getLocalName().equals("title")) 
        	{
        		text = text.replace(TEXT_OPTION_TITLE, fp.get(name).toString());
        	}
        	if(name != null && fp.get(name) != null  && name.getLocalName().equals("modifier")) 
        	{
        		text = text.replace(TEXT_OPTION_MODIFIER, fp.get(name).toString());
        	}
    	}
    	
    	//today's date
    	text = text.replace(TEXT_OPTION_DATE, sdf.format(new java.util.Date()));
    	
    	return text;
    }
    
    /**
     * Determines whether or not a watermark should be applied to a given page
     * @param pages
     * @param current
     * @param numpages
     * @return
     */
	private boolean checkPage(String pages, int current, int numpages)
	{
		
		boolean markPage = false;
		
		if(pages.equals(PAGE_EVEN) || pages.equals(PAGE_ODD)) 
		{
			if(current % 2 == 0) 
			{
				markPage = true;
			}
		} 
		else if (pages.equals(PAGE_ODD))
		{
			if(current % 2 != 0) 
			{
				markPage = true;
			}
		}
		else if (pages.equals(PAGE_FIRST)) 
		{
			if(current == 1) 
			{
				markPage = true;
			}
		} 
		else if (pages.equals(PAGE_LAST)) 
		{
			if(current == numpages) 
			{
				markPage = true;;
			}
		} 
		else 
		{
			markPage = true;
		}
		
		return markPage;
	}

    /**
     * Gets the X value for centering the watermark image
     * @param r
     * @param img
     * @return
     */
    private float getCenterX(Rectangle r, Image img)
    {
    	float x = 0;
    	float pdfwidth = r.getWidth();
    	float imgwidth = img.getWidth();
    	
    	x = (pdfwidth - imgwidth) / 2;
    	
    	return x;
    }
    
    /**
     * Gets the Y value for centering the watermark image
     * @param r
     * @param img
     * @return
     */
    private float getCenterY(Rectangle r, Image img)
    {
    	float y = 0;
    	float pdfheight = r.getHeight();
    	float imgheight = img.getHeight();
    	
    	y = (pdfheight - imgheight) / 2;
    	
    	return y;
    }
    
    /**
     * @param actionedUponNodeRef
     * @return
     */
    protected ContentReader getReader(NodeRef nodeRef)
    {
        // First check that the node is a sub-type of content
        QName typeQName = this.nodeService.getType(nodeRef);
        if (this.dictionaryService.isSubClass(typeQName,
                ContentModel.TYPE_CONTENT) == false)
        {
            // it is not content, so can't transform
            return null;
        }

        // Get the content reader
        ContentReader contentReader = this.contentService.getReader(
                nodeRef, ContentModel.PROP_CONTENT);

        return contentReader;
    }

    /**
     * @param ruleAction
     * @param filename
     * @return
     */
    protected ContentWriter getWriter(Action ruleAction, String filename)
    {
        // Get the details of the copy destination
        NodeRef destinationParent = (NodeRef) ruleAction
                .getParameterValue(PARAM_DESTINATION_FOLDER);

        FileInfo fileInfo = this.fileFolderService.create(destinationParent,
                filename, ContentModel.TYPE_CONTENT);

        // get the writer and set it up
        ContentWriter contentWriter = this.contentService.getWriter(fileInfo
                .getNodeRef(), ContentModel.PROP_CONTENT, true);

        return contentWriter;
    }
}
