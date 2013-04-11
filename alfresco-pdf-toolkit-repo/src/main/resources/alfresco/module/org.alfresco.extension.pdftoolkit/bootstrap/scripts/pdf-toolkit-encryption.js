function encrypt()
{
	/*
	 * This script depends on the test data from the bootstrap ACP file being loaded into
	 * the PDF Toolkit Examples folder in the repository.  This can be done by enabling the bootstrap
	 * load of this test data in context/bootstrap-context.xml
	 */
	
	// Get the PDF to be encrypted
	var pdf = companyhome.childByNamePath("PDF Toolkit Examples/Assets/PDF Toolkit Test Document.pdf");
	
	// Get the destination folder for the encrypted document
	var destination = companyhome.childByNamePath("PDF Toolkit Examples");
	
	// Get the encryption action
	var encryptAction = actions.create("pdf-encryption");
	
	// Set the action parameters
	
	// Execute the encryption action
	encryptAction.execute(pdf);
}

encrypt();