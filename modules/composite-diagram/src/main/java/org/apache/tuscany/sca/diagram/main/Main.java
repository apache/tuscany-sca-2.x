/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.diagram.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.tuscany.sca.diagram.generator.DiagramGenerator;
import org.apache.tuscany.sca.diagram.html.HTMLWrapper;
import org.apache.tuscany.sca.diagram.layout.CompositeEntity;
import org.apache.tuscany.sca.diagram.layout.EntityBuilder;
import org.apache.tuscany.sca.diagram.layout.TuscanyCompositeEntityBuilder;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.extensibility.NodeExtension;
import org.w3c.dom.Document;

public class Main {

    static Options getCommandLineOptions() {
        Options options = new Options();
        Option opt1 = new Option("s", "svg", false, "Generate SVG diagrams");
        options.addOption(opt1);
        Option opt2 = new Option("h", "html", false, "Generate HTML documents");
        options.addOption(opt2);

        Option opt3 = new Option("j", "jpeg", false, "Generate JPEG diagrams");
        options.addOption(opt3);

        Option opt4 = new Option("o", "output", true, "Output directory");
        options.addOption(opt4);

        return options;
    }

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new PosixParser();
        Options options = getCommandLineOptions();
        CommandLine cli = null;
        boolean help = false;
        try {
            cli = parser.parse(options, args);
            if (cli.getArgList().size() == 0) {
                help = true;
            }
        } catch (ParseException e) {
            System.out.println(e);
            help = true;
        }

