package com.converter;

import com.opencsv.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class currency_converter {

    public static void main(String[] args) {
        Map arguments = getArguments(args);
        int FIELD = (int) arguments.get("field"); FIELD = FIELD - 1;
        double MULTIPLIER = (double) arguments.get("multiplier");
        String INPUT = (String) arguments.get("input");
        String OUTPUT = (String) arguments.get("output");

        try {
            FileReader fileReader = new FileReader(INPUT);
            //CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
            //CSVReader csvReader = new CSVReaderBuilder(filereader).withCSVParser(parser).build();
            CSVReader csvReader = new CSVReader(fileReader);
            String[] nextLine;
            double PRICE;

            // Read the first line with a Header
            nextLine = csvReader.readNext();
            writeData(OUTPUT, nextLine);

            while ((nextLine = csvReader.readNext()) != null) {
                DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.FRANCE);
                //DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
                //decimalFormatSymbols.setDecimalSeparator('.');

                DecimalFormat df = new DecimalFormat("#,##0.00", decimalFormatSymbols);

                nextLine[FIELD] = nextLine[FIELD].replace("\"","");
                nextLine[FIELD] = nextLine[FIELD].replace(",",".");
                PRICE = Double.parseDouble(nextLine[FIELD].replace("\"","")) * MULTIPLIER;
                if ( OUTPUT.equals("stdout") ) {
                    nextLine[FIELD] = String.valueOf('"' + df.format(PRICE) + '"');
                } else {
                    nextLine[FIELD] = String.valueOf(df.format(PRICE));
                }
                appendData(OUTPUT, nextLine);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeData(String OUTPUT, String[] data){
        try {
            if ( OUTPUT.equals("stdout") ){
                System.out.println(String.join(",", data));
            } else {
                FileWriter fileWriter = new FileWriter(OUTPUT);
                CSVWriter csvWriter = new CSVWriter(fileWriter);
                csvWriter.writeNext(data, false);
                csvWriter.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void appendData(String OUTPUT, String[] data){
        try {
            if ( OUTPUT.equals("stdout") ){
                System.out.println(String.join(",", data));
            } else {
                FileWriter fileWriter = new FileWriter(OUTPUT, true);
                CSVWriter csvWriter = new CSVWriter(fileWriter);
                csvWriter.writeNext(data, false);
                csvWriter.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map getArguments(String[] args) {
        Map arguments = new HashMap<>();
        Options options = new Options();

        Option field = new Option("f", "field", true, "Convert CSV field N (2 by default)");
        field.setRequired(false);
        options.addOption(field);

        Option multiplier = new Option("m", "multiplier", true, "Multiply currency value by N for the current conversion rate");
        multiplier.setRequired(true);
        options.addOption(multiplier);

        Option input = new Option("i", "input", true, "Read from input file (or stdin)");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "Write to output file (stdout by default)");
        output.setRequired(false);
        options.addOption(output);

        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("currency_converter <--field N> [--multiplier N] [-i input] <-o output>", options);
            System.exit(1);
        }

        if ( cmd.hasOption("field") ) {
            arguments.put("field", Integer.parseInt(cmd.getOptionValue("field")));
        } else {
            arguments.put("field", 2);
        }
        if ( cmd.hasOption("output") ) {
            arguments.put("output", cmd.getOptionValue("output"));
        } else {
            arguments.put("output", "stdout");
        }

        arguments.put("multiplier", Double.parseDouble(cmd.getOptionValue("multiplier")));
        arguments.put("input", cmd.getOptionValue("input"));
        return arguments;
    }


}