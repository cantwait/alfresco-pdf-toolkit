function sign()
{
	/*
	 * This script depends on the test data from the bootstrap ACP file being loaded into
	 * the PDF Toolkit Examples folder in the repository.  This can be done by enabling the bootstrap
	 * load of this test data in context/bootstrap-context.xml
	 */
	
	// Get the PDF to be signed
	var pdf = companyhome.childByNamePath("PDF Toolkit Examples/Assets/PDF Toolkit Test Document.pdf");
	
	// Get the destination folder for the signed document
	var destination = companyhome.childByNamePath("PDF Toolkit Examples");

	// Get the signature action
	var signatureAction = actions.create("pdf-signature");
	
	// Set the action parameters
	
	// Execute the signature action
	signatureAction.execute(pdf);
	
}

sign();