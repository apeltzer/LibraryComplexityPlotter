import IO.ReadCCurve;
import IO.ReadLCExtrap;
import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * Created by peltzer on 07/11/2016.
 */
public class LCP {
    private double y_min = 0.0;
    private double y_max = 0.0;
    private XYSeriesCollection xydataset;
    private String title;

    public LCP(String title, String input1, String input2, String outputpath) throws IOException, DocumentException {
        this.title = title;
        ReadCCurve rcc = new ReadCCurve();
        ReadLCExtrap lcp = new ReadLCExtrap();

        xydataset = new XYSeriesCollection();
        XYSeries xy_ccurve = rcc.readData(new File(input1));
        XYSeries xy_lcp = lcp.readData(new File(input2));
        xydataset.addSeries(xy_ccurve);
        xydataset.addSeries(xy_lcp);
        if (xy_ccurve.getMaxY() > y_max) {
            y_max = xy_ccurve.getMaxY();
        }
        if (xy_ccurve.getMinY() < y_min) {
            y_min = xy_ccurve.getMinY();
        }
        if (xy_lcp.getMaxY() > y_max) {
            y_max = xy_lcp.getMaxY();
        }
        if (xy_lcp.getMinY() < y_min) {
            y_min = xy_lcp.getMinY();
        }

        JFreeChart chart = createChart(xydataset,y_max);
        createPdf("test.pdf",new JFreeChart[]{chart},"Thisisjustatest");



    }

    public LCP(String title, String input1) {
        this.title = title;
    }


    public static void main(String[] args) throws IOException, DocumentException {
        if (args.length < 2) {
            System.err.println("Please specify either a single report from ccurve or ccurve and lc_extrap output files accordingly.");
            System.exit(-1);
        } else {
            if (args.length == 2) {
                LCP lcp = new LCP(args[0], args[1]);
            } else {
                LCP lcp = new LCP(args[0], args[1], args[2], args[3]);
            }
        }
    }


    public JFreeChart createChart(final XYDataset dataset, double yMax) {
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
                title,                   // chart title
                "",                       // x axis label
                "Frequency",              // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );
        return chart;
    }


    /**
     * Creates a PDF document.
     *
     * @param filename the path to the new PDF document
     * @throws DocumentException
     * @throws IOException
     *
     */
    public void createPdf(String filename, JFreeChart[] charts, String file) throws IOException, DocumentException {
        // step 1
        Document document = new Document(PageSize.A4.rotate());
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("/Users/peltzer/Desktop" + "/" + filename));
        // step 3
        document.open();
        // compute percentage of used reads
        // draw text
        String title = file;
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        Font fontbold = FontFactory.getFont("Times-Roman", 18, Font.BOLD);
        Font font = FontFactory.getFont("Times-Roman", 14);
        Paragraph para = new Paragraph();
        Chunk c_title = new Chunk(title, fontbold);
        Phrase p1 = new Phrase(c_title);
        para.add(p1);
        document.add(para);
        PdfContentByte cb = writer.getDirectContent();
        float height = PageSize.A4.getWidth() * (float)0.8;
        float width = PageSize.A4.getHeight() / 2;
        // create plots, both three prime and five prime and add them
        // to one PDF
        float xpos = 0;
//        // place histogram in the center if only one plot
//        if(charts.length==1){
//            xpos=width*(float)0.5;
//        }
        for(JFreeChart chart : charts){
            PdfTemplate plot = cb.createTemplate(width, height);
            Graphics2D g2d = new PdfGraphics2D(plot, width, height);
            Rectangle2D r2d = new Rectangle2D.Double(0, 0, width, height);
            chart.draw(g2d, r2d);
            g2d.dispose();
            cb.addTemplate(plot, xpos, 20);
            xpos += width;
        }
        // step 5
        document.close();
    }




}
