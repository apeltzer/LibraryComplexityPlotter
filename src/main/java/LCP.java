import IO.ReadCCurve;
import IO.ReadLCExtrap;
import com.google.common.io.Files;
import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * Created by peltzer on 07/11/2016.
 */
public class LCP {
    private double y_min = 0.0;
    private double y_max = 0.0;
    private double x_max = 0.0;
    private XYSeriesCollection xydataset;
    private String title;

    public LCP(String input1, String input2, String outputpath) throws IOException, DocumentException {
        this.title = "Observed and expected complexity curve.";
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
        if(xy_ccurve.getMaxX() > x_max){
            x_max = xy_ccurve.getMaxX();
        }
        if (xy_lcp.getMaxY() > y_max) {
            y_max = xy_lcp.getMaxY();
        }
        if (xy_lcp.getMinY() < y_min) {
            y_min = xy_lcp.getMinY();
        }
        if(xy_lcp.getMaxX() > x_max){
            x_max = xy_lcp.getMaxX();
        }

        JFreeChart chart = createChart(xydataset,y_max);
        String fileName = Files.getNameWithoutExtension(input1);
        createPdf(fileName.substring(0,8)+"_Complexity_Chart.pdf",new JFreeChart[]{chart},outputpath);



    }

    public LCP(String input1, String outputpath) throws IOException, DocumentException {
        this.title = "Observed complexity curve.";
        ReadCCurve rcc = new ReadCCurve();
        ReadLCExtrap lcp = new ReadLCExtrap();

        xydataset = new XYSeriesCollection();
        XYSeries xy_ccurve = rcc.readData(new File(input1));
        xydataset.addSeries(xy_ccurve);
        if (xy_ccurve.getMaxY() > y_max) {
            y_max = xy_ccurve.getMaxY();
        }
        if (xy_ccurve.getMinY() < y_min) {
            y_min = xy_ccurve.getMinY();
        }
        if(xy_ccurve.getMaxX() > x_max){
            x_max = xy_ccurve.getMaxX();
        }

        JFreeChart chart = createChart(xydataset,y_max);
        String fileName = Files.getNameWithoutExtension(input1);
        createPdf(fileName.substring(0,10)+"_Complexity_Chart_ObservedOnly.pdf",new JFreeChart[]{chart},outputpath);
    }


    public static void main(String[] args) throws IOException, DocumentException {
        if (args.length < 2) {
            System.err.println("Please specify the c-curve and lcextrap output or just the c-curve output file and an ");
            System.exit(-1);
        } else {
            if (args.length == 2) {
                LCP lcp = new LCP(args[0], args[1]);
            } else {
                LCP lcp = new LCP(args[0], args[1], args[2]);
            }
        }
    }


    public JFreeChart createChart(final XYDataset dataset, double yMax) {
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
                title,                   // chart title
                "Number of Total Reads",                       // x axis label
                "Distinct/Unique Reads",              // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );

        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);


        LegendItemCollection legendItemsOld = plot.getLegendItems();
        final LegendItemCollection legendItemsNew = new LegendItemCollection();
        legendItemsNew.add(legendItemsOld.get(0));
        legendItemsNew.add(legendItemsOld.get(1));

        // set range of axis
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        xAxis.setRange(0.00, x_max);
       // xAxis.setTickUnit(new NumberTickUnit(100000));
        xAxis.setVerticalTickLabels(true);
        yAxis.setVerticalTickLabels(true);
        yAxis.setRange(0.0, yMax+0.05);

        NumberFormat formatter = new DecimalFormat("0.0E00");
        xAxis.setTickUnit(new NumberTickUnit(x_max/6, formatter));
        yAxis.setTickUnit(new NumberTickUnit(y_max/6, formatter));
        //if(yMax+0.1 > 0.5){
          //  yAxis.setTickUnit(new NumberTickUnit(1000));
        //} else {
        //    yAxis.setTickUnit(new NumberTickUnit(1000));
       // }


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
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file + "/" + filename));
        // step 3
        document.open();
        // compute percentage of used reads
        // draw text
        String title = filename;
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
        float width = PageSize.A4.getHeight() ;
        // create plots, both three prime and five prime and add them
        // to one PDF
        float xpos = 0;

            PdfTemplate plot = cb.createTemplate(width, height);
            Graphics2D g2d = new PdfGraphics2D(plot, width, height);
            Rectangle2D r2d = new Rectangle2D.Double(0, 0, width, height);
            charts[0].draw(g2d, r2d);
            g2d.dispose();
            cb.addTemplate(plot, xpos, 20);
            xpos += width;
        //}
        // step 5
        document.close();
    }




}
