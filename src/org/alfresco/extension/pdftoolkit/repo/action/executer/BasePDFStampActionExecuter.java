package org.alfresco.extension.pdftoolkit.repo.action.executer;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;

public abstract class BasePDFStampActionExecuter extends BasePDFActionExecuter {

	/*
	 * Page and position constants
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
    
    /**
     * Determines whether or not a watermark should be applied to a given page
     * @param pages
     * @param current
     * @param numpages
     * @return
     */
	protected boolean checkPage(String pages, int current, int numpages)
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
    protected float getCenterX(Rectangle r, Image img)
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
    protected float getCenterY(Rectangle r, Image img)
    {
    	float y = 0;
    	float pdfheight = r.getHeight();
    	float imgheight = img.getHeight();
    	
    	y = (pdfheight - imgheight) / 2;
    	
    	return y;
    }
}
