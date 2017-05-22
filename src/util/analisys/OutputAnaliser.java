/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.analisys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import simulation.Params;

/**
 *
 * @author Ricardo Grunitzki
 */
public class OutputAnaliser {

    public static AnalysisType type;
    public static Integer[] columnsToPrint = {0, 1};
    public static String fileToBePrinted = "mean.txt";
//    public static String fileToBePrinted = "stdev.txt";
//

    public static void main(String[] args) throws IOException {
        System.out.println("!-----------------------------------------------------!");
        System.out.println("!Generates the average files of repetead simulations  !");
        System.out.println("!-----------------------------------------------------!");
        String experimentsDirectory = "/home/gauss/rgrunitzki/Dropbox/Profissional/UFRGS/Submissões/DEVELOPMENT/TRI15/SF Experiments/IQ-Learning";
        //Params.OUTPUTS_DIRECTORY + File.separator + "braess";
        type = AnalysisType.GENERATE_AVERAGES;
        Params.COLUMN_SEPARATOR = ";";

        walk(experimentsDirectory);

    }

    public static void walk(String path) throws FileNotFoundException, IOException {
        File root = new File(path);
        File[] files = root.listFiles();
        Arrays.sort(files, (File f1, File f2) -> String.valueOf(f1.getName()).compareTo(f2.getName()));

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                walk(file.getAbsolutePath());
            } else {

                switch (type) {
                    case GENERATE_AVERAGES:
                        System.out.print("Analisyng Directory: " + file.getParent());
                        generateAverages(files);
                        System.out.println(" <--- mean.txt and stdev.txt created");
                        break;
                    case PRINT_SELECTED_COLUMNS:
                        System.out.println("");
                        printSelectedColumns(files);

                        break;
                    case PRINT_MEAN_CONVERGENCE_CURVE:
                        printMeanConvergenceCurve(files);
                        break;
                    default:
                        throw new AssertionError(type.name());

                }

                break;
            }
        }
    }

    public static void generateAverages(File[] files) throws FileNotFoundException, IOException {
        List<List<DescriptiveStatistics>> summary = new ArrayList<>();
        String line = "";
        String commentedContent = "";

        //read files
        for (int file = 0; file < files.length; file++) {
            BufferedReader reader = new BufferedReader(new FileReader(files[file]));
            int lineCounter = 0;
            while ((line = reader.readLine()) != null) {
                String[] rows = line.trim().split(Params.COLUMN_SEPARATOR);
                if (!rows[0].contains(Params.COMMENT_CHARACTER)) {
                    if (summary.size() <= lineCounter) {
                        summary.add(new ArrayList<>());
                    }
                    for (int row = 0; row < rows.length; row++) {
                        if (summary.get(lineCounter).size() <= row) {
                            summary.get(lineCounter).add(new DescriptiveStatistics());
                        }
                        summary.get(lineCounter).get(row).addValue(Double.parseDouble(rows[row]));
                    }
                    lineCounter++;
                } else if (file == 0) {
                    commentedContent += line + "\n";
                }

            }
        }

        String path = files[0].getParent();

        FileWriter mean = new FileWriter(path + File.separator + "mean.txt");
        FileWriter stdev = new FileWriter(path + File.separator + "stdev.txt");
        mean.write(commentedContent);
        stdev.write(commentedContent);

        //print mean and standar deviation
        for (int summaryLines = 0; summaryLines < summary.size(); summaryLines++) {
            for (int summaryLineRow = 0; summaryLineRow < summary.get(summaryLines).size(); summaryLineRow++) {

            }
        }
        for (int summaryLines = 0; summaryLines < summary.size(); summaryLines++) {
            for (int summaryLineRow = 0; summaryLineRow < summary.get(summaryLines).size(); summaryLineRow++) {
                if (summaryLineRow == 0) {
                    mean.write(summaryLines + Params.COLUMN_SEPARATOR);
                    stdev.write(summaryLines + Params.COLUMN_SEPARATOR);
                } else {
                    mean.write(summary.get(summaryLines).get(summaryLineRow).getMean() + Params.COLUMN_SEPARATOR);
                    stdev.write(summary.get(summaryLines).get(summaryLineRow).getStandardDeviation() + Params.COLUMN_SEPARATOR);
                }
            }
            mean.write("\n");
            stdev.write("\n");
        }
        mean.flush();
        stdev.flush();
        mean.close();
        stdev.close();

    }

    public static void printSelectedColumns(File[] files) throws FileNotFoundException, IOException {
        String line = "";
        String selectedContent = "";

        //read files
        for (int file = 0; file < files.length; file++) {

            //print only average file
//            if (files[file].getName().contains(fileToBePrinted)) {
            BufferedReader reader = new BufferedReader(new FileReader(files[file]));
            while ((line = reader.readLine()) != null) {
                String[] rows = line.trim().split(Params.COLUMN_SEPARATOR);
                if (!rows[0].contains(Params.COMMENT_CHARACTER)) {
                    for (int column : columnsToPrint) {
                        selectedContent = rows[column] + Params.COLUMN_SEPARATOR;
                    }
                }
            }
            System.out.print(selectedContent + "\n");
//            System.out.print(files[file].getParent() + Params.COLUMN_SEPARATOR + selectedContent + "\n");
//            }

        }
    }

    public static void printMeanConvergenceCurve(File[] files) throws FileNotFoundException, IOException {
        String line = "";
        String selectedContent = "";

        //read files
        for (int file = 0; file < files.length; file++) {
            if (files[file].getName().equals(fileToBePrinted)) {
                BufferedReader reader = new BufferedReader(new FileReader(files[file]));
                while ((line = reader.readLine()) != null) {
                    String[] rows = line.trim().split(Params.COLUMN_SEPARATOR);
                    if (!rows[0].contains(Params.COMMENT_CHARACTER)) {
                        for (int column : columnsToPrint) {
                            selectedContent += rows[column] + Params.COLUMN_SEPARATOR;
                        }
                        selectedContent += "\n";
                    }
                }
                System.out.println(files[file].getCanonicalPath() + "\n" + selectedContent);
            }
        }
    }

}

enum AnalysisType {

    GENERATE_AVERAGES("Generate Averages"),
    PRINT_SELECTED_COLUMNS("Print selected columns"),
    PRINT_MEAN_CONVERGENCE_CURVE("Print main convergence curve");

    private final String value;

    private AnalysisType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
