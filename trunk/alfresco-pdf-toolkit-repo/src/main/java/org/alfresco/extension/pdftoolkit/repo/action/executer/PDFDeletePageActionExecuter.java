package org.alfresco.extension.pdftoolkit.repo.action.executer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

public class PDFDeletePageActionExecuter extends BasePDFActionExecuter {

    /**
     * The logger
     */
    private static Log         logger                   	= LogFactory.getLog(PDFDeletePageActionExecuter.class);

    /**
     * Action constants
     */
    public static final String NAME                     	= "pdf-delete-page";
    public static final String PARAM_DESTINATION_FOLDER 	= "destination-folder";
    public static final String PARAM_DELETE_PAGES 	    	= "delete-pages";
    public static final String PARAM_DESTINATION_NAME 	    = "destination-name";

    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, true, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PARAM_DELETE_PAGES, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_DELETE_PAGES)));
        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_NAME, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_DESTINATION_NAME)));
    }
    
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		
        if (serviceRegistry.getNodeService().exists(actionedUponNodeRef) == false)
        {
            // node doesn't exist - can't do anything
            return;
        }

        ContentReader contentReader = getReader(actionedUponNodeRef);

        if (contentReader != null)
        {
            // Do the work....split the PDF
            doDelete(action, actionedUponNodeRef, contentReader);

            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Can't execute rule: \n" + "   node: " + actionedUponNodeRef + "\n" + "   reader: "
                                 + contentReader + "\n" + "   action: " + this);
                }
            }
        }
	}

	/**
	 * Delete the requested pages from the PDF doc and save it to the destination location.
	 * 
	 * @param action
	 * @param actionedUponNodeRef
	 * @param reader
	 */
	private void doDelete(Action action, NodeRef actionedUponNodeRef, ContentReader reader)
	{
		InputStream is = null;
        File tempDir = null;
        ContentWriter writer = null;
        PdfReader pdfReader = null;

        try
        {
            is = reader.getContentInputStream();

            File alfTempDir = TempFileProvider.getTempDir();
            tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
            tempDir.mkdir();
            
            String fileName = action.getParameterValue(PARAM_DESTINATION_NAME).toString();
            File file = new File(tempDir, serviceRegistry.getFileFolderService().getFileInfo(actionedUponNodeRef).getName());

            pdfReader = new PdfReader(is);
            Document doc = new Document(pdfReader.getPageSizeWithRotation(1));
            PdfCopy copy = new PdfCopy(doc, new FileOutputStream(file));
            doc.open();

            List<Integer> toDelete = parseDeleteList(action.getParameterValue(PARAM_DELETE_PAGES).toString());
            
            for (int pageNum = 1; pageNum <= pdfReader.getNumberOfPages(); pageNum++) 
            {
            	if (!toDelete.contains(pageNum)) {
            		copy.addPage(copy.getImportedPage(pdfReader, pageNum));
            	}
            }
            doc.close();

            NodeRef destinationNode = createDestinationNode(fileName, 
            		(NodeRef)action.getParameterValue(PARAM_DESTINATION_FOLDER), actionedUponNodeRef);
            writer = serviceRegistry.getContentService().getWriter(destinationNode, ContentModel.PROP_CONTENT, true);

            writer.setEncoding(reader.getEncoding());
            writer.setMimetype(FILE_MIMETYPE);

            // Put it in the repository
            writer.putContent(file);

            // Clean up
            file.delete();

        }
        catch (IOException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        } 
        catch (DocumentException e) 
        {
			throw new AlfrescoRuntimeException(e.getMessage(), e);
		}
        catch (Exception e)
        {
        	throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
        finally
        {
            if (pdfReader != null)
            {
            	pdfReader.close();
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    throw new AlfrescoRuntimeException(e.getMessage(), e);
                }
            }

            if (tempDir != null)
            {
                tempDir.delete();
            }
        }
	}
	
	/**
	 * Parses the list of pages or page ranges to delete and returns a list of page numbers 
	 * 
	 * @param list
	 * @return
	 */
	private List<Integer> parseDeleteList(String list)
	{
		List<Integer> toDelete = new ArrayList<Integer>();
		String[] tokens = list.split(",");
		for(String token : tokens)
		{
			//parse each, if one is not an int, log it but keep going
			try 
			{
				toDelete.add(Integer.parseInt(token));
			}
			catch(NumberFormatException nfe)
			{
				logger.warn("Delete list contains non-numeric values");
			}
		}
		return toDelete;
	}
}
