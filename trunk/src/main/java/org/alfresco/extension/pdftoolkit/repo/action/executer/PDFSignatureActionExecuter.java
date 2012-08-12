/*
 * Copyright 2008-2012 Alfresco Software Limited.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * This file is part of an unsupported extension to Alfresco.
 */

package org.alfresco.extension.pdftoolkit.repo.action.executer;


import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.extension.pdftoolkit.constraints.MapConstraint;
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

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;


public class PDFSignatureActionExecuter
    extends BasePDFStampActionExecuter

{

    /**
     * The logger
     */
    private static Log                    logger                   = LogFactory.getLog(PDFSignatureActionExecuter.class);

    /**
     * Constraints
     */
    public static HashMap<String, String> visibilityConstraint     = new HashMap<String, String>();
    public static HashMap<String, String> keyTypeConstraint        = new HashMap<String, String>();

    /**
     * Action constants
     */
    public static final String            NAME                     = "pdf-signature";
    public static final String            PARAM_PRIVATE_KEY        = "private-key";
    public static final String            PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String            PARAM_VISIBILITY         = "visibility";
    /*
     * don't confuse the location field below with the position field inherited
     * from parent. "location, in the context of a PDF signature, means the
     * location where it was signed, not the location of the signature block,
     * which is handled by position
     */
    public static final String            PARAM_LOCATION           = "location";
    public static final String            PARAM_REASON             = "reason";
    public static final String            PARAM_KEY_PASSWORD       = "key-password";
    public static final String            PARAM_WIDTH              = "width";
    public static final String            PARAM_HEIGHT             = "height";
    public static final String            PARAM_KEY_TYPE           = "key-type";

    // Aditional parameters to accessing the keystore file
    public static final String            PARAM_ALIAS              = "alias";
    public static final String            PARAM_STORE_PASSWORD     = "store-password";

    public static final String            VISIBILITY_HIDDEN        = "hidden";
    public static final String            VISIBILITY_VISIBLE       = "visible";

    public static final String            KEY_TYPE_PKCS12          = "pkcs12";
    public static final String            KEY_TYPE_DEFAULT         = "default";


    /**
     * Constraint beans
     * 
     * @param mc
     */
    public void setKeyTypeConstraint(MapConstraint mc)
    {
        keyTypeConstraint.putAll(mc.getAllowableValues());
    }


    public void setVisibilityConstraint(MapConstraint mc)
    {
        visibilityConstraint.putAll(mc.getAllowableValues());
    }


    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {

        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, true, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PARAM_PRIVATE_KEY, DataTypeDefinition.NODE_REF, true, getParamDisplayLabel(PARAM_PRIVATE_KEY)));
        paramList.add(new ParameterDefinitionImpl(PARAM_VISIBILITY, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_VISIBILITY), false, "pdfc-visibility"));
        paramList.add(new ParameterDefinitionImpl(PARAM_LOCATION, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_LOCATION)));
        paramList.add(new ParameterDefinitionImpl(PARAM_REASON, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_REASON)));
        paramList.add(new ParameterDefinitionImpl(PARAM_KEY_PASSWORD, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_KEY_PASSWORD)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WIDTH, DataTypeDefinition.INT, false, getParamDisplayLabel(PARAM_WIDTH)));
        paramList.add(new ParameterDefinitionImpl(PARAM_HEIGHT, DataTypeDefinition.INT, false, getParamDisplayLabel(PARAM_HEIGHT)));
        paramList.add(new ParameterDefinitionImpl(PARAM_KEY_TYPE, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_KEY_TYPE), false, "pdfc-keytype"));
        paramList.add(new ParameterDefinitionImpl(PARAM_ALIAS, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_ALIAS)));
        paramList.add(new ParameterDefinitionImpl(PARAM_STORE_PASSWORD, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_STORE_PASSWORD)));

        super.addParameterDefinitions(paramList);

    }


    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.repository.NodeRef,
     * org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action ruleAction, NodeRef actionedUponNodeRef)
    {
        if (serviceRegistry.getNodeService().exists(actionedUponNodeRef) == false)
        {
            // node doesn't exist - can't do anything
            return;
        }

        ContentReader actionedUponContentReader = getReader(actionedUponNodeRef);

        if (actionedUponContentReader != null)
        {
            // Add the signature to the PDF
            doSignature(ruleAction, actionedUponNodeRef, actionedUponContentReader);

            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Can't execute rule: \n" + "   node: " + actionedUponNodeRef + "\n" + "   reader: "
                                 + actionedUponContentReader + "\n" + "   action: " + this);
                }
            }
        }
    }


    /**
     * 
     * @param ruleAction
     * @param actionedUponNodeRef
     * @param actionedUponContentReader
     */
    protected void doSignature(Action ruleAction, NodeRef actionedUponNodeRef, ContentReader actionedUponContentReader)
    {

        NodeRef privateKey = (NodeRef)ruleAction.getParameterValue(PARAM_PRIVATE_KEY);
        String location = (String)ruleAction.getParameterValue(PARAM_LOCATION);
        String reason = (String)ruleAction.getParameterValue(PARAM_REASON);
        String visibility = (String)ruleAction.getParameterValue(PARAM_VISIBILITY);
        String keyPassword = (String)ruleAction.getParameterValue(PARAM_KEY_PASSWORD);
        String keyType = (String)ruleAction.getParameterValue(PARAM_KEY_TYPE);
        int height = Integer.parseInt((String)ruleAction.getParameterValue(PARAM_HEIGHT));
        int width = Integer.parseInt((String)ruleAction.getParameterValue(PARAM_WIDTH));

        // New keystore parameters
        String alias = (String)ruleAction.getParameterValue(PARAM_ALIAS);
        String storePassword = (String)ruleAction.getParameterValue(PARAM_STORE_PASSWORD);

        // Ugly and verbose, but fault-tolerant
        String locationXStr = (String)ruleAction.getParameterValue(PARAM_LOCATION_X);
        String locationYStr = (String)ruleAction.getParameterValue(PARAM_LOCATION_Y);
        int locationX = 0;
        int locationY = 0;
        try
        {
            locationX = locationXStr != null ? Integer.parseInt(locationXStr) : 0;
        }
        catch (NumberFormatException e)
        {
            locationX = 0;
        }
        try
        {
            locationY = locationXStr != null ? Integer.parseInt(locationYStr) : 0;
        }
        catch (NumberFormatException e)
        {
            locationY = 0;
        }

        File tempDir = null;
        ContentWriter writer = null;
        KeyStore ks = null;

        try
        {
            // get a keystore instance by
            if (keyType == null || keyType.equalsIgnoreCase(KEY_TYPE_DEFAULT))
            {
                ks = KeyStore.getInstance(KeyStore.getDefaultType());
            }
            else if (keyType.equalsIgnoreCase(KEY_TYPE_PKCS12))
            {
                ks = KeyStore.getInstance("pkcs12");
            }
            else
            {
                throw new AlfrescoRuntimeException("Unknown key type " + keyType + " specified");
            }

            // open the reader to the key and load it
            ContentReader keyReader = getReader(privateKey);
            ks.load(keyReader.getContentInputStream(), storePassword.toCharArray());

            // set alias
            // String alias = (String) ks.aliases().nextElement();

            PrivateKey key = (PrivateKey)ks.getKey(alias, keyPassword.toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);

            // open original pdf
            ContentReader pdfReader = getReader(actionedUponNodeRef);
            PdfReader reader = new PdfReader(pdfReader.getContentInputStream());

            // create temp dir to store file
            File alfTempDir = TempFileProvider.getTempDir();
            tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
            tempDir.mkdir();
            File file = new File(tempDir, serviceRegistry.getFileFolderService().getFileInfo(actionedUponNodeRef).getName());

            FileOutputStream fout = new FileOutputStream(file);
            PdfStamper stamp = PdfStamper.createSignature(reader, fout, '\0');
            PdfSignatureAppearance sap = stamp.getSignatureAppearance();
            sap.setCrypto(key, chain, null, PdfSignatureAppearance.WINCER_SIGNED);

            // set reason for signature and location of signer
            sap.setReason(reason);
            sap.setLocation(location);

            if (visibility.equalsIgnoreCase(PDFSignatureActionExecuter.VISIBILITY_VISIBLE))
            {
                sap.setVisibleSignature(new Rectangle(locationX + width, locationY - height, locationX, locationY), 1, null);
            }

            stamp.close();

            writer = getWriter(file.getName(), (NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER));
            writer.setEncoding(actionedUponContentReader.getEncoding());
            writer.setMimetype(FILE_MIMETYPE);
            writer.putContent(file);

            file.delete();
        }
        catch (Exception e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
        finally
        {
            if (tempDir != null)
            {
                try
                {
                    tempDir.delete();
                }
                catch (Exception ex)
                {
                    throw new AlfrescoRuntimeException(ex.getMessage(), ex);
                }
            }
        }
    }
}