        // Print out the options and quit
        if (help) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("<-h> <-s> <-o outputDirectory> file1 file2 ...: ", options);
            return;
        }

        boolean isHtml = false;
        boolean isSvg = false;
        boolean isJpeg = false;
        isSvg = cli.hasOption('s');
        isHtml = cli.hasOption('h');
        isJpeg = cli.hasOption('j');

        if (!isSvg && !isHtml && !isJpeg) {
            isSvg = true;
            isHtml = true;
        }

        String outFileDir;

        if (cli.hasOption('o')) {
            outFileDir = cli.getOptionValue('o');
        } else {
            outFileDir = ".";
        }
        File dir = new File(outFileDir);
        String[] compositeFiles = cli.getArgs();
        generate(dir, null, isSvg, isHtml, false, compositeFiles);
    }

    public static void generate(File dir,
                                String baseURL,
                                boolean isSvg,
                                boolean isHtml,
                                boolean isJpeg,
                                String... compositeFiles) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        for (String str : compositeFiles) {

            if (str != null) {

                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(str);

                EntityBuilder eb = new EntityBuilder(doc);
                CompositeEntity comp = eb.buildCompositeEntity();

                DiagramGenerator dg = new DiagramGenerator(comp, isHtml, baseURL);
                Document svg = dg.buildSVGDocument();

                if (isJpeg) {
                    String jpgFileName = comp.getName() + comp.getFileNameSuffix() + ".jpg";
                    svgToJPEG(svg, new File(dir, jpgFileName));
                }

                String svgContent = extractSvg(svg);

                if (isSvg) {
                    writeSVG(dir, svgContent, comp);
                }

                if (isHtml) {
                    writeHTML(dir, svgContent, comp);
                }

            }
        }
    }

    private static void writeHTML(File dir, String svg, CompositeEntity comp) throws Exception {

        File htmlFile = new File(dir, comp.getName() + comp.getFileNameSuffix() + ".html");
        HTMLWrapper html = new HTMLWrapper(svg, comp.getName(), htmlFile);
        html.buildHTML();

        //        System.err.println("--------------HTML Output for " + comp.getName() + "--------------\n");
        //        System.out.println(content);
        //        System.err.println("--------------------------------------------------------------\n");

    }

    private static File writeSVG(File dir, String svg, CompositeEntity comp) throws Exception {

        String svgFileName = comp.getName() + comp.getFileNameSuffix() + ".svg";
        File svgFile = new File(dir, svgFileName);
        FileWriter fw = new FileWriter(svgFile);
        fw.write(svg);
        fw.close();
        return svgFile;
    }

    private static String extractSvg(Document svg) throws Exception {
        // Print the DOM node

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);

        transform(svg, result);
        String svgString = sw.toString();

        return svgString;
    }

    private static void transform(Document svg, StreamResult result) throws Exception {
        // Set up the output transformer
        TransformerFactory transfac = TransformerFactory.newInstance();

        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(svg);
        trans.transform(source, result);
    }

    public static void svgToJPEG(File svg, File jpeg) throws IOException, TranscoderException {
        // Create the transcoder input.
        TranscoderInput input = new TranscoderInput(svg.toURI().toString());
        // Create the transcoder output.
        OutputStream ostream = new FileOutputStream(jpeg);
        TranscoderOutput output = new TranscoderOutput(ostream);

        // Create a JPEG transcoder
        Transcoder t = new JPEGTranscoder();

        // Set the transcoding hints.
        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1.0));

        // Save the image.
        t.transcode(input, output);

        // Flush and close the stream.
        ostream.flush();
        ostream.close();
    }

    public static void svgToJPEG(Document svg, File jpeg) throws IOException, TranscoderException {
        // Create the transcoder input.
        TranscoderInput input = new TranscoderInput(svg);
        // Create the transcoder output.
        OutputStream ostream = new FileOutputStream(jpeg);
        TranscoderOutput output = new TranscoderOutput(ostream);

        // Create a JPEG transcoder
        Transcoder t = new JPEGTranscoder();

        // Set the transcoding hints.
        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1.0));

        // Save the image.
        t.transcode(input, output);

        // Flush and close the stream.
        ostream.flush();
        ostream.close();
    }

    public static void svgToJPEG(InputStream svg, OutputStream jpeg) throws IOException, TranscoderException {
        // Create the transcoder input.
        TranscoderInput input = new TranscoderInput(svg);

        TranscoderOutput output = new TranscoderOutput(jpeg);

        // Create a JPEG transcoder
        Transcoder t = new JPEGTranscoder();

        // Set the transcoding hints.
        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1.0));

        // Save the image.
        t.transcode(input, output);

        // Flush and close the stream.
        svg.close();
        jpeg.flush();
        jpeg.close();
    }

    public static void svgToPNG(InputStream svg, OutputStream png) throws IOException, TranscoderException {
        // Create the transcoder input.
        TranscoderInput input = new TranscoderInput(svg);

        TranscoderOutput output = new TranscoderOutput(png);

        // Create a JPEG transcoder
        Transcoder t = new PNGTranscoder();

        // Save the image.
        t.transcode(input, output);

        // Flush and close the stream.
        svg.close();
        png.flush();
        png.close();
    }

    /**
     * Generate the SVG diagram from 
     * @param configuration
     * @param classLoader
     * @return The XML string for the SVG
     * @throws Exception
     */
    public static String generateDiagram(NodeConfiguration configuration, ClassLoader classLoader, String baseURL)
        throws Exception {
        ClassLoader currentTCCL = null;
        if (classLoader != null) {
            currentTCCL = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
        }

        try {
            NodeFactory factory = NodeFactory.getInstance();
            NodeExtension node = factory.loadNode(configuration);
            TuscanyCompositeEntityBuilder builder = new TuscanyCompositeEntityBuilder(node.getDomainComposite());
            CompositeEntity compositeEntity = builder.buildCompositeEntity();
            DiagramGenerator generator = new DiagramGenerator(compositeEntity, false, baseURL);
            Document doc = generator.buildSVGDocument();
            return extractSvg(doc);
        } finally {
            if (currentTCCL != null) {
                Thread.currentThread().setContextClassLoader(currentTCCL);
            }
        }
    }

}
