package org.alfresco.extension.pdftoolkit.repo.action.executer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFEncryptionActionExecuter extends ActionExecuterAbstractBase 
{

    /**
     * The logger
     */
    private static Log logger = LogFactory
            .getLog(PDFEncryptionActionExecuter.class);

    /**
     * Action constants
     */
    public static final String NAME = "pdf-encryption";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    
    private static final String FILE_MIMETYPE = "application/pdf";

    private NodeService nodeService;
    private DictionaryService dictionaryService;
    private ContentService contentService;
    private FileFolderService fileFolderService;

    /**
     * Encryption constants
     */
	public static final String PARAM_USER_PASSWORD = "UserPassword";
	public static final String PARAM_OWNER_PASSWORD = "OwnerPassword";
	public static final String PARAM_ALLOW_PRINT = "AllowPrint";
	public static final String PARAM_ALLOW_COPY = "AllowCopy";
	public static final String PARAM_ALLOW_CONTENT_MODIFICATION = "AllowContentModification";
	public static final String PARAM_ALLOW_ANNOTATION_MODIFICATION = "AllowAnnotationModification";
	public static final String PARAM_ALLOW_FORM_FILL = "AllowFormFill";
	public static final String PARAM_ALLOW_SCREEN_READER = "AllowScreenReader";
	public static final String PARAM_ALLOW_DEGRADED_PRINT = "AllowDegradedPrint";
	public static final String PARAM_ALLOW_ASSEMBLY = "AllowAssembly";
	public static final String PARAM_ENCRYPTION_LEVEL = "EncryptionLevel";
	public static final String PARAM_EXCLUDE_METADATA = "ExcludeMetadata";
	public static final String PARAM_OPTIONS_LEVEL = "LevelOptions";
    
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
            // Encrypt the document with the requested options
            doEncrypt(ruleAction, actionedUponNodeRef,
            		actionedUponContentReader);

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
     * @see org.alfresco.repo.action.executer.TransformActionExecuter#doTransform(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.ContentReader, org.alfresco.service.cmr.repository.ContentWriter)
     */
    protected void doEncrypt(Action ruleAction, NodeRef actionedUponNodeRef,
            ContentReader actionedUponContentReader)
    {

        Map<String, Object> options = new HashMap<String, Object>(5);

        options.put(PARAM_DESTINATION_FOLDER, ruleAction
                .getParameterValue(PARAM_DESTINATION_FOLDER));
        options.put(PARAM_USER_PASSWORD, ruleAction
                .getParameterValue(PARAM_USER_PASSWORD));
        options.put(PARAM_OWNER_PASSWORD, ruleAction
                .getParameterValue(PARAM_OWNER_PASSWORD));
        options.put(PARAM_ALLOW_PRINT, ruleAction
                .getParameterValue(PARAM_ALLOW_PRINT));
        options.put(PARAM_ALLOW_COPY, ruleAction
                .getParameterValue(PARAM_ALLOW_COPY));
        options.put(PARAM_ALLOW_CONTENT_MODIFICATION, ruleAction
                .getParameterValue(PARAM_ALLOW_CONTENT_MODIFICATION));
        options.put(PARAM_ALLOW_ANNOTATION_MODIFICATION, ruleAction
                .getParameterValue(PARAM_ALLOW_ANNOTATION_MODIFICATION));
        options.put(PARAM_ALLOW_FORM_FILL, ruleAction
                .getParameterValue(PARAM_ALLOW_FORM_FILL));
        options.put(PARAM_ALLOW_SCREEN_READER, ruleAction
                .getParameterValue(PARAM_ALLOW_SCREEN_READER));
        options.put(PARAM_ALLOW_DEGRADED_PRINT, ruleAction
                .getParameterValue(PARAM_ALLOW_DEGRADED_PRINT));
        options.put(PARAM_ALLOW_ASSEMBLY, ruleAction
                .getParameterValue(PARAM_ALLOW_ASSEMBLY));
        options.put(PARAM_ENCRYPTION_LEVEL, ruleAction
                .getParameterValue(PARAM_ENCRYPTION_LEVEL));
        options.put(PARAM_EXCLUDE_METADATA, ruleAction
                .getParameterValue(PARAM_EXCLUDE_METADATA));
        options.put(PARAM_OPTIONS_LEVEL, ruleAction
                .getParameterValue(PARAM_OPTIONS_LEVEL));

        try
        {
            this.action(ruleAction, actionedUponNodeRef,
            		actionedUponContentReader, options);
        } 
        catch (AlfrescoRuntimeException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param reader
     * @param writer
     * @param options
     * @throws Exception
     */
    protected final void action(Action ruleAction, NodeRef actionedUponNodeRef,
            ContentReader actionedUponContentReader, Map<String, Object> options)
            throws AlfrescoRuntimeException
    {
    	
    	PdfStamper stamp = null;
    	File tempDir = null;
    	ContentWriter writer = null;
    	
    	try 
    	{
    		// get the parameters
    		String userPassword = (String) options.get(PARAM_USER_PASSWORD);
    		String ownerPassword = (String) options.get(PARAM_OWNER_PASSWORD);
    		int permissions = buildPermissionMask(options);
    		int encryptionType = Integer.parseInt((String)options.get(PARAM_ENCRYPTION_LEVEL));
    		
    		// if metadata is excluded, alter encryption type
    		if((Boolean)options.get(PARAM_EXCLUDE_METADATA))
    		{
    			encryptionType = encryptionType | PdfWriter.DO_NOT_ENCRYPT_METADATA;
    		}
    		
	    	// get temp file
	    	File alfTempDir = TempFileProvider.getTempDir();
	    	tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
	        tempDir.mkdir();
	        File file = new File(tempDir, fileFolderService.getFileInfo(actionedUponNodeRef).getName());
	        
	    	//get the PDF input stream and create a reader for iText
	    	PdfReader reader = new PdfReader(actionedUponContentReader.getContentInputStream());
	    	stamp = new PdfStamper(reader, new FileOutputStream(file));
	    	
	    	// encrypt PDF
	    	stamp.setEncryption(userPassword.getBytes(), ownerPassword.getBytes(), permissions, encryptionType);
	    	stamp.close();
	    	
	    	// write out to destination
	    	String filename = file.getName();
            writer = getWriter(ruleAction, filename);
            writer.setEncoding(actionedUponContentReader.getEncoding());
            writer.setMimetype(FILE_MIMETYPE);
            writer.putContent(file);
            file.delete();
    	}
    	catch(Exception e)
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
     * Build the permissions mask for iText
     * @param options
     * @return
     */
    private int buildPermissionMask(Map<String, Object> options)
    {
    	int permissions = 0;
    	
    	if((Boolean)options.get(PARAM_ALLOW_PRINT)) {
    		permissions = permissions | PdfWriter.ALLOW_PRINTING;
    	}
    	if((Boolean)options.get(PARAM_ALLOW_COPY)) {
    		permissions = permissions | PdfWriter.ALLOW_COPY;
    	}
    	if((Boolean)options.get(PARAM_ALLOW_CONTENT_MODIFICATION)) {
    		permissions = permissions | PdfWriter.ALLOW_MODIFY_CONTENTS;
    	}
    	if((Boolean)options.get(PARAM_ALLOW_ANNOTATION_MODIFICATION)) {
    		permissions = permissions | PdfWriter.ALLOW_MODIFY_ANNOTATIONS;
    	}
    	if((Boolean)options.get(PARAM_ALLOW_SCREEN_READER)) {
    		permissions = permissions | PdfWriter.ALLOW_SCREENREADERS;
    	}
    	if((Boolean)options.get(PARAM_ALLOW_DEGRADED_PRINT)) {
    		permissions = permissions | PdfWriter.ALLOW_DEGRADED_PRINTING;
    	}
    	if((Boolean)options.get(PARAM_ALLOW_ASSEMBLY)) {
    		permissions = permissions | PdfWriter.ALLOW_ASSEMBLY;
    	}
    	if((Boolean)options.get(PARAM_ALLOW_FORM_FILL)) {
    		permissions = permissions | PdfWriter.ALLOW_FILL_IN;
    	}
    	
    	return permissions;
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