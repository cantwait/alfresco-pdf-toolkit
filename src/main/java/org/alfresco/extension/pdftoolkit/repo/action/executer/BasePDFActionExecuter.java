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


import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


public abstract class BasePDFActionExecuter
    extends ActionExecuterAbstractBase
{

    protected static final String FILE_EXTENSION = ".pdf";
    protected static final String FILE_MIMETYPE  = "application/pdf";
    protected ServiceRegistry     serviceRegistry;


    /**
     * Set a service registry to use, this will do away with all of the
     * individual service registrations
     * 
     * @param serviceRegistry
     */
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }


    /**
     * @param actionedUponNodeRef
     * @return
     */
    protected ContentReader getReader(NodeRef nodeRef)
    {
        // First check that the node is a sub-type of content
        QName typeQName = serviceRegistry.getNodeService().getType(nodeRef);
        if (serviceRegistry.getDictionaryService().isSubClass(typeQName, ContentModel.TYPE_CONTENT) == false)
        {
            // it is not content, so can't transform
            return null;
        }

        // Get the content reader
        ContentReader contentReader = serviceRegistry.getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);

        return contentReader;
    }


    /**
     * @param ruleAction
     * @param filename
     * @return
     */
    protected ContentWriter getWriter(String filename, NodeRef destinationParent)
    {

        FileInfo fileInfo = serviceRegistry.getFileFolderService().create(destinationParent, filename, ContentModel.TYPE_CONTENT);

        // get the writer and set it up
        ContentWriter contentWriter = serviceRegistry.getContentService().getWriter(fileInfo.getNodeRef(), ContentModel.PROP_CONTENT, true);

        return contentWriter;
    }
}
