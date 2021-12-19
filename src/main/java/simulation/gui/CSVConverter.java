package simulation.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

// inspirowane https://www.codegrepper.com/code-examples/java/convert+arraylist+to+csv+file+java

public class CSVConverter {
    public static void convert(List<List<Double>> stats, boolean firstMap) throws IOException {
        for (int i = 0; i < 5; i++) {
            Double sum = (double) 0;
            for (int j = 0; j < stats.get(i).size(); j++) {
                sum += stats.get(i).get(j);
            }
            Double avg = sum / stats.get(i).size();
            stats.get(i).add(avg);
        }

        File file;
        if (firstMap) {
            file = new File("statsLeft.csv");
        }
        else {
            file = new File("statsRight.csv");
        }
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("[\"Epoque\",\"Animals_amount\",\"Plants_amount\",\"Average_energy\",\"Average_life_length\"," +
                "\"Average_children_amount\"]");
        bw.newLine();

        for (int i = 0; i < stats.get(0).size(); i++) {
            for (int j = -1; j < 5; j++) {
                if (j < 0) {
                    if (i == stats.get(0).size() - 1) {
                        bw.write("\"Average\",");
                    }
                    else {
                        bw.write("\"" + Integer.toString(i + 1) + "\",");
                    }
                }
                else if (j == 4) {
                    bw.write("\""+stats.get(j).get(i).toString()+"\"");
                    bw.newLine();
                }
                else {
                    bw.write("\""+stats.get(j).get(i).toString()+"\",");
                }
            }
        }
        bw.close();
        fw.close();
    }
}
