package imageconversion;

import java.io.FileOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.imageio.ImageIO;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Element;

public class ImageToPDF {
	
	public static void main(String args[]) {
		
		try {
			ImageToPDF imgToPDF = new ImageToPDF();
			//convertJPGTOPDFinSpecificFolder(imgToPDF.getRootFolderPathFromConfig());
			convertJPGToPDFInAllTheSubFolders(imgToPDF.getRootFolderPathFromConfig());
			System.out.println("----------------------------------------------------------------------------------------");
			System.out.println("All the JPG images are in '" + imgToPDF.getRootFolderPathFromConfig() 
					+ "' folder and it's sub folders, have been converted to PDF successfully");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Reading the root folder path from the configuration file
	private String getRootFolderPathFromConfig() throws IOException {
		InputStream inputStream;
		Properties prop = new Properties();
		String propFileName = "config.properties";
		
		inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		 
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath");
		}
		
		String rootPath = prop.getProperty("rootPath");
		return rootPath;
	}
	
	// This converts JPG images to PDF in root folder specified in configuration file
	private static void convertJPGTOPDFinSpecificFolder(String folderPath) {
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles.length == 0) {
			System.out.println("No Image files in the " + folderPath + " directory");
		}
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				
				String fileName = listOfFiles[i].getName();
				String absoluteFilePath = listOfFiles[i].getAbsolutePath();
				
				if (getFileExtension(fileName).equalsIgnoreCase("JPG") || getFileExtension(fileName).equalsIgnoreCase("JPEG")) {
					convertJPGToPDF(fileName, absoluteFilePath);
				}
				
			}
		}
	}
	
	// This is for travel through all the sub folders from the root folder specified in the configuration file
	// and convert JPG images to PDF
	private static void convertJPGToPDFInAllTheSubFolders(String folderPath) {

        File root = new File( folderPath );
        File[] listOfFiles = root.listFiles();

		if (listOfFiles == null) {
			System.out.println("No files in the directory : " + folderPath);
			return;
		}

		for (File file : listOfFiles) {
			if (file.isDirectory()) {
				convertJPGToPDFInAllTheSubFolders(file.getAbsolutePath());
			} else if (file.isFile()) {

				String fileName = file.getName();
				String absoluteFilePath = file.getAbsolutePath();

				if (getFileExtension(fileName).equalsIgnoreCase("JPG") || 
						getFileExtension(fileName).equalsIgnoreCase("JPEG")) {
					convertJPGToPDF(fileName, absoluteFilePath);
				} 
			}
		}
			
	}

	// Method which convert JPG to PDF
	private static void convertJPGToPDF(String fileName, String absoluteFilePath) {
		try {
			String currentFolder = absoluteFilePath.substring(0, absoluteFilePath.lastIndexOf(fileName));
			System.out.println("----------------------------------------------------------------------------------------");
			System.out.println("Starting to convert the image '" + fileName + "' to PDF in '" + currentFolder + "'");

			String inputImageFileName = absoluteFilePath;
			String outputPDFFileName = absoluteFilePath.substring(0, absoluteFilePath.lastIndexOf('.')) + ".pdf";

			// Deleting the PDF iff it exists
			deleteExsistingPDF(outputPDFFileName);
			
			// create document object
			Document doc = new Document();
			
			// create pdf writer object to write the document to the output file
			PdfWriter.getInstance(doc, new FileOutputStream(outputPDFFileName));
			
			// get a4 paper size
			Rectangle r = PageSize.A4;
			
			// read the image
			BufferedImage orImg = ImageIO.read(new File(inputImageFileName));
			
			// initialize image width and height
			int width = orImg.getWidth();
			int height = orImg.getHeight();
			// resize the image that is bigger than A4 size
			if (width > r.getWidth())
				width = (int) r.getWidth();
			if (height > r.getHeight())
				height = (int) r.getHeight();
			
			// create a blank buffered image
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			
			// create graphic2d object from the buffered image
			Graphics2D g2d = bi.createGraphics();
			
			// draw the original image on the buffered image
			// so the image is resized to fit the A4 paper size if it is bigger
			// than A4 size
			g2d.drawImage(orImg, 0, 0, width, height, null);
			
			// store the image data in memory
			ByteArrayOutputStream bas = new ByteArrayOutputStream();
			ImageIO.write(bi, "png", bas);
			
			// create image from the image data stored in memory
			Image img = Image.getInstance(bas.toByteArray());
			
			// centrally align the image on the pdf page
			img.setAlignment(Element.ALIGN_CENTER);
			
			// open document
			doc.open();
			// add image to the document
			doc.add(img);
			// close the document
			doc.close();
			
			File outputFile = new File(outputPDFFileName);
			System.out.println("'" + fileName + "' was successfully converted to '" + outputFile.getName() + "' in '" + currentFolder + "'");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// This is to get the file extension
	private static String getFileExtension(String fileName) {
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}
	
	// This is to delete the existing PDF file
	private static void deleteExsistingPDF(String fileName) {
		File file = new File(fileName);
		String currentFolder = fileName.substring(0, fileName.lastIndexOf(file.getName()));

		if (file.exists()) {
			if (file.delete()) {
				System.out.println("Existing '" + file.getName() + "' was deleted in '" + currentFolder + "'");
			} else {
				System.out.println("Delete operation failed. Please check '" + file + "'");
			}
		}
	}
	
	// This is basic method to convert JPG to PDF. Usage as below
	// String input = "C:\\Users\\sgat001\\Desktop\\Images\\Test1.jpg";
	// String output = "C:\\Users\\sgat001\\Desktop\\Images\\Test1.pdf";
	// imageTopdf(input, output);
	private static void imageTopdf(String input, String output) {
		Document document = new Document();
		try {
			FileOutputStream fos = new FileOutputStream(output);
			PdfWriter writer = PdfWriter.getInstance(document, fos);

			writer.open();
			document.open();
			document.add(Image.getInstance(input));
			document.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

