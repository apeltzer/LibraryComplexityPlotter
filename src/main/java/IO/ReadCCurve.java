package IO.ReadCCurve;

import org.jfree.data.xy.XYSeries;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by peltzer on 07/11/2016.
 */
public class ReadCCurve {
    private BufferedReader bfr ;
    private FileReader fr;


    public ArrayList<XYSeries> readData(File input) throws IOException {
        //ignore total_read and distinct_reads lines completely, just check whether they are here!
        fr = new FileReader(input);
        bfr = new BufferedReader(fr);

        String currLine = "";

        while((currLine = bfr.readLine()) != null) {
            String[] split = currLine.split("\t");
            
        }

    }

}
