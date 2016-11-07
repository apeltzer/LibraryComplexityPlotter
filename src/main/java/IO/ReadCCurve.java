package IO;
import org.jfree.data.xy.XYSeries;

import java.io.*;

/**
 * Created by peltzer on 07/11/2016.
 */
public class ReadCCurve {
    private BufferedReader bfr ;
    private FileReader fr;

    public ReadCCurve(){

    }


    public XYSeries readData(File input) throws IOException {
        //ignore total_read and distinct_reads lines completely, just check whether they are here!
        fr = new FileReader(input);
        bfr = new BufferedReader(fr);

        XYSeries data = new XYSeries("Observed Complexity based on N reads.");
        String currLine = "";

        while((currLine = bfr.readLine()) != null) {
            if(currLine.startsWith("total_reads")){
                continue;
            }

            String[] split = currLine.split("\t");
            Double value_total = Double.parseDouble(split[0]);
            Double value_distinct = Double.parseDouble(split[1]);

            data.add(value_total, value_distinct);
        }
        return data;

    }

}
