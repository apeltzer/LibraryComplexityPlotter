package IO;

import java.io.File;
import org.jfree.data.xy.XYSeries;

import java.io.*;

/**
 * Created by peltzer on 07/11/2016.
 */
public class ReadLCExtrap {
    private BufferedReader bfr ;
    private FileReader fr;

    public ReadLCExtrap(){

    }

    public XYSeries readData(File input) throws IOException {
        //ignore total_read and distinct_reads lines completely, just check whether they are here!
        fr = new FileReader(input);
        bfr = new BufferedReader(fr);

        XYSeries data = new XYSeries("Extrapolated LC_Extrap Expected");
        String currLine = "";

        while((currLine = bfr.readLine()) != null) {
            if(currLine.startsWith("TOTAL_READS") || currLine.startsWith("0\t0\t0\0")){
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
