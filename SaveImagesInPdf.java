package com.instant.impact360.pdfbox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

public class SaveImagesInPdf extends PDFStreamEngine {


    public int imageNumber = 1;


    public static void main(String[] args) throws Exception {
        PDDocument document = null;
       //String fileName = "D:\\Buzzi_Annual Report Marked(1).pdf";
        //String fileName = "D:\\AARef.pdf";
        //String fileName = "D:\\EMPLOYMENTCONTRACTTEMPLATE.pdf";
       String fileName = "D:\\lafargeholcim.pdf";
       // String fileName = "D:\\heidelbergce.pdf";
        try {PDDocument doc_ = new PDDocument();
            document = PDDocument.load(new File(fileName));
            SaveImagesInPdf printer = new SaveImagesInPdf();
            int pageNum = 0;
            for (PDPage page : document.getPages()) {
                    PDPage updatedPage = page;
                pageNum++;
                System.out.println("Processing page: " + pageNum);

               if(printer.removeText(page, pageNum))
               {
                   PDDocument doc__ = new PDDocument();
                  /* doc__.addPage(updatedPage);
                   PDFRenderer renderer = new PDFRenderer(doc__);

                   //Rendering an image from the PDF document
                   BufferedImage image = renderer.renderImage(0);

                   //Writing the image to a file
                   ImageIO.write(image, "JPEG", new File("D:/ExtractedImages/Page"+pageNum+".jpg"));
                   System.out.println("Page is written to Image : "+ pageNum);*/




                   doc__.addPage(page);
                   File f= new File("D:/ExtractedImages/lafargeholcim_S/page_"+pageNum+".pdf");
                   FileOutputStream fOut = new FileOutputStream(f);
                   doc__.save(fOut);
                   doc__.close();



               }



            }
           /* File f= new File("D:/ExtractedImages/FinalPage.pdf");
            FileOutputStream fOut = new FileOutputStream(f);
            doc_.save(fOut);
            doc_.close();*/
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    private static boolean checkPatternForDoc(PDDocument doc) throws IOException {

        boolean pass = false;
        PDPage page = doc.getPage(0);
        PDFStreamParser parser = new PDFStreamParser(page);
        List<Object> tokens = parser.getTokens();
        final List<String> graphsOperator = Arrays.asList("q", "cm", "m", "l");
        LinkedList<String> patternCheck = new LinkedList<>();
        for (Object token : tokens) {
            if (token instanceof Operator) {
                Operator op = (Operator) token;
                if (graphsOperator.contains(op.getName())) {
                    switch (op.getName()) {
                        case "q":

                            if (patternCheck.size() == 4 && patternCheck.toString().equals("qcmml")) {
                                patternCheck.clear();
                                pass = true;
                                break;
                            }
                            patternCheck.add("q");

                            break;

                        case "cm":
                            if (patternCheck.peekLast() != null && patternCheck.peekLast().equals("q"))
                                patternCheck.add("cm");
                            break;

                        case "m":
                            if (patternCheck.peekLast() != null && patternCheck.peekLast().equals("m"))
                                patternCheck.add("m");
                            break;

                        case "l":
                            if (patternCheck.peekLast() != null && patternCheck.peekLast().equals("l"))
                                patternCheck.add("l");
                            break;

                    }
                }

            }
        }
        return pass;
    }

    private static boolean withTokens(List<Object> tokens) throws IOException {

        boolean pass = false;
        final List<String> graphsOperator = Arrays.asList("q", "cm", "m", "l","Q"); // Taking in Consideration we will have f before Q
        LinkedList<String> patternCheck = new LinkedList<>();
        int counter = 0;
        outer:
        for (Object token : tokens) {
            if (token instanceof Operator) {
                Operator op = (Operator) token;
                if (graphsOperator.contains(op.getName())) {
                    switch (op.getName()) {
                        case "q":


                            if (patternCheck.size() == 20 /*&& patternCheck.toString().replace("[", "").replace("]", "").replace(",", "").replace(" ", "").equals("qcmmlQ")*/)
                                {
                                    patternCheck.clear();
                                    pass = true;
                                    break outer;
                                }

                            if(patternCheck.size() ==0 || (patternCheck.size()%5 == 0 && patternCheck.peekLast().equals("Q")))
                                    patternCheck.add("q");
                            else
                                    patternCheck.clear();

                           /* if (patternCheck.size() == 20 && patternCheck.toString().replace("[", "").replace("]", "").replace(",", "").replace(" ", "").equals("qcmmlQ")) {
                                patternCheck.clear();

                                counter++;
                                if (counter > 4) {
                                    pass = true;
                                    break outer;
                                }

                            }*/
                           /* patternCheck.clear();
                            patternCheck.add("q");*/
                            break;

                        case "cm":
                            if (patternCheck.peekLast() != null && patternCheck.peekLast().equals("q")) {
                                patternCheck.add("cm");
                            }
                            else
                            {
                                patternCheck.clear();
                            }
                            break;

                        case "m":
                            if (patternCheck.peekLast() != null && patternCheck.peekLast().equals("cm")){
                                patternCheck.add("m");}

                            else
                            {
                                patternCheck.clear();
                            }
                            break;

                        case "l":
                            if (patternCheck.peekLast() != null && patternCheck.peekLast().equals("m")){
                                patternCheck.add("l");
                            }
                            else
                            {
                                patternCheck.clear();
                            }

                            break;

                        case "Q":
                            if (patternCheck.peekLast() != null && patternCheck.peekLast().equals("l")){
                                patternCheck.add("Q");
                            }

                            break;





                    }
                }
              /*  else if (patternCheck.size() != 4) { //  pattern didn't reaches upper limit, need to celar it, as it will added bogus Code issue *
                    patternCheck.clear();
                }*/

            }
        }
        return pass;
    }


    public static PDDocument addText(PDPage page) throws IOException {

        PDFStreamParser parser = new PDFStreamParser(page);
        parser.parse();
        List<Object> tokens = parser.getTokens();
        List<Object> newTokens = new ArrayList<>();
        for (Object token : tokens) {
            final List<String> PAINTING_PATH_OPS = Arrays.asList("S", "s", "F", "f", "f*", "B", "b", "B*", "b*");

            if (token instanceof Operator) {
                Operator op = (Operator) token;
                if (op.getName().equals("Do")) {
                    //remove the one argument to this operator
                    COSName name = (COSName) newTokens.remove(newTokens.size() - 1);
                    continue;
                } else if (PAINTING_PATH_OPS.contains(op.getName())) {
                    // replace path painting operator by path no-op
                    token = Operator.getOperator("n");
                }
            }


            newTokens.add(token);
        }

        PDDocument document = new PDDocument();
        document.addPage(page);

        PDStream newContents = new PDStream(document);
        OutputStream out = newContents.createOutputStream(COSName.FLATE_DECODE);
        ContentStreamWriter writer = new ContentStreamWriter(out);
        writer.writeTokens(newTokens);
        out.close();
        page.setContents(newContents);

        return document;

    }

    public static /*PDDocument*/ boolean removeText(PDPage page, int pageNum) throws IOException {
        PDFStreamParser parser = new PDFStreamParser(page);
        List<Object> tokens = parser.getTokens();
        List<Object> newTokens = new ArrayList<>();

        parser.parse();
        final List<String> PAINTING_PATH_OPS = Arrays.asList("Tf", "Tw", "Td", "Tm");
        final List<String> graphsOperator = Arrays.asList("q", "Q", "cm", "w", "j", "M", "d", "ri", "gs");
        for (Object token : tokens) {
            if (token instanceof Operator) {
                Operator op = (Operator) token;
                //  if (Pattern.matches("GS*",  op.getName()) || op.getName().equals("gs") || op.getName().equals("Tj") ||op.getName().equals("TJ") || graphsOperator.contains(op.getName())  ) {
                if (op.getName().equals("Tj") || op.getName().equals("TJ") || op.getName().equals("Do")) {
                    newTokens.remove(newTokens.size() - 1);
                    continue;
                }
            }
            newTokens.add(token);
        }/*
        PDDocument docc = new PDDocument();
        docc.addPage(page);

        PDStream newContents = new PDStream(docc);
        OutputStream out = newContents.createOutputStream(COSName.FLATE_DECODE);
        ContentStreamWriter writer = new ContentStreamWriter(out);
        writer.writeTokens(newTokens);
        out.close();
        page.setContents(newContents);
*/






        boolean graphExistforPage = withTokens(newTokens);
        if (graphExistforPage)
            System.out.println("Graph Exist for Page : " + pageNum);

        return graphExistforPage;
    }

    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        String operation = operator.getName();
        if ("Do".equals(operation)) {
            COSName objectName = (COSName) operands.get(0);
            PDXObject xobject = getResources().getXObject(objectName);
            if (xobject instanceof PDImageXObject) {
                PDImageXObject image = (PDImageXObject) xobject;
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();

                // same image to local
                BufferedImage bImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
                bImage = image.getImage();
                ImageIO.write(bImage, "PNG", new File("image_" + imageNumber + ".png"));
                System.out.println("Image saved.");
                imageNumber++;

            } else if (xobject instanceof PDFormXObject) {
                PDFormXObject form = (PDFormXObject) xobject;
                showForm(form);
            }
        } else {
            super.processOperator(operator, operands);
        }
    }




















































/*

    public static void strip(String pdfFileOut) throws Exception {

        PDDocument doc = null;
        String fileName = "D:\\Buzzi_Annual Report Marked(1).pdf";

        doc = PDDocument.load( new File(fileName) );

        for( PDPage page : doc.getPages() )
        {
         COSDictionary newDictionary = new COSDictionary(page.getCOSObject());
        PDFStreamParser parser = new PDFStreamParser(page);
        parser.parse();
        List<Object> tokens = parser.getTokens();
        List<Object> newTokens = new ArrayList<>();
        for (Object token : tokens) {
            if (token instanceof Operator) {
                if (((Operator) token).getName().equals("Do"))
                    {
                        //remove the one argument to this operator
                        // added
                        COSName name = (COSName)newTokens.remove( newTokens.size() -1 );
                        // added
                        deleteObject(newDictionary, name);
                        continue;

                }
            }
            newTokens.add( token );
        }

            PDStream newContents = new PDStream( doc );
            ContentStreamWriter writer = new ContentStreamWriter( newContents.createOutputStream() );
            writer.writeTokens( newTokens );

            //newContents.addCompression();

            page.setContents( newContents );

            // added
            PDResources newResources = new PDResources(newDictionary);
            page.setResources(newResources);

break;
            // added


             }

        doc.save(pdfFileOut);
        doc.close();
    }
*/


    // added


} 