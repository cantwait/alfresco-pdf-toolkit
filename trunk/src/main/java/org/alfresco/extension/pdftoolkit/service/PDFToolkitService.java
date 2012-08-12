package org.alfresco.extension.pdftoolkit.service;

import org.alfresco.repo.processor.BaseProcessorExtension;

public class PDFToolkitService extends BaseProcessorExtension{
    
    public PDFToolkitService() {
    }

    public void jsConstructor() {
    }

    public String getClassName() {
        return "PDFToolkitService";
    }

    /**
     * Wrapper for the encrypt PDF method.  This will call the same underlying code as
     * the PDFEncryptionActionExecuter once the actual PDF encryption code is refactored into
     * a new class.
     */
    public String encryptPDF() {
    	return "encrypted pdf";
    }
    
    public void signPDF() {}
    
    public void watermarkPDF() {}
    
    public void splitPDF() {}
    
    public void appendPDF() {}
    
}
