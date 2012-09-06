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

package org.alfresco.extension.pdftoolkit.service;


import org.alfresco.repo.processor.BaseProcessorExtension;


public class PDFToolkitService
    extends BaseProcessorExtension
{

    public PDFToolkitService()
    {
    }


    public void jsConstructor()
    {
    }


    public String getClassName()
    {
        return "PDFToolkitService";
    }


    /**
     * Wrapper for the encrypt PDF method. This will call the same underlying
     * code as the PDFEncryptionActionExecuter once the actual PDF encryption
     * code is refactored into a new class.
     */
    public String encryptPDF()
    {
        return "encrypted pdf";
    }


    public void signPDF()
    {
    }


    public void watermarkPDF()
    {
    }


    public void splitPDF()
    {
    }


    public void appendPDF()
    {
    }

}
