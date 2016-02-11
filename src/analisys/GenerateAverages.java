/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class GenerateAverages {

    public static void main(String[] args) throws IOException {
        System.out.println("!-----------------------------------------------------!");
        System.out.println("!Generates the average files of repetead simulations  !");
        System.out.println("!-----------------------------------------------------!");
        String experimentsDirectory = Params.OUTPUTS_DIRECTORY;
//        File directory = new File(experimentsDirectory);
        File directory = new File("/media/Dados/Dropbox/Profissional/UFRGS/Doutorado/Pesquisa/tap-simulator/results/ow/qlstatefull/epsilon_0.99/alpha_0.9/gamma_0.99");
        walk(experimentsDirectory);
        
        generate_summary_files(experimentsDirectory);
    }

    public static void walk(String path) throws FileNotFoundException, IOException {
        File root = new File(path);
        File[] list = root.listFiles();
        Arrays.sort(list, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return String.valueOf(f1.getName()).compareTo(f2.getName());
            }
        });

        if (list == null) {
            return;
        }

        for (File file : list) {
            if (file.isDirectory()) {
                walk(file.getAbsolutePath());
            } else {
                System.out.print("Analisyng Directory: " + file.getParent());
                //analize all files                               
                generate_averages(list);
                
//                print_selected_cells(list);
                
                System.out.println(" <--- mean.txt and stdev.txt created");
                break;
            }
        }
    }

    public static void generate_averages(File[] files) throws FileNotFoundException, IOException {
        List<List<DescriptiveStatistics>> summary = new ArrayList<>();
        String line = "";
        String commentedContent = "";

        //read files
        for (int file = 0; file < files.length; file++) {
            BufferedReader reader = new BufferedReader(new FileReader(files[file]));
            int lineCounter = 0;
            while ((line = reader.readLine()) != null) {
                String[] rows = line.trim().split(Params.SEPARATOR);
                if (!rows[0].contains(Params.COMMENT)) {
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
                    mean.write(summaryLines + Params.SEPARATOR);
                    stdev.write(summaryLines + Params.SEPARATOR);
                } else {
                    mean.write(summary.get(summaryLines).get(summaryLineRow).getMean() + Params.SEPARATOR);
                    stdev.write(summary.get(summaryLines).get(summaryLineRow).getStandardDeviation() + Params.SEPARATOR);
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
    
    public static void print_selected_cells(File[] files) throws FileNotFoundException, IOException {
        List<List<DescriptiveStatistics>> summary = new ArrayList<>();
        String line = "";
        String selectedContent = "";

        //read files
        for (int file = 0; file < files.length; file++) {
            BufferedReader reader = new BufferedReader(new FileReader(files[file]));
            int lineCounter = 0;
            while ((line = reader.readLine()) != null) {
                String[] rows = line.trim().split(Params.SEPARATOR);
                if (!rows[0].contains(Params.COMMENT)) {
                    
                    selectedContent = rows[1];
                    lineCounter++;
                } 
            }
            System.out.println(files[file].getName() + Params.SEPARATOR + selectedContent);
        }
    }

    public static void generate_summary_files(String path) throws IOException {

        int[] rowsToPrint = {1};

        File root = new File(path);
        File[] list = root.listFiles();
        Arrays.sort(list, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return String.valueOf(f1.getName()).compareTo(f2.getName());
            }
        });

        if (list == null) {
            return;
        }

        for (File file : list) {
            if (file.isDirectory()) {
                generate_summary_files(file.getAbsolutePath());
            } else {

                FilenameFilter filterByName = (File dir, String name) -> {
                    if (name.endsWith("stdev.txt")) {
                        return true;
                    } else {
                        return false;
                    }
                };

                File[] meanFile = root.listFiles(filterByName);
                File f = meanFile[0];

                BufferedReader reader = new BufferedReader(new FileReader(f));
                int lineCounter = 0;
                String line = "";
                String content = "";
                while ((line = reader.readLine()) != null) {
                    String[] rows = line.trim().split(Params.SEPARATOR);
                    if (!rows[0].contains(Params.COMMENT)) {
                        for (int row = 0; row < rows.length; row++) {
                            if (row==1) {
                                content = rows[row];
                            }
                        }
                        lineCounter++;
                    }

                }
                System.out.println(f.getParent() + Params.SEPARATOR + content);
                break;
            }
        }

    }
}
