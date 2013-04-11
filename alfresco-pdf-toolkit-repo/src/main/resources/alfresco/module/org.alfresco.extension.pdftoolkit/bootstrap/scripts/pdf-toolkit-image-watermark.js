function watermark()
{
	/*
	 * This script depends on the test data from the bootstrap ACP file being loaded into
	 * the PDF Toolkit Examples folder in the repository.  This can be done by enabling the bootstrap
	 * load of this test data in context/bootstrap-context.xml
	 */
	
	// Get the PDF to be watermarked
	var pdf = companyhome.childByNamePath("PDF Toolkit Examples/Assets/PDF Toolkit Test Document.pdf");
	
	// Get the destination folder for the watermarked document
	var destination = companyhome.childByNamePath("PDF Toolkit Examples");
	
	// Get the image to use as the watermark
	var image = companyhome.childByNamePath("PDF Toolkit Examples/Assets/Watermark Background Image.png");
	
	// Create an instance of the watermark action using the action name
	var watermarkAction = actions.create("pdf-watermark");
	
	// Set the watermark action parameters
	watermarkAction.parameters["destination-folder"] = destination;
	watermarkAction.parameters["watermark-image"] = image;
	watermarkAction.parameters["watermark-type"] = "image";
	watermarkAction.parameters["watermark-pages"] = "all";
	watermarkAction.parameters["watermark-depth"] = "under";
	watermarkAction.parameters["position"] = "center";
	
	// Execute the watermark action
	watermarkAction.execute(pdf);
}

watermark();